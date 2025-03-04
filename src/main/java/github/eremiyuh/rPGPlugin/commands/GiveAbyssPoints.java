package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveAbyssPoints implements CommandExecutor {

    private final PlayerProfileManager profileManager;

    public GiveAbyssPoints(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by players.");
            return true;
        }

        if (!(sender.isOp())) {
            sender.sendMessage("This command can only be executed by admins.");
            return true;
        }

        if ( !sender.getName().equals("Eremiyuh") ) {
            sender.sendMessage("Not allowed.");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage("Usage: /giveabysspoints <playername> <numberofpoints>");
            return true;
        }

        String playerName = args[0];
        int points;

        try {
            points = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("Please enter a valid number of points.");
            return true;
        }

        Player targetPlayer = Bukkit.getPlayer(playerName);
        if (targetPlayer == null) {
            sender.sendMessage("Player not found.");
            return true;
        }

        // Get the profile of the target player
        UserProfile targetProfile = profileManager.getProfile(playerName);
        if (targetProfile == null) {
            sender.sendMessage("No profile found for " + playerName);
            return true;
        }

        // Add the attribute points to the target player's profile
        targetProfile.setAbysspoints(targetProfile.getAbysspoints() + points);
        sender.sendMessage("Successfully gave " + points + " abyss points to " + playerName + ".");
        return true;
    }
}
