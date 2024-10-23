package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PVPStatusCommand implements CommandExecutor {

    private final PlayerProfileManager profileManager;

    public PVPStatusCommand( PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        UserProfile profile = profileManager.getProfile(player.getName());

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /pvpstatus <on|off>");
            return false;
        }

        String option = args[0].toLowerCase();
        switch (option) {
            case "on":
                setPvPStatus(player, profile, true);
                break;
            case "off":
                setPvPStatus(player, profile, false);
                break;
            default:
                player.sendMessage(ChatColor.RED + "Invalid option. Use /pvpstatus <on|off>");
        }

        return true;
    }

    private void setPvPStatus(Player player, UserProfile profile, boolean status) {
        profile.setPvpEnabled(status); // Set PvP status using the profile's method
        String statusMessage = status ? "enabled" : "disabled";
        player.sendMessage(ChatColor.GREEN + "PvP has been " + statusMessage + "!");
    }
}
