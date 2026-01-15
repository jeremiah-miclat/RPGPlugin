package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.buffs.PlayerStatBuff;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TpaCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final HashMap<String, String> teleportRequests;
    private final PlayerProfileManager profileManager;
    private final PlayerStatBuff playerStatBuff;

    // Players currently casting teleport without ender pearl
    private final Set<UUID> teleportingPlayers = new HashSet<>();

    public TpaCommand(JavaPlugin plugin, HashMap<String, String> teleportRequests,
                      PlayerProfileManager profileManager, PlayerStatBuff playerStatBuff) {
        this.plugin = plugin;
        this.teleportRequests = teleportRequests;
        this.profileManager = profileManager;
        this.playerStatBuff = playerStatBuff;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player accepter)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length != 1) {
            accepter.sendMessage(ChatColor.RED + "Usage: /tpa <playername>");
            return true;
        }

        String requesterName = args[0];
        Player requester = Bukkit.getPlayer(requesterName);

        if (requester == null || !requester.isOnline()) {
            accepter.sendMessage(ChatColor.RED + "Player not found or not online.");
            return true;
        }

        // Check if there is a pending request
        if (!teleportRequests.containsKey(requesterName) || !teleportRequests.get(requesterName).equals(accepter.getName())) {
            accepter.sendMessage(ChatColor.RED + "You do not have a pending teleport request from " + requesterName + ".");
            return true;
        }

        // Check solid block under accepter
        Block blockBelow = accepter.getLocation().subtract(0, 1, 0).getBlock();
        if (blockBelow == null || !blockBelow.getType().isSolid()) {
            accepter.sendMessage(ChatColor.RED + "You must stand on a solid block to accept a teleport request.");
            requester.sendMessage(ChatColor.RED + accepter.getName() + " is not in a valid location for teleportation.");
            return true;
        }

        UserProfile requesterProfile = profileManager.getProfile(requesterName);
        if (requesterProfile == null) {
            accepter.sendMessage(ChatColor.RED + "Requester profile not found.");
            return true;
        }

        // =========================
        // HAS ENDER PEARL → instant
        // =========================
        if (requesterProfile.getEnderPearl() >= 1) {
            requesterProfile.setEnderPearl(requesterProfile.getEnderPearl() - 1);
            teleportPlayer(requester, accepter);
            return true;
        }

        // =========================
        // NO ENDER PEARL → 5s cast
        // =========================
        if (teleportingPlayers.contains(requester.getUniqueId())) {
            requester.sendMessage("You are already teleporting!");
            return true;
        }

        requester.sendMessage("No Ender Pearl detected. Stand still for 5 seconds to teleport...");
        teleportingPlayers.add(requester.getUniqueId());
        Location startLocation = requester.getLocation().clone();

        new BukkitRunnable() {
            int secondsLeft = 5;

            @Override
            public void run() {

                if (!requester.isOnline()) {
                    teleportingPlayers.remove(requester.getUniqueId());
                    cancel();
                    return;
                }

                if (!requester.getWorld().equals(startLocation.getWorld()) ||
                        requester.getLocation().distance(startLocation) > 0.5) {
                    requester.sendMessage("Teleport cancelled because you moved or changed world.");
                    teleportingPlayers.remove(requester.getUniqueId());
                    cancel();
                    return;
                }

                if (secondsLeft <= 0) {
                    teleportPlayer(requester, accepter);
                    teleportingPlayers.remove(requester.getUniqueId());
                    cancel();
                    return;
                }

                requester.sendMessage("Teleporting in " + secondsLeft + "...");
                secondsLeft--;
            }
        }.runTaskTimer(plugin, 0L, 20L);

        return true;
    }

    private void teleportPlayer(Player requester, Player accepter) {
        if (requester.teleport(accepter.getLocation())) {

            UserProfile profile = profileManager.getProfile(requester.getName());
            if (profile != null && profile.getEnderPearl() > 0) {
                profile.setEnderPearl(profile.getEnderPearl() - 1);
            }

            // Update stats based on world type
            if (!accepter.getWorld().getName().contains("_rpg") && !accepter.getWorld().getName().contains("labyrinth")) {
                playerStatBuff.updatePlayerStatsToNormal(requester);
            } else {
                playerStatBuff.updatePlayerStatsToRPG(requester);
            }

            accepter.sendMessage(ChatColor.GREEN + "You have successfully teleported " + requester.getName() + " to your location.");
            requester.sendMessage(ChatColor.GREEN + "You have been teleported to " + accepter.getName() + "'s location.");

            teleportRequests.remove(requester.getName());

        } else {
            accepter.sendMessage(ChatColor.RED + "Teleportation failed. Ensure you are in a safe location.");
            requester.sendMessage(ChatColor.RED + "Teleportation failed. The accepter's location is not safe.");
        }
    }
}
