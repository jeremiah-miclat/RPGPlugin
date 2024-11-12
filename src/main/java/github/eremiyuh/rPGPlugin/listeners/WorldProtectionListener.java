package github.eremiyuh.rPGPlugin.listeners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;

import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class WorldProtectionListener implements Listener {

    private final JavaPlugin plugin;
    private final String protectedWorldName = "world_rpg"; // Name of the protected world

    public WorldProtectionListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private static final Set<Material> ANNOYING_BLOCKS;

    static {
        ANNOYING_BLOCKS = new HashSet<>();
        ANNOYING_BLOCKS.add(Material.SHORT_GRASS);
        ANNOYING_BLOCKS.add(Material.TALL_GRASS);
        ANNOYING_BLOCKS.add(Material.FERN);
        ANNOYING_BLOCKS.add(Material.DEAD_BUSH);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (ANNOYING_BLOCKS.contains(event.getBlock().getType())) {
            return;
        }

        // Cancel block break in protected world unless the player is in creative mode
        if (isInProtectedWorld(event.getBlock().getWorld()) && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }

        if (isInProtectedWorld(event.getBlock().getWorld())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        // Cancel block place in protected world unless the player is in creative mode
        if (isInProtectedWorld(event.getBlock().getWorld()) && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }

        if (isInProtectedWorld(event.getBlock().getWorld())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        // Cancel bucket usage (water, lava) in protected world unless in creative mode
        if (isInProtectedWorld(event.getBlockClicked().getWorld()) && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
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
    public void onEntitySpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();

        if (isInProtectedWorld(event.getEntity().getWorld())) {
            if (entity.getType() == EntityType.ARMOR_STAND) {
//                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEndermanChangeBlock(EntityChangeBlockEvent event) {

        if (event.getEntity() instanceof Enderman) {

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
            }

            if (item == Material.WATER_BUCKET || item == Material.LAVA_BUCKET || item == Material.BUCKET) {
                event.setCancelled(true);
            }


            // Cancel interactions with trapdoors and fences
            if (isTrapdoorOrFence(item)) {
                event.setCancelled(true);
            }

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
