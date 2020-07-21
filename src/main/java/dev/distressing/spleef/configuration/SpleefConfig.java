package dev.distressing.spleef.configuration;

import dev.distressing.spleef.SpleefPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class SpleefConfig {
    static YamlConfiguration config;
    private static File CONFIG_FILE;

    public static void init(SpleefPlugin plugin) {
        CONFIG_FILE = new File(plugin.getDataFolder() + "/config.yml");
        config = new YamlConfiguration();

        try {
            config.load(CONFIG_FILE);
        } catch (IOException ex) {
        } catch (InvalidConfigurationException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to load config");
            ex.printStackTrace();
        }

        config.options().copyHeader(true);
        config.options().copyDefaults(true);
    }

    public static void save() {
        try {
            config.save(CONFIG_FILE);
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save " + CONFIG_FILE, ex);
        }
    }

    public static YamlConfiguration getConfig() {
        return config;
    }

    public static Double getDouble(String location, Double def) {
        config.addDefault(location, def);
        save();
        return config.getDouble(location, def);
    }

    public static String getDBURI() {
        config.addDefault("database.uri", "mongodb://username:password@host:27017/database?ssl=false&authSource=authdb&retryWrites=true&w=majority");
        save();
        return config.getString("database.uri");
    }
}
