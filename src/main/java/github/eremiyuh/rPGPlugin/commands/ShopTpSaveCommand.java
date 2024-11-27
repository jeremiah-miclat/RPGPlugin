package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.ShopTpSaveManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ShopTpSaveCommand implements CommandExecutor {
    private final ShopTpSaveManager shopTpSaveManager;
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private static final long COOLDOWN_TIME = 5000; // Cooldown time in milliseconds (5 seconds)

    public ShopTpSaveCommand(ShopTpSaveManager shopTpSaveManager) {
        this.shopTpSaveManager = shopTpSaveManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }



        Player player = (Player) sender;

        if (player.getWorld().getName().contains("resource")
                || player.getWorld().getName().contains("rpg")
                || player.getWorld().getName().contains("labyrinth")
        ) {
            sender.sendMessage(ChatColor.RED + "Not allowed.");
            return true;
        }
        UUID playerUUID = player.getUniqueId();

        // Check for cooldown
        long currentTime = System.currentTimeMillis();
        if (cooldowns.containsKey(playerUUID)) {
            long lastUsedTime = cooldowns.get(playerUUID);
            long timeRemaining = (lastUsedTime + COOLDOWN_TIME) - currentTime;

            if (timeRemaining > 0) {
                player.sendMessage(ChatColor.RED + "You must wait " + (timeRemaining / 1000.0) + " seconds before using this command again.");
                return true;
            }
        }

        // Update cooldown time
        cooldowns.put(playerUUID, currentTime);

        boolean isOverwrite = shopTpSaveManager.hasShop(player);
        boolean success = shopTpSaveManager.saveShopLocation(player);

        if (success) {
            if (isOverwrite) {
                player.sendMessage(ChatColor.YELLOW + "Your shop location has been updated.");
            } else {
                player.sendMessage(ChatColor.GREEN + "Your shop location has been saved successfully!");
            }
        } else {
            player.sendMessage(ChatColor.RED + "An error occurred while saving your shop location.");
        }

        return true;
    }
}
