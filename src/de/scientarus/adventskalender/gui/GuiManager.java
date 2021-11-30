package de.scientarus.adventskalender.gui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import de.scientarus.adventskalender.config.ConfigManager;
import de.scientarus.adventskalender.messages.Message;
import de.scientarus.adventskalender.player.PlayerManager;

public class GuiManager {

	public static final String TYPE_PAST = "past";
	public static final String TYPE_TODAY_CLOSED = "closed";
	public static final String TYPE_TODAY_OPEN = "open";
	public static final String TYPE_FUTURE = "future";
	public static final String TYPE_FILLER = "filler";

	protected final Plugin plugin;
	protected final ConfigManager configManager;
	protected final PlayerManager playerManager;
	protected final NamespacedKey doorletDayNamespacedKey;
	protected final NamespacedKey doorletTypeNamespacedKey;

	public GuiManager(Plugin plugin, ConfigManager configManager, PlayerManager playerManager) {
		this.plugin = plugin;
		this.configManager = configManager;
		this.playerManager = playerManager;
		doorletDayNamespacedKey = new NamespacedKey(plugin, "DoorletDay");
		doorletTypeNamespacedKey = new NamespacedKey(plugin, "DoorletType");
	}

	public int getDoorletDay(ItemStack itemStack) {
		if (itemStack == null) {
			return 0;
		}
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (itemMeta == null) {
			return 0;
		}
		PersistentDataContainer container = itemMeta.getPersistentDataContainer();
		if (!container.has(doorletDayNamespacedKey, PersistentDataType.INTEGER)) {
			return 0;
		}
		return container.get(doorletDayNamespacedKey, PersistentDataType.INTEGER);
	}

	public String getDoorletType(ItemStack itemStack) {
		if (itemStack == null) {
			return "";
		}
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (itemMeta == null) {
			return "";
		}
		PersistentDataContainer container = itemMeta.getPersistentDataContainer();
		if (!container.has(doorletTypeNamespacedKey, PersistentDataType.STRING)) {
			return "";
		}
		return container.get(doorletTypeNamespacedKey, PersistentDataType.STRING);
	}

	public boolean isDoorlet(ItemStack itemStack) {
		String doorletType = getDoorletType(itemStack);
		return !doorletType.isEmpty();
	}

	public boolean isOpenableDoorlet(ItemStack itemStack) {
		String doorletType = getDoorletType(itemStack);
		return doorletType.equals(TYPE_TODAY_CLOSED);
	}

	public boolean isAlreadyOpenedDoorlet(ItemStack itemStack) {
		String doorletType = getDoorletType(itemStack);
		return doorletType.equals(TYPE_TODAY_OPEN);
	}

	protected ItemStack generateDoorlet(int doorletDay, String doorletType, Material material, String displayName) {
		ItemStack itemStack = new ItemStack(material, doorletDay);
		ItemMeta itemMeta = itemStack.getItemMeta();
		PersistentDataContainer container = itemMeta.getPersistentDataContainer();
		container.set(doorletDayNamespacedKey, PersistentDataType.INTEGER, doorletDay);
		container.set(doorletTypeNamespacedKey, PersistentDataType.STRING, doorletType);
		itemMeta.setDisplayName(displayName);
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

	protected ItemStack generatePastDoorlet(int doorletDay) {
		return generateDoorlet(doorletDay, TYPE_PAST, configManager.getIconPast(),
				configManager.getMessage(Message.GUI_PAST));
	}

	protected ItemStack generateClosedDoorlet(int doorletDay) {
		return generateDoorlet(doorletDay, TYPE_TODAY_CLOSED, configManager.getIconClosed(),
				configManager.getMessage(Message.GUI_TODAY_CLOSED));
	}

	protected ItemStack generateOpenDoorlet(int doorletDay) {
		return generateDoorlet(doorletDay, TYPE_TODAY_OPEN, configManager.getIconOpen(),
				configManager.getMessage(Message.GUI_TODAY_OPEN));
	}

	protected ItemStack generateFutureDoorlet(int doorletDay) {
		return generateDoorlet(doorletDay, TYPE_FUTURE, configManager.getIconFuture(),
				configManager.getMessage(Message.GUI_FUTURE));
	}

	protected ItemStack generateFillerDoorlet() {
		Material iconFiller = configManager.getIconFiller();
		if (iconFiller == null) {
			return null;
		} else {
			return generateDoorlet(1, TYPE_FILLER, iconFiller, " ");
		}
	}

	public List<ItemStack> createDoorletItems(int doorletDay, Player player) {
		List<ItemStack> newDoorletItems = new ArrayList<>();
		for (int i = 1; i < doorletDay; i++) {
			newDoorletItems.add(generatePastDoorlet(i));
		}
		if (playerManager.hasDayPlayer(player, doorletDay)) {
			newDoorletItems.add(generateOpenDoorlet(doorletDay));
		} else {
			newDoorletItems.add(generateClosedDoorlet(doorletDay));
		}
		for (int i = doorletDay + 1; i <= 24; i++) {
			newDoorletItems.add(generateFutureDoorlet(i));
		}
		return newDoorletItems;
	}

	public List<ItemStack> createGuiItems(int doorletDay, Player player) {
		List<ItemStack> newGuiItems = new ArrayList<>();
		List<ItemStack> doorletItems = createDoorletItems(doorletDay, player);
		int day = 0;
		String guiLayout = configManager.getMessage(Message.GUI_LAYOUT);
		for (int i = 0; i < guiLayout.length(); i++) {
			char hint = guiLayout.charAt(i);
			switch (hint) {
			case '+':
				newGuiItems.add(doorletItems.get(day));
				day = day + 1;
				break;
			case '-':
				newGuiItems.add(generateFillerDoorlet());
				break;
			default:
				newGuiItems.add(null);
			}
		}
		return newGuiItems;
	}

	public Inventory openGui(Player player) {
		Calendar calendar = new GregorianCalendar();
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int month = calendar.get(Calendar.MONTH);
		int fakeday = configManager.getFakeDay();
		if (fakeday > 0) {
			day = fakeday;
			month = 11; // Januar = 0
		}
		if ((month < 11) || (day > 24)) {
			player.sendMessage(configManager.getMessage(Message.OUT_OF_TIMEFRAME));
			return null;
		}
		List<ItemStack> guiItems = createGuiItems(day, player);
		int guiItemsSize = guiItems.size();
		Inventory newInventory = Bukkit.createInventory(null, guiItemsSize,
				configManager.getMessage(Message.GUI_TITLE));
		for (int i = 0; i < guiItemsSize; i++) {
			if (guiItems.get(i) != null) {
				newInventory.setItem(i, guiItems.get(i));
			}
		}
		player.openInventory(newInventory);
		return newInventory;
	}

}
