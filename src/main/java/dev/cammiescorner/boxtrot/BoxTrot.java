package dev.cammiescorner.boxtrot;

import com.teamresourceful.resourcefulconfig.common.config.Configurator;
import dev.cammiescorner.boxtrot.common.config.BoxTrotConfig;
import dev.cammiescorner.boxtrot.common.packets.SyncBoxTrotConfig;
import dev.cammiescorner.boxtrot.common.packets.SyncStandingStillTimer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;

public class BoxTrot implements ModInitializer {
	public static final String MOD_ID = "boxtrot";
	public static final Configurator configurator = new Configurator();
	int timer = 600;

	@Override
	public void onInitialize() {
		configurator.registerConfig(BoxTrotConfig.class);

		ServerPlayNetworking.registerGlobalReceiver(SyncStandingStillTimer.ID, SyncStandingStillTimer::handler);

		ServerTickEvents.END_SERVER_TICK.register((server -> {
			timer--;

			if(timer <= 0) {
				timer = 600;
				SyncBoxTrotConfig.sendToAll(server);
			}
		}));

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			SyncBoxTrotConfig.send(handler.getPlayer(), server);
		});
	}

	public static ResourceLocation id(String name) {
		return new ResourceLocation(MOD_ID, name);
	}
}
