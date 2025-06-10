package dev.cammiescorner.boxtrot.client;

import dev.cammiescorner.boxtrot.client.models.SussyBarrelModel;
import dev.cammiescorner.boxtrot.common.packets.SyncBoxTrotConfig;
import dev.cammiescorner.boxtrot.mixin.client.LevelRendererAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class BoxTrotClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityModelLayerRegistry.registerModelLayer(SussyBarrelModel.MODEL_LAYER, SussyBarrelModel::getTexturedModelData);

		ClientPlayNetworking.registerGlobalReceiver(SyncBoxTrotConfig.ID, SyncBoxTrotConfig::handler);

		WorldRenderEvents.AFTER_ENTITIES.register(context -> {
			Minecraft client = Minecraft.getInstance();

			if(client.hitResult instanceof EntityHitResult hitResult && hitResult.getEntity() instanceof Player target && target.getItemBySlot(EquipmentSlot.HEAD).is(Items.BARREL) && target.isCrouching() && (target.getX() == target.getBlockX() + 0.5 && target.getZ() == target.getBlockZ() + 0.5)) {
				Vec3 camPos = context.camera().getPosition();

				((LevelRendererAccessor) client.levelRenderer).boxtrot$drawBlockOutline(
						context.matrixStack(), context.consumers().getBuffer(RenderType.lines()), client.player,
						camPos.x(), camPos.y(), camPos.z(), target.blockPosition(), Blocks.BARREL.defaultBlockState());
			}
		});
	}
}
