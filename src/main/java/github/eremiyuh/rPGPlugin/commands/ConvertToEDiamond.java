package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ConvertToEDiamond implements CommandExecutor {

    private final PlayerProfileManager profileManager;

    public ConvertToEDiamond(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        // Get the player's inventory and the UserProfile
        ItemStack[] inventoryContents = player.getInventory().getContents();
        UserProfile profile = profileManager.getProfile(player.getName());

        // Count the number of diamonds in the player's inventory
        int diamondCount = 0;
        for (ItemStack item : inventoryContents) {
            if (item != null && item.getType() == Material.DIAMOND) {
                diamondCount += item.getAmount();
            }
        }

        // Check if the player has any diamonds
        if (diamondCount == 0) {
            player.sendMessage("You do not have any diamonds in your inventory to convert.");
            return true;
        }

        // Convert the diamonds to the UserProfile's diamond balance
        double currentProfileDiamonds = profile.getDiamond();
        double newProfileBalance = currentProfileDiamonds + diamondCount;

        // Update the UserProfile's diamond balance
        profile.setDiamond(newProfileBalance);

        // Remove the diamonds from the player's inventory
        for (ItemStack item : inventoryContents) {
            if (item != null && item.getType() == Material.DIAMOND) {
                player.getInventory().remove(item);
            }
        }

        // Inform the player of the conversion
        player.sendMessage("You have converted " + diamondCount + " diamond(s) to your profile's diamond balance!");
        player.sendMessage("Your new balance is: " + newProfileBalance + " diamonds.");

        return true; // Indicates the command was processed successfully
    }
}
