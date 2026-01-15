package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PremiumSetCommand implements CommandExecutor {

    private final PlayerProfileManager profileManager;

    public PremiumSetCommand(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        // Security check
        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "Unauthorized");
            return true;
        }

        // Usage check
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /setPremiumStatus <playerName> <on|off>");
            return true;
        }

        String targetName = args[0];
        String option = args[1].toLowerCase();

        UserProfile profile = profileManager.getProfile(targetName);
        if (profile == null) {
            player.sendMessage(ChatColor.RED + "Player profile not found.");
            return true;
        }

        switch (option) {
            case "on":
                setPremiumStatus(player, profile, targetName, true);
                break;

            case "off":
                setPremiumStatus(player, profile, targetName, false);
                break;

            default:
                player.sendMessage(ChatColor.RED + "Invalid option. Use on or off.");
        }

        return true;
    }

    private void setPremiumStatus(Player sender, UserProfile profile, String targetName, boolean status) {
        profile.setIsPremium(status);
        String statusMessage = status ? "enabled" : "disabled";
        sender.sendMessage(ChatColor.GREEN + "Premium status for " + targetName + " has been " + statusMessage + "!");
    }
}

