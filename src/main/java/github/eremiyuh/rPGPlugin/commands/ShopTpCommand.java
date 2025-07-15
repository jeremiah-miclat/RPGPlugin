package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.manager.ShopTpSaveManager;
import github.eremiyuh.rPGPlugin.manager.ShopsManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

public class ShopTpCommand implements CommandExecutor {
    private final ShopsManager shopsManager;
    private final ShopTpSaveManager shopTpSaveManager;
    private final PlayerProfileManager playerProfileManager;

    public ShopTpCommand(ShopsManager shopsManager, github.eremiyuh.rPGPlugin.manager.ShopTpSaveManager shopTpSaveManager, PlayerProfileManager playerProfileManager) {
        this.shopsManager = shopsManager;
        this.shopTpSaveManager = shopTpSaveManager;
        this.playerProfileManager = playerProfileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /shop <playerName>");
            return true;
        }

        UserProfile profile = playerProfileManager.getProfile(player.getName());

        if (profile.getEnderPearl() <= 0) {
            player.sendMessage(net.md_5.bungee.api.ChatColor.RED + "Needs an ender pearl to teleport. /convertmaterial enderpearl");
            return true;
        }

        String targetPlayerName = args[0];

        // Get the saved shop location for the target player (the seller)
        Location shopLocation = shopTpSaveManager.getSavedSellerShopLocation(targetPlayerName);

        if (shopLocation == null) {
            player.sendMessage(ChatColor.RED + "No saved shop location found for player " + targetPlayerName);
            return true;
        }

        // List of all shop locations for the target player
        List<Location> targetShops = shopsManager.listPlayerShops(targetPlayerName);

        boolean isNearbyShopFound = false;

        // Check if the saved shop location is within 5 blocks of any shop
        for (Location shops : targetShops) {
            if (shops.getWorld().equals(shopLocation.getWorld()) &&
                    shops.distance(shopLocation) <= 5) {
                isNearbyShopFound = true;
                break;  // Found a shop within 5 blocks, no need to check further
            }
        }

        if (!isNearbyShopFound) {
            player.sendMessage(ChatColor.RED + "No nearby shop found within 5 blocks.");
            return true;
        }

        // Check if the saved shop location is a solid block (safe to teleport)
        Block block = shopLocation.add(0,-1,0).getBlock();
        if (!block.getType().isSolid()) {
            player.sendMessage(ChatColor.RED + "The shop location is not safe to teleport to (block is not solid).");
            return true;
        }


        // Teleport the player to the shop location
        player.teleport(shopLocation.add(0,1,0));
        player.sendMessage(ChatColor.GREEN + "Teleported to the shop of " + targetPlayerName);

        // Decrease the player's Ender Pearl count
        profile.setEnderPearl(profile.getEnderPearl() - 1);
        playerProfileManager.saveProfile(player.getName());

        return true;
    }

}
