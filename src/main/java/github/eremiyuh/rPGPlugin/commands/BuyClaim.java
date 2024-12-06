package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
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
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        // Check if the number of claims is provided
        if (args.length != 1) {
            player.sendMessage("Usage: /buyclaim <number of claims>");
            return true;
        }

        // Parse the number of claims from the argument
        int numberOfClaims;
        try {
            numberOfClaims = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid number of claims. Please enter a valid number.");
            return true;
        }

        if (numberOfClaims <= 0) {
            player.sendMessage("You must buy at least 1 claim point.");
            return true;
        }

        // Retrieve the player's profile
        UserProfile profile = profileManager.getProfile(player.getName());
        double playerEmeralds = profile.getEmerald();
        double playerClaimPoints = profile.getClaimPoints();

        // Calculate the total cost for the requested claim points
        double totalCost = numberOfClaims * 10; // 10 diamond per claim point

        // Check if the player has enough diamonds
        if (playerEmeralds >= totalCost) {
            // Deduct the diamond cost and increase claim points
            profile.setDiamond(playerEmeralds - totalCost);
            profile.setClaimPoints(playerClaimPoints + numberOfClaims);
            player.sendMessage("You have successfully purchased " + numberOfClaims + " claim point(s)!");
            player.sendMessage("You now have " + (playerClaimPoints + numberOfClaims) + " claim point(s).");
        } else {
            player.sendMessage("You do not have enough emerald to buy " + numberOfClaims + " claim point(s).");
            player.sendMessage("You currently have " + playerEmeralds + " emerald.");
        }

        return true; // Indicates the command was processed successfully
    }
}
