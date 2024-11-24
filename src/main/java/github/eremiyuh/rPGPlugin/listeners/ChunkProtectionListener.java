package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.manager.ChunkManager;
import github.eremiyuh.rPGPlugin.manager.ShopsManager;
import github.eremiyuh.rPGPlugin.methods.ChunkBorderBlueVisualizer;
import github.eremiyuh.rPGPlugin.methods.ChunkBorderRedVisualizer;
import github.eremiyuh.rPGPlugin.profile.OwnedChunk;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrowableProjectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChunkProtectionListener implements Listener {
    private final ChunkManager chunkManager;
    private final ChunkBorderBlueVisualizer chunkBorderBlueVisualizer;
    private final ChunkBorderRedVisualizer chunkBorderRedVisualizer;
    private final ShopsManager shopsManager;

    // Caching chunk ownership data for quicker access
    private final Map<Chunk, OwnedChunk> cachedOwnedChunks = new HashMap<>();
    private final Map<Chunk, String> cachedOwners = new HashMap<>();

    public ChunkProtectionListener(ChunkManager chunkManager, ChunkBorderBlueVisualizer chunkBorderBlueVisualizer, ChunkBorderRedVisualizer chunkBorderRedVisualizer, ShopsManager shopsManager) {
        this.chunkManager = chunkManager;
        this.chunkBorderBlueVisualizer = chunkBorderBlueVisualizer;
        this.chunkBorderRedVisualizer = chunkBorderRedVisualizer;
        this.shopsManager = shopsManager;
    }

    // Helper method to get cached owned chunk
    private OwnedChunk getCachedOwnedChunk(Chunk chunk) {
        // Check cache first
        OwnedChunk ownedChunk = cachedOwnedChunks.get(chunk);
        if (ownedChunk == null) {
            ownedChunk = chunkManager.getOwnedChunk(chunk);
            if (ownedChunk != null) {
                cachedOwnedChunks.put(chunk, ownedChunk);
            }
        }
        return ownedChunk;
    }

    // Helper method to get cached chunk owner
    private String getCachedOwner(Chunk chunk) {
        // Check cache first
        String owner = cachedOwners.get(chunk);
        if (owner == null) {
            owner = chunkManager.getOwner(chunk);
            if (owner != null) {
                cachedOwners.put(chunk, owner);
            }
        }
        return owner;
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

    // Handle player block interaction (place or break)
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();


        if (event.hasBlock()) { // Check if the interaction involves a block
            Block block = event.getClickedBlock();
            assert block != null;

            Chunk chunk = block.getChunk();
            if (isInProtectedChunk(chunk, player)) {
                event.setCancelled(true); // Prevent interaction

                if (block.getType().name().endsWith("_WALL_SIGN")) {
                    boolean isWallSign = block.getType().name().endsWith("_WALL_SIGN");
                    boolean isRightClick = event.getAction() == Action.RIGHT_CLICK_BLOCK;
                    boolean isHoldingPaper = player.getInventory().getItemInMainHand().getType() == Material.PAPER;
                    boolean isShop = shopsManager.isShop(Objects.requireNonNull(getAttachedBlock(block)).getLocation());

                    if ((isWallSign && isRightClick && isHoldingPaper && isShop)) return;
                }

                chunkHasOwnedChunkNearbyVisualizer(chunk, player);

            }
        }
    }

    // Handle block place events to prevent block placement in protected chunks
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = event.getBlock().getChunk();
        if (isInProtectedChunk(chunk, player)) {
            event.setCancelled(true); // Prevent block placement
            chunkHasOwnedChunkNearbyVisualizer(chunk, player);
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
        OwnedChunk ownedChunk = getCachedOwnedChunk(chunk);
        return ownedChunk != null && !chunkManager.isOwner(player.getName(), chunk) && !chunkManager.isTrusted(player.getName(), chunk);
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
