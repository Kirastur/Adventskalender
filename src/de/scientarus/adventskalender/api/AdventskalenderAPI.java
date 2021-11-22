package de.scientarus.adventskalender.api;

import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import de.scientarus.adventskalender.config.ConfigManager;
import de.scientarus.adventskalender.exception.AdventskalenderException;
import de.scientarus.adventskalender.gui.GuiManager;
import de.scientarus.adventskalender.listener.ListenManager;
import de.scientarus.adventskalender.messages.Message;
import de.scientarus.adventskalender.player.PlayerManager;

public class AdventskalenderAPI {
	protected final Plugin plugin;
	protected final ConfigManager configManager;
	protected final PlayerManager playerManager;
	protected final GuiManager guiManager;
	protected final ListenManager listenManager;

	public AdventskalenderAPI(Plugin plugin) throws AdventskalenderException, IOException {
		this.plugin = plugin;
		configManager = new ConfigManager(plugin);
		playerManager = new PlayerManager(plugin);
		guiManager = new GuiManager(plugin, configManager, playerManager);
		listenManager = new ListenManager(plugin, configManager, playerManager, guiManager);
	}

	public Inventory openGui(Player player) {
		return guiManager.openGui(player);
	}

	public void reload() throws AdventskalenderException {
		plugin.reloadConfig();
		configManager.loadConfig();
		playerManager.reload();
	}

	public String getMessage(Message message) {
		return configManager.getMessage(message);
	}
}
