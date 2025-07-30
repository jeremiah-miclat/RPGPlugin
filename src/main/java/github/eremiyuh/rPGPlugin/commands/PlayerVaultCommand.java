package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.manager.VaultManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class PlayerVaultCommand implements CommandExecutor {

    private final VaultManager vaultManager;
    private final PlayerProfileManager profileManager;
    Set<String> admins = Set.of("Eremiyuh", "DogWannaEat");

    public PlayerVaultCommand(VaultManager vaultManager, PlayerProfileManager profileManager) {
        this.vaultManager = vaultManager;
        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length != 2) {
            player.sendMessage("Usage: /playerVault <ownerName> <vaultNumber>");
            return true;
        }

        String ownerName = args[0];
        int vaultNumber;

        try {
            vaultNumber = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("Vault number must be a valid number.");
            return true;
        }

        if (vaultNumber < 1 || vaultNumber > vaultManager.getMaxVaults()) {
            player.sendMessage("Vault number must be between 1 and " + vaultManager.getMaxVaults() + ".");
            return true;
        }

        UserProfile ownerProfile = profileManager.getProfile(ownerName);
        if (ownerProfile == null) {
            player.sendMessage("Vault owner not found.");
            return true;
        }

        List<String> accessList;
        switch (vaultNumber) {
            case 1 -> accessList = ownerProfile.getVault1Players();
            case 2 -> accessList = ownerProfile.getVault2Players();
            case 3 -> accessList = ownerProfile.getVault3Players();
            case 4 -> accessList = ownerProfile.getVault4Players();
            case 5 -> accessList = ownerProfile.getVault5Players();
            default -> {
                player.sendMessage("Vault access check failed.");
                return true;
            }
        }

        if (!accessList.contains(player.getName()) && !admins.contains(player.getName())) {
            player.sendMessage("You don't have permission to access vault " + vaultNumber + " of " + ownerName + ".");
            return true;
        }

        vaultManager.openVault(player, vaultNumber, ownerName);
        player.sendMessage("Opening vault " + vaultNumber + " of " + ownerName + "...");
        return true;
    }
}
