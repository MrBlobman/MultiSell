package me.MrBlobman.MultiSell;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import me.MrBlobman.MultiSell.Boosters.Booster;
import me.MrBlobman.MultiSell.Boosters.BoosterCommandExecutor;
import me.MrBlobman.MultiSell.Boosters.LongBooster;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class MultiSell extends JavaPlugin{
	
	Logger logger = Logger.getLogger("Minecraft");
	public static Economy econ = null;
	public static MultiSell plugin;
	public static double multiplier = 1.0;
	public static Collection<Booster> boosters = new ArrayList<Booster>();
	
	public void onDisable(){
		PluginDescriptionFile pdfFile = getDescription();
		this.logger.info(pdfFile.getName() + " has been disabled!");
		for (Booster booster : boosters){
			if (booster instanceof LongBooster){
				LongBooster.write((LongBooster) booster);
			}
		}
	}
	
	public void onEnable(){
		MultiSell.plugin = this;
		this.saveDefaultConfig();
		if (!setupEconomy() ) {
            logger.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }registerCmds();
        logger.info("[MultiSell] resuming all boosters...");
        if (this.getConfig().contains("ActiveBoosters")){
			for (String key : this.getConfig().getConfigurationSection("ActiveBoosters").getKeys(false)){
				boosters.add(LongBooster.read(key));
			}
			logger.info("[MultiSell] Boosters resumed.");
        }else{
        	logger.warning("[MultiSell] No booster configuration section available. This may be a problem, if not ignore this message.");
        }
		PluginDescriptionFile pdfFile = getDescription();
		this.logger.info(pdfFile.getName() + " Version  " + pdfFile.getVersion() + " has been enabled!");
	}
	
	private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
	private void registerCmds(){
		getCommand("sell").setExecutor(new MultiSellCommandExecutor(this));
		getCommand("createshop").setExecutor(new MultiSellCommandExecutor(this));
		getCommand("editshop").setExecutor(new MultiSellCommandExecutor(this));
		getCommand("shopinfo").setExecutor(new MultiSellCommandExecutor(this));
		getCommand("addBooster").setExecutor(new BoosterCommandExecutor());
		getCommand("removeBooster").setExecutor(new BoosterCommandExecutor());
		getCommand("getBoosters").setExecutor(new BoosterCommandExecutor());
	}
}
