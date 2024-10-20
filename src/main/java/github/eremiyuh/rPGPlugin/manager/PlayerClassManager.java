package github.eremiyuh.rPGPlugin.manager;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerClassManager {
    private final JavaPlugin plugin;
    private final Map<UUID, String> playerClasses = new HashMap<>();
    private final Map<UUID, Long> lastClassSelection = new HashMap<>();
    private final Map<UUID, Map<String, Integer>> playerAttributes = new HashMap<>();
    private final Map<UUID, Integer> attributePoints = new HashMap<>(); // Track remaining attribute points

    private File classFile;
    private FileConfiguration classData;

    public PlayerClassManager(JavaPlugin plugin) {
        this.plugin = plugin;
        setupClassFile(); // Initialize file for storing data
    }

    // Setup the playerClasses.yml file to store player data
    private void setupClassFile() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

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

    // Save player's class and attributes
    public void savePlayerClass(Player player, String className) {
        UUID playerUUID = player.getUniqueId();
        String currentClass = playerClasses.get(playerUUID);

        // If the player is switching classes, refund their allocated points and reset attributes
        if (currentClass != null && !currentClass.equals(className)) {
            refundAllocatedPoints(playerUUID);
            resetPlayerAttributes(playerUUID);
        }

        playerClasses.put(playerUUID, className);
        classData.set(playerUUID.toString() + ".class", className);

        // Save or update the player's attributes
        Map<String, Integer> currentAttributes = playerAttributes.getOrDefault(playerUUID, getDefaultAttributes());
        saveAttributes(playerUUID, currentAttributes);

        // Assign 1 attribute point to the player after class selection
        attributePoints.put(playerUUID, 1);
        classData.set(playerUUID.toString() + ".attributePoints", 1);

        // Save last class selection time
        long currentTime = System.currentTimeMillis();
        lastClassSelection.put(playerUUID, currentTime);
        classData.set(playerUUID.toString() + ".lastSelection", currentTime);

        saveData(); // Save everything to the file
    }

    // Load player's class and attributes from file
    public String loadPlayerClass(Player player) {
        UUID playerUUID = player.getUniqueId();

        // Load the player's class from the config
        String playerClass = classData.getString(playerUUID.toString() + ".class");

        // Load attributes from file (if they exist)
        if (classData.contains(playerUUID.toString() + ".attributes")) {
            Map<String, Integer> attributes = new HashMap<>();
            for (String attribute : getDefaultAttributes().keySet()) {
                int value = classData.getInt(playerUUID.toString() + ".attributes." + attribute, 0);
                attributes.put(attribute, value);
            }
            playerAttributes.put(playerUUID, attributes);
        }

        // Load attribute points from file
        attributePoints.put(playerUUID, classData.getInt(playerUUID.toString() + ".attributePoints", 1));

        return playerClass;
    }

    // Check if a player can select a new class (based on cooldown)
    public boolean canSelectClass(Player player) {
        Long lastSelection = lastClassSelection.get(player.getUniqueId());
        return lastSelection == null || (System.currentTimeMillis() - lastSelection) >= 86400000; // 24-hour cooldown
    }

    // Get the last time the player selected a class
    public long getLastSelectionTime(Player player) {
        return lastClassSelection.getOrDefault(player.getUniqueId(), 0L);
    }

    // Get the player's attributes
    public Map<String, Integer> getPlayerAttributes(Player player) {
        return playerAttributes.getOrDefault(player.getUniqueId(), getDefaultAttributes());
    }

    // Get default attribute values (all set to 0 initially)
    private Map<String, Integer> getDefaultAttributes() {
        Map<String, Integer> defaultAttributes = new HashMap<>();
        defaultAttributes.put("strength", 0);
        defaultAttributes.put("agility", 0);
        defaultAttributes.put("dexterity", 0);
        defaultAttributes.put("intelligence", 0);
        defaultAttributes.put("vitality", 0);
        return defaultAttributes;
    }

    // Get the number of remaining attribute points
    public int getRemainingAttributePoints(Player player) {
        return attributePoints.getOrDefault(player.getUniqueId(), 0);
    }

    // Allocate an attribute point to a specified attribute
    public void allocateAttributePoint(Player player, String attribute) {
        UUID playerUUID = player.getUniqueId();
        Map<String, Integer> attributes = playerAttributes.get(playerUUID);
        if (attributes != null && attributePoints.get(playerUUID) > 0) {
            // Increase the specified attribute by 1
            attributes.put(attribute, attributes.get(attribute) + 1);
            playerAttributes.put(playerUUID, attributes);

            // Decrease the remaining points
            attributePoints.put(playerUUID, attributePoints.get(playerUUID) - 1);
            classData.set(playerUUID.toString() + ".attributePoints", attributePoints.get(playerUUID));

            saveAttributes(playerUUID, attributes); // Save updated attributes
        }
    }

    // Refund allocated points when switching classes
    private void refundAllocatedPoints(UUID playerUUID) {
        int currentPoints = attributePoints.getOrDefault(playerUUID, 0);
        int allocatedPoints = getTotalAllocatedPoints(playerUUID);
        attributePoints.put(playerUUID, currentPoints + allocatedPoints); // Refund the allocated points
        classData.set(playerUUID.toString() + ".attributePoints", attributePoints.get(playerUUID));
    }

    // Reset player attributes to default values
    private void resetPlayerAttributes(UUID playerUUID) {
        playerAttributes.put(playerUUID, getDefaultAttributes());
        saveAttributes(playerUUID, getDefaultAttributes());
    }

    // Calculate the total allocated points
    private int getTotalAllocatedPoints(UUID playerUUID) {
        Map<String, Integer> attributes = playerAttributes.get(playerUUID);
        return attributes.values().stream().mapToInt(Integer::intValue).sum();
    }

    public int getAllocatedAttributePoints(Player player) {
        UUID playerUUID = player.getUniqueId();
        // Return the number of allocated attribute points for the player
        return playerAttributes.getOrDefault(playerUUID, getDefaultAttributes()).values().stream().reduce(0, Integer::sum);
    }

    // Get the player's strength attribute
    public int getPlayerStrength(Player player) {
        UUID playerUUID = player.getUniqueId();
        return playerAttributes.getOrDefault(playerUUID, getDefaultAttributes()).getOrDefault("strength", 0);
    }

    // Get the player's agility attribute
    public int getPlayerAgility(Player player) {
        UUID playerUUID = player.getUniqueId();
        return playerAttributes.getOrDefault(playerUUID, getDefaultAttributes()).getOrDefault("agility", 0);
    }

    // Get the player's dexterity attribute
    public int getPlayerDexterity(Player player) {
        UUID playerUUID = player.getUniqueId();
        return playerAttributes.getOrDefault(playerUUID, getDefaultAttributes()).getOrDefault("dexterity", 0);
    }

    // Get the player's intelligence attribute
    public int getPlayerIntelligence(Player player) {
        UUID playerUUID = player.getUniqueId();
        return playerAttributes.getOrDefault(playerUUID, getDefaultAttributes()).getOrDefault("intelligence", 0);
    }

    // Get the player's vitality attribute
    public int getPlayerVitality(Player player) {
        UUID playerUUID = player.getUniqueId();
        return playerAttributes.getOrDefault(playerUUID, getDefaultAttributes()).getOrDefault("vitality", 0);
    }



    // Save player's attributes to the file
    private void saveAttributes(UUID playerUUID, Map<String, Integer> attributes) {
        for (Map.Entry<String, Integer> entry : attributes.entrySet()) {
            classData.set(playerUUID.toString() + ".attributes." + entry.getKey(), entry.getValue());
        }
        classData.set(playerUUID.toString() + ".attributePoints", attributePoints.get(playerUUID));
        saveData();
    }

    // Save all data for all players (called when plugin is disabled or for backups)
    public void saveAllData() {
        for (UUID playerUUID : playerClasses.keySet()) {
            classData.set(playerUUID.toString() + ".class", playerClasses.get(playerUUID));
            classData.set(playerUUID.toString() + ".lastSelection", lastClassSelection.get(playerUUID));
            saveAttributes(playerUUID, playerAttributes.getOrDefault(playerUUID, getDefaultAttributes()));
        }
        saveData();
    }

    // Save the classData to the YAML file
    private void saveData() {
        try {
            classData.save(classFile);
        } catch (IOException e) {
            Bukkit.getLogger().severe("Could not save playerClasses.yml file!");
            e.printStackTrace();
        }
    }
}
