package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ConvertEDiamond implements CommandExecutor {

    private final PlayerProfileManager profileManager;

    public ConvertEDiamond(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Ensure the command sender is a player
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        // Check if the correct number of arguments is provided
        if (args.length != 1) {
            player.sendMessage("Usage: /convertEdiamond <amount>");
            return true;
        }

        // Try to parse the amount of diamonds to convert
        int amountToConvert;
        try {
            amountToConvert = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage("Please enter a valid number.");
            return true;
        }

        // Get the player's profile
        UserProfile profile = profileManager.getProfile(player.getName());
        double profileDiamonds = profile.getDiamond();

        // Check if the player has enough diamonds in their profile
        if (amountToConvert <= 0) {
            player.sendMessage("You must convert at least one diamond.");
            return true;
        }

        if (amountToConvert > profileDiamonds) {
            player.sendMessage("You do not have enough diamonds in your profile to convert that amount.");
            return true;
        }

        // Create a new ItemStack of diamonds
        ItemStack diamondStack = new ItemStack(Material.DIAMOND, amountToConvert);

        // Check if the player's inventory is full
        if (player.getInventory().firstEmpty() == -1) {
            // Drop the diamonds on the ground
            player.getWorld().dropItemNaturally(player.getLocation(), diamondStack);
            // Notify the player
            player.sendMessage("Your inventory was full, so the diamonds have been dropped on the ground.");
        } else {
            // Add diamonds to the player's inventory
            player.getInventory().addItem(diamondStack);
        }

        // Remove diamonds from the UserProfile
        profile.setDiamond(profileDiamonds - amountToConvert);

        // Optionally save the player's profile
        profileManager.saveProfile(player.getName());

        // Send a confirmation message to the player
        player.sendMessage("Successfully converted " + amountToConvert + " diamonds to your inventory.");

        return true;
    }
}
