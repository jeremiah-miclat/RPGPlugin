package github.eremiyuh.rPGPlugin.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

public class HologramUtil {

    // Method to create a single hologram at a given location
    public static ArmorStand createHologram(World world, double x, double y, double z, String text) {
        // Set the location for the hologram
        Location location = new Location(world, x, y, z);

        // Spawn the ArmorStand at the location
        ArmorStand armorStand = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);

        // Set the custom name (the text of the hologram) and make it visible
        armorStand.setCustomName(ChatColor.translateAlternateColorCodes('&', text));
        armorStand.setCustomNameVisible(true);

        // Make the ArmorStand invisible and disable its gravity
        armorStand.setInvisible(true);
        armorStand.setGravity(false);

        // Disable any interactions and make sure it's not vulnerable
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);  // Makes the ArmorStand even smaller and removes hitbox

        return armorStand;
    }

    // Method to create a stack of holograms (multiple lines) at a given location
    public static void createHologramStack(World world, double x, double y, double z, String[] lines) {
        double offsetY = 0;  // Adjust vertical spacing between lines
        for (String line : lines) {
            createHologram(world, x, y + offsetY, z, line);
            offsetY += 0.25;  // Adjust the vertical spacing between lines (this value can be changed)
        }
    }

    // Method to create multiple stacks of holograms at different locations
    public static void createMultipleStacks(World world, Location[] locations, String[][] stacks) {
        for (int i = 0; i < locations.length; i++) {
            Location loc = locations[i];      // The location of the current stack
            String[] lines = stacks[i];        // The text lines for the current stack

            // Call the createHologramStack method to create a stack of holograms
            createHologramStack(world, loc.getX(), loc.getY(), loc.getZ(), lines);
        }
    }
}
