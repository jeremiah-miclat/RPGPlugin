package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class VaultAccessCommand implements CommandExecutor {

    private final PlayerProfileManager profileManager;

    public VaultAccessCommand(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length < 2 || args.length > 3) {
            player.sendMessage("Usage: /vaultAccess <add|remove|list> <vaultNumber> [playerName]");
            return true;
        }

        String action = args[0].toLowerCase();
        int vaultNumber;

        try {
            vaultNumber = Integer.parseInt(args[1]);
            if (vaultNumber < 1 || vaultNumber > 5) {
                player.sendMessage("Vault number must be between 1 and 5.");
                return true;
            }
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid vault number.");
            return true;
        }

        UserProfile profile = profileManager.getProfile(player.getName());
        List<String> vaultList = switch (vaultNumber) {
            case 1 -> profile.getVault1Players();
            case 2 -> profile.getVault2Players();
            case 3 -> profile.getVault3Players();
            case 4 -> profile.getVault4Players();
            case 5 -> profile.getVault5Players();
            default -> null;
        };

        if (vaultList == null) {
            vaultList = new ArrayList<>();
        }

        switch (action) {
            case "add" -> {
                if (args.length != 3) {
                    player.sendMessage("Usage: /vaultAccess add <vaultNumber> <playerName>");
                    return true;
                }
                String targetPlayer = args[2];
                if (!vaultList.contains(targetPlayer)) {
                    vaultList.add(targetPlayer);
                    player.sendMessage("✅ " + targetPlayer + " has been granted access to vault " + vaultNumber + ".");
                } else {
                    player.sendMessage("⚠️ " + targetPlayer + " already has access.");
                }
            }
            case "remove" -> {
                if (args.length != 3) {
                    player.sendMessage("Usage: /vaultAccess remove <vaultNumber> <playerName>");
                    return true;
                }
                String targetPlayer = args[2];
                if (vaultList.remove(targetPlayer)) {
                    player.sendMessage("❌ " + targetPlayer + "'s access to vault " + vaultNumber + " has been removed.");
                } else {
                    player.sendMessage("⚠️ " + targetPlayer + " does not have access.");
                }
            }
            case "list" -> {
                if (args.length != 2) {
                    player.sendMessage("Usage: /vaultAccess list <vaultNumber>");
                    return true;
                }
                if (vaultList.isEmpty()) {
                    player.sendMessage("📦 Vault " + vaultNumber + " has no players with access.");
                } else {
                    player.sendMessage("📋 Vault " + vaultNumber + " access list:");
                    for (String name : vaultList) {
                        player.sendMessage(" - " + name);
                    }
                }
            }
            default -> {
                player.sendMessage("Invalid action. Use add, remove, or list.");
                return true;
            }
        }

        // Save back to profile
        switch (vaultNumber) {
            case 1 -> profile.setVault1Players(vaultList);
            case 2 -> profile.setVault2Players(vaultList);
            case 3 -> profile.setVault3Players(vaultList);
            case 4 -> profile.setVault4Players(vaultList);
            case 5 -> profile.setVault5Players(vaultList);
        }

        return true;
    }
}
