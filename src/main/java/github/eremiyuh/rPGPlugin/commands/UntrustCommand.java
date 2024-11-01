package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.ChunkManager;
import github.eremiyuh.rPGPlugin.profile.OwnedChunk;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UntrustCommand implements CommandExecutor {

    private final ChunkManager chunkManager;

    public UntrustCommand(ChunkManager chunkManager) {
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
            player.sendMessage("Please specify a player to untrust.");
            return false;
        }

        String playerNameToUntrust = args[0];
        Chunk currentChunk = player.getLocation().getChunk();
        OwnedChunk ownedChunk = chunkManager.getOwnedChunk(currentChunk);

        if (ownedChunk == null) {
            player.sendMessage("This chunk is not claimed.");
            return true;
        }

        if (!ownedChunk.getOwner().equals(player.getName())) {
            player.sendMessage("You do not own this chunk.");
            return true;
        }

        if (ownedChunk.getTrustedPlayers().contains(playerNameToUntrust)) {
            chunkManager.untrustPlayer(player.getName(), currentChunk, playerNameToUntrust);
            player.sendMessage(playerNameToUntrust + " has been removed from trusted players in this chunk.");
        } else {
            player.sendMessage(playerNameToUntrust + " is not trusted in this chunk.");
        }

        return true;
    }
}
