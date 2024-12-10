package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddHomesCommand implements CommandExecutor {
    private final PlayerProfileManager profileManager;

    public AddHomesCommand(PlayerProfileManager profileManager) {
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
            player.sendMessage(ChatColor.YELLOW + "Usage: /buyhomeslot <number of home slot>");
            return true;
        }

        // Parse the number of claims from the argument
        int numberOfSlot;
        try {
            numberOfSlot = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid number. Please enter a valid number.");
            return true;
        }

        if (numberOfSlot <= 0) {
            player.sendMessage(ChatColor.RED + "You must buy at least 1 slot.");
            return true;
        }

        // Retrieve the player's profile
        UserProfile profile = profileManager.getProfile(player.getName());
        double playerEmeralds = profile.getEmerald();
        int playerHomeSlots = profile.getMaxHomes();

        if (numberOfSlot+playerHomeSlots >= 11) {
            player.sendMessage(ChatColor.RED + "Can only set 10 homes. " + "You currently have " + playerHomeSlots);
            return true;
        }

        double totalCost = numberOfSlot * 1000;

        // Check if the player has enough emeralds
        if (playerEmeralds >= totalCost) {
            // Deduct the emerald cost and increase claim points
            profile.setEmerald(playerEmeralds - totalCost);
            profile.setMaxHomes(playerHomeSlots + numberOfSlot);
            player.sendMessage(ChatColor.GREEN + "You have successfully purchased "
                    + ChatColor.GOLD + numberOfSlot
                    + ChatColor.GREEN + " extra set homes for "
                    + ChatColor.GOLD + (int) totalCost
                    + ChatColor.GREEN + " emerald(s)!");
        } else {
            player.sendMessage(ChatColor.RED + "You do not have enough emeralds to buy "
                    + ChatColor.GOLD + numberOfSlot
                    + ChatColor.RED + " slot(s).");
            player.sendMessage(ChatColor.YELLOW + "You need "
                    + ChatColor.GOLD + totalCost
                    + ChatColor.YELLOW + " emerald(s), but you only have "
                    + ChatColor.GOLD + (int) playerEmeralds
                    + ChatColor.YELLOW + " emerald(s).");
        }

        return true; // Indicates the command was processed successfully
    }

}
