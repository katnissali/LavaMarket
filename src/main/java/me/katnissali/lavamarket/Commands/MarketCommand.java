package me.katnissali.lavamarket.Commands;

import me.katnissali.lavamarket.Core.Dependencies;
import me.katnissali.lavamarket.Core.Sale;
import me.katnissali.lavamarket.Core.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class MarketCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){

        //  CHECK CONSOLE
        if(!(sender instanceof Player)){
            Util.onlyPlayers();
            return false;
        }

        //  GET PRICES
        int maxPrice = Util.getConfig().getInt("prices.max");
        int minPrice = Util.getConfig().getInt("prices.min");

        Player player = (Player) sender;

        //  CHECK ARGS
        if(args.length == 1){
            if(args[0].equalsIgnoreCase("help")) {
                if (player.hasPermission("LavaMarket.help")) {
                    sendHelpMessage(player);
                } else {
                    Util.noPermission(player);
                }
            } else if(args[0].equalsIgnoreCase("reload")) {
                if(player.hasPermission("LavaMarket.admin.reload")){
                    player.sendMessage(Util.getPrefix() + "Reloading market...");
                    Util.getMain().reload();
                    player.sendMessage(Util.getPrefix() + "Market reloaded!");
                } else {
                    Util.noPermission(player);
                }
            } else if(args[0].equalsIgnoreCase("clear")){
                if(player.hasPermission("LavaMarket.admin.clear")){
                    player.sendMessage(Util.getPrefix() + "Clearing market...");
                    Util.clearMarket();
                    player.sendMessage(Util.getPrefix() + "Market cleared!");
                } else {
                    Util.noPermission(player);
                }
            } else {
                wrongUsage(player);
            }
        }
        else if(args.length == 2) {
            if (args[0].equalsIgnoreCase("sell")) {
                //  SELL COMMAND

                //  CHECK PERMS
                if(!player.hasPermission("LavaMarket.sell")){
                    Util.noPermission(player);
                    return false;
                }
                //  CHECK PRICE
                int price = invalidNumber(args[1]);
                if(price == -1 || price > maxPrice || price < minPrice){
                    player.sendMessage(ChatColor.RED + "Invalid price! Price must be between " + minPrice + " and " + maxPrice + ".");
                    return false;
                }

                //  CHECK NULL ITEM
                ItemStack item = player.getItemInHand();
                if(item == null || item.getType().equals(Material.AIR)){
                    player.sendMessage(ChatColor.RED + "You cannot sell air.");
                    return false;
                }

                //  CHECK SLOTS
                if(Util.getSaleManager().slotsLeft(player) <= 0){
                    player.sendMessage(ChatColor.RED + "You have run out of slots! Upgrade your rank or remove a sale to sell this item.");
                    return false;
                }

                Util.getSaleManager().newSale(player.getName(), price, player.getItemInHand());
                player.sendMessage(Util.getPrefix() + "Sold item for $" + price + ".");
                player.getInventory().remove(player.getItemInHand());

            }
            else if (args[0].equalsIgnoreCase("list")) {
                //  LIST COMMAND

                //  CHECK PERMS
                if(!player.hasPermission("LavaMarket.list")){
                    Util.noPermission(player);
                    return false;
                }

                //  CHECK PLAYER
                String target = args[1];
                if(Util.getSaleManager().getSales(target) == null) {
                    invalidTarget(player);
                    return false;
                }

                //  SEND LIST
                player.sendMessage("" + ChatColor.GRAY + ChatColor.UNDERLINE + "________________" + ChatColor.RESET + " " + Util.getPrefix() + " " + ChatColor.GRAY + ChatColor.UNDERLINE  + "________________");
                player.sendMessage("");

                ArrayList<Sale> sales = Util.getSaleManager().getSales(target);
                for(int i = 0; i < sales.size(); i++){

                   // String type = sales.get(i).getItem().getType().toString().replace("_", " ");
                   // String displayName = sales.get(i).getItem().getItemMeta().getDisplayName();

                    String msg1 = ChatColor.GOLD + "  " + (i+1) + ": " + ChatColor.GRAY + sales.get(i).getItem().getType().toString().replace("_", " ").toLowerCase();
                    String msg2 = ChatColor.GOLD + "     Price: " + ChatColor.GREEN + "$" + sales.get(i).getPrice();

                    player.sendMessage(msg1);
                    player.sendMessage(msg2);
                    player.sendMessage("");
                }

                player.sendMessage("" + ChatColor.GRAY + ChatColor.UNDERLINE + "______________" + ChatColor.GRAY + ChatColor.BOLD + " Player: " + target + " " + ChatColor.GRAY + ChatColor.UNDERLINE  + "______________");

            } else {
                wrongUsage(player);
            }
        }
        else if(args.length == 3){

            if(args[0].equalsIgnoreCase("buy")){
                //  BUY COMMAND

                //  CHECK PERMS
                if(!player.hasPermission("LavaMarket.buy")){
                    Util.noPermission(player);
                    return false;
                }

                //  CHECK TARGET
                String target = args[1];
                if(!Util.getSaleManager().getSales().containsKey(target)){
                    invalidTarget(player);
                    return false;
                }

                //  CHECK ID
                int ID = invalidNumber(args[2]);
                if(ID < 1 || ID > Util.getSaleManager().getSales(target).size()){
                    player.sendMessage(ChatColor.RED + "Invalid ID! ID must be between 1 and " + Util.getSaleManager().getSales(target).size() + ".");
                    return false;
                }

                //  CHECK BALANCE
                if(Dependencies.getEconomy().getBalance(player) < Util.getSaleManager().getSale(target, ID-1).getPrice()){
                    player.sendMessage(ChatColor.RED + "You cannot afford this item!");
                    return false;
                }

                //  BUY ITEM
                ItemStack item = Util.getSaleManager().getSale(target, ID-1).getItem();

                if(player.getName().equals(target)){
                    player.sendMessage(Util.getPrefix() + "You removed your item.");
                } else {
                    if(Bukkit.getPlayer(target) != null){
                        Bukkit.getPlayer(target).sendMessage(Util.getPrefix() + player.getName() + " bought " + item.getAmount() + " " + item.getType().toString().toLowerCase().replace("_", " ") + " from you for $" + Util.getSaleManager().getSale(target, ID-1).getPrice() + ".");
                    }
                    player.sendMessage(Util.getPrefix() + "You bought an item from " + target + " for $" + Util.getSaleManager().getSale(target, ID - 1).getPrice() + ".");
                }
                Util.getSaleManager().buySale(player, target, ID-1);

            } else {
                wrongUsage(player);
            }
        }
        else {
            wrongUsage(player);
        }
        return false;

    }

    private int invalidNumber(String str){
        try{
            return Integer.valueOf(str);
        } catch (NumberFormatException e){
            return -1;
        }
    }
    private void invalidTarget(Player player){
        player.sendMessage(ChatColor.RED + "Invalid playername!");
    }
    private void wrongUsage(Player player){
        player.sendMessage(ChatColor.RED + "Invalid command. Use /market help for more info.");
    }

    private void sendHelpMessage(CommandSender sender){
        if(sender instanceof Player){
            Player player = (Player) sender;

            player.sendMessage("");
            player.sendMessage("" + ChatColor.GRAY + ChatColor.UNDERLINE + "________________" + ChatColor.RESET + " " + Util.getPrefix() + " " + ChatColor.UNDERLINE  + "________________");

            player.sendMessage(ChatColor.GREEN + "LavaMarket commands and permissions:");
            sendCommandMsg(player, "market help", "This menu.", "LavaMarket.help");
            sendCommandMsg(player, "market reload", "Reloads config.", "LavaMarket.admin.reload");
            sendCommandMsg(player, "market clear", "DELETE ALL SALES (irreversible!)", "LavaMarket.admin.clear");
            sendCommandMsg(player, "market sell", "Create a sale.", "LavaMarket.sell");
            sendCommandMsg(player, "market buy", "Buy a sale.", "LavaMarket.buy");
            sendCommandMsg(player, "market list", "Get a list of sales.", "LavaMarket.list");

            player.sendMessage("" + ChatColor.GRAY + ChatColor.UNDERLINE + "__________________________________________");

        } else {
            Util.print("");
            Util.print("" + ChatColor.GRAY + ChatColor.UNDERLINE + "________________" + ChatColor.RESET + " " + Util.getPrefix() + " " + ChatColor.UNDERLINE  + "________________");
            Util.print("");

            Util.print(ChatColor.GREEN + "LavaMarket commands and permissions:");
            sendCommandMsg("market help", "This menu.", "LavaMarket.help");
            sendCommandMsg("market reload", "Reloads config.", "LavaMarket.admin.reload");
            sendCommandMsg("market clear", "DELETE ALL SALES (irreversible!)", "LavaMarket.admin.clear");
            sendCommandMsg("market sell", "Create a sale.", "LavaMarket.sell");
            sendCommandMsg("market buy", "Buy a sale.", "LavaMarket.buy");
            sendCommandMsg("market list", "Get a list of sales.", "LavaMarket.list");

            Util.print("" + ChatColor.GRAY + ChatColor.UNDERLINE + "__________________________________________");

        }
    }

    private static void sendCommandMsg(Player player, String command, String description, String permission) {
        player.sendMessage("" + ChatColor.GOLD + "     - " + ChatColor.GRAY + "/" + command);
        if(!description.equalsIgnoreCase("")) {
            player.sendMessage("" + ChatColor.GRAY + "       " + description);
        }
        player.sendMessage("" + ChatColor.GRAY + "       Permission: " + permission);
    }
    private static void sendCommandMsg(String command, String description, String permission) {
        Util.print("" + ChatColor.DARK_GRAY + "     - " + ChatColor.GRAY + "/" + command);
        if(!description.equalsIgnoreCase("")) {
            Util.print("" + ChatColor.GRAY + "       " + description);
        }
        Util.print("" + ChatColor.GRAY + "       Permission: " + permission);
    }


}
