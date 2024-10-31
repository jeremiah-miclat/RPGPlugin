package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.ChunkManager;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
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


    public ChunkCommand(ChunkManager chunkManager, PlayerProfileManager profileManager) {
        this.chunkManager = chunkManager;
        this.profileManager = profileManager;
    }

    public boolean chunkHasOwnedChunkNearby(Chunk chunk, Player player) {
        String playerName = player.getName(); // Assume chunkManager has a method to get owner

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


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            UserProfile userProfile = profileManager.getProfile(player.getName());
            double userClaimPoints = userProfile.getClaimPoints();
            userProfile.setClaimPoints(userClaimPoints-1);
            Chunk chunk = player.getLocation().getChunk();
            OwnedChunk ownedChunk = chunkManager.getOwnedChunk(chunk);

            switch (args[0]) {

                case "claim":
                    if (chunkHasOwnedChunkNearby(chunk, player)) {
                        player.sendMessage("Can't claim a chunk near owned chunk");
                        break;
                    }

                    if (userClaimPoints>=1) {




                        if (ownedChunk==null) {
                            chunkManager.claimChunk(player, player.getLocation().getChunk());
                        } else {
                            player.sendMessage("Chunk already owned");
                        }
                    } else {
                        player.sendMessage("You don't have claim points");
                    }
                    break;
//                case "trust":
//                    // Add logic to trust another player
//                    break;
//                case "untrust":
//                    // Add logic to remove a trusted player
//                    break;
                default:
                    player.sendMessage("Unknown command.");
                    break;
            }
        }
        return true;
    }
}
