package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.VaultManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VaultCommand implements CommandExecutor {

    private final VaultManager vaultManager;

    public VaultCommand(VaultManager vaultManager) {
        this.vaultManager = vaultManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the command sender is a player
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        // Ensure a vault number is provided
        if (args.length != 1) {
            player.sendMessage("Usage: /vault <number>");
            return true;
        }

        try {
            int vaultNumber = Integer.parseInt(args[0]);

            // Check if the vault number is within the valid range
            if (vaultNumber >= 1 && vaultNumber <= vaultManager.getMaxVaults()) {
                vaultManager.openVault(player, vaultNumber, player.getName());
                player.sendMessage("Opening vault " + vaultNumber + "...");
            } else {
                player.sendMessage("Vault number must be between 1 and " + vaultManager.getMaxVaults() + ".");
            }

        } catch (NumberFormatException e) {
            player.sendMessage("Please provide a valid vault number.");
        }

        return true;
    }
}
