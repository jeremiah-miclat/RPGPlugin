package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.utils.ItemUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class GiveResetTokenCommand implements CommandExecutor {
    private final List<String> allowedPlayers = List.of("Eremiyuh");


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {

            if (!allowedPlayers.contains(player.getName())) {
                player.sendMessage("§cYou do not have permission to use this command.");
                return true;
            }

            // Get the custom item from ItemUtils
            ItemStack resetToken = ItemUtils.getResetItem();

            // Try to add the item to the player's inventory
            PlayerInventory inventory = player.getInventory();
            HashMap<Integer, ItemStack> leftover = inventory.addItem(resetToken);

            // Check if the item couldn't fit in the inventory
            if (!leftover.isEmpty()) {
                // Drop the leftover item(s) at the player's location
                player.getWorld().dropItemNaturally(player.getLocation(), resetToken);
                player.sendMessage("§eYour inventory is full! The Attribute Reset Token has been dropped at your feet.");
            } else {
                player.sendMessage("§aYou have received an Attribute Reset Token!");
            }

            return true;
        } else {
            sender.sendMessage("This command can only be used by players.");
            return false;
        }
    }
}
