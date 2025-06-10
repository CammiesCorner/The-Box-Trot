package dev.cammiescorner.boxtrot.client.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.cammiescorner.boxtrot.BoxTrot;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

public class SussyBarrelModel<T extends Entity> extends EntityModel<T> {
	public static final ModelLayerLocation MODEL_LAYER = new ModelLayerLocation(BoxTrot.id("sussy_barrel"), "main");
	private final ModelPart barrel;

	public SussyBarrelModel(ModelPart root) {
		this.barrel = root.getChild("barrel");
	}

	public static LayerDefinition getTexturedModelData() {
		MeshDefinition data = new MeshDefinition ();
		PartDefinition root = data.getRoot();

		root.addOrReplaceChild("barrel", CubeListBuilder.create().texOffs(0, 0).addBox(-8F, -16F, -8F, 16F, 16F, 16F, new CubeDeformation(0F)), PartPose.offset(0F, 24F, 0F));

		return LayerDefinition.create(data, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		barrel.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
