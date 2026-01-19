package github.eremiyuh.rPGPlugin.manager;

import github.eremiyuh.rPGPlugin.utils.Waypoint;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class WaypointManager {

    private final JavaPlugin plugin;
    private final Map<String, Waypoint> waypoints;
    private final File file;
    private final YamlConfiguration config;

    public WaypointManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.waypoints = new HashMap<>();
        this.file = new File(plugin.getDataFolder(), "waypoints.yml");

        if (!file.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        this.config = YamlConfiguration.loadConfiguration(file);
        loadAll();
    }

    private void loadAll() {
        if (!config.contains("waypoints")) return;

        for (String key : config.getConfigurationSection("waypoints").getKeys(false)) {
            String path = "waypoints." + key;

            Waypoint wp = new Waypoint(
                    key,
                    config.getString(path + ".creator"),
                    new Location(
                            Bukkit.getWorld(config.getString(path + ".world")),
                            config.getDouble(path + ".x"),
                            config.getDouble(path + ".y"),
                            config.getDouble(path + ".z"),
                            (float) config.getDouble(path + ".yaw"),
                            (float) config.getDouble(path + ".pitch")
                    ),
                    BlockFace.valueOf(config.getString(path + ".facing"))
            );

            waypoints.put(key.toLowerCase(), wp);
        }
    }

    // ================= MEMORY =================

    public boolean exists(String name) {
        return waypoints.containsKey(name.toLowerCase());
    }

    public Waypoint get(String name) {
        return waypoints.get(name.toLowerCase());
    }

    public Collection<Waypoint> getAll() {
        return waypoints.values();
    }

    // ================= WRITE =================

    public void add(Waypoint wp) {
        waypoints.put(wp.getName().toLowerCase(), wp);
        save(wp);
    }

    private void save(Waypoint wp) {
        String path = "waypoints." + wp.getName().toLowerCase();

        config.set(path + ".creator", wp.getCreator());
        config.set(path + ".world", wp.getWorld());
        config.set(path + ".x", wp.getX());
        config.set(path + ".y", wp.getY());
        config.set(path + ".z", wp.getZ());
        config.set(path + ".yaw", wp.getYaw());
        config.set(path + ".pitch", wp.getPitch());
        config.set(path + ".facing", wp.getFacing().name());

        saveFile();
    }

    public void saveAll() {
        saveFile();
    }

    private void saveFile() {
        try {
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Collection<Waypoint> getAllWaypoints() {
        return waypoints.values(); // returns all waypoints in memory
    }

    public boolean delete(String name, String playerName) {
        Waypoint wp = waypoints.get(name.toLowerCase());
        if (wp == null) return false; // does not exist

        // Check if player is owner
        if (!wp.getCreator().equalsIgnoreCase(playerName)) {
            return false; // not owner
        }

        // Remove from memory
        waypoints.remove(name.toLowerCase());

        // Remove from file
        String path = "waypoints." + name.toLowerCase();
        config.set(path, null);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

}
