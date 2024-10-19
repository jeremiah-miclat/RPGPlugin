package github.eremiyuh.rPGPlugin.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PlayerClassManager {
    private final JavaPlugin plugin;
    private final Map<Player, String> playerClasses = new HashMap<>();
    private final Map<Player, Long> lastClassSelection = new HashMap<>(); // To track timestamps
    private File classFile;
    private FileConfiguration classData;

    public PlayerClassManager(JavaPlugin plugin) {
        this.plugin = plugin;

        // Create the plugin's data folder if it doesn't exist
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs(); // Create the folder
        }

        // Create a file to store player classes
        classFile = new File(plugin.getDataFolder(), "playerClasses.yml");
        if (!classFile.exists()) {
            try {
                classFile.createNewFile();
            } catch (IOException e) {
                Bukkit.getLogger().severe("Could not create playerClasses.yml file!");
                e.printStackTrace();
            }
        }
        classData = YamlConfiguration.loadConfiguration(classFile);
    }

    public Map<Player, String> getPlayerClasses() {
        return playerClasses;
    }

    public void savePlayerClass(Player player, String className) {
        playerClasses.put(player, className);
        classData.set(player.getUniqueId().toString(), className);
        lastClassSelection.put(player, System.currentTimeMillis()); // Update timestamp
        try {
            classData.save(classFile);
        } catch (IOException e) {
            Bukkit.getLogger().severe("Could not save player class for " + player.getName());
            e.printStackTrace();
        }
    }

    public String loadPlayerClass(Player player) {
        return classData.getString(player.getUniqueId().toString());
    }

    public boolean canSelectClass(Player player) {
        Long lastSelection = lastClassSelection.get(player);
        // If player hasn't selected a class yet, allow selection
        if (lastSelection == null) {
            return true;
        }
        long timeSinceLastSelection = System.currentTimeMillis() - lastSelection;
        // Check if 24 hours have passed
        return timeSinceLastSelection >= TimeUnit.HOURS.toMillis(24);
    }

    public long getLastSelectionTime(Player player) {
        return lastClassSelection.getOrDefault(player, 0L);
    }

    public void loadClassDataFromFile() {
        // Load player classes and last selection timestamps from the YAML file
        for (String key : classData.getKeys(false)) {
            Player player = Bukkit.getPlayer(java.util.UUID.fromString(key));
            if (player != null) {
                String className = classData.getString(key);
                playerClasses.put(player, className);
                lastClassSelection.put(player, classData.getLong(key + ".lastSelection", 0)); // Load last selection timestamp
            }
        }
    }

    public void saveAllData() {
        // Save all player classes and last selection timestamps back to the YAML file
        for (Player player : playerClasses.keySet()) {
            String className = playerClasses.get(player);
            classData.set(player.getUniqueId().toString(), className);
            classData.set(player.getUniqueId().toString() + ".lastSelection", lastClassSelection.get(player)); // Save last selection timestamp
        }
        try {
            classData.save(classFile);
        } catch (IOException e) {
            Bukkit.getLogger().severe("Could not save player classes and selection timestamps!");
            e.printStackTrace();
        }
    }
}
