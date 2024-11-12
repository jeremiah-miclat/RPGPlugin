package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.ChunkManager;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.methods.ChunkBorderBlueVisualizer;
import github.eremiyuh.rPGPlugin.methods.ChunkBorderRedVisualizer;
import github.eremiyuh.rPGPlugin.profile.OwnedChunk;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChunkCommand implements CommandExecutor {
    private final ChunkManager chunkManager;
    private final PlayerProfileManager profileManager;
    private final ChunkBorderBlueVisualizer chunkBorderBlueVisualizer;
    private final ChunkBorderRedVisualizer chunkBorderRedVisualizer;

    public ChunkCommand(ChunkManager chunkManager, PlayerProfileManager profileManager, ChunkBorderBlueVisualizer chunkBorderBlueVisualizer, ChunkBorderRedVisualizer chunkBorderRedVisualizer) {
        this.chunkManager = chunkManager;
        this.profileManager = profileManager;
        this.chunkBorderBlueVisualizer = chunkBorderBlueVisualizer;
        this.chunkBorderRedVisualizer = chunkBorderRedVisualizer;
    }


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





    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (!player.getWorld().getName().equals("world")
                || !player.getWorld().getName().equals("world_nether")
                || !player.getWorld().getName().equals("world_end")
            ) {
                player.sendMessage("Can not claim chunks on this world");
                return true;
            }

            UserProfile userProfile = profileManager.getProfile(player.getName());
            double userClaimPoints = userProfile.getClaimPoints();

            Chunk chunk = player.getLocation().getChunk();
            OwnedChunk ownedChunk = chunkManager.getOwnedChunk(chunk);

            switch (args[0]) {

                case "claim":
                   if (chunkHasOwnedChunkNearby(chunk,player)) {
                       chunkHasOwnedChunkNearbyVisualizer(chunk,player);
                       player.sendMessage("There's a nearby owned chunk");
                       break;
                   }
                   if (userClaimPoints>1) {
                       if (ownedChunk == null) {
                           userProfile.setClaimPoints(userClaimPoints-1);
                           profileManager.saveProfile(userProfile.getPlayerName());
                       }
                       chunkManager.claimChunk(player,chunk);
                       chunkHasOwnedChunkNearbyVisualizer(chunk,player);
                   } else {
                       player.sendMessage("No claim points :(");
                   }
                    break;
                case "check":
                    chunkHasOwnedChunkNearbyVisualizer(chunk,player);
                    break;
                case "unclaim":
                    if (ownedChunk != null && ownedChunk.getOwner().equals(player.getName())) {
                        chunkManager.unclaimChunk(player, chunk);
                        userProfile.setClaimPoints(userProfile.getClaimPoints() + 1);
                        profileManager.saveProfile(userProfile.getPlayerName());
                        chunkHasOwnedChunkNearbyVisualizer(chunk, player);
                    } else {
                        player.sendMessage("You cannot unclaim this chunk.");
                    }
                    break;
                default:
                    player.sendMessage("Unknown command.");
                    break;
            }
        }
        return true;
    }


}
