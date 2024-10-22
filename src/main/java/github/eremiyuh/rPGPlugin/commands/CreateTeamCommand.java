package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateTeamCommand implements CommandExecutor {

    private final PlayerProfileManager profileManager;

    public CreateTeamCommand(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Ensure the sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can create teams!");
            return false;
        }

        Player player = (Player) sender;
        String playerName = player.getName();

        // Get the player's profile
        UserProfile profile = profileManager.getProfile(playerName);

        // Check if the player already has a team
        if (profile.getTeam() != null && !profile.getTeam().isEmpty() && !profile.getTeam().equals("none")) {
            player.sendMessage(ChatColor.RED + "You are already in a team: " + profile.getTeam());
            return true;
        }

        // Create a new team with the player's name
        profile.setTeam(playerName);
        profileManager.saveProfile(playerName);  // Save the updated profile

        // Notify the player
        player.sendMessage(ChatColor.GREEN + "Team " + ChatColor.GOLD + playerName + ChatColor.GREEN + " has been created!");

        return true;
    }
}
