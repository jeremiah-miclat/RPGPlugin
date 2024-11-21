package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ConvertLevelsCommand implements CommandExecutor {

    private final PlayerProfileManager profileManager;

    public ConvertLevelsCommand(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        UserProfile profile = profileManager.getProfile(player.getName());
        double playerClassTotalAttrib = profile.getTotalAllocatedPoints()+profile.getCurrentAttributePoints();
        double abyssPoints = profile.getAbyssPoints();

        if (playerClassTotalAttrib >= 20000) {
            player.sendMessage("Max attributes reached.");
            return true;
        }

        // Determine the number of attribute points to convert
        int pointsToConvert = 1; // default to 1 if no argument provided
        if (args.length > 0) {
            try {
                pointsToConvert = Integer.parseInt(args[0]);
                if (pointsToConvert <= 0) {
                    player.sendMessage(ChatColor.RED + "Please specify a positive number of attribute points to convert.");
                    return true;
                }
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Invalid number of attribute points.");
                return true;
            }
        }

        if (pointsToConvert+playerClassTotalAttrib > 20000) {
            player.sendMessage("Max attributes reached.");
            return true;
        }

        // Calculate the total abyss points needed by dynamically adjusting cost per point
        int totalAbyssPointsRequired = 0;
        double currentAttribPoints = playerClassTotalAttrib;

        for (int i = 0; i < pointsToConvert; i++) {
            int costForCurrentPoint = getLevelsRequiredForConversion(currentAttribPoints);
            totalAbyssPointsRequired += costForCurrentPoint;
            currentAttribPoints++;
        }

        // Check if the player has enough abyss points for the entire transaction
        if (abyssPoints < totalAbyssPointsRequired) {
            player.sendMessage(ChatColor.RED + "You need at least " + totalAbyssPointsRequired + " abyss points to convert " + pointsToConvert + " attribute point(s).");
            return true;
        }

        // Perform the conversion, updating the player's profile
        profile.setCurrentAttributePoints(profile.getCurrentAttributePoints() + pointsToConvert);
        profile.setAbyssPoints(profile.getAbyssPoints() - totalAbyssPointsRequired);

        // Save the profile
        profileManager.saveProfile(player.getName());

        player.sendMessage(ChatColor.GREEN + "You have converted " + totalAbyssPointsRequired + " abyss points into " + pointsToConvert + " attribute point(s)!");

        return true;
    }

    /**
     * This method determines how many abyss points are required to convert 1 attribute point,
     * based on the total allocated attribute points.
     *
     * @param totalAttrib The total allocated attribute points.
     * @return The number of abyss points required for conversion.
     */
    private int getLevelsRequiredForConversion(double totalAttrib) {
        // Adjust the conversion rate of abyss points based on total allocated attribute points
        if (totalAttrib < 100) {
            return 200;
        } else if (totalAttrib < 200) {
            return 400;
        } else if (totalAttrib < 300) {
            return 600;
        } else if (totalAttrib < 400) {
            return 800;
        } else if (totalAttrib < 500) {
            return 1000;
        } else if (totalAttrib < 600) {
            return 2000;
        } else if (totalAttrib < 700) {
            return 4000;
        } else if (totalAttrib < 800) {
            return 6000;
        } else if (totalAttrib < 900) {
            return 8000;
        } else if (totalAttrib < 1000) {
            return 10000;
        } else if (totalAttrib < 2000) {
            return 20000;
        } else if (totalAttrib < 3000) {
            return 30000;
        } else if (totalAttrib < 4000) {
            return 40000;
        } else if (totalAttrib < 5000) {
            return 50000;
        } else if (totalAttrib < 6000) {
            return 60000;
        } else if (totalAttrib < 7000) {
            return 70000;
        } else if (totalAttrib < 8000) {
            return 80000;
        } else if (totalAttrib < 9000) {
            return 90000;
        } else if (totalAttrib < 10000) {
            return 100000;
        } else {
            return 100000;
        }
    }
}
