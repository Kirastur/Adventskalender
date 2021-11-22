package de.scientarus.adventskalender.listener;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import de.scientarus.adventskalender.config.ConfigManager;
import de.scientarus.adventskalender.gui.GuiManager;
import de.scientarus.adventskalender.messages.Message;
import de.scientarus.adventskalender.player.PlayerManager;

public class GuiListener implements Listener {

	protected final Plugin plugin;
	protected final ConfigManager configManager;
	protected final PlayerManager playerManager;
	protected final GuiManager guiManager;

	public GuiListener(Plugin plugin, ConfigManager configManager, PlayerManager playerManager, GuiManager guiManager) {
		this.plugin = plugin;
		this.configManager = configManager;
		this.playerManager = playerManager;
		this.guiManager = guiManager;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	protected boolean containsDoorlet(Inventory inventory) {
		for (ItemStack myItemStack : inventory.getContents()) {
			if (guiManager.isDoorlet(myItemStack)) {
				return true;
			}
		}
		return false;
	}

	protected boolean hasSpaceInInventory(Player player) {
		Inventory inventory = player.getInventory();
		for (ItemStack myItemStack : inventory.getStorageContents()) {
			if (myItemStack == null) {
				return true;
			}
		}
		return false;
	}

	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent event) {
		if (containsDoorlet(event.getInventory())) {
			event.setCancelled(true);
		}
		ItemStack itemStack = event.getCurrentItem();
		if (!guiManager.isDoorlet(itemStack)) {
			return;
		}
		event.setCancelled(true);

		HumanEntity humanEntity = event.getWhoClicked();
		if (!(humanEntity instanceof Player)) {
			return;
		}
		Player player = (Player) humanEntity;
		int day = guiManager.getDoorletDay(itemStack);

		if (guiManager.isAlreadyOpenedDoorlet(itemStack)) {
			player.sendMessage(configManager.getMessage(Message.ALREADY_OPEN));
			player.closeInventory();
			return;
		}

		if (guiManager.isOpenableDoorlet(itemStack)) {
			if (playerManager.hasDayPlayer(player, day)) {
				player.sendMessage(configManager.getMessage(Message.ALREADY_OPEN));
				player.closeInventory();
				return;
			}
			if (!hasSpaceInInventory(player)) {
				humanEntity.closeInventory();
				player.sendMessage(configManager.getMessage(Message.INVENTORY_FULL));
				player.closeInventory();
				return;
			}
			player.sendMessage(String.format(configManager.getMessage(Message.OPEN_OK), day));
			ItemStack rewardItemStack = configManager.buildItemStackFromDay(day);
			if (rewardItemStack != null) {
				player.getInventory().addItem(rewardItemStack);
			}
			try {
				playerManager.setDayPlayer(player, day);
			} catch (Exception e) {
				e.printStackTrace();
			}
			player.closeInventory();
		}
	}

}