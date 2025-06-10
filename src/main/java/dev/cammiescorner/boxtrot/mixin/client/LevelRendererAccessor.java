package dev.cammiescorner.boxtrot.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LevelRenderer.class)
public interface LevelRendererAccessor {
	@Invoker("renderHitOutline")
	void boxtrot$drawBlockOutline(PoseStack matrices, VertexConsumer vertexConsumer, Entity entity, double d, double e, double f, BlockPos pos, BlockState state);
}
