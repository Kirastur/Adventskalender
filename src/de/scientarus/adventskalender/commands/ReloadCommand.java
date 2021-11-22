package de.scientarus.adventskalender.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.scientarus.adventskalender.api.AdventskalenderAPI;
import de.scientarus.adventskalender.exception.AdventskalenderException;
import de.scientarus.adventskalender.main.Main;
import de.scientarus.adventskalender.messages.Message;

public class ReloadCommand implements CommandExecutor {

	protected final Main main;
	protected final AdventskalenderAPI api;

	public ReloadCommand(Main main, AdventskalenderAPI api) {
		this.main = main;
		this.api = api;
		main.getCommand("adventskalenderreload").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			api.reload();
			sender.sendMessage(api.getMessage(Message.RELOAD_OK));
		} catch (AdventskalenderException ae) {
			sender.sendMessage(ae.getMessage());
		} catch (Exception e) {
			sender.sendMessage(e.getMessage());
			e.printStackTrace();
		}
		return true;
	}

}
