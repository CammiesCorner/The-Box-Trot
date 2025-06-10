package dev.cammiescorner.boxtrot.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.cammiescorner.boxtrot.common.config.BoxTrotConfig;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	@Shadow public abstract ItemStack getItemBySlot(EquipmentSlot slot);

	public LivingEntityMixin(EntityType<?> type, Level level) { super(type, level); }

	@WrapWithCondition(method = "tickEffects", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
	))
	private boolean noParticles(Level instance, ParticleOptions particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
		return !(BoxTrotConfig.doesBarrelHideParticles && isCrouching() && getItemBySlot(EquipmentSlot.HEAD).is(Items.BARREL));
	}

	@Inject(method = "canAttack(Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At("HEAD"), cancellable = true)
	private void noTarget(LivingEntity target, CallbackInfoReturnable<Boolean> info) {
		if(BoxTrotConfig.doesBarrelFoolAttackers && target instanceof Player player && player.isCrouching() && player.getItemBySlot(EquipmentSlot.HEAD).is(Items.BARREL))
			info.setReturnValue(false);
	}

	@Inject(method = "hasLineOfSight", at = @At("HEAD"), cancellable = true)
	private void noSee(Entity entity, CallbackInfoReturnable<Boolean> info) {
		if(BoxTrotConfig.doesBarrelFoolMobs && entity instanceof Player player && player.isCrouching() && player.getItemBySlot(EquipmentSlot.HEAD).is(Items.BARREL))
			info.setReturnValue(false);
	}
}
