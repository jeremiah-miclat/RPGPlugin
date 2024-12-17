package github.eremiyuh.rPGPlugin.listeners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
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
    private final Set<String> protectedWorldNames; // Set of protected world names

    public WorldProtectionListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.protectedWorldNames = Set.of("world_rpg", "world_labyrinth", "world_labyrinth2"); // Add all protected world names here
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

        if (isInProtectedWorld(event.getBlock().getWorld()) && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (isInProtectedWorld(event.getBlock().getWorld()) && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        if (isInProtectedWorld(event.getBlockClicked().getWorld()) && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onVehiclePlace(VehicleCreateEvent event) {
        if (isInProtectedWorld(event.getVehicle().getWorld())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onVehicleRide(VehicleEnterEvent event) {
        if (isInProtectedWorld(event.getVehicle().getWorld())) {
            event.setCancelled(true);
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
        if (isInProtectedWorld(event.getPlayer().getWorld()) && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            if (item == Material.FLINT_AND_STEEL || item == Material.TNT) {
                event.setCancelled(true);
            }

            if (item == Material.WATER_BUCKET || item == Material.LAVA_BUCKET || item == Material.BUCKET) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (isInProtectedWorld(Objects.requireNonNull(event.getLocation().getWorld()))) {
            event.blockList().clear();
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        if (isInProtectedWorld(event.getBlock().getWorld())) {
            event.blockList().clear();
        }
    }


    @EventHandler
    public void entitySpawn(EntityPlaceEvent event) {
        Entity entity = event.getEntity();
        if (!entity.getWorld().getName().contains("labyrinth") && !entity.getWorld().getName().equals("world_rpg")) {
            return;
        }

        if (isInProtectedWorld(event.getEntity().getWorld())) {
            event.setCancelled(true);
        }

    }

    // Helper method to check if the event occurs in a protected world
    private boolean isInProtectedWorld(World world) {
        return protectedWorldNames.contains(world.getName().toLowerCase());
    }
}
