package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamLeaveCommand implements CommandExecutor {

    private final PlayerProfileManager profileManager;

    public TeamLeaveCommand(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        // Retrieve the player's profile
        UserProfile profile = profileManager.getProfile(player.getName());
        if (profile == null) {
            player.sendMessage(ChatColor.RED + "Your profile could not be found.");
            return true;
        }

        // Check if the player is part of a team
        if (profile.getTeam() == null || profile.getTeam().equalsIgnoreCase("none")) {
            player.sendMessage(ChatColor.YELLOW + "You are not currently part of a team.");
            return true;
        }

        // Store the name of the current team for notification
        String teamName = profile.getTeam();

        // Set the player's team to none
        profile.setTeam("none");
        profileManager.saveProfile(player.getName()); // Save the updated profile

        // Notify the player
        player.sendMessage(ChatColor.GREEN + "You have successfully left the team: " + teamName + ".");

        return true;
    }
}
