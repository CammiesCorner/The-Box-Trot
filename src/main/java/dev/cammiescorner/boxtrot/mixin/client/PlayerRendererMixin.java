package dev.cammiescorner.boxtrot.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
	public PlayerRendererMixin(EntityRendererProvider.Context ctx, PlayerModel<AbstractClientPlayer> model, float shadowRadius) { super(ctx, model, shadowRadius); }

	@Inject(method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"), cancellable = true)
	private void ceaseRendering(AbstractClientPlayer player, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
		if(player.getItemBySlot(EquipmentSlot.HEAD).is(Items.BARREL) && player.isCrouching()) {
			for(RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> layer : layers)
				if(layer instanceof CustomHeadLayer)
					layer.render(poseStack, buffer, packedLight, player, 0, 0, partialTicks, 0, 0, 0);

			ci.cancel();
		}
	}

	@Inject(method = "setModelProperties", at = @At("TAIL"))
	private void hideModelParts(AbstractClientPlayer player, CallbackInfo info) {
		if(player.getItemBySlot(EquipmentSlot.HEAD).is(Items.BARREL)) {
			Minecraft client = Minecraft.getInstance();
			boolean renderArms = client.getCameraEntity() == player && client.options.getCameraType().isFirstPerson();

			getModel().head.visible = false;
			getModel().hat.visible = false;
			getModel().body.visible = false;
			getModel().jacket.visible = false;

			getModel().leftArm.visible = renderArms;
			getModel().leftSleeve.visible = renderArms;
			getModel().rightArm.visible = renderArms;
			getModel().rightSleeve.visible = renderArms;
		}
	}

	@Inject(method = "renderNameTag(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V", at = @At("HEAD"), cancellable = true)
	public void hideName(AbstractClientPlayer entity, Component displayName, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float partialTick, CallbackInfo ci) {
		if(entity.getItemBySlot(EquipmentSlot.HEAD).is(Items.BARREL) && entity.isCrouching())
			ci.cancel();
	}
}
