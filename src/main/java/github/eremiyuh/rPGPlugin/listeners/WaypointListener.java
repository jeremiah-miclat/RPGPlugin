package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.manager.WaypointManager;
import github.eremiyuh.rPGPlugin.methods.WaypointGUI;
import github.eremiyuh.rPGPlugin.utils.Waypoint;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.List;
import java.util.stream.Collectors;

public class WaypointListener implements Listener {

    private final JavaPlugin plugin;
    private final WaypointManager waypointManager;
    private static final double CHECK_RADIUS = 2.0; // How close obsidian must be

    public WaypointListener(JavaPlugin plugin, WaypointManager waypointManager) {
        this.plugin = plugin;
        this.waypointManager = waypointManager;
    }

    // Left-click obsidian opens GUI
    @EventHandler
    public void onLeftClickObsidian(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.OBSIDIAN) return;

        Player player = event.getPlayer();

        // Only trigger if main hand is empty
        if (player.getInventory().getItemInMainHand().getType() != Material.AIR) return;

        if (isWaypointNearby(block)) {
            event.setCancelled(true);
            WaypointGUI.open(player, waypointManager, 0);
        }
    }

    // Prevent breaking obsidian near a waypoint
    @EventHandler
    public void onBreakObsidian(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.OBSIDIAN) return;

        if (isWaypointNearby(block)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot break obsidian near a waypoint!");
        }
    }

    // Prevent placing obsidian near a waypoint
    @EventHandler
    public void onPlaceObsidian(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.OBSIDIAN) return;

        if (isWaypointNearby(block)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot place obsidian near a waypoint!");
        }
    }

    // Helper method to check nearby waypoints
    private boolean isWaypointNearby(Block block) {
        return waypointManager.getAllWaypoints().stream()
                .anyMatch(wp -> wp.getWorld().equals(block.getWorld().getName())
                        && wp.toLocation().distance(block.getLocation()) <= CHECK_RADIUS);
    }
}

