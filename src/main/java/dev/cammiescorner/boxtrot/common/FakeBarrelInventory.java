package dev.cammiescorner.boxtrot.common;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FakeBarrelInventory extends SimpleContainer {
	private Player target;
	private final Level world;

	public FakeBarrelInventory(Player target) {
		super(27);
		this.target = target;
		this.world = target.level();

		for(int i = 0; i < 27; i++)
			setItem(i, target.getInventory().getItem(i + 9));
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		super.setItem(slot, stack);
		target.getInventory().setItem(slot + 9, stack);
	}

	@Override
	public void startOpen(Player player) {
		super.startOpen(player);
		world.playSound(null, target.getX(), target.getY() + 0.5, target.getZ(), SoundEvents.BARREL_OPEN, SoundSource.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
	}

	@Override
	public void stopOpen(Player player) {
		super.stopOpen(player);
		world.playSound(null, target.getX(), target.getY() + 0.5, target.getZ(), SoundEvents.BARREL_OPEN, SoundSource.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
	}

	public Player getTarget() {
		return target;
	}

	public void setTarget(Player player) {
		target = player;
	}
}
