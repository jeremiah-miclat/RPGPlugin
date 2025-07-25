package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.manager.VaultManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamVaultCommand implements CommandExecutor {

    private final VaultManager vaultManager;
    private final PlayerProfileManager profileManager;

    public TeamVaultCommand(VaultManager vaultManager, PlayerProfileManager profileManager) {
        this.vaultManager = vaultManager;
        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the command sender is a player
        if (!(sender instanceof Player playerOpener)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        UserProfile openerProfile = profileManager.getProfile(playerOpener.getName());
        String vaultOwnerName = openerProfile.getTeam();

        if (vaultOwnerName.equals("none") || vaultOwnerName.isEmpty()) {
            sender.sendMessage("You are not in a team.");
            return true;
        }

        if (vaultOwnerName.equals(sender.getName())) {
            sender.sendMessage("You are the team leader. Use /vault");
            return true;
        }

        UserProfile ownerProfile = profileManager.getProfile(vaultOwnerName);

        if (!ownerProfile.isShareVault()) {
            sender.sendMessage("Team lead's vaults not shared");
            return true;
        }

        Player owner = Bukkit.getPlayer(vaultOwnerName);

        // Ensure a vault number is provided
        if (args.length != 1) {
            playerOpener.sendMessage("Usage: /vault <number>");
            return true;
        }

        try {
            int vaultNumber = Integer.parseInt(args[0]);

            // Check if the vault number is within the valid range
            if (vaultNumber >= 1 && vaultNumber <= vaultManager.getMaxVaults()) {
                vaultManager.openVault(playerOpener, vaultNumber,vaultOwnerName);
                playerOpener.sendMessage("Opening vault " + vaultNumber + "...");
            } else {
                playerOpener.sendMessage("Vault number must be between 1 and " + vaultManager.getMaxVaults() + ".");
            }

        } catch (NumberFormatException e) {
            playerOpener.sendMessage("Please provide a valid vault number.");
        }

        return true;
    }
}
