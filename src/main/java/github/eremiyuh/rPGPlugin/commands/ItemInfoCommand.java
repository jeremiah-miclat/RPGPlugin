package github.eremiyuh.rPGPlugin.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemInfoCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack itemInHand = player.getInventory().getItemInMainHand();

            // Check if the player is holding an item
            if (itemInHand != null && itemInHand.getType() != Material.AIR) {
                Material material = itemInHand.getType();
                player.sendMessage("You are holding: " + material.name());
            } else {
                player.sendMessage("You are not holding any item.");
            }
        } else {
            sender.sendMessage("This command can only be used by a player.");
        }
        return true;
    }
}