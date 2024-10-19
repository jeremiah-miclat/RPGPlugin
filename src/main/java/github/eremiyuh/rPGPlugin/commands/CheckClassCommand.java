package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerClassManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckClassCommand implements CommandExecutor {
    private final PlayerClassManager playerClassManager;

    // Constructor that accepts PlayerClassManager
    public CheckClassCommand(PlayerClassManager playerClassManager) {
        this.playerClassManager = playerClassManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Ensure the sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can check their class!");
            return true;
        }

        Player player = (Player) sender;

        // Check if the player has selected a class using PlayerClassManager
        String playerClass = playerClassManager.loadPlayerClass(player);
        if (playerClass != null) {
            player.sendMessage("Your current class is: " + playerClass);
        } else {
            player.sendMessage("You haven't selected a class yet.");
        }

        return true;
    }
}
