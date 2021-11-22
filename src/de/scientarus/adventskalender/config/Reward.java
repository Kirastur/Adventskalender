package de.scientarus.adventskalender.config;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Reward {

	private final Material material;
	private final int amount;
	private final String name;

	public Reward(Material material, int amount, String name) {
		this.material = material;
		this.amount = amount;
		this.name = name;
	}

	public Material getMaterial() {
		return material;
	}

	public int getAmount() {
		return amount;
	}

	public String getName() {
		return name;
	}

	public ItemStack buildItemStack() {
		if ((material == null) || (amount == 0)) {
			return null;
		}
		ItemStack itemStack = new ItemStack(material, amount);
		if ((name != null) && !name.isEmpty()) {
			ItemMeta itemMeta = itemStack.getItemMeta();
			itemMeta.setDisplayName(name);
			itemStack.setItemMeta(itemMeta);
		}
		return itemStack;
	}
}
