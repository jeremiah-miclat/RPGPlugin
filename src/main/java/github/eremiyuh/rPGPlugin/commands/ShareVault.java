package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShareVault implements CommandExecutor {
    private final PlayerProfileManager profileManager;

    public ShareVault(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        UserProfile profile = profileManager.getProfile(player.getName());

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /shareVault < on or off >");
            return false;
        }

        String option = args[0].toLowerCase();
        switch (option) {
            case "on":
                setShareVaultStatus(player, profile, true);
                break;
            case "off":
                setShareVaultStatus(player, profile, false);
                break;
            default:
                player.sendMessage(ChatColor.RED + "Invalid option. Use /shareVault < on or off >");
        }

        return true;
    }

    private void setShareVaultStatus(Player player, UserProfile profile, boolean status) {
        profile.setShareVault(status);
        String statusMessage = status ? "enabled" : "disabled";
        player.sendMessage(ChatColor.GREEN + "Vault sharing with team has been " + statusMessage + "!");
    }
}
