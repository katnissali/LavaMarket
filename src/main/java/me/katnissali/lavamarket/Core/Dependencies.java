package me.katnissali.lavamarket.Core;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Dependencies {

    private static Economy economy;

    public static boolean setup() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            Util.print(Util.getPrefix() + "Please install Vault before enabling this plugin.");
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = Util.getMain().getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            Util.print(Util.getPrefix() + ChatColor.RED + "Error hooking into vault. (ERROR 1)");
            Util.print(Util.getPrefix() + ChatColor.RED + "Try installing an economy plugin such as Essentials. Vault only manages an economy, it does not create one.");
            Util.print(Util.getPrefix() + ChatColor.RED + "Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(Util.getMain());
            return false;
        }
        economy = rsp.getProvider();
        if(economy == null){
            Util.print(Util.getPrefix() + "Error hooking into vault. (ERROR 2)");
            return false;
        } else {
            Util.print(Util.getPrefix() + "Successfully hooked into vault.");
            return true;
        }
    }




    public static Economy getEconomy(){
        return economy;
    }

}
