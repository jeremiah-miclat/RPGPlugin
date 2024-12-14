package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.buffs.PlayerStatBuff;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.manager.TpAllowManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
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
    private final TpAllowManager tpAllowManager;
    private final PlayerStatBuff playerStatBuff;

    public TptCommand(JavaPlugin plugin, HashMap<String, String> teleportRequests, PlayerProfileManager profileManager, TpAllowManager tpAllowManager, PlayerStatBuff playerStatBuff) {
        this.plugin = plugin;
        this.teleportRequests = teleportRequests; // Pass the shared map
        this.profileManager = profileManager;

        this.tpAllowManager = tpAllowManager;
        this.playerStatBuff = playerStatBuff;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player requester = (Player) sender;

            if (args.length != 1) {
                requester.sendMessage(ChatColor.RED + "Usage: /tpt <playername>");
                return true;
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
                return true;
            }

            boolean allowed = tpAllowManager.isPlayerAllowedToTeleport(targetName,sender.getName());
            if (allowed) {
                Block blockBelow = target.getLocation().subtract(0, 1, 0).getBlock();
                if (blockBelow == null || !blockBelow.getType().isSolid()) {
                    target.sendMessage(ChatColor.RED + "You must stand on a solid block so your friend could teleport.");
                    requester.sendMessage(ChatColor.RED + targetName + " is not in a valid location for teleportation.");
                    return true;
                }


                // Teleport the requester to the accepter's location
                if (requester.teleport(target.getLocation())) {
                    if (!target.getWorld().getName().contains("_rpg") && !target.getWorld().getName().contains("labyrinth")) {
                        playerStatBuff.updatePlayerStatsToNormal(requester);
                    }

                    if (target.getWorld().getName().contains("_rpg") || target.getWorld().getName().contains("labyrinth")) {
                        playerStatBuff.updatePlayerStatsToRPG(requester);
                    }
                    profile.setEnderPearl(profile.getEnderPearl() - 1);
                    target.sendMessage(ChatColor.GREEN + "You have successfully teleported " + requester.getName() + " to your location.");
                    requester.sendMessage(ChatColor.GREEN + "You have been teleported to " + target.getName() + "'s location.");

                    // Remove the teleport request after accepting
                    teleportRequests.remove(sender.getName());
                } else {
                    target.sendMessage(ChatColor.RED + "Teleportation failed. Ensure you are in a safe location.");
                    requester.sendMessage(ChatColor.RED + "Teleportation failed. The accepter's location is not safe.");
                }
                return true;

            }

            // Check if there's already a pending teleport request
            if (teleportRequests.containsKey(requester.getName())) {
                requester.sendMessage(ChatColor.RED + "You already have a pending teleport request.");
                return true;
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