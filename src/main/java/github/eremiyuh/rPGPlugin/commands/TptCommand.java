package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.buffs.PlayerStatBuff;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.manager.TpAllowManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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

public class TptCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final HashMap<String, String> teleportRequests; // requester -> target
    private final int requestTimeout = 30 * 20; // 30 seconds in ticks
    private final PlayerProfileManager profileManager;
    private final TpAllowManager tpAllowManager;
    private final PlayerStatBuff playerStatBuff;

    // Players currently casting tpt without ender pearl
    private final Set<UUID> teleportingPlayers = new HashSet<>();

    public TptCommand(JavaPlugin plugin, HashMap<String, String> teleportRequests,
                      PlayerProfileManager profileManager, TpAllowManager tpAllowManager,
                      PlayerStatBuff playerStatBuff) {
        this.plugin = plugin;
        this.teleportRequests = teleportRequests;
        this.profileManager = profileManager;
        this.tpAllowManager = tpAllowManager;
        this.playerStatBuff = playerStatBuff;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player requester)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length != 1) {
            requester.sendMessage(ChatColor.RED + "Usage: /tpt <playername>");
            return true;
        }

        if (requester.getWorld().getName().contains("_br")) {
            requester.sendMessage("Not allowed here.");
            return true;
        }

        UserProfile profile = profileManager.getProfile(requester.getName());
        if (profile == null) {
            requester.sendMessage("Profile not found.");
            return true;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);

        if (target == null || !target.isOnline()) {
            requester.sendMessage(ChatColor.RED + "Player not found or not online.");
            return true;
        }

        if (target.getWorld().getName().contains("_br")) {
            requester.sendMessage("Target is in a restricted world.");
            return true;
        }

        // Check if requester is allowed to teleport immediately
        boolean allowed = tpAllowManager.isPlayerAllowedToTeleport(targetName, requester.getName());

        if (allowed) {
            Block blockBelow = target.getLocation().subtract(0, 1, 0).getBlock();
            if (blockBelow == null || !blockBelow.getType().isSolid()) {
                target.sendMessage(ChatColor.RED + "You must stand on a solid block so your friend could teleport.");
                requester.sendMessage(ChatColor.RED + targetName + " is not in a valid location for teleportation.");
                return true;
            }

            // =========================
            // HAS ENDER PEARL → INSTANT TELEPORT
            // =========================
            if (profile.getEnderPearl() >= 1) {
                profile.setEnderPearl(profile.getEnderPearl() - 1);
                teleportPlayer(requester, target.getLocation(), target);
                return true;
            }

            // =========================
            // NO ENDER PEARL → 5s CAST TIME
            // =========================
            if (teleportingPlayers.contains(requester.getUniqueId())) {
                requester.sendMessage("You are already teleporting!");
                return true;
            }

            requester.sendMessage("No Ender Pearl detected. Stand still for 5 seconds to teleport...");
            Location startLocation = requester.getLocation().clone();
            teleportingPlayers.add(requester.getUniqueId());

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
                        teleportPlayer(requester, target.getLocation(), target);
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

        // =========================
        // SEND TELEPORT REQUEST
        // =========================
        if (teleportRequests.containsKey(requester.getName())) {
            requester.sendMessage(ChatColor.RED + "You already have a pending teleport request.");
            return true;
        }

        target.sendMessage(ChatColor.YELLOW + requester.getName() + " has requested to teleport to you. Type /tpa to accept.");
        requester.sendMessage(ChatColor.GREEN + "Teleport request sent to " + target.getName() + ". You have 30 seconds to accept.");

        teleportRequests.put(requester.getName(), target.getName());

        new BukkitRunnable() {
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

    private void teleportPlayer(Player requester, Location targetLocation, Player target) {
        if (requester.teleport(targetLocation)) {

            UserProfile profile = profileManager.getProfile(requester.getName());
            if (profile != null) {
                profile.setEnderPearl(Math.max(0, profile.getEnderPearl() - 1));
            }

            // Update stats based on world type
            if (!target.getWorld().getName().contains("_rpg") && !target.getWorld().getName().contains("labyrinth")) {
                playerStatBuff.updatePlayerStatsToNormal(requester);
            } else {
                playerStatBuff.updatePlayerStatsToRPG(requester);
            }

            target.sendMessage(ChatColor.GREEN + "You have successfully teleported " + requester.getName() + " to your location.");
            requester.sendMessage(ChatColor.GREEN + "You have been teleported to " + target.getName() + "'s location.");
        } else {
            target.sendMessage(ChatColor.RED + "Teleportation failed. Ensure you are in a safe location.");
            requester.sendMessage(ChatColor.RED + "Teleportation failed. The accepter's location is not safe.");
        }
    }
}
