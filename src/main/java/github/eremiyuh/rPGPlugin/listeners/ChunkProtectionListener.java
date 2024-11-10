package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.manager.ChunkManager;
import github.eremiyuh.rPGPlugin.methods.ChunkBorderBlueVisualizer;
import github.eremiyuh.rPGPlugin.methods.ChunkBorderRedVisualizer;
import github.eremiyuh.rPGPlugin.profile.OwnedChunk;
import org.bukkit.Chunk;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrowableProjectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;

public class ChunkProtectionListener implements Listener {
    private final ChunkManager chunkManager;
    private final ChunkBorderBlueVisualizer chunkBorderBlueVisualizer;
    private final ChunkBorderRedVisualizer chunkBorderRedVisualizer;


    public ChunkProtectionListener(ChunkManager chunkManager, ChunkBorderBlueVisualizer chunkBorderBlueVisualizer, ChunkBorderRedVisualizer chunkBorderRedVisualizer) {
        this.chunkManager = chunkManager;
        this.chunkBorderBlueVisualizer = chunkBorderBlueVisualizer;
        this.chunkBorderRedVisualizer = chunkBorderRedVisualizer;
    }

    // Additional EventHandler for damaging entities
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            Chunk chunk = event.getEntity().getLocation().getChunk();
            OwnedChunk ownedChunk = chunkManager.getOwnedChunk(chunk);
            if (ownedChunk != null && !chunkManager.isOwner(player.getName(), chunk) && !chunkManager.isTrusted(player.getName(), chunk)) {
                event.setCancelled(true); // Prevent entity damage
                chunkHasOwnedChunkNearbyVisualizer(chunk, player);
            }
            if (chunkHasOwnedChunkNearby(chunk, player)) {
                chunkHasOwnedChunkNearbyVisualizer(chunk, player);
                event.setCancelled(true); // Prevent block placement
            }
        }

        if (event.getDamager() instanceof Projectile projectile) {
            if (projectile instanceof Arrow && projectile.getShooter() instanceof Player player) {
                Chunk chunk = event.getEntity().getLocation().getChunk();
                OwnedChunk ownedChunk = chunkManager.getOwnedChunk(chunk);
                if (ownedChunk != null && !chunkManager.isOwner(player.getName(), chunk) && !chunkManager.isTrusted(player.getName(), chunk)) {
                    event.setCancelled(true); // Prevent entity damage
                    chunkHasOwnedChunkNearbyVisualizer(chunk, player);
                }
                if (chunkHasOwnedChunkNearby(chunk, player)) {
                    chunkHasOwnedChunkNearbyVisualizer(chunk, player);
                    event.setCancelled(true); // Prevent block placement
                }
            }

        }

        if (event.getDamager() instanceof Projectile projectile) {
            if (projectile instanceof ThrowableProjectile && projectile.getShooter() instanceof Player player) {
                Chunk chunk = event.getEntity().getLocation().getChunk();
                OwnedChunk ownedChunk = chunkManager.getOwnedChunk(chunk);
                if (ownedChunk != null && !chunkManager.isOwner(player.getName(), chunk) && !chunkManager.isTrusted(player.getName(), chunk)) {
                    event.setCancelled(true); // Prevent entity damage
                    chunkHasOwnedChunkNearbyVisualizer(chunk, player);
                }
                if (chunkHasOwnedChunkNearby(chunk, player)) {
                    chunkHasOwnedChunkNearbyVisualizer(chunk, player);
                    event.setCancelled(true); // Prevent block placement
                }
            }

        }
    }

    // Additional EventHandler for interacting with blocks or items
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.hasBlock()) { // Check if the interaction involves a block
            Chunk chunk = Objects.requireNonNull(event.getClickedBlock()).getChunk();
            OwnedChunk ownedChunk = chunkManager.getOwnedChunk(chunk);
            if (ownedChunk != null && !chunkManager.isOwner(player.getName(), chunk) && !chunkManager.isTrusted(player.getName(), chunk)) {
                event.setCancelled(true); // Prevent interaction
                chunkHasOwnedChunkNearbyVisualizer(chunk, player);
            }
            if (chunkHasOwnedChunkNearby(chunk, player)) {
                chunkHasOwnedChunkNearbyVisualizer(chunk,player);
                event.setCancelled(true); // Prevent block placement
            }
        }

    }
    // Add similar event handlers for BlockPlaceEvent, EntityDamageByEntityEvent, rightclicking or interacting etc.

    public boolean chunkHasOwnedChunkNearby(Chunk chunk, Player player) {
        String playerName = player.getName();

        int currentX = chunk.getX();
        int currentZ = chunk.getZ();
        String worldName = chunk.getWorld().getName();

        // Check surrounding chunks in a 3x3 grid
        for (int xOffset = -1; xOffset <= 1; xOffset++) {
            for (int zOffset = -1; zOffset <= 1; zOffset++) {
                // Skip the current chunk
                if (xOffset == 0 && zOffset == 0) continue;

                // Get the neighboring chunk
                Chunk nearbyChunk = chunk.getWorld().getChunkAt(currentX + xOffset, currentZ + zOffset);
                String nearbyOwner = chunkManager.getOwner(nearbyChunk);
//                if (nearbyOwner != null &&  player.getName().equals(nearbyOwner)) {
//                    chunkBorderBlueVisualizer.showChunkBoundary(player, nearbyChunk);
//                }
                // Check if the neighboring chunk is owned by a different player
                if (nearbyOwner != null && !nearbyOwner.equals(playerName)) {

                    return true; // Found a nearby chunk owned by another player
                }
            }
        }
        return false; // No nearby chunks owned by a different player
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

}

