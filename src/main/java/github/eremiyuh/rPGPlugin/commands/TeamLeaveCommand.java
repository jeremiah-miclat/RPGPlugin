package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Bukkit;
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
        UserProfile teamOwnerProfile = profileManager.getProfile(teamName);

        // If the player is the team owner, disband the team
        if (teamOwnerProfile != null && teamOwnerProfile == profile) {
            for (String memberName : teamOwnerProfile.getTeamMembers()) {
                UserProfile memberProfile = profileManager.getProfile(memberName);
                if (memberProfile != null) {
                    // Update team status
                    memberProfile.setTeam("none");
                    profileManager.saveProfile(memberName); // Save each updated profile

                    // Notify online members
                    Player teamMember = Bukkit.getPlayer(memberName);
                    if (teamMember != null && teamMember.isOnline()) {
                        teamMember.sendMessage(ChatColor.RED + "The team " + teamName + " has been disbanded by the owner.");
                    }
                }
            }
            // Clear team owner's own profile and notify them
            teamOwnerProfile.getTeamMembers().clear();
            teamOwnerProfile.setTeam("none");
            profileManager.saveProfile(player.getName());

            player.sendMessage(ChatColor.GREEN + "You have disbanded the team: " + teamName + ".");
            return true;
        }

        // If the player is a team member, remove them from the team
        if (teamOwnerProfile != null && teamOwnerProfile.isTeamMember(player.getName())) {
            if (teamOwnerProfile.removeTeamMember(player.getName())) {
                profile.setTeam("none");
                profileManager.saveProfile(player.getName()); // Save the updated profile

                // Notify the player and online team members
                player.sendMessage(ChatColor.GREEN + "You have successfully left the team: " + teamName + ".");
                for (String memberName : teamOwnerProfile.getTeamMembers()) {
                    Player teamMember = Bukkit.getPlayer(memberName);
                    if (teamMember != null && teamMember.isOnline()) {
                        teamMember.sendMessage(ChatColor.YELLOW + player.getName() + " has left the team.");
                    }
                }

                // Notify the team owner if they are online
                Player teamOwner = Bukkit.getPlayer(teamOwnerProfile.getPlayerName());
                if (teamOwner != null && teamOwner.isOnline()) {
                    teamOwner.sendMessage(ChatColor.RED + player.getName() + " has left the team.");
                }
            }
            return true;
        }

        player.sendMessage(ChatColor.RED + "An error occurred while attempting to leave the team.");
        return true;
    }
}
