package de.scientarus.adventskalender.player;

import java.io.File;
import java.io.IOException;
import java.time.Instant;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import de.scientarus.adventskalender.exception.AdventskalenderException;

public class PlayerManager {

	public static final String PLAYER_FILENAME = "player.yml";
	protected final Plugin plugin;
	protected File playerFile;
	protected FileConfiguration playerFileConfiguration;

	public PlayerManager(Plugin plugin) throws AdventskalenderException, IOException {
		this.plugin = plugin;
		playerFile = new File(plugin.getDataFolder(), PLAYER_FILENAME);
		if (!playerFile.exists() && !playerFile.createNewFile()) {
			throw new AdventskalenderException(String.format("Cannot create config file %s", PLAYER_FILENAME));
		}
		playerFileConfiguration = YamlConfiguration.loadConfiguration(playerFile);
	}

	public void setDayPlayer(Player player, int day) throws IOException {
		String uuIdString = player.getUniqueId().toString();
		if (!playerFileConfiguration.isConfigurationSection(uuIdString)) {
			playerFileConfiguration.createSection(uuIdString);
		}
		ConfigurationSection playerSection = playerFileConfiguration.getConfigurationSection(uuIdString);
		playerSection.set("name", player.getName());
		playerSection.set(Integer.toString(day), Instant.now().toString());
		playerFileConfiguration.save(playerFile);
	}

	public boolean hasDayPlayer(Player player, int day) {
		String uuIdString = player.getUniqueId().toString();
		if (!playerFileConfiguration.isConfigurationSection(uuIdString)) {
			return false;
		}
		ConfigurationSection playerSection = playerFileConfiguration.getConfigurationSection(uuIdString);
		String s = playerSection.getString(Integer.toString(day), "");
		return (!s.isEmpty());
	}

	public void reload() {
		playerFileConfiguration = YamlConfiguration.loadConfiguration(playerFile);
	}

}
