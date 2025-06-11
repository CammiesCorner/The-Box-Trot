package dev.cammiescorner.boxtrot.common.config;

import com.teamresourceful.resourcefulconfig.api.types.ResourcefulConfig;
import com.teamresourceful.resourcefulconfig.client.ConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.cammiescorner.boxtrot.BoxTrot;

public class BoxTrotModMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return screen -> {
			ResourcefulConfig config = BoxTrot.configurator.getConfig(BoxTrotConfig.class);

			if(config == null)
				return null;

			return new ConfigScreen(null, config);
		};
	}
}
