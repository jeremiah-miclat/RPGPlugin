package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class AreaProtectionListener implements Listener {

    private final World world;
    private final int x1 = 78, z1 = 78;
    private final int x2 = -92, z2 = -51;

    public AreaProtectionListener(RPGPlugin plugin) {
        this.world = Bukkit.getWorld("world"); // Ensure this is the correct world name
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    // Check if the coordinates are within the protected area
    private boolean isInProtectedArea(int x, int z) {
        return x >= Math.min(x1, x2) && x <= Math.max(x1, x2) &&
                z >= Math.min(z1, z2) && z <= Math.max(z1, z2);
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        if (isInProtectedArea(entity.getLocation().getBlockX(), entity.getLocation().getBlockZ())) {
            if (entity.getType() != EntityType.PLAYER) { // Allow players to spawn
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        int x = event.getBlock().getX();
        int z = event.getBlock().getZ();

        if (isInProtectedArea(x, z) && !player.isOp()) {
            event.setCancelled(true);
            player.sendMessage("Block breaking is disabled in this protected area!");
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        int x = block.getX();
        int z = block.getZ();

        // Disallow placing any blocks in the protected area, except item frames and armor stands for OP players
        if (isInProtectedArea(x, z)) {
            Material type = block.getType();
            if (type == Material.WATER || type == Material.LAVA || type == Material.WATER_BUCKET || type == Material.LAVA_BUCKET) {
                player.sendMessage("Placing water and lava is disabled in this protected area!");
                event.setCancelled(true);
            } else if (type == Material.ITEM_FRAME || type == Material.ARMOR_STAND) {
                if (!player.isOp()) {
                    player.sendMessage("Only OP players can place item frames and armor stands in this protected area!");
                    event.setCancelled(true);
                }
            } else if (!player.isOp()) {
                player.sendMessage("Building is disabled in this protected area!");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // Handle right-clicking to place item frames or armor stands
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null) {
            Material item = event.getItem().getType();
            Block block = event.getClickedBlock();

            if (block != null && isInProtectedArea(block.getX(), block.getZ())) {
                if ((item == Material.ITEM_FRAME || item == Material.ARMOR_STAND) && !player.isOp()) {
                    player.sendMessage("Only OP players can place item frames and armor stands in this protected area!");
                    event.setCancelled(true);
                }
            }
        }

        // Disable interactions with specific blocks in the protected area
        if (event.hasBlock()) {
            Block block = event.getClickedBlock();
            int x = block.getX();
            int z = block.getZ();

            if (isInProtectedArea(x, z) && !player.isOp()) {
                Material type = block.getType();
                if (type == Material.CHEST || type == Material.FURNACE ||
                        type.name().contains("DOOR") || type.name().contains("TRAPDOOR")) {
                    event.setCancelled(true);
                    player.sendMessage("Interacting with this block is disabled in this protected area!");
                }
            }
        }

        // Disable right-clicking water or lava buckets in the area
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (event.getItem() != null && !player.isOp()) {
                Material item = event.getItem().getType();
                if ((item == Material.WATER_BUCKET || item == Material.LAVA_BUCKET) &&
                        isInProtectedArea(player.getLocation().getBlockX(), player.getLocation().getBlockZ())) {
                    event.setCancelled(true);
                    player.sendMessage("Using water and lava buckets is disabled in this protected area!");
                }
            }
        }
    }
}
