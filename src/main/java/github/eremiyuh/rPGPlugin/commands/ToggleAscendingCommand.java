package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleAscendingCommand implements CommandExecutor {
    private final PlayerProfileManager profileManager;

    public ToggleAscendingCommand(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Ensure only players can use this command
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        UserProfile userProfile = profileManager.getProfile(player.getName());

        // Toggle the isAscending state
        boolean newAscendingState = !userProfile.isAscending();
        userProfile.setAscending(newAscendingState);

        // Notify the player of the change
        player.sendMessage("Ascension mode is now " + (newAscendingState ? "enabled." : "disabled."));

        return true;
    }
}
