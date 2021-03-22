package me.katnissali.lavamarket.Core;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class Sale {

    private final String player;
    private final int price;
    private final ItemStack item;

    public Sale(String player, int price, ItemStack item){
        if(item == null){
            this.item = Bukkit.getPlayer(player).getItemInHand().clone();
        } else {
            this.item = item.clone();
        }
        this.player = player;
        this.price = price;
    }

    //  GETTERS
    public String toString(){
        String displayName = item.getItemMeta().getDisplayName();
        String itemMsg = (displayName == null || displayName.equals("") || displayName.toUpperCase().equals(displayName) ? item.getType().toString() : displayName);
        return ("[PLAYER:" + player + ", PRICE:" + price + ", ITEM:" + itemMsg);
    }
    public String getPlayer(){ return player; }
    public int getPrice(){ return price; }
    public ItemStack getItem(){ return item; }

}
