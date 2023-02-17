package me.elixirrpg.net.dungeons.extras;

import me.elixirrpg.net.dungeons.Dungeons;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Skull;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.List;

public class Utils {
    private final Dungeons plugin;

    public Utils(Dungeons plugin) {
        this.plugin = plugin;
    }

    public boolean isAtEndOfDungeon(Location location) {
        // Check if the location is at the end of the dungeon
        // This method could be implemented differently depending on the specific dungeon layout
        return location.getBlockX() >= 50 && location.getBlockX() <= 55
                && location.getBlockY() >= 0 && location.getBlockY() <= 5
                && location.getBlockZ() >= 50 && location.getBlockZ() <= 55;
    }

    public void removeSpawnPoint(Location location) {
        // Remove a spawn point from the list of available spawn points
        plugin.spawnPoints.remove(location);

        // Remove the spawn point from all categories
        for (List<Location> categorySpawnPoints : plugin.categorySpawnPointMap.values()) {
            categorySpawnPoints.remove(location);
        }
    }
    public String formatTime(int minutes) {
        int seconds = minutes * 60;
        int minutesRemaining = seconds / 60;
        int secondsRemaining = seconds % 60;
        return String.format("%02d:%02d", minutesRemaining, secondsRemaining);
    }


    public static String translate(String msg){
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
