package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.ChunkManager;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TrustCommand implements CommandExecutor {
    private final ChunkManager chunkManager;

    public TrustCommand(ChunkManager chunkManager) {
        this.chunkManager = chunkManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length < 1) { // Changed from 2 to 1
                player.sendMessage("Usage: /trust <playername>");
                return false;
            }

            String targetPlayerName = args[0]; // Use args[0] for player name
            Chunk chunk = player.getLocation().getChunk();

            if (chunkManager.isOwner(player.getName(), chunk)) {
                chunkManager.trustPlayer(player.getName(), chunk, targetPlayerName);
                player.sendMessage("You have trusted " + targetPlayerName + " in this chunk.");
            } else {
                player.sendMessage("You don't own this chunk.");
            }
        }
        return true;
    }
}
