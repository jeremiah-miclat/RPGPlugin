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

public class CurrencyConverter implements CommandExecutor {

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
        currencyMaterials.put("copper", Material.COPPER_INGOT);
        currencyMaterials.put("netherite", Material.NETHERITE_INGOT);
    }

    public CurrencyConverter(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage("Please specify a currency (diamond, gold, lapis, emerald, iron, enderpearl, copper, netherite) and an amount.");
            return true;
        }

        String currencyName = args[0].toLowerCase();
        Material currencyMaterial = currencyMaterials.get(currencyName);

        if (currencyMaterial == null) {
            player.sendMessage("Invalid currency type. Available types: (diamond, gold, lapis, emerald, iron, enderpearl, copper, netherite).");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
            if (amount <= 0) {
                player.sendMessage("The amount must be a positive number greater than zero.");
                return true;
            }
        } catch (NumberFormatException e) {
            player.sendMessage("The amount must be a valid number.");
            return true;
        }

        UserProfile profile = profileManager.getProfile(player.getName());
        double currentBalance = profile.getCurrency(currencyName);

        // Check if the player has enough balance
        if (currentBalance < amount) {
            player.sendMessage("You do not have enough " + currencyName + " to convert.");
            return true;
        }

        // Create an ItemStack for the specified currency type
        ItemStack itemStack = new ItemStack(currencyMaterial, amount);

        // Check if the player has enough empty slots
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage("You do not have enough space in your inventory to convert these items. Please free up some space.");
            return true;
        }

        // Add the items to the player's inventory
        player.getInventory().addItem(itemStack);
        player.sendMessage("You have converted " + amount + " " + currencyName + "(s) to items!");

        // Deduct the amount from the UserProfile's balance
        profile.setCurrency(currencyName, currentBalance - amount);

        return true;
    }
}
