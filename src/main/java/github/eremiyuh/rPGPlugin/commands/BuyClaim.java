package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BuyClaim implements CommandExecutor {

    private final PlayerProfileManager profileManager;

    public BuyClaim(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        // Check if the number of claims is provided
        if (args.length != 1) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /buyclaim <number of claims>");
            return true;
        }

        // Parse the number of claims from the argument
        int numberOfClaims;
        try {
            numberOfClaims = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid number of claims. Please enter a valid number.");
            return true;
        }

        if (numberOfClaims <= 0) {
            player.sendMessage(ChatColor.RED + "You must buy at least 1 claim point.");
            return true;
        }

        // Retrieve the player's profile
        UserProfile profile = profileManager.getProfile(player.getName());
        double playerEmeralds = profile.getEmerald();
        double playerClaimPoints = profile.getClaimPoints();

        // Calculate the total cost for the requested claim points
        double totalCost = numberOfClaims * 1000; // 1000 emeralds per claim point

        // Check if the player has enough emeralds
        if (playerEmeralds >= totalCost) {
            // Deduct the emerald cost and increase claim points
            profile.setEmerald(playerEmeralds - totalCost);
            profile.setClaimPoints(playerClaimPoints + numberOfClaims);
            player.sendMessage(ChatColor.GREEN + "You have successfully purchased "
                    + ChatColor.GOLD + numberOfClaims
                    + ChatColor.GREEN + " claim point(s) for "
                    + ChatColor.GOLD + (int) totalCost
                    + ChatColor.GREEN + " emerald(s)!");
            player.sendMessage(ChatColor.GREEN + "You now have "
                    + ChatColor.GOLD + (playerClaimPoints + numberOfClaims)
                    + ChatColor.GREEN + " claim point(s).");
        } else {
            player.sendMessage(ChatColor.RED + "You do not have enough emeralds to buy "
                    + ChatColor.GOLD + numberOfClaims
                    + ChatColor.RED + " claim point(s).");
            player.sendMessage(ChatColor.YELLOW + "You need "
                    + ChatColor.GOLD + totalCost
                    + ChatColor.YELLOW + " emerald(s), but you only have "
                    + ChatColor.GOLD + (int) playerEmeralds
                    + ChatColor.YELLOW + " emerald(s).");
        }

        return true; // Indicates the command was processed successfully
    }

}
