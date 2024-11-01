package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.ChunkManager;
import github.eremiyuh.rPGPlugin.profile.OwnedChunk;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class TrustAllCommand implements CommandExecutor {
    private final ChunkManager chunkManager;

    public TrustAllCommand(ChunkManager chunkManager) {
        this.chunkManager = chunkManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return true;
        }

        Player player = (Player) sender;

        // Check if the player provided a username
        if (args.length != 1) {
            player.sendMessage("Usage: /trustall <playername>");
            return true;
        }

        String playerToTrust = args[0];

        // Iterate over all owned chunks
        for (OwnedChunk ownedChunk : chunkManager.getOwnedChunksByOwner(player.getName())) {
            Chunk chunk = ownedChunk.getChunk();
            // Trust the specified player
            chunkManager.trustPlayer(player.getName(), chunk, playerToTrust);
        }

        player.sendMessage("Successfully trusted " + playerToTrust + " in all your chunks.");
        return true;
    }
}
