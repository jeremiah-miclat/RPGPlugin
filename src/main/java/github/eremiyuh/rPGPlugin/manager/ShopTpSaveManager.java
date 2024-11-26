package github.eremiyuh.rPGPlugin.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class ShopTpSaveManager {
    private final File shopsFile;
    private final YamlConfiguration shopsConfig;

    public ShopTpSaveManager(File dataFolder) {
        // Initialize shops.yml file
        this.shopsFile = new File(dataFolder, "shopsloc.yml");
        if (!shopsFile.exists()) {
            try {
                shopsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.shopsConfig = YamlConfiguration.loadConfiguration(shopsFile);
    }

    public boolean saveShopLocation(Player player) {
        String playerName = player.getName();
        Location loc = player.getLocation();
        String path = "shops." + playerName;

        // Save location details
        shopsConfig.set(path + ".world", loc.getWorld().getName());
        shopsConfig.set(path + ".x", loc.getX());
        shopsConfig.set(path + ".y", loc.getY());
        shopsConfig.set(path + ".z", loc.getZ());
        shopsConfig.set(path + ".yaw", loc.getYaw());
        shopsConfig.set(path + ".pitch", loc.getPitch());

        try {
            shopsConfig.save(shopsFile);
        } catch (IOException e) {
            e.printStackTrace();
            return false; // Failed to save
        }
        return true; // Successfully saved
    }

    public boolean hasShop(Player player) {
        String playerName = player.getName();
        return shopsConfig.contains("shops." + playerName);
    }

    public Location getSavedSellerShopLocation(String playerName) {
        if (shopsConfig.contains("shops." + playerName)) {
            // Retrieve world name from config
            String worldName = shopsConfig.getString("shops." + playerName + ".world");

            // Use Bukkit to get the world object
            World world = Bukkit.getServer().getWorld(worldName);

            if (world != null) {
                // Retrieve coordinates and rotation from config
                double x = shopsConfig.getDouble("shops." + playerName + ".x");
                double y = shopsConfig.getDouble("shops." + playerName + ".y");
                double z = shopsConfig.getDouble("shops." + playerName + ".z");
                float yaw = (float) shopsConfig.getDouble("shops." + playerName + ".yaw");
                float pitch = (float) shopsConfig.getDouble("shops." + playerName + ".pitch");

                // Return the location object
                return new Location(world, x, y, z, yaw, pitch);
            }
        }
        return null; // Return null if the shop location does not exist for the player
    }
}
