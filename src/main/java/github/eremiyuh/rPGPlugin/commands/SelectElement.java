package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SelectElement implements CommandExecutor {

    private final PlayerProfileManager profileManager;

    public SelectElement(PlayerProfileManager profileManager) {
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
            player.sendMessage("Usage: /selectelement <fire|water|earth|air>");
            return true;
        }

        String element = args[0].toLowerCase();

        // Check if the player can select a new element
        if (!profile.canSelectNewElement()) {
            player.sendMessage("You cannot switch elements for another 10 minutes.");
            return true;
        }

        // Validate the chosen element and set it
        switch (element) {
            case "fire":
//            case "earth":
//            case "wind":
//            case "lightning":
            case "water":
            case "ice":
                profile.setSelectedElement(element);
                player.sendMessage("You have chosen the " + element + " element!");
                break;
            default:
                player.sendMessage("Invalid element. Choose either fire, water, or ice.");
                return true;
        }

        profileManager.saveProfile(player.getName());  // Save after changing element
        return true;
    }
}
