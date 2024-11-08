package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChangePassCommand implements CommandExecutor {
    private final PlayerProfileManager profileManager;

    public ChangePassCommand(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if sender has admin privileges
        if (!(sender.isOp())) {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }

        // Check if the correct arguments are provided
        if (args.length < 2) {
            sender.sendMessage("Usage: /changepass <playername> <newpassword>");
            return false;
        }

        String playerName = args[0];
        String newPassword = args[1];

        // Retrieve the player's profile
        UserProfile profile = profileManager.getProfile(playerName);
        if (profile == null) {
            sender.sendMessage("Player not found or does not have a profile.");
            return true;
        }

        // Update password and save profile
        profile.setPassword(newPassword);
        profileManager.saveProfile(playerName);

        sender.sendMessage("Password for " + playerName + " has been successfully updated.");
        return true;
    }
}
