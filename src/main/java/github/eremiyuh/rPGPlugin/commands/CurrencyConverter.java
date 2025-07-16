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
            player.sendMessage("Invalid currency type. Available types: diamond, gold, lapis, emerald, iron, enderpearl, copper, netherite.");
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

        if (currentBalance < amount) {
            player.sendMessage("You do not have enough " + currencyName + " to convert.");
            return true;
        }

        // Try to add as many items as possible in stacks (max 64 per stack)
        int remaining = amount;
        int actuallyAdded = 0;

        while (remaining > 0) {
            int stackSize = Math.min(remaining, currencyMaterial.getMaxStackSize());
            ItemStack stack = new ItemStack(currencyMaterial, stackSize);

            Map<Integer, ItemStack> notAdded = player.getInventory().addItem(stack);

            if (notAdded.isEmpty()) {
                actuallyAdded += stackSize;
                remaining -= stackSize;
            } else {
                // Could not add this stack — inventory full
                break;
            }
        }

        if (actuallyAdded > 0) {
            profile.setCurrency(currencyName, currentBalance - actuallyAdded);
            player.sendMessage("You have converted " + actuallyAdded + " " + currencyName + "(s) to items!");
        }

        if (actuallyAdded < amount) {
            int failedAmount = amount - actuallyAdded;
            player.sendMessage("You did not have enough inventory space for " + failedAmount + " " + currencyName + "(s). They were not converted.");
        }

        return true;
    }
}

