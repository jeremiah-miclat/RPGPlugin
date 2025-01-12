package github.eremiyuh.rPGPlugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ViewItemCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Ensure the command sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player viewer = (Player) sender;

        // Check if the player has the required permission
        if (!viewer.isOp() && !viewer.getName().equals("Eremiyuh")) {
            viewer.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        // Check if the correct number of arguments is provided
        if (args.length != 1) {
            viewer.sendMessage(ChatColor.RED + "Usage: /viewitem <playerName>");
            return true;
        }

        // Get the target player
        Player target = Bukkit.getPlayer(args[0]);

        if (target == null || !target.isOnline()) {
            viewer.sendMessage(ChatColor.RED + "The player " + args[0] + " is not online.");
            return true;
        }

        // Open the target's inventory for the viewer
        viewer.openInventory(target.getInventory());
        viewer.sendMessage(ChatColor.GREEN + "You are now viewing " + target.getName() + "'s inventory.");
        return true;
    }
}
