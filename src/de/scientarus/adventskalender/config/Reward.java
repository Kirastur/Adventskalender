package de.scientarus.adventskalender.config;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public record Reward(Material material, int amount, String name, String description, Enchantment enchantmentType,
		int EnchantmentLevel) {

	public ItemStack buildItemStack() {
		if ((material == null) || (amount == 0)) {
			return null;
		}
		ItemStack itemStack = new ItemStack(material, amount);
		if ((name != null) && !name.isEmpty()) {
			ItemMeta itemMeta = itemStack.getItemMeta();
			itemMeta.setDisplayName(name);
			if ((description != null) && !description.isEmpty()) {
				List<String> lore = new ArrayList<>();
				lore.add(description);
				itemMeta.setLore(lore);
			}
			itemStack.setItemMeta(itemMeta);
		}
		if (enchantmentType != null) {
			itemStack.addEnchantment(enchantmentType, EnchantmentLevel);
		}
		return itemStack;
	}
}
