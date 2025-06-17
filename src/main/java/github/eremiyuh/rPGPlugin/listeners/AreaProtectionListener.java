package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import io.papermc.paper.event.player.PlayerTradeEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.inventory.TradeSelectEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.raid.RaidEvent;
import org.bukkit.event.raid.RaidTriggerEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;

public class AreaProtectionListener implements Listener {

    private final World world;


    private final int xx1 = 292, zz1 = -293;
    private final int xx2 = -338, zz2 = 350;

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

            if (entity instanceof Monster || entity instanceof Phantom) {
                event.setCancelled(true);
            }
        }
    }


//    @EventHandler
//    public void onEntityLeash(PlayerLeashEntityEvent event) {
//            Entity entity = event.getEntity();
//        if (!entity.getWorld().getName().equals(world.getName()) && !entity.getWorld().getName().equals("world_rpg")) {
//            return;
//        }
//
//            if (isInProtectedArea(entity.getLocation().getBlockX(), entity.getLocation().getBlockZ())&& !event.getPlayer().isOp()) {
//                event.setCancelled(true);
//            }
//
//    }
//
//    @EventHandler
//    public void entitySpawn(EntityPlaceEvent event) {
//        Entity entity = event.getEntity();
//        if (entity instanceof Minecart || entity instanceof Boat) {
//            return;
//        }
//
//        if (!entity.getWorld().getName().equals(world.getName()) && !entity.getWorld().getName().equals("world_rpg")) {
//            return;
//        }
//
//        if (isInProtectedArea(entity.getLocation().getBlockX(), entity.getLocation().getBlockZ())&& !Objects.requireNonNull(event.getPlayer()).isOp()) {
//            event.setCancelled(true);
//        }
//
//    }
//
//
//    @EventHandler
//    public void raidEvent(RaidTriggerEvent event) {
//        Player player = event.getPlayer();
//        if (isInProtectedArea(player.getLocation().getBlockX(), player.getLocation().getBlockZ())) {
//            player.sendMessage(ChatColor.RED +"...");
//            event.setCancelled(true);
//        }
//    }
//
//    @EventHandler
//    public void onBlockBreak(BlockBreakEvent event) {
//        Player player = event.getPlayer();
//        Block block = event.getBlock();
//        if (!block.getWorld().getName().equals(world.getName()) && !block.getWorld().getName().equals("world_rpg") && !block.getWorld().getName().equals("world_nether")) {
//            return;
//        }
//
//        if (isCrop(block) && isInProtectedArea(block.getLocation().getBlockX(), block.getLocation().getBlockZ())&& !player.isOp()) {
//            event.setCancelled(true);
//        }
//
//        if (isInProtectedArea(block.getLocation().getBlockX(), block.getLocation().getBlockZ())&& !player.isOp()) {
//                event.setCancelled(true);
//        }
//    }
//
//    private boolean isCrop(Block block) {
//        Material type = block.getType();
//        return type == Material.WHEAT || type == Material.CARROTS || type == Material.POTATOES
//                || type == Material.BEETROOTS;
//    }
//
//    @EventHandler
//    public void onBlockPlace(BlockPlaceEvent event) {
//        Player player = event.getPlayer();
//        Block block = event.getBlock();
//
//
//        if (!block.getWorld().getName().equals(world.getName()) && !block.getWorld().getName().equals("world_rpg") && !block.getWorld().getName().equals("world_nether")) {
//            return;
//        }
//
//        if (ANNOYING_BLOCKS.contains(event.getBlock().getType())) {
//            return;
//        }
//
//        if (isInProtectedArea(block.getLocation().getBlockX(), block.getLocation().getBlockZ()) && !player.isOp()) {
//
//
//                event.setCancelled(true);
//
//        }
//    }
//
//    @EventHandler
//    public void onRide(EntityMountEvent event) {
//        if (event.getMount() instanceof Minecart || event.getMount() instanceof Boat || event.getMount() instanceof Animals) {
//            return;
//        }
//
//        if (!event.getEntity().getWorld().getName().equals(world.getName()) && !event.getEntity().getWorld().getName().equals("world_rpg")) {
//            return;
//        }
//
//
//        if (event.getEntity() instanceof  Player player && !player.isOp() && isInProtectedArea(event.getEntity().getLocation().getBlockX(), event.getEntity().getLocation().getBlockZ())) {
//
//            event.setCancelled(true);
//
//        }
//    }
//
//    @EventHandler
//    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
//        if (!event.getEntity().getWorld().getName().equals(world.getName()) && !event.getEntity().getWorld().getName().equals("world_rpg")  && !event.getEntity().getWorld().getName().equals("world_nether")) {
//            return;
//        }
//
//        if (event.getRemover() instanceof  Player player && !player.isOp() && isInProtectedArea(event.getEntity().getLocation().getBlockX(), event.getEntity().getLocation().getBlockZ())) {
//
//            event.setCancelled(true);
//
//        }
//
//    }
//
//    @EventHandler
//    public void onUnlead(PlayerUnleashEntityEvent event) {
//        if (!event.getEntity().getWorld().getName().equals(world.getName()) && !event.getEntity().getWorld().getName().equals("world_rpg")) {
//            return;
//        }
//        Entity entity = event.getEntity();
//        if (isInProtectedArea(entity.getLocation().getBlockX(), entity.getLocation().getBlockZ())&& !event.getPlayer().isOp()) {
//            event.setCancelled(true);
//        }
//    }
//
//    @EventHandler
//    public void onFeed(EntityBreedEvent event) {
//        if (!event.getEntity().getWorld().getName().equals(world.getName()) && !event.getEntity().getWorld().getName().equals("world_rpg")) {
//            return;
//        }
//        Entity entity = event.getEntity();
//
//
//
//        if (isInProtectedArea(entity.getLocation().getBlockX(), entity.getLocation().getBlockZ())) {
//            event.setCancelled(true);
//        }
//    }
//
//
//    @EventHandler
//    public void onRename(PlayerInteractAtEntityEvent event) {
//        if (!event.getPlayer().getWorld().getName().equals(world.getName())) {
//            return;
//        }
//        Player player = event.getPlayer();
//        Entity entity = event.getRightClicked();
//
//        ItemStack itemInHand = player.getInventory().getItemInMainHand();
//        if (itemInHand == null || itemInHand.getType() != Material.NAME_TAG) {
//            return;
//        }
//
//        if (!(entity instanceof Animals) && isInProtectedArea(entity.getLocation().getBlockX(), entity.getLocation().getBlockZ())) {
//            event.setCancelled(true);
//        }
//
//    }
//
//    @EventHandler
//    public void onPlayerInteract(PlayerInteractEvent event) {
//        if (!event.getPlayer().getWorld().getName().equals(world.getName()) && !event.getPlayer().getWorld().getName().equals("world_rpg")) {
//            return;
//        }
//
//        if (event.getClickedBlock()!= null && (isInProtectedArea(event.getClickedBlock().getLocation().getBlockX(), event.getClickedBlock().getLocation().getBlockZ()))) {
//            Player player = event.getPlayer();
//
//            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null) {
//                Material item = event.getItem().getType();
//                Block block = event.getClickedBlock();
//
//                // Allow all trapdoors, fence doors, and doors
//                if (block != null && isTrapdoorOrFence(block.getType())) {
//                    return; // Allow interaction, do not cancel
//                }
//
//                // Prevent using flint and steel, water, lava, or empty buckets if player is not op
//                if ((item == Material.FLINT_AND_STEEL ||
//                        item == Material.WATER_BUCKET ||
//                        item == Material.LAVA_BUCKET ||
//                        item == Material.ITEM_FRAME ||
//                        item == Material.GLOW_ITEM_FRAME ||
//                        item == Material.BUCKET ||
//                        item== Material.LEAD)  && !player.isOp()) {
//                    event.setCancelled(true);
//                }
//
//
//
//            }
//
//            if (event.getAction() == Action.LEFT_CLICK_BLOCK && event.getItem() != null) {
//
//                if ((event.getClickedBlock().getType() == Material.ITEM_FRAME || event.getClickedBlock().getType() == Material.GLOW_ITEM_FRAME) && !player.isOp()) {
//                    event.setCancelled(true);
//                }
//
//            }
//        }
//
//    }

    @EventHandler
    public void onSpawnEgg(PlayerInteractEvent event) {

        if (event.getHand() != EquipmentSlot.HAND) return; // Only check for main hand
        if (event.getItem() == null || (event.getItem().getType() != Material.WARDEN_SPAWN_EGG && event.getItem().getType() != Material.RAVAGER_SPAWN_EGG
                && event.getItem().getType() != Material.EVOKER_SPAWN_EGG
        )) return; // Only check for spawn eggs
        if (!event.getPlayer().getWorld().getName().equals("world_rpg")) {
            event.setCancelled(true);
            return;
        }
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();

        if (clickedBlock == null) {
            event.setCancelled(true);
            return;
        }

        Location location = Objects.requireNonNull(event.getClickedBlock()).getLocation();

        // Check for air 3 blocks above
        for (int i = 1; i <= 3; i++) {
            Block blockAbove = location.clone().add(0, i, 0).getBlock();
            if (blockAbove.getType() != Material.AIR) {
                event.setCancelled(true);
                player.sendMessage("§cNot enough space above to spawn!");
                return;
            }
        }

        // Check for air 2 blocks around x and z, excluding player's location
        for (int xOffset = -3; xOffset <= 3; xOffset++) {
            for (int zOffset = -3; zOffset <= 3; zOffset++) {
                Location checkLocation = location.clone().add(xOffset, 1, zOffset);
                if (!checkLocation.equals(player.getLocation())) { // Check if it's not the player's location
                    Block blockAround = checkLocation.getBlock();
                    if (blockAround.getType().isSolid()) {
                        event.setCancelled(true);
                        player.sendMessage("§cNot enough space around to spawn!");
                        return;
                    }
                }
            }
        }
    }

//    @EventHandler
//    public void onLightning(LightningStrikeEvent event) {
//        if (!event.getWorld().getName().equals(world.getName())) {
//            return;
//        }
//        if (isInProtectedArea(event.getLightning().getLocation().getBlockX(),event.getLightning().getLocation().getBlockZ())) {
//            event.setCancelled(true);
//        }
//    }
//
//
//
//
//    @EventHandler
//    public void onPlayerInteractWithArmorStand(PlayerInteractAtEntityEvent event) {
//        if (!event.getPlayer().getWorld().getName().equals(world.getName()) && !event.getPlayer().getWorld().getName().equals("world_rpg")) {
//            return;
//        }
//
//        if (isInProtectedArea(event.getRightClicked().getLocation().getBlockX(), event.getRightClicked().getLocation().getBlockZ())) {
//            Player player = event.getPlayer();
//            if (event.getRightClicked().getType() == EntityType.ARMOR_STAND && !player.isOp()) {
//                event.setCancelled(true);
//            }
//
//            if (event.getRightClicked().getType() == EntityType.ITEM_FRAME && !player.isOp()) {
//                event.setCancelled(true);
//            }
//
//            if (event.getRightClicked().getType() == EntityType.GLOW_ITEM_FRAME && !player.isOp()) {
//                event.setCancelled(true);
//            }
//
//            if (event.getRightClicked().getType() == EntityType.ALLAY && !player.isOp()) {
//                event.setCancelled(true);
//            }
//        }
//    }
//
//    @EventHandler
//    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
//        Player player = event.getPlayer();
//        ArmorStand armorStand = event.getRightClicked();
//        Chunk chunk = armorStand.getLocation().getChunk();
//
//        if (isInProtectedArea((int) event.getRightClicked().getX(), (int) event.getRightClicked().getZ()) && !player.isOp()) {
//            event.setCancelled(true);
//        }
//    }
//
//
//    @EventHandler
//    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
//        if (!event.getPlayer().getWorld().getName().equals(world.getName()) && !event.getPlayer().getWorld().getName().equals("world_rpg") && !event.getPlayer().getWorld().getName().equals("world_nether")) {
//            return;
//        }
//
//        Player player = event.getPlayer();
//        if (isInProtectedArea(event.getBlockClicked().getX(), event.getBlockClicked().getZ()) && !player.isOp()) {
//            event.setCancelled(true);
//        }
//    }
//
//    @EventHandler
//    public void onEntityDamage(EntityDamageEvent event) {
//        if (!event.getEntity().getWorld().getName().equals(world.getName())
////                && !event.getEntity().getWorld().getName().equals("world_rpg")
//
//        ) {
//            return;
//        }
//
//        Entity entity = event.getEntity();
//
//        // Only proceed if the entity is within the protected area
//        if (isInProtectedArea(entity.getLocation().getBlockX(), entity.getLocation().getBlockZ())) {
//
//            // Protect non-living entities or Allays
//            if (!(entity instanceof LivingEntity) || entity.getType() == EntityType.ALLAY || entity.getType() == EntityType.ARMOR_STAND
//                    || entity.getType() == EntityType.ITEM_FRAME
//                    || entity.getType() == EntityType.GLOW_ITEM_FRAME
//                    || entity instanceof Animals
//                    || entity instanceof Fish
//                    || entity instanceof WanderingTrader
//                    || entity instanceof Villager
//            ) {
//                if (event instanceof EntityDamageByEntityEvent) {
//                    Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
//
//                    if (damager instanceof Player && ((Player) damager).isOp()) {
//                        return; // Allow OP player to damage protected entities
//                    }
//
//                }
//                // Cancel damage for protected entities if not by an OP player
//                event.setCancelled(true);
//            }
//        }
//    }
//
//    @EventHandler
//    public void onBlockChange(EntityChangeBlockEvent event) {
//        if (!event.getEntity().getWorld().getName().equals(world.getName()) && !event.getEntity().getWorld().getName().equals("world_rpg")) {
//            return;
//        }
//
//        if (event.getBlock().getType() == Material.FARMLAND && isInProtectedArea(event.getEntity().getLocation().getBlockX(), event.getEntity().getLocation().getBlockZ())) {
//            event.setCancelled(true);
//        }
//
//        if (isInProtectedArea(event.getEntity().getLocation().getBlockX(), event.getEntity().getLocation().getBlockZ())
//                && event.getEntity() instanceof Enderman)
//        {
//            event.setCancelled(true);
//        }
//
//
//
//    }
//
//    @EventHandler
//    public void onEntityExplode(EntityExplodeEvent event) {
//        if (!event.getEntity().getWorld().getName().equals(world.getName()) && !event.getEntity().getWorld().getName().equals("world_rpg") && !event.getEntity().getWorld().getName().equals("world_nether")) {
//            return;
//        }
//        if (isInProtectedArea(event.getEntity().getLocation().getBlockX(), event.getEntity().getLocation().getBlockZ())) {
//            event.blockList().clear();
//        }
//    }
//
//
//    @EventHandler
//    public void onEntityExplode(BlockExplodeEvent event) {
//        if (!event.getBlock().getWorld().getName().equals(world.getName()) && !event.getBlock().getWorld().getName().equals("world_rpg") && !event.getBlock().getWorld().getName().equals("world_nether")) {
//            return;
//        }
//        if (isInProtectedArea(event.getBlock().getLocation().getBlockX(), event.getBlock().getLocation().getBlockZ())) {
//            event.blockList().clear();
//        }
//    }
//
//    @EventHandler
//    public void onTradeSelect(PlayerTradeEvent event) {
//        if (!event.getPlayer().getWorld().getName().equals(world.getName()) && !event.getPlayer().getWorld().getName().equals("world_rpg")) {
//            return;
//        }
//
//        if (!isInProtectedArea(event.getPlayer().getLocation().getBlockX(), event.getPlayer().getLocation().getBlockZ())) return;
//
//        Player player =event.getPlayer();
//        AbstractVillager villager = event.getVillager();
//        MerchantRecipe recipe = event.getTrade();
//        player.giveExp(5);
//
//    }

//    @EventHandler
//    public void onTame(EntityTameEvent event) {
//        if (!event.getEntity().getWorld().getName().equals(world.getName())) {
//            return;
//        }
//
//        if (!isInProtectedArea(event.getEntity().getLocation().getBlockX(), event.getEntity().getLocation().getBlockZ())) return;
//
//        Entity entity = event.getEntity();
//        AnimalTamer tamer = event.getOwner();
//
//        if (tamer instanceof Player player) {
//
//            if (!player.isOp()) {
//                event.setCancelled(true);
//                player.sendMessage("You are not allowed to tame this animal!");
//            }
//        }
//    }



//    @EventHandler
//    public void signChange(SignChangeEvent event) {
//        try {
//            Player player = event.getPlayer();
//            World world = player.getWorld();
//
//            // Check if the player is in the correct world
//            if (!world.getName().equals(this.world.getName()) && !world.getName().equals("world_rpg")) {
//                return;
//            }
//
//            // Check if the player is OP or not in a protected area
//            if (!player.isOp() && isInProtectedArea(player.getLocation().getBlockX(), player.getLocation().getBlockZ())) {
//                // Check if the player is within the allowed coordinates
//                if (!(player.getLocation().getBlockX() >= -15 && player.getLocation().getBlockX() <= 15 &&
//                        player.getLocation().getBlockZ() >= -80 && player.getLocation().getBlockZ() <= -50)) {
//                    event.setCancelled(true);
//                    return;
//                }
//            }
//        } catch (Exception e) {
//            // Log the error for debugging
//            Bukkit.getLogger().log(Level.SEVERE, "Error handling sign change event:", e);
//        }
//    }
//
//    // Helper method to check if the material is a trapdoor or fence
//    private boolean isTrapdoorOrFence(Material material) {
//        return material.name().endsWith("TRAPDOOR") || material.name().endsWith("FENCE") || material.name().endsWith("_DOOR");
//    }


}
