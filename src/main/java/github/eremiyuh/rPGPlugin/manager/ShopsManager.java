package github.eremiyuh.rPGPlugin.manager;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ShopsManager {

    private final RPGPlugin plugin;
    private File shopsFile;
    private YamlConfiguration shopsConfig;

    public ShopsManager(RPGPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Save a shop for a player.
     * This method now appends a new shop under a unique shop id.
     */
    public void saveShopForPlayer(String playerName, ItemStack item, int itemAmount, int currencyAmount, String currency, Location location) {
        // Define the file path for the player's shops
        shopsFile = new File(plugin.getDataFolder(), playerName + "_shops.yml");

        // Load or create the configuration
        shopsConfig = YamlConfiguration.loadConfiguration(shopsFile);

        // Ensure the 'shops' section exists
        if (!shopsConfig.contains("shops")) {
            shopsConfig.createSection("shops");
        }

        // Generate a new shop ID (next available shop number)
        int shopID = findNextAvailableShopId();
        String shopPath = "shops.shop" + shopID;

        // Save the location details as separate components
        shopsConfig.set(shopPath + ".id.world", location.getWorld().getName());
        shopsConfig.set(shopPath + ".id.x", location.getX());
        shopsConfig.set(shopPath + ".id.y", location.getY());
        shopsConfig.set(shopPath + ".id.z", location.getZ());
        shopsConfig.set(shopPath + ".id.pitch", location.getPitch());
        shopsConfig.set(shopPath + ".id.yaw", location.getYaw());

        // Serialize the item and save to the config
        shopsConfig.set(shopPath + ".item", item.serialize());

        // Save other shop data (seller, item amount, currency info)
        shopsConfig.set(shopPath + ".seller", playerName);
        shopsConfig.set(shopPath + ".itemAmount", itemAmount);
        shopsConfig.set(shopPath + ".currencyAmount", currencyAmount);
        shopsConfig.set(shopPath + ".currency", currency);

        // Save the file
        saveFile(shopsFile, shopsConfig);
    }

    private int findNextAvailableShopId() {
        List<Integer> existingShopIds = shopsConfig.getConfigurationSection("shops")
                .getKeys(false)
                .stream()
                .map(key -> Integer.parseInt(key.replace("shop", "")))
                .collect(Collectors.toList());

        if (existingShopIds.isEmpty()) {
            return 1;
        }

        return Collections.max(existingShopIds) + 1;
    }


    // Helper method to save the configuration file
    private void saveFile(File file, YamlConfiguration config) {
        try {
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isShop(Location location) {
        Block block = location.getBlock();

        // Check if the block is a Chest or Barrel
        if (block.getState() instanceof Chest || block.getState() instanceof Barrel) {
            // Chest logic
            if (block.getState() instanceof Chest) {
                Chest chest = (Chest) block.getState();
                return hasShopMetadata(chest);  // Check if metadata exists
            }
            // Barrel logic
            else if (block.getState() instanceof Barrel) {
                Barrel barrel = (Barrel) block.getState();
                return hasShopMetadata(barrel);  // Check if metadata exists
            }
        }

        // Return false if it's neither a Chest nor a Barrel
        return false;
    }

    /**
     * Check if the Chest block has shop metadata.
     * @param chest The chest block.
     * @return True if the chest contains shop metadata, false otherwise.
     */
    private boolean hasShopMetadata(Chest chest) {
        return chest.hasMetadata("seller") &&
                chest.hasMetadata("item") &&
                chest.hasMetadata("itemAmount") &&
                chest.hasMetadata("currency") &&
                chest.hasMetadata("currencyAmount");
    }

    /**
     * Check if the Barrel block has shop metadata.
     * @param barrel The barrel block.
     * @return True if the barrel contains shop metadata, false otherwise.
     */
    private boolean hasShopMetadata(Barrel barrel) {
        return barrel.hasMetadata("seller") &&
                barrel.hasMetadata("item") &&
                barrel.hasMetadata("itemAmount") &&
                barrel.hasMetadata("currency") &&
                barrel.hasMetadata("currencyAmount");
    }



    public void loadAllShops() {
        // Get the plugin's data folder
        File dataFolder = plugin.getDataFolder();

        // List all files in the data folder
        File[] files = dataFolder.listFiles((dir, name) -> name.endsWith("_shops.yml"));

        // Check if there are any shop files
        if (files == null || files.length == 0) {
            plugin.getLogger().info("No shop files found to load.");
            return;
        }

        // Iterate through each shop file
        for (File shopFile : files) {
            // Extract the player name from the file name
            String fileName = shopFile.getName();
            String playerName = fileName.substring(0, fileName.indexOf("_shops.yml"));

            // Load the shops for this player
            loadShopsFromFile(playerName, shopFile);
        }
    }

    private void loadShopsFromFile(String playerName, File shopFile) {
        // Load the configuration
        YamlConfiguration shopConfig = YamlConfiguration.loadConfiguration(shopFile);

        // Ensure the 'shops' section exists
        if (!shopConfig.contains("shops")) {
            plugin.getLogger().warning("No shops section found in file: " + shopFile.getName());
            return;
        }

        boolean modified = false; // Track if the file is modified

        // Iterate through each shop entry
        for (String shopKey : shopConfig.getConfigurationSection("shops").getKeys(false)) {
            String shopPath = "shops." + shopKey;

            // Get the location details
            String worldName = shopConfig.getString(shopPath + ".id.world");
            assert worldName != null;
            World world = plugin.getServer().getWorld(worldName);
            if (world == null) {
                plugin.getLogger().warning("World not found for shop: " + shopKey + " in file: " + shopFile.getName());
                shopConfig.set(shopPath, null); // Remove invalid shop record
                modified = true;
                continue;
            }

            double x = shopConfig.getDouble(shopPath + ".id.x");
            double y = shopConfig.getDouble(shopPath + ".id.y");
            double z = shopConfig.getDouble(shopPath + ".id.z");
            float pitch = (float) shopConfig.getDouble(shopPath + ".id.pitch");
            float yaw = (float) shopConfig.getDouble(shopPath + ".id.yaw");
            Location location = new Location(world, x, y, z, yaw, pitch);

            // Get the block at the location
            Block block = location.getBlock();

            // Verify that the block is a valid shop container
            if (!(block.getState() instanceof Chest || block.getState() instanceof Barrel)) {
                plugin.getLogger().warning("Invalid shop container at: " + location + " for shop: " + shopKey);
                shopConfig.set(shopPath, null); // Remove invalid shop record
                modified = true;
                continue;
            }

            // Get other shop details
            String seller = shopConfig.getString(shopPath + ".seller");
            Map<String, Object> itemData = shopConfig.getConfigurationSection(shopPath + ".item").getValues(false);
            ItemStack item = ItemStack.deserialize(itemData);
            int itemAmount = shopConfig.getInt(shopPath + ".itemAmount");
            int currencyAmount = shopConfig.getInt(shopPath + ".currencyAmount");
            String currency = shopConfig.getString(shopPath + ".currency");

            // Set metadata on the block
            block.setMetadata("seller", new FixedMetadataValue(plugin, seller));
            block.setMetadata("item", new FixedMetadataValue(plugin, item));
            block.setMetadata("itemAmount", new FixedMetadataValue(plugin, itemAmount));
            block.setMetadata("currency", new FixedMetadataValue(plugin, currency));
            block.setMetadata("currencyAmount", new FixedMetadataValue(plugin, currencyAmount));

            plugin.getLogger().info("Loaded shop " + shopKey + " for player: " + playerName);
        }

        // Save changes if modifications were made
        if (modified) {
            try {
                shopConfig.save(shopFile);
                plugin.getLogger().info("Removed invalid shop records from file: " + shopFile.getName());
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to save shop file: " + shopFile.getName());
                e.printStackTrace();
            }
        }
    }


    public boolean removeShopRecord(String playerName, int shopID) {
        // Define the file path for the player's shops
        shopsFile = new File(plugin.getDataFolder(), playerName + "_shops.yml");

        // Check if the file exists
        if (!shopsFile.exists()) {
            plugin.getLogger().warning("Shop file not found for player: " + playerName);
            return false;
        }

        // Load the configuration
        shopsConfig = YamlConfiguration.loadConfiguration(shopsFile);

        // Get the path to the shop section
        String shopPath = "shops.shop" + shopID;

        // Check if the shop exists
        if (!shopsConfig.contains(shopPath)) {
            plugin.getLogger().warning("Shop ID " + shopID + " not found for player: " + playerName);
            return false;
        }

        // Remove the shop from the configuration
        shopsConfig.set(shopPath, null);

        // Save the updated configuration back to the file
        saveFile(shopsFile, shopsConfig);

        plugin.getLogger().info("Successfully removed shop " + shopID + " for player: " + playerName);
        return true;
    }

    public List<Location> listPlayerShops(String playerName) {
        // Define the file path for the player's shops
        shopsFile = new File(plugin.getDataFolder(), playerName + "_shops.yml");

        // Create a list to store the locations
        List<Location> shopLocations = new ArrayList<>();

        // Check if the file exists
        if (!shopsFile.exists()) {
            plugin.getLogger().warning("No shop file found for player: " + playerName);
            return shopLocations; // Return an empty list if no shops are found
        }

        // Load the configuration
        shopsConfig = YamlConfiguration.loadConfiguration(shopsFile);

        // Ensure the 'shops' section exists
        if (!shopsConfig.contains("shops")) {
            plugin.getLogger().warning("No shops found for player: " + playerName);
            return shopLocations; // Return an empty list if no shops are found
        }

        // Iterate through each shop entry and collect the location details
        for (String shopKey : shopsConfig.getConfigurationSection("shops").getKeys(false)) {
            String shopPath = "shops." + shopKey;

            // Get the location details
            String worldName = shopsConfig.getString(shopPath + ".id.world");
            World world = plugin.getServer().getWorld(worldName);
            if (world == null) {
                plugin.getLogger().warning("World not found for shop: " + shopKey + " for player: " + playerName);
                continue; // Skip this shop if the world is not found
            }

            double x = shopsConfig.getDouble(shopPath + ".id.x");
            double y = shopsConfig.getDouble(shopPath + ".id.y");
            double z = shopsConfig.getDouble(shopPath + ".id.z");
            float pitch = (float) shopsConfig.getDouble(shopPath + ".id.pitch");
            float yaw = (float) shopsConfig.getDouble(shopPath + ".id.yaw");

            // Create the Location object and add it to the list
            Location location = new Location(world, x, y, z, yaw, pitch);
            shopLocations.add(location);
        }

        // Return the list of shop locations
        return shopLocations;
    }



}
