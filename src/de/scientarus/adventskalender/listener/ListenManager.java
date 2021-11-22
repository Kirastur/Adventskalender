package de.scientarus.adventskalender.listener;

import org.bukkit.plugin.Plugin;

import de.scientarus.adventskalender.config.ConfigManager;
import de.scientarus.adventskalender.gui.GuiManager;
import de.scientarus.adventskalender.player.PlayerManager;

public class ListenManager {

	protected final Plugin plugin;
	protected final ConfigManager configManager;
	protected final PlayerManager playerManager;
	protected final GuiListener guiListener;

	public ListenManager(Plugin plugin, ConfigManager configManager, PlayerManager playerManager,
			GuiManager guiManager) {
		this.plugin = plugin;
		this.configManager = configManager;
		this.playerManager = playerManager;
		guiListener = new GuiListener(plugin, configManager, playerManager, guiManager);
	}

}
