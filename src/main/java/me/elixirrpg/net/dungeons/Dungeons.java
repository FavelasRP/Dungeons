package me.elixirrpg.net.dungeons;

import me.elixirrpg.net.dungeons.commands.DungeonCommand;
import me.elixirrpg.net.dungeons.configs.CBeacons;
import me.elixirrpg.net.dungeons.events.whenPlayersKillsInDungeon;
import me.elixirrpg.net.dungeons.extras.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Dungeons extends JavaPlugin implements Listener {


    public final int dungeonDurationInMinutes = 2;
    public int dungeonTicksRemaining = 0;

    public boolean dungeonRunning;
    public World dungeonWorld;
    public String currentDungeonCategory;
    public Map<String, List<Location>> categorySpawnPointMap;
    // Variables to keep track of the dungeon run
    public List<Player> participants;

    public boolean dungeonActive;
    public int timeRemaining;
    public int taskID;
    public Location spawnLocation;
    public List<Location> spawnPoints;
    public final boolean isDungeonRunning = false;
    // Create a Random object
    public final Random random = new Random();

    Utils utils = new Utils(this);

    @Override
    public void onEnable() {
        // Initialize variables
        participants = new ArrayList<>();
        dungeonActive = false;
        timeRemaining = 0;
        taskID = -1;
        spawnLocation = getServer().getWorlds().get(0).getSpawnLocation();
        spawnPoints = new ArrayList<>();
        categorySpawnPointMap = new HashMap<>();
        dungeonWorld = Bukkit.getWorld("world");
        loadSpawnPoints();
        getCommand("dungeon").setExecutor(new DungeonCommand(this));

        // Register events
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);
        pm.registerEvents(new whenPlayersKillsInDungeon(), this);




        //CARICA IL CONFIG DI PRISON CORE
        try {
            CBeacons.loadConfig();
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        // Cancel the dungeon task if it is running
        if (taskID != -1) {
            getServer().getScheduler().cancelTask(taskID);
        }
        saveSpawnPoints();
    }

    public void loadSpawnPoints() {
        FileConfiguration config = getConfig();
        ConfigurationSection spawnPointsSection = config.getConfigurationSection("spawnPoints");
        if (spawnPointsSection != null) {
            for (String category : spawnPointsSection.getKeys(false)) {
                ConfigurationSection categorySection = spawnPointsSection.getConfigurationSection(category);
                if (categorySection != null) {
                    List<Location> spawnPoints = new ArrayList<>();
                    for (String spawnPointKey : categorySection.getKeys(false)) {
                        ConfigurationSection spawnPointSection = categorySection.getConfigurationSection(spawnPointKey);
                        if (spawnPointSection != null) {
                            World world = Bukkit.getWorld(spawnPointSection.getString("world"));
                            double x = spawnPointSection.getDouble("x");
                            double y = spawnPointSection.getDouble("y");
                            double z = spawnPointSection.getDouble("z");
                            float yaw = (float) spawnPointSection.getDouble("yaw");
                            float pitch = (float) spawnPointSection.getDouble("pitch");
                            Location location = new Location(world, x, y, z, yaw, pitch);
                            spawnPoints.add(location);
                        }
                    }
                    categorySpawnPointMap.put(category, spawnPoints);
                }
            }
        }
    }

    public void addSpawnPoint(String category, Location location) {
        if (!categorySpawnPointMap.containsKey(category)) {
            categorySpawnPointMap.put(category, new ArrayList<>());
        }
        List<Location> spawnPoints = categorySpawnPointMap.get(category);
        spawnPoints.add(location);

        // Save the new spawnpoint to file
        saveSpawnPoints();
    }

    public void saveSpawnPoints() {
        FileConfiguration config = getConfig();
        ConfigurationSection spawnPointsSection = config.createSection("spawnPoints");
        for (Map.Entry<String, List<Location>> entry : categorySpawnPointMap.entrySet()) {
            String category = entry.getKey();
            List<Location> spawnPoints = entry.getValue();
            ConfigurationSection categorySection = spawnPointsSection.createSection(category);
            for (int i = 0; i < spawnPoints.size(); i++) {
                Location spawnPoint = spawnPoints.get(i);
                ConfigurationSection spawnPointSection = categorySection.createSection("spawnPoint" + (i + 1));
                spawnPointSection.set("world", spawnPoint.getWorld().getName());
                spawnPointSection.set("x", spawnPoint.getX());
                spawnPointSection.set("y", spawnPoint.getY());
                spawnPointSection.set("z", spawnPoint.getZ());
                spawnPointSection.set("yaw", spawnPoint.getYaw());
                spawnPointSection.set("pitch", spawnPoint.getPitch());
            }
        }
        saveConfig();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Teleport players who joined during a dungeon run to the spawn
        Player player = event.getPlayer();
        if (dungeonActive) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lDUNGEONS &fYou are loosing a &cDungeon &fplease join now"));

        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Remove players who quit during a dungeon run from the list of participants
        Player player = event.getPlayer();
        if (dungeonActive) {
            participants.remove(player);
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        // Teleport players who try to leave the dungeon area during a dungeon run back to the spawn
        Player player = event.getPlayer();
        if (dungeonActive && participants.contains(player)) {
            Location to = event.getTo();
            if (!isInDungeonArea(to)) {
                player.teleport(spawnLocation);
            }
        }
    }

    public boolean isDungeonRunning() {
        return dungeonRunning;
    }

    public String getRandomCategory() {
        if (categorySpawnPointMap.isEmpty()) {
            return null;
        }
        List<String> categoryList = new ArrayList<>(categorySpawnPointMap.keySet());
        return categoryList.get(random.nextInt(categoryList.size()));
    }
    private String formatDungeonCountdown(int dungeonTicksRemaining) {
        int totalSecondsRemaining = dungeonTicksRemaining / 20; // 20 ticks per second
        int minutes = totalSecondsRemaining / 60;
        int seconds = totalSecondsRemaining % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }


    public void startDungeonRun() {
        // Set dungeonRunning flag to true
        dungeonRunning = true;

        // Choose a random category for the dungeon run
        currentDungeonCategory = getRandomCategory();

        // Send message to all players on the server

        getServer().broadcastMessage("A new dungeon run has started! Category: " + currentDungeonCategory);

        // Teleport all players who typed /dungeon to a random spawn point in the chosen category
        for (Player player : getServer().getOnlinePlayers()) {
            if (player.hasPermission("hourlydungeonrun.join")) {
                player.sendMessage("Message for players who can join. Category: " + currentDungeonCategory);

            }
        }
        // Start the dungeon countdown
        dungeonTicksRemaining = dungeonDurationInMinutes * 1200;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (dungeonTicksRemaining <= 0) {
                    cancel();
                    return;
                }

                // Calculate the remaining time in minutes
                int minutesRemaining = dungeonTicksRemaining;

                Bukkit.broadcastMessage("The dungeon will end in " + formatDungeonCountdown(dungeonTicksRemaining) + " ");

                // Decrement the tick counter
                dungeonTicksRemaining--;
            }
            // I set 60 seconds just for test
        }.runTaskTimer(this, 0, 20 * 60);

        // Schedule the end of the dungeon run after 30 minutes
        getServer().getScheduler().scheduleSyncDelayedTask(this, () -> endDungeonRun(), 20 * 60 * dungeonDurationInMinutes);
    }

    public void endDungeonRun() {
        // Set dungeonRunning flag to false
        dungeonRunning = false;

        // Send message to all players on the server
        getServer().broadcastMessage("The dungeon run has ended!");

        // Teleport all players back to spawn
        for (Player player : getServer().getOnlinePlayers()) {
            if (player.hasPermission("hourlydungeonrun.join")) {
                player.teleport(player.getWorld().getSpawnLocation());
            }
        }
    }




    public boolean isInDungeonArea(Location location) {
        // Check if the location is within the boundaries of the dungeon area
        // This method could be implemented differently depending on the specific dungeon layout
        return location.getBlockX() >= 0 && location.getBlockX() <= 50
                && location.getBlockY() >= 0 && location.getBlockY() <= 50
                && location.getBlockZ() >= 0 && location.getBlockZ() <= 50;
    }

    public boolean isAtEndOfDungeon(Location location) {
        // Check if the location is at the end of the dungeon
        // This method could be implemented differently depending on the specific dungeon layout
        return location.getBlockX() >= 50 && location.getBlockX() <= 55
                && location.getBlockY() >= 0 && location.getBlockY() <= 5
                && location.getBlockZ() >= 50 && location.getBlockZ() <= 55;
    }

    public Location getRandomSpawnPoint() {
        // Return a random spawn point from the list of available spawn points
        int index = (int) (Math.random() * spawnPoints.size());
        return spawnPoints.get(index);
    }


    public void removeSpawnPoint(Location location) {
        // Remove a spawn point from the list of available spawn points
        spawnPoints.remove(location);

        // Remove the spawn point from all categories
        for (List<Location> categorySpawnPoints : categorySpawnPointMap.values()) {
            categorySpawnPoints.remove(location);
        }
    }

    public Location getRandomSpawnPointInCategory(String category) {
        // Return a random spawn point from the list of available spawn points in the specified category
        List<Location> categorySpawnPoints = categorySpawnPointMap.get(category);
        if (categorySpawnPoints == null || categorySpawnPoints.isEmpty()) {
            // If there are no spawn points in the specified category, return a random spawn point from the general list
            return getRandomSpawnPoint();
        } else {
            int index = (int) (Math.random() * categorySpawnPoints.size());
            return categorySpawnPoints.get(index);
        }
    }


    public boolean isInDungeon(Player player) {
        return participants.contains(player);
    }

    public void quitDungeon(Player player) {
        if (dungeonActive) {
            participants.remove(player);
        }
    }
}

