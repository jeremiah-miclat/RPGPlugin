package github.eremiyuh.rPGPlugin.listeners;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class DragonEggListener implements Listener {

    // Listen for player interaction with the Dragon Egg
    @EventHandler
    public void onDragonEggInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        // Check if the item in hand is the Dragon Egg
        if (item.getType() == Material.DRAGON_EGG) {

            // If the player right-clicks (or left-clicks), summon the Ender Dragon
            if (event.getAction().toString().contains("RIGHT_CLICK")) {
                summonEnderDragon(player.getLocation());
            }
        }
    }

    // Method to summon the Ender Dragon at a specific location
    private void summonEnderDragon(Location location) {
        World world = location.getWorld();

        // Ensure the world is the correct one (e.g., the End world or a custom world)
        if (world != null && world.getName().contains("resource")) {
            // Spawn the Ender Dragon in the End world (this will require the world to exist)
            world.spawnEntity(location, EntityType.ENDER_DRAGON);
            // Play sound effect to indicate the dragon is summoned
            world.playSound(location, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0F, 1.0F);
        }

    }
}
