package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleBossIndicatorCommand implements CommandExecutor {

    private final PlayerProfileManager profileManager;

    public ToggleBossIndicatorCommand(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Ensure the sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        UserProfile userProfile = profileManager.getProfile(player.getName());

        // Toggle the boss indicator state
        boolean currentIndicatorState = userProfile.isBossIndicator();
        userProfile.setBossIndicator(!currentIndicatorState);

        // Provide feedback to the player
        if (userProfile.isBossIndicator()) {
            player.sendMessage("Boss health indicator enabled.");
        } else {
            player.sendMessage("Boss health indicator disabled.");
        }

        return true;
    }
}

