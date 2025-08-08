package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import io.papermc.paper.event.player.PlayerTradeEvent;
import net.md_5.bungee.api.ChatColor;
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
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;

public class AreaProtectionListener implements Listener {

    private final World world;


    private final int xx1 = -1903, zz1 = -125;
    private final int xx2 = -1862, zz2 = -84;

    private final int x1 = -150, z1 = 150;
    private final int x2 = 150, z2 = -150;

    private final int xxx1 = -1928, zzz1 = -150;
    private final int xxx2 = -1837, zzz2 = -59;

    private static final Set<Material> ANNOYING_BLOCKS;

    static {
        ANNOYING_BLOCKS = new HashSet<>();
        ANNOYING_BLOCKS.add(Material.SHORT_GRASS);
        ANNOYING_BLOCKS.add(Material.TALL_GRASS);
        ANNOYING_BLOCKS.add(Material.DEAD_BUSH);
    }

    private static final Set<EntityType> TRACKED_ANIMALS = EnumSet.of(
            EntityType.COW,
            EntityType.PIG,
            EntityType.SHEEP,
            EntityType.CHICKEN,
            EntityType.HORSE,
            EntityType.DONKEY,
            EntityType.MULE,
            EntityType.LLAMA,
            EntityType.RABBIT,
            EntityType.GOAT,
            EntityType.CAMEL,
            EntityType.FROG,
            EntityType.SNIFFER
    );

    private static final int MAX_PER_TYPE_PER_3x3_CHUNKS = 25;

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

    private boolean isInNoSpawnArea2(int x, int z) {
        return x >= Math.min(xxx1, xxx2) && x <= Math.max(xxx1, xxx2) &&
                z >= Math.min(zzz1, zzz2) && z <= Math.max(zzz1, zzz2);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        EntityType type = event.getEntityType();


        if (!TRACKED_ANIMALS.contains(type)) return;

        Location loc = event.getLocation();
        World world = loc.getWorld();
        int chunkX = loc.getChunk().getX();
        int chunkZ = loc.getChunk().getZ();

        int count = 0;

        // Loop over 3x3 chunks centered on the spawn chunk
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                Chunk chunk = world.getChunkAt(chunkX + dx, chunkZ + dz);
                for (@NotNull Entity entity : chunk.getEntities()) {
                    if (entity.getType() == type) {
                        count++;
                        if (count >= MAX_PER_TYPE_PER_3x3_CHUNKS) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onCreatureSpawnOnSpawn(CreatureSpawnEvent event) {
        Entity entity = event.getEntity();

        if (isInNoSpawnArea2(entity.getLocation().getBlockX(), entity.getLocation().getBlockZ())
                && entity.getWorld().getName().equals("world")) {

            if (entity instanceof Monster || entity instanceof Phantom || entity instanceof Chicken) {

                // Optional: check all spawn reasons
                CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();

                switch (reason) {
                    case NATURAL:
                    case BREEDING:
                    case SPAWNER:
                    case SPAWNER_EGG:
                    case DISPENSE_EGG:
                    case EGG: // baby chicken from egg
                    case COMMAND:
                    case CUSTOM:
                    case DEFAULT:
                    case REINFORCEMENTS:
                        event.setCancelled(true);
                        return;
                    default:
                        break; // allow others if necessary
                }
            }
        }
    }




    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();

        if (isInNoSpawnArea(entity.getLocation().getBlockX(), entity.getLocation().getBlockZ())
                && entity.getWorld().getName().equals("world_rpg")) {

            if (entity instanceof Monster || entity instanceof Phantom) {
                event.setCancelled(true);
            }
        }

        if (isInNoSpawnArea2(entity.getLocation().getBlockX(), entity.getLocation().getBlockZ())
                && entity.getWorld().getName().equals("world")) {

            if (entity instanceof Monster || entity instanceof Phantom || entity instanceof Chicken) {
                event.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void onEntityLeash(PlayerLeashEntityEvent event) {
            Entity entity = event.getEntity();
        if (!entity.getWorld().getName().equals(world.getName())) {
            return;
        }

            if (isInProtectedArea(entity.getLocation().getBlockX(), entity.getLocation().getBlockZ())&& !event.getPlayer().isOp()) {
                event.setCancelled(true);
            }

    }
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
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (!block.getWorld().getName().equals(world.getName())) {
            return;
        }

        if (isCrop(block) && isInProtectedArea(block.getLocation().getBlockX(), block.getLocation().getBlockZ())&& !player.isOp()) {
            event.setCancelled(true);
        }

        if (isInProtectedArea(block.getX(), block.getZ()) && !player.isOp()) {
            Material type = block.getType();

            // Allow breaking Shulker Boxes
            if (!type.name().endsWith("SHULKER_BOX")) {
                player.sendMessage(ChatColor.RED + "No permission to break blocks here.");
                event.setCancelled(true);
            }
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
        Location loc = block.getLocation();

        if (!block.getWorld().getName().equals(world.getName())) {
            return;
        }


        if (isInProtectedArea(loc.getBlockX(), loc.getBlockZ()) && !player.isOp()) {
            Block below = loc.clone().subtract(0, 1, 0).getBlock();
            Material placedType = block.getType();

            boolean isPlacingOnEndStone = below.getType() == Material.END_STONE;
            boolean isShulkerBox = placedType.name().endsWith("SHULKER_BOX"); // handles all colors

            if (!(isPlacingOnEndStone && isShulkerBox)) {
                player.sendMessage(ChatColor.RED + "You can only place shulker boxes on End Stone here.");
                event.setCancelled(true);
            }
        }
    }
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
    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        if (!event.getEntity().getWorld().getName().equals(world.getName())) {
            return;
        }

        if (event.getRemover() instanceof  Player player && !player.isOp() && isInProtectedArea(event.getEntity().getLocation().getBlockX(), event.getEntity().getLocation().getBlockZ())) {

            event.setCancelled(true);

        }

    }
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
    @EventHandler
    public void onRename(PlayerInteractAtEntityEvent event) {
        if (!event.getPlayer().getWorld().getName().equals(world.getName())) {
            return;
        }
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand == null || itemInHand.getType() != Material.NAME_TAG) {
            return;
        }

        if (!(entity instanceof Animals) && isInProtectedArea(entity.getLocation().getBlockX(), entity.getLocation().getBlockZ())) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getPlayer().getWorld().getName().equals(world.getName())) {
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
                        item== Material.LEAD)  && !player.isOp()) {
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
    public void onSpawnEgg(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        ItemStack item = event.getItem();
        if (item == null || (item.getType() != Material.WARDEN_SPAWN_EGG &&
                item.getType() != Material.RAVAGER_SPAWN_EGG &&
                item.getType() != Material.EVOKER_SPAWN_EGG &&
                item.getType() != Material.WITHER_SPAWN_EGG &&
                item.getType() != Material.VILLAGER_SPAWN_EGG)) return;

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            event.setCancelled(true);
            return;
        }

        Location location = clickedBlock.getLocation();

        if (!event.getPlayer().isOp() && isInProtectedArea(location.getBlockX(), location.getBlockZ())) {
            event.setCancelled(true);
            return;
        }

        if (!event.getPlayer().getWorld().getName().equals("world_rpg")) {
            return;
        }

        Player player = event.getPlayer();

        if (location.getY() <= 56) {
            event.setCancelled(true);
            player.sendMessage("§cNot enough space above to spawn!");
            return;
        }

        for (int i = 1; i <= 3; i++) {
            Block blockAbove = location.clone().add(0, i, 0).getBlock();
            if (blockAbove.getType() != Material.AIR) {
                event.setCancelled(true);
                player.sendMessage("§cNot enough space above to spawn!");
                return;
            }
        }

        for (int xOffset = -3; xOffset <= 3; xOffset++) {
            for (int zOffset = -3; zOffset <= 3; zOffset++) {
                Location checkLocation = location.clone().add(xOffset, 1, zOffset);
                if (!checkLocation.equals(player.getLocation())) {
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

    @EventHandler
    public void onLightning(LightningStrikeEvent event) {
        if (!event.getWorld().getName().equals(world.getName())) {
            return;
        }
        if (isInProtectedArea(event.getLightning().getLocation().getBlockX(),event.getLightning().getLocation().getBlockZ())) {
            event.setCancelled(true);
        }
    }
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
    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        if (!event.getPlayer().getWorld().getName().equals(world.getName())) {
            return;
        }

        Player player = event.getPlayer();
        if (isInProtectedArea(event.getBlockClicked().getX(), event.getBlockClicked().getZ()) && !player.isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!event.getEntity().getWorld().getName().equals(world.getName())
//                && !event.getEntity().getWorld().getName().equals("world_rpg")

        ) {
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
        if (!event.getEntity().getWorld().getName().equals(world.getName())) {
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
        if (!event.getEntity().getWorld().getName().equals(world.getName())) {
            return;
        }
        if (isInProtectedArea(event.getEntity().getLocation().getBlockX(), event.getEntity().getLocation().getBlockZ())) {
            event.blockList().clear();
        }
    }


    @EventHandler
    public void onEntityExplode(BlockExplodeEvent event) {
        if (!event.getBlock().getWorld().getName().equals(world.getName())) {
            return;
        }
        if (isInProtectedArea(event.getBlock().getLocation().getBlockX(), event.getBlock().getLocation().getBlockZ())) {
            event.blockList().clear();
        }
    }
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



    @EventHandler
    public void signChange(SignChangeEvent event) {
        try {
            Player player = event.getPlayer();
            World world = player.getWorld();

            // Check if the player is in the correct world
            if (!world.getName().equals(this.world.getName())) {
                return;
            }

            // Check if the player is OP or not in a protected area
            if (!player.isOp() && isInProtectedArea(player.getLocation().getBlockX(), player.getLocation().getBlockZ())) {
                player.sendMessage(ChatColor.RED + "No permission");
                event.setCancelled(true);
            }
        } catch (Exception e) {
            // Log the error for debugging
            Bukkit.getLogger().log(Level.SEVERE, "Error handling sign change event:", e);
        }
    }

    // Helper method to check if the material is a trapdoor or fence
    private boolean isTrapdoorOrFence(Material material) {
        return material.name().endsWith("TRAPDOOR") || material.name().endsWith("FENCE") || material.name().endsWith("_DOOR");
    }


}
