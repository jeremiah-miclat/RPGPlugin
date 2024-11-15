package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class TpaCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final HashMap<String, String> teleportRequests;
    private final PlayerProfileManager profileManager;

    public TpaCommand(JavaPlugin plugin, HashMap<String, String> teleportRequests, PlayerProfileManager profileManager) {
        this.plugin = plugin;
        this.teleportRequests = teleportRequests;
        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player accepter = (Player) sender;

            if (args.length != 1) {
                accepter.sendMessage(ChatColor.RED + "Usage: /tpa <playername>");
                return false;
            }

            String requesterName = args[0];
            Player requester = Bukkit.getPlayer(requesterName);

            if (requester == null || !requester.isOnline()) {
                accepter.sendMessage(ChatColor.RED + "Player not found or not online.");
                return false;
            }

            // Check if there's a pending teleport request
            if (!teleportRequests.containsKey(requesterName) || !teleportRequests.get(requesterName).equals(accepter.getName())) {
                accepter.sendMessage(ChatColor.RED + "You do not have a pending teleport request from " + requester.getName() + ".");
                return false;
            }

            // Check if the accepter is standing on a solid block
            Block blockBelow = accepter.getLocation().subtract(0, 1, 0).getBlock();
            if (blockBelow == null || !blockBelow.getType().isSolid()) {
                accepter.sendMessage(ChatColor.RED + "You must stand on a solid block to accept a teleport request.");
                requester.sendMessage(ChatColor.RED + accepter.getName() + " is not in a valid location for teleportation.");
                return true;
            }

            // Check if the requester has an ender pearl
            UserProfile requesterProfile = profileManager.getProfile(requesterName);
            if (requesterProfile.getEnderPearl() < 1) {
                accepter.sendMessage(ChatColor.RED + "The requester does not have enough ender pearls.");
                requester.sendMessage(ChatColor.RED + "You need at least one ender pearl to teleport.");
                return true;
            }

            // Teleport the requester to the accepter's location
            if (requester.teleport(accepter.getLocation())) {
                requesterProfile.setEnderPearl(requesterProfile.getEnderPearl() - 1);
                accepter.sendMessage(ChatColor.GREEN + "You have successfully teleported " + requester.getName() + " to your location.");
                requester.sendMessage(ChatColor.GREEN + "You have been teleported to " + accepter.getName() + "'s location.");

                // Remove the teleport request after accepting
                teleportRequests.remove(requesterName);
            } else {
                accepter.sendMessage(ChatColor.RED + "Teleportation failed. Ensure you are in a safe location.");
                requester.sendMessage(ChatColor.RED + "Teleportation failed. The accepter's location is not safe.");
            }

            return true;
        }
        return false;
    }
}
