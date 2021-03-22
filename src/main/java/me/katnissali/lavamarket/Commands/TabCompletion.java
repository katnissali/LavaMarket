package me.katnissali.lavamarket.Commands;

import me.katnissali.lavamarket.Core.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TabCompletion implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){

        ArrayList<String> list = new ArrayList<>();
        /*
          trade sell price
          trade buy  player ID
          trade list player
        */

        if(args.length == 1){

            if(sender.hasPermission("LavaMarket.help")){
                list.add("help");
            }
            if(sender.hasPermission("LavaMarket.sell")){
                list.add("sell");
            }
            if(sender.hasPermission("LavaMarket.list")){
                list.add("list");
            }
            if(sender.hasPermission("LavaMarket.buy")){
                list.add("buy");
            }
            if(sender.hasPermission("LavaMarket.admin.reload")){
                list.add("reload");
            }
            if(sender.hasPermission("LavaMarket.admin.clear")){
                list.add("clear");
            }
        } else if(args.length == 2){
            if(args[0].equalsIgnoreCase("buy")){
                if(sender.hasPermission("LavaMarket.buy")) {
                    list.addAll(Util.getSaleManager().getSales().keySet());
                }
            } else if(args[0].equalsIgnoreCase("list")) {
                if(sender.hasPermission("LavaMarket.list")) {
                    list.addAll(Util.getSaleManager().getSales().keySet());
                }
            }
        } else if(args.length == 3){
            if(args[1].equalsIgnoreCase("buy") && Util.getSaleManager().getSales(args[1]) != null) {
                int max = Util.getSaleManager().getSales(args[1]).size();
                for (int i = 1; i <= max; i++) {
                    list.add(String.valueOf(i));
                }
            }
        }

        return list.stream().filter(a -> a.toLowerCase().startsWith(args[args.length-1].toLowerCase())).collect(Collectors.toList());

    }

}
