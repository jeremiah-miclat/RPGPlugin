package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.ChunkManager;
import github.eremiyuh.rPGPlugin.profile.OwnedChunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class UntrustAllCommand implements CommandExecutor {

    private final ChunkManager chunkManager;

    public UntrustAllCommand(ChunkManager chunkManager) {
        this.chunkManager = chunkManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        if (args.length < 1) {
            player.sendMessage("Please specify a player to untrust from all chunks.");
            return false;
        }

        String playerNameToUntrust = args[0];
        List<OwnedChunk> ownedChunks = chunkManager.getOwnedChunksByOwner(player.getName());

        if (ownedChunks.isEmpty()) {
            player.sendMessage("You do not own any chunks.");
            return true;
        }

        boolean playerRemoved = false;

        for (OwnedChunk ownedChunk : ownedChunks) {
            if (ownedChunk.getTrustedPlayers().remove(playerNameToUntrust)) {
                playerRemoved = true;
                chunkManager.saveChunkData(); // Save changes after modifying trusted players
            }
        }

        if (playerRemoved) {
            player.sendMessage(playerNameToUntrust + " has been removed from all trusted players in your chunks.");
        } else {
            player.sendMessage(playerNameToUntrust + " is not trusted in any of your chunks.");
        }

        return true;
    }
}
