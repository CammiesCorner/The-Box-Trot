package dev.cammiescorner.boxtrot.mixin;

import dev.cammiescorner.boxtrot.common.FakeBarrel;
import dev.cammiescorner.boxtrot.common.FakeBarrelInventory;
import dev.cammiescorner.boxtrot.common.config.BoxTrotConfig;
import dev.cammiescorner.boxtrot.common.packets.SyncStandingStillTimer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.OptionalInt;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements FakeBarrel {
	@Unique private int stoodStillFor;
	@Unique private FakeBarrelInventory barrelInventory;
	@Unique private int barrelYaw;
	@Unique private int barrelPitch;

	@Shadow public AbstractContainerMenu containerMenu;
	@Shadow protected abstract void closeContainer();
	@Shadow public abstract ItemStack getItemBySlot(EquipmentSlot slot);
	@Shadow public abstract OptionalInt openMenu(@Nullable MenuProvider menu);

	protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) { super(entityType, level); }

	@Inject(method = "tick", at = @At("HEAD"))
	public void boxtrot$tick(CallbackInfo info) {
		FakeBarrelInventory barrelInventory = boxtrot$getFakeBarrelInv();

		if(barrelInventory != null && containerMenu != null) {
			Player target = barrelInventory.getTarget();

			if(!(target.isCrouching() && target.getItemBySlot(EquipmentSlot.HEAD).is(Items.BARREL)) || distanceToSqr(target) > 64) {
				closeContainer();
				this.barrelInventory = null;
				return;
			}

			Inventory playerInv = target.getInventory();

			for(int i = 0; i < 27; i++)
				barrelInventory.setItem(i, playerInv.getItem(i + 9));
		}

		if(getItemBySlot(EquipmentSlot.HEAD).is(Items.BARREL) && isCrouching()) {
			ScaleData data = ScaleTypes.HITBOX_HEIGHT.getScaleData(this);
			data.setScale(1F / 1.5F);

			data = ScaleTypes.HITBOX_WIDTH.getScaleData(this);
			data.setScale(1F / 0.6F);

			data = ScaleTypes.EYE_HEIGHT.getScaleData(this);
			data.setScale(1F / 1.5F);

			if(level().isClientSide()) {
				if(getDeltaMovement().horizontalDistance() > 0)
					boxtrot$setStoodStillFor(0);
				else
					boxtrot$setStoodStillFor(boxtrot$getStoodStillFor() + 1);

				if (boxtrot$getStoodStillFor() == 0 || boxtrot$getStoodStillFor() == 1) {
					barrelYaw = Math.round(getYRot() / 90f) * 90; // 90 degree increments to line up with the block
					barrelPitch = Math.round(getXRot() / 90f) * 90;
				}
				else if (boxtrot$getStoodStillFor() == 10) {
					setPos(getBlockX() + 0.5, getY(), getBlockZ() + 0.5);
				}

				SyncStandingStillTimer.send(stoodStillFor);
			}
		}
		else {
			boxtrot$setStoodStillFor(0);
			ScaleData data = ScaleTypes.HITBOX_HEIGHT.getScaleData(this);
			data.setScale(1F);

			data = ScaleTypes.HITBOX_WIDTH.getScaleData(this);
			data.setScale(1F);

			data = ScaleTypes.EYE_HEIGHT.getScaleData(this);
			data.setScale(1F);
		}
	}

	@Inject(method = "interactOn", at = @At("HEAD"), cancellable = true)
	private void boxtrot$openInventory(Entity entityToInteractOn, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		if(BoxTrotConfig.canOpenPlayerBarrels && entityToInteractOn instanceof Player target && target.getItemBySlot(EquipmentSlot.HEAD).is(Items.BARREL) && target.isCrouching()) {
			barrelInventory = new FakeBarrelInventory(target);

			openMenu(new SimpleMenuProvider((syncId, inv, player) ->
				ChestMenu.threeRows(syncId, inv, barrelInventory), Component.translatable("container.barrel")
			));

			cir.setReturnValue(InteractionResult.sidedSuccess(level().isClientSide()));
		}
	}

	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
	public void boxtrot$writeNbt(CompoundTag tag, CallbackInfo info) {
		tag.putInt("StoodStillFor", stoodStillFor);
		tag.putInt("BarrelYaw", barrelYaw);
		tag.putInt("BarrelPitch", barrelPitch);
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	public void boxtrot$readNbt(CompoundTag tag, CallbackInfo info) {
		stoodStillFor = tag.getInt("StoodStillFor");
		barrelYaw = tag.getInt("BarrelYaw");
		barrelPitch = tag.getInt("BarrelPitch");
	}

	@Override
	public boolean canBeCollidedWith() {
		return getItemBySlot(EquipmentSlot.HEAD).is(Items.BARREL) && isCrouching();
	}

	@Override
	public boolean isPushable() {
		return !(getItemBySlot(EquipmentSlot.HEAD).is(Items.BARREL) && isCrouching());
	}

	@Override
	public FakeBarrelInventory boxtrot$getFakeBarrelInv() {
		return barrelInventory;
	}

	@Override
	public int boxtrot$getStoodStillFor() {
		return stoodStillFor;
	}

	@Override
	public void boxtrot$setStoodStillFor(int value) {
		stoodStillFor = value;
	}

	@Override
	public int boxtrot$getBarrelYaw() {
		return barrelYaw;
	}

	@Override
	public int boxtrot$getBarrelPitch() {
		return barrelPitch;
	}
}
