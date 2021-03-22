package me.katnissali.lavamarket.Core;

import me.katnissali.lavamarket.LavaMarket;
import me.katnissali.lavamarket.Managers.ConfigManager;
import me.katnissali.lavamarket.Managers.SaleManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Util {

    //  SETUP
    private static LavaMarket main;
    private static SaleManager saleManager;
    private static ConfigManager configManager;

    public static void setup(LavaMarket lm){
        main = lm;
        Dependencies.setup();
        if(Bukkit.getPluginManager().isPluginEnabled("LavaMarket")) {
            configManager = new ConfigManager();
            saleManager = new SaleManager();

            Util.print(Util.getPrefix() + "Loading sales from sales.yml...");
            configManager.loadSales();
            Util.print(Util.getPrefix() + "Sales loaded!");
        }
    }

    //  GETTERS
    public static LavaMarket getMain(){ return main; }
    public static ConfigManager getConfigManager(){ return configManager; }
    public static SaleManager getSaleManager(){ return saleManager; }
    public static FileConfiguration getConfig(){ return main.getConfig(); }
    public static String getPrefix(){ return format(getConfig().getString("messages.prefix")); }
    public static String format(String str){ return ChatColor.translateAlternateColorCodes('&', str); }
    public static String getColoredConfigString(String path){ return format(getConfig().getString(path)); }

    //  SETTERS
    public static void clearMarket(){
        saleManager = new SaleManager();
        Util.getConfigManager().saveSales();
    }
    public static void noPermission(Player player){ player.sendMessage(ChatColor.RED + "You do not have permission to do this."); }
    public static void print(String str){
        Bukkit.getServer().getConsoleSender().sendMessage(str);
    }
    public static void onlyPlayers(){
        print(ChatColor.RED + "Only players can do this.");
    }


}
