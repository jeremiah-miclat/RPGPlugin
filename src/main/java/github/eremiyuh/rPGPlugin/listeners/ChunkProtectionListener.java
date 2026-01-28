package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.manager.ChunkManager;
import github.eremiyuh.rPGPlugin.manager.ShopsManager;
import github.eremiyuh.rPGPlugin.methods.ChunkBorderBlueVisualizer;
import github.eremiyuh.rPGPlugin.methods.ChunkBorderRedVisualizer;
import github.eremiyuh.rPGPlugin.profile.OwnedChunk;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;

public class ChunkProtectionListener implements Listener {
    private final ChunkManager chunkManager;
    private final ChunkBorderBlueVisualizer chunkBorderBlueVisualizer;
    private final ChunkBorderRedVisualizer chunkBorderRedVisualizer;
    private final ShopsManager shopsManager;
    private static final int PROTECTED_LEVEL = -264;

    public ChunkProtectionListener(ChunkManager chunkManager, ChunkBorderBlueVisualizer chunkBorderBlueVisualizer, ChunkBorderRedVisualizer chunkBorderRedVisualizer, ShopsManager shopsManager) {
        this.chunkManager = chunkManager;
        this.chunkBorderBlueVisualizer = chunkBorderBlueVisualizer;
        this.chunkBorderRedVisualizer = chunkBorderRedVisualizer;
        this.shopsManager = shopsManager;
    }


    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof Creeper creeper) {
            Chunk chunk = creeper.getChunk();
            if (isInProtectedChunkFromCreeper(chunk)) {
                event.blockList().removeIf(block -> block.getY() > PROTECTED_LEVEL);
            }
        }


//        if (Objects.requireNonNull(event.getLocation().getWorld()).getEnvironment() == World.Environment.NORMAL && !event.getLocation().getWorld().getName().contains("resource")) {
//            event.blockList().removeIf(block -> block.getY() > SEA_LEVEL);
//        }
    }


    // Handle entity damage events (player vs entity or player vs projectile)
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            Chunk chunk = event.getEntity().getLocation().getChunk();
            if (isInProtectedChunk(chunk, player)) {
                event.setCancelled(true); // Prevent entity damage
                chunkHasOwnedChunkNearbyVisualizer(chunk, player);
            }
        }

        if (event.getDamager() instanceof Projectile projectile && projectile.getShooter() instanceof Player player) {
            Chunk chunk = event.getEntity().getLocation().getChunk();
            if (isInProtectedChunk(chunk, player)) {
                event.setCancelled(true); // Prevent entity damage
                chunkHasOwnedChunkNearbyVisualizer(chunk, player);
            }
        }
    }

    @EventHandler
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        Player player = event.getPlayer();
        ArmorStand armorStand = event.getRightClicked();
        Chunk chunk = armorStand.getLocation().getChunk();

        if (isInProtectedChunk(chunk, player)) {
            event.setCancelled(true); // Prevent manipulation of the armor stand
            chunkHasOwnedChunkNearbyVisualizer(chunk, player);
        }
    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.hasBlock()) { // Check if the interaction involves a block
            Block block = event.getClickedBlock();
            assert block != null;

            BlockState state = block.getState();
            if (state instanceof Chest) {
                Chest chest = (Chest) state;
                if (chest.hasMetadata("seller")) {
                    String ownerName =chest.getMetadata("seller").getFirst().asString();
                    if (!event.getPlayer().getName().equals(ownerName)) {
                        event.setCancelled(true);
                    }
                }
            }

            if (state instanceof Barrel) {
                Barrel barrel = (Barrel) state;
                if (barrel.hasMetadata("seller")) {
                    String ownerName =barrel.getMetadata("seller").getFirst().asString();
                    if (!event.getPlayer().getName().equals(ownerName)) {
                        event.setCancelled(true);
                    }
                }
            }

            Chunk chunk = block.getChunk();
            if (isInProtectedChunk(chunk, player)) {
                event.setCancelled(true); // Prevent interaction

                if (block.getType().name().endsWith("_WALL_SIGN")) {
                    boolean isWallSign = block.getType().name().endsWith("_WALL_SIGN");
                    boolean isRightClick = event.getAction() == Action.RIGHT_CLICK_BLOCK;
                    boolean isShop = shopsManager.isShop(Objects.requireNonNull(getAttachedBlock(block)).getLocation());

                    if ((isWallSign && isRightClick && isShop)) return;
                }

                chunkHasOwnedChunkNearbyVisualizer(chunk, player);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Chunk chunk = block.getChunk();

        // Check if the block being placed is a piston or sticky piston
        if (block.getType() == Material.PISTON || block.getType() == Material.STICKY_PISTON || block.getType() == Material.TNT) {

            // Iterate through neighboring chunks in a 1-radius around the chunk where the piston is being placed
            for (int xOffset = -1; xOffset <= 1; xOffset++) {
                for (int zOffset = -1; zOffset <= 1; zOffset++) {
                    Chunk nearbyChunk = chunk.getWorld().getChunkAt(chunk.getX() + xOffset, chunk.getZ() + zOffset);

                    // Skip the chunk the piston is being placed in
                    if (nearbyChunk.equals(chunk)) continue;

                    // Check if a neighboring chunk is owned by someone else
                    String owner = chunkManager.getOwner(nearbyChunk);
                    if (owner != null && !owner.equals(player.getName())) {
                        // If the player is not trusted in the neighboring chunk, cancel the event
                        if (!chunkManager.isTrusted(player.getName(), nearbyChunk)) {
                            event.setCancelled(true);
                            chunkHasOwnedChunkNearbyVisualizer(chunk, player);
                            player.sendActionBar("You cannot place this near protected chunks owned by others.");
                            return; // Exit the method once the event is canceled
                        }
                    }
                }
            }
        }

        // Check if the current chunk is protected and the player doesn't have permission to build
        if (isInProtectedChunk(chunk, player)) {
            event.setCancelled(true);
            chunkHasOwnedChunkNearbyVisualizer(chunk, player);
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        Block blockClicked = event.getBlockClicked(); // The block the player clicked when emptying the bucket
        Chunk chunk = blockClicked.getChunk(); // The chunk where the player is trying to empty the bucket

        // Check surrounding chunks in a 1-block radius of the clicked block's chunk
        for (int xOffset = -1; xOffset <= 1; xOffset++) {
            for (int zOffset = -1; zOffset <= 1; zOffset++) {
                // Get the surrounding chunk
                Chunk surroundingChunk = blockClicked.getWorld().getChunkAt(chunk.getX() + xOffset, chunk.getZ() + zOffset);

                // If the surrounding chunk is protected and the player is not allowed to interact, cancel the event
                if (isInProtectedChunk(surroundingChunk, player)) {
                    event.setCancelled(true); // Cancel the event
                    player.sendActionBar("You cannot empty the bucket near a protected area.");
                    return; // Exit once the event is cancelled
                }
            }
        }
    }

    // Handle block break events to prevent block breaking in protected chunks
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = event.getBlock().getChunk();
        if (isInProtectedChunk(chunk, player)) {
            event.setCancelled(true); // Prevent block breaking
            chunkHasOwnedChunkNearbyVisualizer(chunk, player);
        }
    }

    // Helper method to check if a chunk is protected (owned by someone else or the player is not trusted)
    private boolean isInProtectedChunk(Chunk chunk, Player player) {
        OwnedChunk ownedChunk = chunkManager.getOwnedChunk(chunk);
        return ownedChunk != null && !chunkManager.isOwner(player.getName(), chunk) && !chunkManager.isTrusted(player.getName(), chunk);
    }

    private boolean isInProtectedChunkFromCreeper(Chunk chunk) {
        OwnedChunk ownedChunk = chunkManager.getOwnedChunk(chunk);
        return ownedChunk != null;
    }

    public void chunkHasOwnedChunkNearbyVisualizer(Chunk chunk, Player player) {
        String playerName = player.getName();

        int currentX = chunk.getX();
        int currentZ = chunk.getZ();
        String worldName = chunk.getWorld().getName();

        // Check surrounding chunks in a 9x9 grid, including the center chunk
        for (int xOffset = -4; xOffset <= 4; xOffset++) {
            for (int zOffset = -4; zOffset <= 4; zOffset++) {
                // Get the neighboring chunk (including the center chunk)
                Chunk nearbyChunk = chunk.getWorld().getChunkAt(currentX + xOffset, currentZ + zOffset);
                String nearbyOwner = chunkManager.getOwner(nearbyChunk);

                // Check if the neighboring chunk is owned by the player
                if (nearbyOwner != null && player.getName().equals(nearbyOwner)) {
                    chunkBorderBlueVisualizer.showChunkBoundary(player, nearbyChunk);
                }
                // Check if the neighboring chunk is owned by a different player
                else if (nearbyOwner != null && !nearbyOwner.equals(playerName)) {
                    chunkBorderRedVisualizer.showChunkBoundary(player, nearbyChunk);
                }
            }
        }
    }

    private Block getAttachedBlock(Block signBlock) {
        if (!(signBlock.getState() instanceof Sign)) {
            return null; // Not a sign
        }

        // Check if it's a wall sign or a standing sign
        if (signBlock.getType().name().contains("WALL_SIGN")) {
            // Wall signs are attached to the block they face
            return signBlock.getRelative(((org.bukkit.block.data.type.WallSign) signBlock.getBlockData()).getFacing().getOppositeFace());
        } else if (signBlock.getType().name().contains("SIGN")) {
            // Standing signs are usually placed on the block below them
            return signBlock.getRelative(org.bukkit.block.BlockFace.DOWN);
        }
        return null;
    }
}
