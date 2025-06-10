package dev.cammiescorner.boxtrot.common.config;

import com.teamresourceful.resourcefulconfig.common.annotations.Config;
import com.teamresourceful.resourcefulconfig.common.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.common.config.EntryType;
import dev.cammiescorner.boxtrot.BoxTrot;

import java.util.ArrayList;
import java.util.List;

@Config(BoxTrot.MOD_ID)
public final class BoxTrotConfig {
	@ConfigEntry(id = "doesBarrelFoolEndermen", type = EntryType.BOOLEAN, translation = "config." + BoxTrot.MOD_ID + ".does_barrel_fool_endermen")
	public static boolean doesBarrelFoolEndermen = true;
	@ConfigEntry(id = "doesBarrelHideParticles", type = EntryType.BOOLEAN, translation = "config." + BoxTrot.MOD_ID + ".does_barrel_hide_particles")
	public static boolean doesBarrelHideParticles = true;
	@ConfigEntry(id = "doesBarrelFoolMobs", type = EntryType.BOOLEAN, translation = "config." + BoxTrot.MOD_ID + ".does_barrel_fool_mobs")
	public static boolean doesBarrelFoolMobs = true;
	@ConfigEntry(id = "doesBarrelFoolAttackers", type = EntryType.BOOLEAN, translation = "config." + BoxTrot.MOD_ID + ".does_barrel_fool_attackers")
	public static boolean doesBarrelFoolAttackers = false;
	@ConfigEntry(id = "canOpenPlayerBarrels", type = EntryType.BOOLEAN, translation = "config." + BoxTrot.MOD_ID + ".can_open_player_barrels")
	public static boolean canOpenPlayerBarrels = true;
	@ConfigEntry(id = "barrelRotates", type = EntryType.BOOLEAN, translation = "config." + BoxTrot.MOD_ID + ".barrel_rotates")
	public static boolean barrelRotates = true;

	public static List<Boolean> getConfigValues() {
		List<Boolean> configValues =  new ArrayList<>();
		configValues.add(doesBarrelHideParticles);
		configValues.add(barrelRotates);
		return configValues;
	}
}
