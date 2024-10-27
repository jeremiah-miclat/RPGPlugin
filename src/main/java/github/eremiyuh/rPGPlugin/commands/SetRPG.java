package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetRPG implements CommandExecutor {

    private final PlayerProfileManager profileManager;

    public SetRPG(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        UserProfile profile = profileManager.getProfile(player.getName());

        if (args.length != 1) {
            player.sendMessage("Usage: /setRPG <on OR off>");
            return true;
        }

        String rpg = args[0].toLowerCase();

        // Check if the player can select a new element
        if (!profile.canSwitchRPG()) {
            player.sendMessage("You cannot switch RPGStatus, it has 10 hr cooldown.");
            return true;
        }

        // Validate the chosen element and set it
        switch (rpg) {
            case "off", "on":
                profile.setRPG(rpg);
                profile.setLastRpgSwitchTime(System.currentTimeMillis());
                player.sendMessage("You have turned rpg " + rpg);
                break;
            default:
                player.sendMessage("Invalid input. Enter ON or OFF");
                return true;
        }

        profileManager.saveProfile(player.getName());  // Save after changing element
        return true;
    }
}
