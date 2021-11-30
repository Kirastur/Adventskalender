package de.scientarus.adventskalender.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import de.scientarus.adventskalender.exception.AdventskalenderException;
import de.scientarus.adventskalender.messages.Message;
import joptsimple.internal.Strings;

public class ConfigManager {

	protected static final String SECTION_REWARDS = "rewards";
	protected static final String SECTION_ICONS = "icons";
	protected static final String SECTION_MESSAGES = "messages";
	protected static final String REWARDS_AMOUNT = "amount";
	protected static final String REWARDS_MATERIAL = "material";
	protected static final String REWARDS_NAME = "name";
	protected static final String REWARDS_DESCRIPTION = "description";
	protected static final String REWARDS_ENCHANTMENT_TYPE = "enchantmentType";
	protected static final String REWARDS_ENCHANTMENT_LEVEL = "enchantmentLevel";
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

	protected Enchantment findEnchantment(String enchantmentType) {
		for (Enchantment myEnchantment : Enchantment.values()) {
			if (myEnchantment.getKey().getKey().equalsIgnoreCase(enchantmentType)) {
				return myEnchantment;
			}
		}
		return null;
	}

	protected Reward loadReward(ConfigurationSection dayConfigurationSection) throws AdventskalenderException {
		String myDay = dayConfigurationSection.getName();
		int count = dayConfigurationSection.getInt(REWARDS_AMOUNT, 1);
		String materialString = dayConfigurationSection.getString(REWARDS_MATERIAL);

		String name = "";
		String description = "";
		if (dayConfigurationSection.contains(REWARDS_NAME, true)) {
			name = dayConfigurationSection.getString(REWARDS_NAME);

			if (dayConfigurationSection.contains(REWARDS_DESCRIPTION, true)) {
				description = dayConfigurationSection.getString(REWARDS_DESCRIPTION);
			}
		}

		String enchantmentString = "";
		int enchantmentLevel = 0;
		if (dayConfigurationSection.contains(REWARDS_ENCHANTMENT_TYPE, true)) {
			enchantmentString = dayConfigurationSection.getString(REWARDS_ENCHANTMENT_TYPE);
		}
		if (dayConfigurationSection.contains(REWARDS_ENCHANTMENT_LEVEL, true)) {
			enchantmentLevel = dayConfigurationSection.getInt(REWARDS_ENCHANTMENT_LEVEL, 0);
		}

		Material material = Material.getMaterial(materialString);
		if (material == null) {
			throw new AdventskalenderException(
					String.format(getMessage(Message.WRONG_MATERIAL), myDay, materialString));
		}

		Enchantment enchantmentType = null;
		if (!enchantmentString.isEmpty()) {
			enchantmentType = findEnchantment(enchantmentString);
			if (enchantmentType == null) {
				String s = String.format("Valid enchantments are: %s", Strings.join(getEnchantmentNames(), ", "));
				plugin.getLogger().info(s);
				throw new AdventskalenderException(
						String.format(getMessage(Message.WRONG_ENCHANTMENT_TYPE), myDay, materialString));
			}
			if (!enchantmentType.canEnchantItem(new ItemStack(material))) {
				throw new AdventskalenderException(
						String.format(getMessage(Message.INCOMPATIBLE_ENCHANTMENT), myDay, materialString));
			}
			if (enchantmentLevel == 0) {
				enchantmentLevel = enchantmentType.getMaxLevel();
			}
			if (enchantmentLevel > enchantmentType.getMaxLevel()) {
				throw new AdventskalenderException(
						String.format(getMessage(Message.WRONG_ENCHANTMENT_LEVEL), myDay, materialString));
			}
		}

		return new Reward(material, count, name, description, enchantmentType, enchantmentLevel);
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
			rewards.add(loadReward(dayConfigurationSection));
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

	public Set<String> getEnchantmentNames() {
		Set<String> enchantmentNames = new TreeSet<>();
		for (Enchantment myEnchantment : Enchantment.values()) {
			enchantmentNames.add(myEnchantment.getKey().getKey());
		}
		return enchantmentNames;
	}

}
