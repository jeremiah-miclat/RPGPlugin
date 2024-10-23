package github.eremiyuh.rPGPlugin.methods;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;


public class Abilities {
    // method to fire at a location
    public void summonFireOnTarget(Location location) {
        Block block = location.getBlock();
        if (block.getType() == Material.AIR || block.getType() == Material.WATER) {
            block.setType(Material.FIRE);
        }
    }

    // method to fire at a location
    public void summonPowderSnowOnTarget(Location location) {
        Block block = location.getBlock();
        if (block.getType() == Material.AIR || block.getType() == Material.WATER) {
            block.setType(Material.POWDER_SNOW);
        }
    }
}
