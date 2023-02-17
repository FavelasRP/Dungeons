// 
// Decompiled by Procyon v0.5.36
// 

package me.elixirrpg.net.dungeons.configs;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class CBeacons
{
    static File arenaFile;
    public static FileConfiguration arenaConfig;
    
    public static void create() throws IOException, InvalidConfigurationException {
        if (!CBeacons.arenaFile.exists()) {
            CBeacons.arenaFile.createNewFile();
            loadConfig();
            saveConfig();
        }
    }
    
    public static void saveConfig() {
        try {
            CBeacons.arenaConfig.save(CBeacons.arenaFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void loadConfig() throws InvalidConfigurationException {
        try {
            CBeacons.arenaConfig.load(CBeacons.arenaFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static FileConfiguration getConfig() {
        return CBeacons.arenaConfig;
    }
    
    static {
        CBeacons.arenaFile = new File("plugins/PrisonCore/Beacons.yml");
        CBeacons.arenaConfig = (FileConfiguration)YamlConfiguration.loadConfiguration(CBeacons.arenaFile);
    }
}
