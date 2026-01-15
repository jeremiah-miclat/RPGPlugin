package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.buffs.PlayerStatBuff;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import github.eremiyuh.rPGPlugin.RPGPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeCommand implements CommandExecutor {

    private final PlayerProfileManager profileManager;
    private final PlayerStatBuff playerStatBuff;
    private final RPGPlugin plugin;

    // Tracks players doing cast-time teleport
    private final Map<UUID, BukkitTask> teleportTasks = new HashMap<>();

    public HomeCommand(PlayerProfileManager profileManager, PlayerStatBuff playerStatBuff, RPGPlugin plugin) {
        this.profileManager = profileManager;
        this.playerStatBuff = playerStatBuff;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        UserProfile profile = profileManager.getProfile(player.getName());
        if (profile == null) {
            player.sendMessage("Your profile could not be found.");
            return true;
        }

        // Display home list if no name provided
        if (args.length < 1) {
            Map<String, Location> homes = profile.getHomes();
            if (homes.isEmpty()) {
                player.sendMessage("You have not set any homes.");
            } else {
                player.sendMessage("Your homes: " + String.join(", ", homes.keySet()));
                player.sendMessage("Use /home <homeName> to teleport to a specific home.");
            }
            return true;
        }

        String homeName = args[0];
        Location homeLocation = profile.getHome(homeName);

        if (homeLocation == null) {
            Map<String, Location> homes = profile.getHomes();
            player.sendMessage("Home '" + homeName + "' does not exist.");
            if (!homes.isEmpty()) {
                player.sendMessage("Your homes: " + String.join(", ", homes.keySet()));
            }
            player.sendMessage("Use /home <homeName> to teleport to a specific home.");
            return true;
        }

        // =========================
        // HAS ENDER PEARL → INSTANT
        // =========================
        if (profile.getEnderPearl() >= 1) {
            profile.setEnderPearl(profile.getEnderPearl() - 1);
            teleportPlayer(player, homeLocation, homeName, profile);
            return true;
        }

        // =========================
        // NO ENDER PEARL → CAST TIME
        // =========================
        player.sendMessage("No Ender Pearl detected. Stand still for 5 seconds to teleport...");

        Location startLocation = player.getLocation().clone();
        UUID uuid = player.getUniqueId();

        BukkitTask task = new BukkitRunnable() {
            int secondsLeft = 5;

            @Override
            public void run() {

                if (!player.isOnline()) {
                    cancel();
                    teleportTasks.remove(uuid);
                    return;
                }

                // Cancel if moved (block-level)
                Location loc = player.getLocation();
                if (loc.getBlockX() != startLocation.getBlockX()
                        || loc.getBlockY() != startLocation.getBlockY()
                        || loc.getBlockZ() != startLocation.getBlockZ()) {
                    player.sendMessage("Teleport cancelled because you moved.");
                    cancel();
                    teleportTasks.remove(uuid);
                    return;
                }

                if (secondsLeft <= 0) {
                    teleportPlayer(player, homeLocation, homeName, profile);
                    cancel();
                    teleportTasks.remove(uuid);
                    return;
                }

                player.sendMessage("Teleporting in " + secondsLeft + "...");
                secondsLeft--;
            }
        }.runTaskTimer(plugin, 0L, 20L); // 20 ticks = 1 second

        teleportTasks.put(uuid, task);
        return true;
    }

    private void teleportPlayer(Player player, Location homeLocation, String homeName, UserProfile profile) {
        if (player.teleport(homeLocation)) {
            player.sendMessage("Teleported to home '" + homeName + "'.");
            playerStatBuff.updatePlayerStatsToNormal(player);

            if (homeLocation.getWorld().getName().contains("labyrinth")
                    || homeLocation.getWorld().getName().contains("_rpg")) {
                playerStatBuff.updatePlayerStatsToRPG(player);
            }
        } else {
            player.sendMessage("Error: failed to teleport. Report to admin.");
        }
    }
}
