package dev.cammiescorner.boxtrot.common.packets;

import commonnetwork.networking.data.PacketContext;
import dev.cammiescorner.boxtrot.BoxTrot;
import dev.cammiescorner.boxtrot.common.config.BoxTrotConfig;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.ArrayList;
import java.util.List;

public record SyncBoxTrotConfig(List<Boolean> configValues) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<SyncBoxTrotConfig> TYPE = new CustomPacketPayload.Type<>(BoxTrot.id("config"));
	public static final StreamCodec<? extends FriendlyByteBuf, SyncBoxTrotConfig> CODEC = StreamCodec.of((buffer, packet) -> {
		buffer.writeCollection(packet.configValues, FriendlyByteBuf::writeBoolean);
	}, buffer -> {
		List<Boolean> configValues = buffer.readCollection(ArrayList::new, FriendlyByteBuf::readBoolean);

		return new SyncBoxTrotConfig(configValues);
	});

	public static void handle(PacketContext<SyncBoxTrotConfig> context) {
		List<Boolean> configValues = context.message().configValues();

		if(!configValues.equals(BoxTrotConfig.getConfigValues()))
			context.sender().connection.onDisconnect(new DisconnectionDetails(Component.translatable("boxtrot.configdesync")));
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
