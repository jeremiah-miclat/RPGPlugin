package github.eremiyuh.rPGPlugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class SwordCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        // Create a new sword item stack
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD); // You can change this to any type of sword
        ItemMeta meta = sword.getItemMeta();

        // Set the display name and lore
        if (meta != null) {
            meta.setDisplayName("Agility Sword"); // Optional: Give it a custom name
            meta.setLore(Arrays.asList("Agility: 1000")); // Set the lore
            sword.setItemMeta(meta);
        }

        // Give the sword to the player
        player.getInventory().addItem(sword);
        player.sendMessage("You have been given a sword with Agility: 1000!");

        return true;
    }
}
