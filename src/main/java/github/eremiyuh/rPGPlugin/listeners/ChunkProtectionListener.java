package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.manager.ChunkManager;
import github.eremiyuh.rPGPlugin.profile.OwnedChunk;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class ChunkProtectionListener implements Listener {
    private final ChunkManager chunkManager;

    public ChunkProtectionListener(ChunkManager chunkManager) {
        this.chunkManager = chunkManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = event.getBlock().getChunk();
        OwnedChunk ownedChunk = chunkManager.getOwnedChunk(chunk);
        if (ownedChunk != null) {  // Only proceed if the chunk is claimed by someone
            if (!chunkManager.isOwner(player.getName(), chunk) && !chunkManager.isTrusted(player.getName(), chunk)) {
//                event.setCancelled(true);
                player.sendMessage("You don't have permission to break blocks in this chunk.");
            }
        }
    }

    // Add similar event handlers for BlockPlaceEvent, EntityDamageByEntityEvent, etc.
}

