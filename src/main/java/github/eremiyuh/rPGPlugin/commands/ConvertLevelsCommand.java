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
    private static final int ATTRIBUTE_CAP = 20000;

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
        double playerClassTotalAttrib = profile.getTotalAllocatedPoints() + profile.getCurrentAttributePoints();
        double abyssPoints = profile.getAbysspoints();

        if (playerClassTotalAttrib >= ATTRIBUTE_CAP) {
            player.sendMessage(ChatColor.RED + "Max attributes reached.");
            return true;
        }

        int pointsToConvert = 1;
        boolean convertAll = false;

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("all")) {
                convertAll = true;
            } else {
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
        }

        if (convertAll) {
            double currentAttrib = playerClassTotalAttrib;
            int abyssAvailable = (int) abyssPoints;
            int maxConvertible = 0;

            while (currentAttrib < ATTRIBUTE_CAP) {
                int cost = getLevelsRequiredForConversion(currentAttrib);
                if (abyssAvailable < cost) break;
                abyssAvailable -= cost;
                currentAttrib++;
                maxConvertible++;
            }

            if (maxConvertible == 0) {
                player.sendMessage(ChatColor.RED + "You don't have enough abyss points to convert any attributes.");
                return true;
            }

            pointsToConvert = maxConvertible;
        }

        if (pointsToConvert + playerClassTotalAttrib > ATTRIBUTE_CAP) {
            player.sendMessage(ChatColor.RED + "Max attributes reached.");
            return true;
        }

        int totalAbyssPointsRequired = 0;
        double currentAttribPoints = playerClassTotalAttrib;

        for (int i = 0; i < pointsToConvert; i++) {
            int cost = getLevelsRequiredForConversion(currentAttribPoints);
            totalAbyssPointsRequired += cost;
            currentAttribPoints++;
        }

        if (abyssPoints < totalAbyssPointsRequired) {
            player.sendMessage(ChatColor.RED + "You need at least " + totalAbyssPointsRequired + " abyss points to convert " + pointsToConvert + " attribute point(s).");
            return true;
        }

        profile.setCurrentAttributePoints(profile.getCurrentAttributePoints() + pointsToConvert);
        profile.setAbysspoints(profile.getAbysspoints() - totalAbyssPointsRequired);
        profileManager.saveProfile(player.getName());

        player.sendMessage(ChatColor.GREEN + "You have converted " + totalAbyssPointsRequired + " abyss points into " + pointsToConvert + " attribute point(s)!");
        return true;
    }

    /**
     * Doubled scaling curve: both thresholds and costs are multiplied
     */
    private int getLevelsRequiredForConversion(double totalAttrib) {
        if (totalAttrib < 200) return 400;
        else if (totalAttrib < 400) return 800;
        else if (totalAttrib < 600) return 1200;
        else if (totalAttrib < 800) return 1600;
        else if (totalAttrib < 1000) return 2000;
        else if (totalAttrib < 1200) return 4000;
        else if (totalAttrib < 1400) return 8000;
        else if (totalAttrib < 1600) return 12000;
        else if (totalAttrib < 1800) return 16000;
        else if (totalAttrib < 2000) return 20000;
        else if (totalAttrib < 4000) return 40000;
        else if (totalAttrib < 6000) return 60000;
        else if (totalAttrib < 8000) return 80000;
        else if (totalAttrib < 10000) return 100000;
        else if (totalAttrib < 11000) return 200000;
        else if (totalAttrib < 12000) return 300000;
        else if (totalAttrib < 13000) return 400000;
        else if (totalAttrib < 14000) return 500000;
        else if (totalAttrib < 15000) return 600000;
        else if (totalAttrib < 16000) return 700000;
        else if (totalAttrib < 17000) return 800000;
        else if (totalAttrib < 18000) return 900000;
        else if (totalAttrib < 19000) return 1000000;
        else if (totalAttrib < 20000) return 1100000;
        else return 1100000;
    }
}
