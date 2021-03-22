package me.katnissali.lavamarket;

import me.katnissali.lavamarket.Commands.TabCompletion;
import me.katnissali.lavamarket.Commands.MarketCommand;
import me.katnissali.lavamarket.Core.Util;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class LavaMarket extends JavaPlugin {

    @Override
    public void onEnable() {
        loadConfig();
        Util.print(Util.format(getConfig().getString("messages.prefix")) + "Enabling LavaMarket...");
        Util.setup(this);
        if(Bukkit.getPluginManager().isPluginEnabled("LavaMarket")) {
            registerCommands();
            registerEvents();
            Util.print(Util.getPrefix() + "LavaMarket enabled!");
        }
    }
    @Override
    public void onDisable(){
        Util.print(Util.getPrefix() + "Saving sales to sales.yml...");
        Util.getConfigManager().saveSales();
        Util.print(Util.getPrefix() + "Sales saved!");
    }

    private void loadConfig(){
        this.getConfig().options().copyDefaults();
        saveDefaultConfig();
        reloadConfig();
    }
    public void reload(){
        reloadConfig();
        Util.setup(this);
    }
    private void registerCommands(){
        getCommand("market").setExecutor(new MarketCommand());
        getCommand("market").setTabCompleter(new TabCompletion());
    }
    private void registerEvents(){}

}
