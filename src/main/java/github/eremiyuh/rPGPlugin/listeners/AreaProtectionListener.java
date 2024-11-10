package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class AreaProtectionListener implements Listener {

    private final World world;
    private final int x1 = -150, z1 = 150;
    private final int x2 = 90, z2 = -110;

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
            if (entity.getType() != EntityType.PLAYER && entity.getType() != EntityType.ARMOR_STAND && event.getEntity() instanceof Monster) { // Allow players to spawn
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (isInProtectedArea(block.getLocation().getBlockX(), block.getLocation().getBlockZ())&& !player.isOp()) {
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (isInProtectedArea(block.getLocation().getBlockX(), block.getLocation().getBlockZ()) && !player.isOp()) {


                event.setCancelled(true);

        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        if (event.getClickedBlock()!= null && (isInProtectedArea(event.getClickedBlock().getLocation().getBlockX(), event.getClickedBlock().getLocation().getBlockZ()))) {
            Player player = event.getPlayer();

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null) {
                Material item = event.getItem().getType();
                Block block = event.getClickedBlock();

                // Allow all trapdoors, fence doors, and doors
                if (block != null && isTrapdoorOrFence(block.getType())) {
                    return; // Allow interaction, do not cancel
                }

                // Prevent using flint and steel, water, lava, or empty buckets if player is not op
                if ((item == Material.FLINT_AND_STEEL ||
                        item == Material.WATER_BUCKET ||
                        item == Material.LAVA_BUCKET ||
                        item == Material.ITEM_FRAME ||
                        item == Material.GLOW_ITEM_FRAME ||
                        item == Material.BUCKET) && !player.isOp()) {
                    event.setCancelled(true);
                }
            }

            if (event.getAction() == Action.LEFT_CLICK_BLOCK && event.getItem() != null) {

                if ((event.getClickedBlock().getType() == Material.ITEM_FRAME || event.getClickedBlock().getType() == Material.GLOW_ITEM_FRAME) && !player.isOp()) {
                    event.setCancelled(true);
                }

            }
        }
    }

    @EventHandler
    public void onPlayerInteractWithArmorStand(PlayerInteractAtEntityEvent event) {

        if (isInProtectedArea(event.getRightClicked().getLocation().getBlockX(), event.getRightClicked().getLocation().getBlockZ())) {
            Player player = event.getPlayer();
            if (event.getRightClicked().getType() == EntityType.ARMOR_STAND && !player.isOp()) {
                event.setCancelled(true);
            }

            if (event.getRightClicked().getType() == EntityType.ITEM_FRAME && !player.isOp()) {
                event.setCancelled(true);
            }

            if (event.getRightClicked().getType() == EntityType.GLOW_ITEM_FRAME && !player.isOp()) {
                event.setCancelled(true);
            }

            if (event.getRightClicked().getType() == EntityType.ALLAY && !player.isOp()) {
                event.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        if (isInProtectedArea(event.getBlockClicked().getX(), event.getBlockClicked().getZ()) && !player.isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();

        // Only proceed if the entity is within the protected area
        if (isInProtectedArea(entity.getLocation().getBlockX(), entity.getLocation().getBlockZ())) {

            // Protect non-living entities or Allays
            if (!(entity instanceof LivingEntity) || entity.getType() == EntityType.ALLAY || entity.getType() == EntityType.ARMOR_STAND
                    || entity.getType() == EntityType.ITEM_FRAME
                    || entity.getType() == EntityType.GLOW_ITEM_FRAME
                    || entity instanceof Animals
                    || entity instanceof Fish
            ) {
                if (event instanceof EntityDamageByEntityEvent) {
                    Entity damager = ((EntityDamageByEntityEvent) event).getDamager();

                    if (damager instanceof Player && ((Player) damager).isOp()) {
                        return; // Allow OP player to damage protected entities
                    }

                }
                // Cancel damage for protected entities if not by an OP player
                event.setCancelled(true);
            }
        }
    }




    // Helper method to check if the material is a trapdoor or fence
    private boolean isTrapdoorOrFence(Material material) {
        return material.name().endsWith("TRAPDOOR") || material.name().endsWith("FENCE") || material.name().endsWith("_DOOR");
    }


}
