package dev.cammiescorner.boxtrot.common.packets;

import dev.cammiescorner.boxtrot.BoxTrot;
import dev.cammiescorner.boxtrot.common.config.BoxTrotConfig;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public class SyncBoxTrotConfig {
	public static final ResourceLocation ID = BoxTrot.id("sync_box_trot_config");

	public static void send(ServerPlayer player, MinecraftServer server) {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

		buf.writeCollection(BoxTrotConfig.getConfigValues(), FriendlyByteBuf::writeBoolean);

		ServerPlayNetworking.send(player, ID, buf);
	}

	public static void sendToAll(MinecraftServer server) {
		for(ServerPlayer player : server.getPlayerList().getPlayers())
			send(player, server);
	}

	public static void handler(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender sender) {
		List<Boolean> configValues = buf.readCollection(ArrayList::new, FriendlyByteBuf::readBoolean);

		if(!configValues.equals(BoxTrotConfig.getConfigValues()))
			handler.onDisconnect(Component.translatable("boxtrot.configdesync"));
	}
}
