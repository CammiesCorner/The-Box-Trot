package dev.cammiescorner.boxtrot.mixin;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.level.block.BarrelBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BarrelBlock.class)
public class BarrelBlockMixin implements Equipable {
	@Override
	public EquipmentSlot getEquipmentSlot() {
		return EquipmentSlot.HEAD;
	}

	@Override
	public SoundEvent getEquipSound() {
		return SoundEvents.BARREL_OPEN;
	}
}
