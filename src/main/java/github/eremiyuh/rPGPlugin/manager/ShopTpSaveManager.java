package github.eremiyuh.rPGPlugin.manager;

import org.bukkit.Location;
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
}
