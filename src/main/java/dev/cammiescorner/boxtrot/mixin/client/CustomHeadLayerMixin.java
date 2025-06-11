package dev.cammiescorner.boxtrot.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.cammiescorner.boxtrot.BoxTrot;
import dev.cammiescorner.boxtrot.client.models.SussyBarrelModel;
import dev.cammiescorner.boxtrot.common.FakeBarrel;
import dev.cammiescorner.boxtrot.common.config.BoxTrotConfig;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true)
@Mixin(CustomHeadLayer.class)
public abstract class CustomHeadLayerMixin<T extends LivingEntity, M extends EntityModel<T> & HeadedModel> extends RenderLayer<T, M> {
	@Unique private boolean isBarrel;
	@Unique private SussyBarrelModel<T> barrelModel;
	@Unique private static final ItemStack BARREL_STACK = new ItemStack(Items.BARREL);
	@Unique private static final ResourceLocation SUSSY_BARREL = BoxTrot.id("textures/entity/sussy_barrel.png");

	@Shadow @Final private ItemInHandRenderer itemInHandRenderer;

	public CustomHeadLayerMixin(RenderLayerParent<T, M> context) { super(context); }

	@Inject(method = "<init>(Lnet/minecraft/client/renderer/entity/RenderLayerParent;Lnet/minecraft/client/model/geom/EntityModelSet;Lnet/minecraft/client/renderer/ItemInHandRenderer;)V", at = @At("TAIL"))
	private void setModel(RenderLayerParent<T, M> renderer, EntityModelSet modelSet, ItemInHandRenderer itemInHandRenderer, CallbackInfo ci) {
		barrelModel = new SussyBarrelModel<T>(modelSet.bakeLayer(SussyBarrelModel.MODEL_LAYER));
	}

	@WrapWithCondition(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/model/geom/ModelPart;translateAndRotate(Lcom/mojang/blaze3d/vertex/PoseStack;)V"
	))
	private boolean noWeirdItemShit(ModelPart instance, PoseStack poseStack) {
		return !isBarrel;
	}

	@Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
	), cancellable = true)
	private void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci, @Local ItemStack itemStack) {
		isBarrel = itemStack.is(Items.BARREL);

		if(isBarrel) {
			poseStack.popPose();
			poseStack.pushPose();

			poseStack.translate(0, 0.625, 0);

			if(!livingEntity.isCrouching()) {
				poseStack.translate(0, -1.375, 0);
				barrelModel.renderToBuffer(poseStack, buffer.getBuffer(RenderType.entityCutout(SUSSY_BARREL)), packedLight, OverlayTexture.NO_OVERLAY, 0xffffffff);
			}
			else {
				if (BoxTrotConfig.barrelRotates && livingEntity instanceof AbstractClientPlayer player) {
					poseStack.mulPose(Axis.YN.rotationDegrees(((FakeBarrel) player).boxtrot$getBarrelYaw()));
					poseStack.mulPose(Axis.XP.rotationDegrees(((FakeBarrel) player).boxtrot$getBarrelPitch()));
				}

				itemInHandRenderer.renderItem(livingEntity, BARREL_STACK, ItemDisplayContext.NONE, false, poseStack, buffer, packedLight);
			}

			poseStack.popPose();
			ci.cancel();
		}
	}
}
