package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetHomeCommand implements CommandExecutor {
    private final PlayerProfileManager profileManager;

    public SetHomeCommand(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can set homes.");
            return true;
        }



        Player player = (Player) sender;
        UserProfile profile = profileManager.getProfile(player.getName());

        if (player.getWorld().getName().contains("resource")

        ) {
            player.sendMessage("Cannot set home here.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Please specify a name for your home. /sethome homename");
            return true;
        }

        String homeName = args[0];

        // Check if the home already exists
        if (profile.homeExists(homeName)) {
            player.sendMessage("A home with that name already exists. Enter /homedelete " + homeName + ", then try again.");
            return true;
        }

        // Check if the player can set more homes
        if (profile.getHomes().size() >= profile.getMaxHomes()) {
            player.sendMessage("You've reached the maximum number of homes.");
            return true;
        }

        // Add the home
        Location location = player.getLocation();
        if (profile.addHome(homeName, location)) {
            player.sendMessage("Home '" + homeName + "' set!");
            profileManager.saveProfile(player.getName());
        } else {
            player.sendMessage("Could not set home.");
        }

        return true;
    }
}

