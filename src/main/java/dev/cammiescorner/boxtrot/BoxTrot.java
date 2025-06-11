package dev.cammiescorner.boxtrot;

import com.teamresourceful.resourcefulconfig.api.loader.Configurator;
import commonnetwork.api.Network;
import dev.cammiescorner.boxtrot.common.config.BoxTrotConfig;
import dev.cammiescorner.boxtrot.common.packets.SyncBoxTrotConfig;
import dev.cammiescorner.boxtrot.common.packets.SyncStandingStillTimer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.resources.ResourceLocation;

public class BoxTrot implements ModInitializer {
	public static final String MOD_ID = "boxtrot";
	public static final Configurator configurator = new Configurator(MOD_ID);
	int timer = 600;

	@Override
	public void onInitialize() {
		configurator.register(BoxTrotConfig.class);

		Network.registerPacket(SyncBoxTrotConfig.TYPE, SyncBoxTrotConfig.class, SyncBoxTrotConfig.CODEC, SyncBoxTrotConfig::handle);
		Network.registerPacket(SyncStandingStillTimer.TYPE, SyncStandingStillTimer.class, SyncStandingStillTimer.CODEC, SyncStandingStillTimer::handle);

		ServerTickEvents.END_SERVER_TICK.register((server -> {
			timer--;

			if(timer <= 0) {
				timer = 600;
				Network.getNetworkHandler().sendToAllClients(new SyncBoxTrotConfig(BoxTrotConfig.getConfigValues()), server);
			}
		}));

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			Network.getNetworkHandler().sendToClient(new SyncBoxTrotConfig(BoxTrotConfig.getConfigValues()), handler.getPlayer());
		});
	}

	public static ResourceLocation id(String name) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
	}
}
