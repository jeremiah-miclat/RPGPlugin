package github.eremiyuh.rPGPlugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class SwordCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    public SwordCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player admin)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (!admin.isOp() || !admin.getName().equals("Eremiyuh")) {
            admin.sendMessage("You do not have permission to use this command.");
            return true;
        }

        if (args.length != 5) {
            admin.sendMessage("Usage: /give <playername> <item_name> <lore_name> <value> <amount>");
            return true;
        }

        String targetName = args[0];
        String itemName = args[1];
        String loreName = args[2];
        String valueString = args[3];
        String amountString = args[4];

        int value;
        int amount;

        try {
            value = Integer.parseInt(valueString);
        } catch (NumberFormatException e) {
            admin.sendMessage("Invalid value. Please enter a valid integer for the lore.");
            return true;
        }

        try {
            amount = Integer.parseInt(amountString);
            if (amount < 1 || amount > 64) {
                admin.sendMessage("Amount must be between 1 and 64.");
                return true;
            }
        } catch (NumberFormatException e) {
            admin.sendMessage("Invalid amount. Please enter a number between 1 and 64.");
            return true;
        }

        Player targetPlayer = Bukkit.getPlayer(targetName);
        if (targetPlayer == null || !targetPlayer.isOnline()) {
            admin.sendMessage("Player " + targetName + " not found or is offline.");
            return true;
        }

        Material itemMaterial;
        try {
            itemMaterial = Material.valueOf(itemName.toUpperCase());
        } catch (IllegalArgumentException e) {
            admin.sendMessage("Invalid item name. Please use a valid item name (e.g., diamond_sword).");
            return true;
        }

        ItemStack item = new ItemStack(itemMaterial, amount);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(itemName.replace('_', ' '));
            String lore = loreName + ": " + value;
            meta.setLore(Arrays.asList(lore));

            // Make COAL glow
            if (itemMaterial == Material.COAL) {
                meta.addEnchant(Enchantment.LURE, 1, true); // harmless enchant
                meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS); // hide the enchant, only glow shows
            }

            NamespacedKey key = new NamespacedKey(plugin, "special_sword");
            meta.getPersistentDataContainer().set(key, org.bukkit.persistence.PersistentDataType.STRING, "unique_value");

            item.setItemMeta(meta);
        }

        targetPlayer.getInventory().addItem(item);
        targetPlayer.sendMessage("You have been given " + amount + " " + itemName + "(s) with " + loreName + " " + value + ".");
        admin.sendMessage("You gave " + targetName + " " + amount + " " + itemName + "(s) with " + loreName + " " + value + ".");

        return true;
    }

}
