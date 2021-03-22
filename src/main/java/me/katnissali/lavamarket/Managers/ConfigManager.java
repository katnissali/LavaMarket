package me.katnissali.lavamarket.Managers;

import me.katnissali.lavamarket.Core.Sale;
import me.katnissali.lavamarket.Core.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ConfigManager {



    private YamlConfiguration salesYaml;
    private File salesFile;

    public ConfigManager(){
        loadSalesConfig();
    }

    private File loadSalesConfig() {
        String fileName = "sales.yml";
        Util.print(Util.getPrefix() + "Loading file: " + fileName);

        File file = new File(Bukkit.getServer().getPluginManager().getPlugin("LavaMarket").getDataFolder(), fileName);
        salesFile = file;

        boolean newFile = false;

        if(!file.exists()){
            try {
                file.createNewFile();
                newFile = true;
            } catch (IOException e) {
                Util.print(Util.getPrefix() + ChatColor.RED + "Error loading " + fileName + "!");
                e.printStackTrace();
            }
        }
        YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(file);
        if (newFile) {
            yamlConfig.createSection("sales");
            Util.print(Util.getPrefix() + "Loading default " + fileName +  "config.");
        }
        try {
            yamlConfig.save(file);
        } catch (IOException e){
            Util.print(Util.getPrefix() + ChatColor.RED + "Error saving " + fileName + "!");
            e.printStackTrace();
            return null;
        }

        Util.print(Util.getPrefix() + "File loaded.");
        salesYaml = yamlConfig;
        return file;
    }
    public void saveFiles(){
        try {
            salesYaml.save(salesFile);
        } catch(IOException e){
            Util.print(Util.getPrefix() + "Error! Cannot save sales.yml!");
            e.printStackTrace();
        }
    }


    public void saveSales(){
        getSalesYaml().set("sales", null);
        getSalesYaml().createSection("sales");
        for(String name : Util.getSaleManager().getSales().keySet()){
            for(int i = 0; i < Util.getSaleManager().getSales(name).size(); i++){
                String path = "sales." + name + ".";
                path = path + (i+1) + ".";
                getSalesYaml().set((path + "item"), Util.getSaleManager().getSale(name, i).getItem());
                getSalesYaml().set((path + "price"), Util.getSaleManager().getSale(name, i).getPrice());
            }
        }
        saveFiles();
    }
    public void loadSales(){
        ArrayList<Sale> queue = new ArrayList<>();

        for(String name : getSalesYaml().getConfigurationSection("sales").getKeys(false)){
            for(String ID : getSalesYaml().getConfigurationSection("sales." + name).getKeys(false)){
                String path = "sales." + name + "." + ID + ".";

                ItemStack item = getSalesYaml().getItemStack(path + "item");
                int price = getSalesYaml().getInt(path + "price");

                queue.add(new Sale(name, price, item));
            }
        }

        for(Sale sale : queue){
            Util.getSaleManager().newSale(sale);
        }

    }


    public YamlConfiguration getSalesYaml(){ return salesYaml; }



}