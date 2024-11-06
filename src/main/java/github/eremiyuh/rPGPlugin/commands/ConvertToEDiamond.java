package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ConvertToEDiamond implements CommandExecutor {

    private final PlayerProfileManager profileManager;

    // Map to associate item names with Materials
    private static final Map<String, Material> currencyMaterials = new HashMap<>();

    static {
        currencyMaterials.put("diamond", Material.DIAMOND);
        currencyMaterials.put("gold", Material.GOLD_INGOT);
        currencyMaterials.put("lapis", Material.LAPIS_LAZULI);
        currencyMaterials.put("emerald", Material.EMERALD);
        currencyMaterials.put("iron", Material.IRON_INGOT);
        currencyMaterials.put("enderpearl", Material.ENDER_PEARL);

    }

    public ConvertToEDiamond(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Please specify a currency to convert (diamond, gold, lapis, emerald, iron).");
            return true;
        }

        String currencyName = args[0].toLowerCase();
        Material currencyMaterial = currencyMaterials.get(currencyName);

        if (currencyMaterial == null) {
            player.sendMessage("Invalid currency type. Available types: diamond, gold, lapis, emerald, iron, enderpearl.");
            return true;
        }

        // Get the player's inventory and the UserProfile
        ItemStack[] inventoryContents = player.getInventory().getContents();
        UserProfile profile = profileManager.getProfile(player.getName());

        // Count the number of specified currency in the player's inventory
        int itemCount = 0;
        for (ItemStack item : inventoryContents) {
            if (item != null && item.getType() == currencyMaterial) {
                itemCount += item.getAmount();
            }
        }

        // Check if the player has any of the specified currency
        if (itemCount == 0) {
            player.sendMessage("You do not have any " + currencyName + " in your inventory to convert.");
            return true;
        }

        // Convert the items to the UserProfile's balance for the specified currency
        double currentBalance = profile.getCurrency(currencyName); // Assuming a generic getCurrency method
        double newBalance = currentBalance + itemCount;

        // Update the UserProfile's balance for the specified currency
        profile.setCurrency(currencyName, newBalance); // Assuming a generic setCurrency method

        // Remove the items from the player's inventory
        for (ItemStack item : inventoryContents) {
            if (item != null && item.getType() == currencyMaterial) {
                player.getInventory().remove(item);
            }
        }

        // Inform the player of the conversion
        player.sendMessage("You have converted " + itemCount + " " + currencyName + "(s) to your profile's balance!");
        player.sendMessage("Your new " + currencyName + " balance is: " + newBalance + ".");

        return true; // Indicates the command was processed successfully
    }
}
