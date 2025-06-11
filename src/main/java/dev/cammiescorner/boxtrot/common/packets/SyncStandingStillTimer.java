package dev.cammiescorner.boxtrot.common.packets;

import commonnetwork.networking.data.PacketContext;
import dev.cammiescorner.boxtrot.BoxTrot;
import dev.cammiescorner.boxtrot.common.FakeBarrel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SyncStandingStillTimer(int timer) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<SyncStandingStillTimer> TYPE = new CustomPacketPayload.Type<>(BoxTrot.id("standing_still_timer"));
	public static final StreamCodec<? extends FriendlyByteBuf, SyncStandingStillTimer> CODEC = StreamCodec.of((buffer, packet) -> {
		buffer.writeVarInt(packet.timer);
	}, buffer -> {
		int timer = buffer.readVarInt();

		return new SyncStandingStillTimer(timer);
	});

	public static void handle(PacketContext<SyncStandingStillTimer> context) {
		((FakeBarrel) context.sender()).boxtrot$setStoodStillFor(context.message().timer());
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
