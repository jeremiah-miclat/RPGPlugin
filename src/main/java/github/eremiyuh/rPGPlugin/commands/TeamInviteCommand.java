package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class TeamInviteCommand implements CommandExecutor {

    private final PlayerProfileManager profileManager;

    public TeamInviteCommand(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {


        // Check if the sender is a player
        if (!(sender instanceof Player inviter)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        UserProfile inviterProfile = profileManager.getProfile(inviter.getName());

        // Ensure a player name is provided
        if (args.length != 1) {
            inviter.sendMessage(ChatColor.RED + "Usage: /teaminvite <player>");
            return true;
        }

        // Get the invitee player
        Player invitee = Bukkit.getPlayer(args[0]);
        assert invitee != null;
        UserProfile inviteeProfile = profileManager.getProfile(invitee.getName());

        if (inviter.getName().equals(invitee.getName())) {
            inviter.sendMessage(ChatColor.RED + "You can not invite yourself.");
            return true;
        }

        if (invitee == null || !invitee.isOnline()) {
            inviter.sendMessage(ChatColor.RED + "Player " + args[0] + " is not online.");
            return true;
        }

        // Check if the inviter has a profile

        if (inviterProfile == null) {
            inviter.sendMessage(ChatColor.RED + "Your profile could not be found.");
            return true;
        }

        if (!Objects.equals(inviteeProfile.getTeam(), "none")) {
            inviter.sendMessage(ChatColor.RED + "Player already had a team");
            return true;
        }

        // Check if the invitee is already invited
        if (inviterProfile.getTeamInvites().contains(invitee.getName())) {
            inviter.sendMessage(ChatColor.YELLOW + invitee.getName() + " has already been invited to your team.");
            return true;
        }

        // Add the invitee to the inviter's invite list
        inviterProfile.getTeamInvites().add(invitee.getName());
        profileManager.saveProfile(inviter.getName()); // Save inviter's profile to persist the invite

        // Notify both players
        inviter.sendMessage(ChatColor.GREEN + "You have invited " + invitee.getName() + " to your team.");
        invitee.sendMessage(ChatColor.YELLOW + inviter.getName() + " has invited you to join their team!");

        return true;
    }
}
