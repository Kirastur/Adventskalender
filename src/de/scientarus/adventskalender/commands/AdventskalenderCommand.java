package de.scientarus.adventskalender.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.scientarus.adventskalender.api.AdventskalenderAPI;
import de.scientarus.adventskalender.main.Main;
import de.scientarus.adventskalender.messages.Message;

public class AdventskalenderCommand implements CommandExecutor {

	protected final Main main;
	protected final AdventskalenderAPI api;

	public AdventskalenderCommand(Main main, AdventskalenderAPI api) {
		this.main = main;
		this.api = api;
		main.getCommand("adventskalender").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player player) {
			api.openGui(player);
		} else {
			sender.sendMessage(api.getMessage(Message.MUST_BE_PLAYER));
		}
		return true;
	}

}
