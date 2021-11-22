package de.scientarus.adventskalender.main;

import org.bukkit.plugin.java.JavaPlugin;

import de.scientarus.adventskalender.api.AdventskalenderAPI;
import de.scientarus.adventskalender.bstats.Metrics;
import de.scientarus.adventskalender.commands.AdventskalenderCommand;
import de.scientarus.adventskalender.commands.ReloadCommand;
import de.scientarus.adventskalender.exception.AdventskalenderException;

public final class Main extends JavaPlugin {

	@Override
	public void onEnable() {
		try {
			this.saveDefaultConfig();
			new Metrics(this, Metrics.PLUGINID_ADVENTSKALENDER);
			AdventskalenderAPI api = new AdventskalenderAPI(this);
			AdventskalenderProvider.setAPI(api);
			new AdventskalenderCommand(this, api);
			new ReloadCommand(this, api);
			api.reload();
			getLogger().info("Der Adventskalender ist gestartet");
		} catch (AdventskalenderException ae) {
			this.getLogger().warning(ae.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
