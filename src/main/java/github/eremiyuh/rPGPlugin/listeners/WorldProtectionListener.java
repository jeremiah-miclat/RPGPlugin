package github.eremiyuh.rPGPlugin.listeners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Boat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;

import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class WorldProtectionListener implements Listener {

    private final JavaPlugin plugin;
    private final String protectedWorldName = "world_rpg"; // Name of the protected world

    public WorldProtectionListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // Cancel block break in protected world unless the player is in creative mode
        if (isInProtectedWorld(event.getBlock().getWorld()) && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("Block breaking is not allowed in this world.");
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        // Cancel block place in protected world unless the player is in creative mode
        if (isInProtectedWorld(event.getBlock().getWorld()) && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("Block placing is not allowed in this world.");
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        // Cancel bucket usage (water, lava) in protected world unless in creative mode
        if (isInProtectedWorld(event.getBlockClicked().getWorld()) && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("Bucket usage is not allowed in this world.");
        }
    }

    @EventHandler
    public void onVehiclePlace(VehicleCreateEvent event) {
        // Cancel vehicle (e.g., boat) placement in protected world unless in creative mode
        if (isInProtectedWorld(event.getVehicle().getWorld())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onVehicleRide(VehicleEnterEvent event) {
        // Cancel vehicle (e.g., boat) placement in protected world unless in creative mode
        if (isInProtectedWorld(event.getVehicle().getWorld())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Material item = event.getMaterial();
        // Cancel interactions with certain items that could damage the world
        if (isInProtectedWorld(event.getPlayer().getWorld()) && event.getPlayer().getGameMode() != GameMode.CREATIVE) {

            if (item == Material.FLINT_AND_STEEL || item == Material.TNT) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("You cannot use that item in this world.");
            }
        }

        // Cancel interactions with trapdoors and fences
        if (isTrapdoorOrFence(item)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("You cannot interact with that in this world.");
        }

    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        // Prevent block damage from explosions caused by entities in the protected world
        if (isInProtectedWorld(Objects.requireNonNull(event.getLocation().getWorld()))) {
            event.blockList().clear(); // Clear the list of blocks to prevent them from being destroyed
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        // Prevent block damage from explosions caused by blocks (e.g., TNT) in the protected world
        if (isInProtectedWorld(event.getBlock().getWorld())) {
            event.blockList().clear(); // Clear the list of blocks to prevent them from being destroyed
        }
    }

    // Helper method to check if the event occurs in the protected world
    private boolean isInProtectedWorld(World world) {
        return world.getName().equalsIgnoreCase(protectedWorldName);
    }

    // Helper method to check if the material is a trapdoor or fence
    private boolean isTrapdoorOrFence(Material material) {
        return material.name().contains("TRAPDOOR") || material.name().contains("FENCE");
    }
}
