package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class TeamInviteAcceptCommand implements CommandExecutor {

    private final PlayerProfileManager profileManager;

    public TeamInviteAcceptCommand(PlayerProfileManager profileManager) {
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

        // Ensure the player provided a name for the inviter
        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /teaminviteaccept <inviterName>");
            return true;
        }

        String inviterName = args[0];
        UserProfile inviterProfile = profileManager.getProfile(inviterName);

        if (inviterProfile == null) {
            player.sendMessage(ChatColor.RED + "The inviter's profile could not be found.");
            return true;
        }

        // Retrieve the player's profile
        UserProfile profile = profileManager.getProfile(player.getName());
        if (profile == null) {
            player.sendMessage(ChatColor.RED + "Your profile could not be found.");
            return true;
        }

        // Check if the player is already part of a team
        if (!Objects.equals(profile.getTeam(), "none") && !inviterProfile.isTeamMember(player.getName())) {
            player.sendMessage(ChatColor.YELLOW + "You are already part of a team. Leave your current team first.");
            return true;
        }



        if (!inviterProfile.getTeamInvites().contains(player.getName())) {
            player.sendMessage(ChatColor.RED + "You have not been invited to join " + inviterName + "'s team.");
            return true;
        }

        // Join the inviter's team
        String teamName = inviterProfile.getTeam(); // Assuming the inviter is already in a team
        if (teamName == null || teamName.equalsIgnoreCase("none")) {
            player.sendMessage(ChatColor.RED + inviterName + " is not currently in a team.");
            return true;
        }

        // Set the player's team to the inviter's team

        if (inviterProfile.addTeamMember(player.getName())){
            profile.setTeam(teamName);
            profileManager.saveProfile(player.getName()); // Save the updated profile
            profileManager.saveProfile(inviterName);
            // Notify the player and the inviter
            player.sendMessage(ChatColor.GREEN + "You have successfully joined " + inviterName + "'s team: " + teamName + ".");
            Player inviter = player.getServer().getPlayer(inviterName);
            if (inviter != null) {
                inviter.sendMessage(ChatColor.GREEN + player.getName() + " has accepted your invitation and joined your team.");
            }

            // Remove the invite from the inviter's list
            inviterProfile.getTeamInvites().remove(player.getName());
        } else {
            Player inviter = player.getServer().getPlayer(inviterName);
            player.sendMessage("Team is full");
            assert inviter != null;
            inviter.sendMessage(player.getName() + " can not join because your team is full");
        }






        return true;
    }
}
