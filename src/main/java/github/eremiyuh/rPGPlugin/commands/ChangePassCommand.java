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

        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can change their password.");
            return true;
        }

        Player player = (Player) sender;

        // Require 2 arguments: /changepass <newpassword> <newpassword>
        if (args.length < 2) {
            player.sendMessage("Usage: /changepass <newpassword> <newpassword>");
            return false;
        }

        String newPassword = args[0];
        String confirmPassword = args[1];

        if (!newPassword.equals(confirmPassword)) {
            player.sendMessage("Passwords do not match. Try again.");
            return true;
        }

        // Get the profile for the player
        UserProfile profile = profileManager.getProfile(player.getName());
        if (profile == null) {
            player.sendMessage("Your profile could not be found.");
            return true;
        }

        // Set and save the new password
        profile.setPassword(newPassword);
        profileManager.saveProfile(player.getName());

        player.sendMessage("Your password has been successfully updated.");
        return true;
    }
}
