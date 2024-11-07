package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class TptCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final HashMap<String, String> teleportRequests; // Use the same map
    private final int requestTimeout = 30 * 20; // 30 seconds in ticks
    private final PlayerProfileManager profileManager;

    public TptCommand(JavaPlugin plugin, HashMap<String, String> teleportRequests, PlayerProfileManager profileManager) {
        this.plugin = plugin;
        this.teleportRequests = teleportRequests; // Pass the shared map
        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player requester = (Player) sender;

            if (args.length != 1) {
                requester.sendMessage(ChatColor.RED + "Usage: /tpt <playername>");
                return false;
            }

            UserProfile profile = profileManager.getProfile(sender.getName());
            if (profile.getEnderPearl() < 1) {
                sender.sendMessage("Need ender pearl to teleport");
                return true;
            }

            String targetName = args[0];
            Player target = Bukkit.getPlayer(targetName);

            if (target == null || !target.isOnline()) {
                requester.sendMessage(ChatColor.RED + "Player not found or not online.");
                return false;
            }

            // Check if there's already a pending teleport request
            if (teleportRequests.containsKey(requester.getName())) {
                requester.sendMessage(ChatColor.RED + "You already have a pending teleport request.");
                return false;
            }

            // Send a teleport request to the target player
            target.sendMessage(ChatColor.YELLOW + requester.getName() + " has requested to teleport to you. Type /tpa to accept.");
            requester.sendMessage(ChatColor.GREEN + "Teleport request sent to " + target.getName() + ". You have 30 seconds to accept.");

            // Store the request for later handling
            teleportRequests.put(requester.getName(), target.getName());

            // Start a task to automatically cancel the request after 30 seconds
            new org.bukkit.scheduler.BukkitRunnable() {
                @Override
                public void run() {
                    if (teleportRequests.containsKey(requester.getName())) {
                        teleportRequests.remove(requester.getName());
                        requester.sendMessage(ChatColor.RED + "Teleport request to " + target.getName() + " has expired.");
                    }
                }
            }.runTaskLater(plugin, requestTimeout);

            return true;
        }
        return false;
    }
}