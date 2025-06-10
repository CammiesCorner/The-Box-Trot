package dev.cammiescorner.boxtrot.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.cammiescorner.boxtrot.BoxTrot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
	@Unique private static final ResourceLocation BARREL_OVERLAY = BoxTrot.id("textures/misc/barrel_hole.png");

	@Shadow @Final private Minecraft minecraft;
	@Shadow protected abstract void renderTextureOverlay(GuiGraphics context, ResourceLocation texture, float opacity);

	@ModifyExpressionValue(method = "renderCrosshair", at = @At(value = "FIELD",
			target = "net/minecraft/client/Minecraft.crosshairPickEntity : Lnet/minecraft/world/entity/Entity;",
			ordinal = 0
	))
	public Entity hideAttackIndicator(Entity original) {
		if(original instanceof Player player && player.getItemBySlot(EquipmentSlot.HEAD).is(Items.BARREL) && player.isCrouching())
			return null;

		return original;
	}

	@Inject(method = "render", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"
	))
	public void renderOverlay(GuiGraphics context, float tickDelta, CallbackInfo ci) {
		if(minecraft.player != null && minecraft.player.getItemBySlot(EquipmentSlot.HEAD).is(Items.BARREL))
			renderTextureOverlay(context, BARREL_OVERLAY, 1F);
	}
}
