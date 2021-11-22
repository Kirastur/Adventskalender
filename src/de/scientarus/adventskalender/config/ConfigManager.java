package de.scientarus.adventskalender.config;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import de.scientarus.adventskalender.exception.AdventskalenderException;
import de.scientarus.adventskalender.messages.Message;

public class ConfigManager {

	protected static final String SECTION_REWARDS = "rewards";
	protected static final String SECTION_ICONS = "icons";
	protected static final String SECTION_MESSAGES = "messages";
	protected static final String REWARDS_AMOUNT = "amount";
	protected static final String REWARDS_MATERIAL = "material";
	protected static final String REWARDS_NAME = "name";
	protected static final String ICONS_PAST = "past";
	protected static final String ICONS_CLOSED = "closed";
	protected static final String ICONS_OPEN = "open";
	protected static final String ICONS_FUTURE = "future";
	protected static final String ICONS_FILLER = "filler";
	protected static final String ROOT_FAKEDAY = "fakeday";

	protected final Plugin plugin;
	protected List<Reward> rewards = new ArrayList<>();
	protected Material iconPast;
	protected Material iconClosed;
	protected Material iconOpen;
	protected Material iconFuture;
	protected Material iconFiller;

	public ConfigManager(Plugin plugin) {
		this.plugin = plugin;
	}

	public Material getIconPast() {
		return iconPast;
	}

	public Material getIconClosed() {
		return iconClosed;
	}

	public Material getIconOpen() {
		return iconOpen;
	}

	public Material getIconFuture() {
		return iconFuture;
	}

	public Material getIconFiller() {
		return iconFiller;
	}

	public void loadConfig() throws AdventskalenderException {
		rewards.clear();
		ConfigurationSection rootConfigurationSection = plugin.getConfig().getRoot();
		ConfigurationSection rewardsConfigurationSection = rootConfigurationSection
				.getConfigurationSection(SECTION_REWARDS);
		for (int i = 1; i <= 24; i++) {
			String myDay = Integer.toString(i);
			if (!rewardsConfigurationSection.isConfigurationSection(myDay)) {
				throw new AdventskalenderException(String.format(getMessage(Message.MISSING_DEFINITION), myDay));
			}
			ConfigurationSection dayConfigurationSection = rewardsConfigurationSection.getConfigurationSection(myDay);

			int count = dayConfigurationSection.getInt(REWARDS_AMOUNT);
			String materialString = dayConfigurationSection.getString(REWARDS_MATERIAL);
			String name = "";
			if (dayConfigurationSection.contains(REWARDS_NAME, true)) {
				name = dayConfigurationSection.getString(REWARDS_NAME);
			}

			Material material = Material.getMaterial(materialString);
			if (material == null) {
				throw new AdventskalenderException(
						String.format(getMessage(Message.WRONG_MATERIAL), myDay, materialString));
			}

			Reward reward = new Reward(material, count, name);
			rewards.add(reward);
		}

		ConfigurationSection iconConfigurationSection = rootConfigurationSection.getConfigurationSection(SECTION_ICONS);
		iconPast = Material.valueOf(iconConfigurationSection.getString(ICONS_PAST));
		iconClosed = Material.valueOf(iconConfigurationSection.getString(ICONS_CLOSED));
		iconOpen = Material.valueOf(iconConfigurationSection.getString(ICONS_OPEN));
		iconFuture = Material.valueOf(iconConfigurationSection.getString(ICONS_FUTURE));
		String iconFillerName = iconConfigurationSection.getString(ICONS_FILLER, "");
		if (iconFillerName.isEmpty()) {
			iconFiller = null;
		} else {
			iconFiller = Material.valueOf(iconFillerName);
		}
	}

	public ItemStack buildItemStackFromDay(int day) {
		day = day - 1;
		return rewards.get(day).buildItemStack();
	}

	public String getMessage(Message message) {
		ConfigurationSection rootConfigurationSection = plugin.getConfig().getRoot();
		ConfigurationSection messagesConfigurationSection = rootConfigurationSection
				.getConfigurationSection(SECTION_MESSAGES);
		return messagesConfigurationSection.getString(message.getMessageName(), message.getMessageName());
	}

	public int getFakeDay() {
		ConfigurationSection rootConfigurationSection = plugin.getConfig().getRoot();
		return rootConfigurationSection.getInt(ROOT_FAKEDAY, 0);
	}

}
