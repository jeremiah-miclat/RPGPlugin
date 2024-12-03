package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import io.papermc.paper.event.player.PlayerTradeEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.inventory.TradeSelectEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.event.raid.RaidEvent;
import org.bukkit.event.raid.RaidTriggerEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class AreaProtectionListener implements Listener {

    private final World world;


    private final int xx1 = 292, zz1 = -293;
    private final int xx2 = -338, zz2 = 330;

    private final int x1 = -150, z1 = 150;
    private final int x2 = 150, z2 = -150;

    private static final Set<Material> ANNOYING_BLOCKS;

    static {
        ANNOYING_BLOCKS = new HashSet<>();
        ANNOYING_BLOCKS.add(Material.SHORT_GRASS);
        ANNOYING_BLOCKS.add(Material.TALL_GRASS);
        ANNOYING_BLOCKS.add(Material.DEAD_BUSH);
    }

    public AreaProtectionListener(RPGPlugin plugin) {
        this.world = Bukkit.getWorld("world"); // Ensure this is the correct world name

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    // Check if the coordinates are within the protected area
    private boolean isInProtectedArea(int x, int z) {
        return x >= Math.min(xx1, xx2) && x <= Math.max(xx1, xx2) &&
                z >= Math.min(zz1, zz2) && z <= Math.max(zz1, zz2);
    }

    private boolean isInNoSpawnArea(int x, int z) {
        return x >= Math.min(x1, x2) && x <= Math.max(x1, x2) &&
                z >= Math.min(z1, z2) && z <= Math.max(z1, z2);
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();

        if (!entity.getWorld().getName().equals(world.getName()) && !entity.getWorld().getName().equals("world_rpg")) {
            return;
        }

        if (isInNoSpawnArea(entity.getLocation().getBlockX(), entity.getLocation().getBlockZ())
                && (entity.getWorld().getName().equals(world.getName()) || entity.getWorld().getName().equals("world_rpg"))) {

            if (entity instanceof Monster) {
                event.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void onEntityLeash(PlayerLeashEntityEvent event) {
            Entity entity = event.getEntity();
        if (!entity.getWorld().getName().equals(world.getName()) && !entity.getWorld().getName().equals("world_rpg")) {
            return;
        }

            if (isInProtectedArea(entity.getLocation().getBlockX(), entity.getLocation().getBlockZ())&& !event.getPlayer().isOp()) {
                event.setCancelled(true);
            }

    }

    @EventHandler
    public void entitySpawn(EntityPlaceEvent event) {
        Entity entity = event.getEntity();
        if (!entity.getWorld().getName().equals(world.getName()) && !entity.getWorld().getName().equals("world_rpg")) {
            return;
        }

        if (isInProtectedArea(entity.getLocation().getBlockX(), entity.getLocation().getBlockZ())&& !Objects.requireNonNull(event.getPlayer()).isOp()) {
            event.setCancelled(true);
        }

    }


    @EventHandler
    public void raidEvent(RaidTriggerEvent event) {
        Player player = event.getPlayer();
        if (isInProtectedArea(player.getLocation().getBlockX(), player.getLocation().getBlockZ())) {
            player.sendMessage(ChatColor.RED +"...");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (!block.getWorld().getName().equals(world.getName()) && !block.getWorld().getName().equals("world_rpg")) {
            return;
        }

        if (isCrop(block) && isInProtectedArea(block.getLocation().getBlockX(), block.getLocation().getBlockZ())&& !player.isOp()) {
            event.setCancelled(true);
        }

        if (isInProtectedArea(block.getLocation().getBlockX(), block.getLocation().getBlockZ())&& !player.isOp()) {
                event.setCancelled(true);
        }
    }

    private boolean isCrop(Block block) {
        Material type = block.getType();
        return type == Material.WHEAT || type == Material.CARROTS || type == Material.POTATOES
                || type == Material.BEETROOTS;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (!block.getWorld().getName().equals(world.getName()) && !block.getWorld().getName().equals("world_rpg")) {
            return;
        }

        if (ANNOYING_BLOCKS.contains(event.getBlock().getType())) {
            return;
        }

        if (isInProtectedArea(block.getLocation().getBlockX(), block.getLocation().getBlockZ()) && !player.isOp()) {


                event.setCancelled(true);

        }
    }

    @EventHandler
    public void onRide(EntityMountEvent event) {
        if (!event.getEntity().getWorld().getName().equals(world.getName()) && !event.getEntity().getWorld().getName().equals("world_rpg")) {
            return;
        }


        if (event.getEntity() instanceof  Player player && !player.isOp() && isInProtectedArea(event.getEntity().getLocation().getBlockX(), event.getEntity().getLocation().getBlockZ())) {

            event.setCancelled(true);

        }
    }

    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        if (!event.getEntity().getWorld().getName().equals(world.getName()) && !event.getEntity().getWorld().getName().equals("world_rpg")) {
            return;
        }

        if (event.getRemover() instanceof  Player player && !player.isOp() && isInProtectedArea(event.getEntity().getLocation().getBlockX(), event.getEntity().getLocation().getBlockZ())) {

            event.setCancelled(true);

        }

    }

    @EventHandler
    public void onUnlead(PlayerUnleashEntityEvent event) {
        if (!event.getEntity().getWorld().getName().equals(world.getName()) && !event.getEntity().getWorld().getName().equals("world_rpg")) {
            return;
        }
        Entity entity = event.getEntity();
        if (isInProtectedArea(entity.getLocation().getBlockX(), entity.getLocation().getBlockZ())&& !event.getPlayer().isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFeed(EntityBreedEvent event) {
        if (!event.getEntity().getWorld().getName().equals(world.getName()) && !event.getEntity().getWorld().getName().equals("world_rpg")) {
            return;
        }
        Entity entity = event.getEntity();



        if (isInProtectedArea(entity.getLocation().getBlockX(), entity.getLocation().getBlockZ())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getPlayer().getWorld().getName().equals(world.getName()) && !event.getPlayer().getWorld().getName().equals("world_rpg")) {
            return;
        }

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
                        item == Material.BUCKET ||
                        item== Material.LEAD ||
                        item == Material.SADDLE)  && !player.isOp()) {
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
    public void onBlockBurn(BlockBurnEvent event) {
        if (!event.getBlock().getWorld().getName().equals(world.getName()) && !event.getBlock().getWorld().getName().equals("world_rpg")) {
            return;
        }
        Block block = event.getBlock();
        if (isInProtectedArea(block.getLocation().getBlockX(), block.getLocation().getBlockZ())) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onBlockBurn(BlockIgniteEvent event) {
        if (!event.getBlock().getWorld().getName().equals(world.getName()) && !event.getBlock().getWorld().getName().equals("world_rpg")) {
            return;
        }
        Block block = event.getBlock();
        if (isInProtectedArea(block.getLocation().getBlockX(), block.getLocation().getBlockZ())) {
            event.setCancelled(true);
        }

    }


    @EventHandler
    public void onPlayerInteractWithArmorStand(PlayerInteractAtEntityEvent event) {
        if (!event.getPlayer().getWorld().getName().equals(world.getName()) && !event.getPlayer().getWorld().getName().equals("world_rpg")) {
            return;
        }

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
        if (!event.getPlayer().getWorld().getName().equals(world.getName()) && !event.getPlayer().getWorld().getName().equals("world_rpg")) {
            return;
        }

        Player player = event.getPlayer();
        if (isInProtectedArea(event.getBlockClicked().getX(), event.getBlockClicked().getZ()) && !player.isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!event.getEntity().getWorld().getName().equals(world.getName()) && !event.getEntity().getWorld().getName().equals("world_rpg")) {
            return;
        }

        Entity entity = event.getEntity();

        // Only proceed if the entity is within the protected area
        if (isInProtectedArea(entity.getLocation().getBlockX(), entity.getLocation().getBlockZ())) {

            // Protect non-living entities or Allays
            if (!(entity instanceof LivingEntity) || entity.getType() == EntityType.ALLAY || entity.getType() == EntityType.ARMOR_STAND
                    || entity.getType() == EntityType.ITEM_FRAME
                    || entity.getType() == EntityType.GLOW_ITEM_FRAME
                    || entity instanceof Animals
                    || entity instanceof Fish
                    || entity instanceof WanderingTrader
                    || entity instanceof Villager
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

    @EventHandler
    public void onBlockChange(EntityChangeBlockEvent event) {
        if (!event.getEntity().getWorld().getName().equals(world.getName()) && !event.getEntity().getWorld().getName().equals("world_rpg")) {
            return;
        }

        if (event.getBlock().getType() == Material.FARMLAND && isInProtectedArea(event.getEntity().getLocation().getBlockX(), event.getEntity().getLocation().getBlockZ())) {
            event.setCancelled(true);
        }

        if (isInProtectedArea(event.getEntity().getLocation().getBlockX(), event.getEntity().getLocation().getBlockZ())
                && event.getEntity() instanceof Enderman)
        {
            event.setCancelled(true);
        }



    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (isInProtectedArea(event.getEntity().getLocation().getBlockX(), event.getEntity().getLocation().getBlockZ())) {
            event.blockList().clear();
        }
    }


    @EventHandler
    public void onEntityExplode(BlockExplodeEvent event) {
        if (isInProtectedArea(event.getBlock().getLocation().getBlockX(), event.getBlock().getLocation().getBlockZ())) {
            event.blockList().clear();
        }
    }

    @EventHandler
    public void onTradeSelect(PlayerTradeEvent event) {
        if (!event.getPlayer().getWorld().getName().equals(world.getName()) && !event.getPlayer().getWorld().getName().equals("world_rpg")) {
            return;
        }

        if (!isInProtectedArea(event.getPlayer().getLocation().getBlockX(), event.getPlayer().getLocation().getBlockZ())) return;

        Player player =event.getPlayer();
        AbstractVillager villager = event.getVillager();
        MerchantRecipe recipe = event.getTrade();
        player.giveExp(5);

    }

    @EventHandler
    public void onTame(EntityTameEvent event) {
        if (!event.getEntity().getWorld().getName().equals(world.getName())) {
            return;
        }

        if (!isInProtectedArea(event.getEntity().getLocation().getBlockX(), event.getEntity().getLocation().getBlockZ())) return;

        Entity entity = event.getEntity();
        AnimalTamer tamer = event.getOwner();

        if (tamer instanceof Player player) {

            if (!player.isOp()) {
                event.setCancelled(true);
                player.sendMessage("You are not allowed to tame this animal!");
            }
        }
    }



    @EventHandler
    public void signChange(SignChangeEvent event) {
        if (!event.getPlayer().getWorld().getName().equals(world.getName()) && !event.getPlayer().getWorld().getName().equals("world_rpg")) {
            return;
        }

        if (!event.getPlayer().isOp() && isInProtectedArea(event.getPlayer().getLocation().getBlockX(), event.getPlayer().getLocation().getBlockZ())) {

            event.setCancelled(true);

        }
    }

    // Helper method to check if the material is a trapdoor or fence
    private boolean isTrapdoorOrFence(Material material) {
        return material.name().endsWith("TRAPDOOR") || material.name().endsWith("FENCE") || material.name().endsWith("_DOOR");
    }


}
