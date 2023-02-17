package me.elixirrpg.net.dungeons.commands;

import me.elixirrpg.net.dungeons.Dungeons;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DungeonCommand implements CommandExecutor {

    private final Dungeons plugin;

    public DungeonCommand(Dungeons plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if(plugin.dungeonRunning && sender instanceof  Player){
                Player player = (Player) sender;
                if(plugin.participants.contains(player)) {
                    sender.sendMessage(ChatColor.RED + "Alex is the best, You are allready in a dungeon!");
                } else {
                    plugin.participants.add((Player) sender);
                    player.teleport(plugin.getRandomSpawnPointInCategory(plugin.currentDungeonCategory));
                    sender.sendMessage(ChatColor.RED + "You got teleported");
                }
            } else {
                if(sender instanceof  Player){
                    Player player = (Player) sender;
                    player.sendMessage("This is the current state:" + plugin.dungeonRunning + " with " + plugin.currentDungeonCategory);
                }
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("start")) {
            if (plugin.dungeonRunning) {
                sender.sendMessage(ChatColor.RED + "Alex is the best, dungeon is running!");
                return true;
            }
            plugin.startDungeonRun();
            return true;
        }

        if (args[0].equalsIgnoreCase("addspawnpoint")) {
            if (args.length != 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /dungeon addspawnpoint <category>");
                return true;
            }
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can add spawn points.");
                return true;
            }
            String category = args[1].toLowerCase();
            Player player = (Player) sender;
            Location location = player.getLocation();
            plugin.addSpawnPoint(category, location);
            sender.sendMessage(ChatColor.GREEN + "Spawn point added for category " + category);
            return true;
        }

        if (args[0].equalsIgnoreCase("quit")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can quit the dungeon.");
                return true;
            }
            Player player = (Player) sender;
            if (!plugin.isInDungeon(player)) {
                sender.sendMessage(ChatColor.RED + "You are not in the dungeon.");
                return true;
            }
            plugin.quitDungeon(player);
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Usage: /dungeon <start|addspawnpoint|quit>");
        return true;
    }
}

