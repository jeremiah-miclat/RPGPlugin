package github.eremiyuh.rPGPlugin.methods;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;


public class Abilities {

    private JavaPlugin plugin;

    public Abilities(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    // method to fire at a location
    public void summonFireOnTarget(Location location) {
        Block block = location.getBlock();
        if (block.getType() == Material.AIR || block.getType() == Material.WATER || block.getType() == Material.POWDER_SNOW || block.getType() == Material.FIRE) {
            block.setType(Material.FIRE);
        }
    }


    // Method to summon Powder Snow at a location and remove it after 5 seconds
    public void summonPowderSnowOnTarget(Location location) {
        Block block = location.getBlock();
        if (block.getType() == Material.AIR || block.getType() == Material.WATER || block.getType() == Material.POWDER_SNOW || block.getType() == Material.FIRE) {
            block.setType(Material.POWDER_SNOW);

            // Schedule a task to remove the Powder Snow after 5 seconds
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (block.getType() == Material.POWDER_SNOW) {
                    block.setType(Material.AIR);
                }
            }, 100L); // 100 ticks = 5 seconds
        }
    }
}
