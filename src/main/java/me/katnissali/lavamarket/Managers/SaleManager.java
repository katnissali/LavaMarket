package me.katnissali.lavamarket.Managers;

import me.katnissali.lavamarket.Core.Dependencies;
import me.katnissali.lavamarket.Core.Sale;
import me.katnissali.lavamarket.Core.Util;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SaleManager {

    private HashMap<String, ArrayList<Sale>> sales = new HashMap<>();

    public SaleManager(){
    }

    //  GETTERS
    public HashMap<String, ArrayList<Sale>> getSales(){ return sales; }
    public ArrayList<Sale> getSales(String player){
        return sales.get(player);
    }
    public Sale getSale(String player, int ID){
        return sales.get(player).get(ID);
    }
    private int howManySlots(Player player){
        List<String> list = Util.getConfig().getStringList("ranks");

        for(String str : list){

            String perm = str.split(", ")[0];
            if(player.hasPermission(perm)) {
                String value = str.split(", ")[1];
                try {
                    return Integer.valueOf(value);
                } catch (NumberFormatException e) {
                    Util.print(Util.getPrefix() + "Error loading rank permission: " + perm + "." + value + " is not a valid number!.");
                }
            }
        }

        String lastPerm = list.get(list.size()-1);
        return Integer.valueOf(lastPerm.split(", ")[1]);
    }
    public int slotsLeft(Player player){
        if(sales.get(player.getName()) == null){
            return howManySlots(player);
        }
        return howManySlots(player) - sales.get(player.getName()).size();
    }

    //  SETTERS
    public Sale removeSale(String player, int ID){
        Sale sale = sales.get(player).get(ID);
        sales.get(player).remove(ID);
        if(sales.get(player).size() == 0){
            sales.remove(player);
        }
        return sale;
    }
    public void buySale(Player buyer, String seller, int ID){
        Sale sale = sales.get(seller).get(ID);
        buyer.getInventory().addItem(sale.getItem());
        Dependencies.getEconomy().withdrawPlayer(buyer, sale.getPrice());
        Dependencies.getEconomy().depositPlayer(seller, sale.getPrice());
        removeSale(seller, ID);
    }
    public void newSale(String player, int price, ItemStack item){
        ArrayList<Sale> list = sales.get(player);
        if(list == null){
            list = new ArrayList<>();
        }
        Sale sale = new Sale(player, price, item);
        list.add(sale);
        sales.remove(player);
        sales.put(player, list);
        Util.getConfigManager().saveSales();
    }
    public void newSale(Sale sale){
        ArrayList<Sale> list = sales.get(sale.getPlayer());
        if(list == null){
            list = new ArrayList<>();
        }
        list.add(sale);
        sales.remove(sale.getPlayer());
        sales.put(sale.getPlayer(), list);
        Util.getConfigManager().saveSales();
    }
}
