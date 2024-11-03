package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamRemoveCommand implements CommandExecutor {

    private final PlayerProfileManager profileManager;

    public TeamRemoveCommand(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player leader = (Player) sender;
        if (args.length != 1) {
            leader.sendMessage(ChatColor.RED + "Usage: /teamremove <playername>");
            return false;
        }

        String targetPlayerName = args[0];
        Player targetPlayer = Bukkit.getPlayer(targetPlayerName);

        if (targetPlayer == null) {
            leader.sendMessage(ChatColor.RED + "Player " + targetPlayerName + " is not online or does not exist.");
            return true;
        }

        // Get the team profiles of both the leader and the target player
        UserProfile leaderProfile = profileManager.getProfile(leader.getName());
        UserProfile targetProfile = profileManager.getProfile(targetPlayer.getName());

        if (leaderProfile == null || targetProfile == null) {
            leader.sendMessage(ChatColor.RED + "Error: Could not retrieve team information.");
            return true;
        }

        String leaderTeam = leaderProfile.getTeam();
        String targetTeam = targetProfile.getTeam();

        // Check if the target is on the same team
        if (leaderTeam == null || !leaderTeam.equals(targetTeam)) {
            leader.sendMessage(ChatColor.RED + "Player " + targetPlayerName + " is not on your team.");
            return true;
        }

        // Check if the leader is the team leader or owner
        if (!isTeamLeaderOrOwner(leader, leaderProfile)) {
            leader.sendMessage(ChatColor.RED + "You must be the team leader or owner to remove players from the team.");
            return true;
        }

        if (leaderProfile.removeTeamMember(targetPlayerName)) {
            // Remove the target player from the team
            targetProfile.setTeam("none"); // Remove the player from the team
            profileManager.saveProfile(targetPlayer.getName()); // Save the changes

            // Notify both players
            leader.sendMessage(ChatColor.GREEN + "You have removed " + targetPlayerName + " from the team.");
            targetPlayer.sendMessage(ChatColor.RED + "You have been removed from the team by " + leader.getName() + ".");
        }

        return true;
    }

    private boolean isTeamLeaderOrOwner(Player player, UserProfile profile) {
        // Assume the leader is tracked in the team data (this can be modified based on your team structure)
        String teamLeader = profile.getTeam(); // This assumes you have a method to get the team leader.
        return player.getName().equals(teamLeader);
    }
}
