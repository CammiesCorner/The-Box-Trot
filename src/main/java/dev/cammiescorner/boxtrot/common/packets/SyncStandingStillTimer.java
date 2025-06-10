package dev.cammiescorner.boxtrot.common.packets;

import dev.cammiescorner.boxtrot.BoxTrot;
import dev.cammiescorner.boxtrot.common.FakeBarrel;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class SyncStandingStillTimer {
	public static final ResourceLocation ID = BoxTrot.id("sync_standing_still_timer_server");

	public static void send(int value) {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

		buf.writeVarInt(value);

		ClientPlayNetworking.send(ID, buf);
	}

	public static void handler(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender sender) {
		int value = buf.readVarInt();

		server.execute(() -> ((FakeBarrel) player).boxtrot$setStoodStillFor(value));
	}
}
