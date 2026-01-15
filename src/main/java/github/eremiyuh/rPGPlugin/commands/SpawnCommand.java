package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import github.eremiyuh.rPGPlugin.buffs.PlayerStatBuff;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SpawnCommand implements CommandExecutor {

    private final PlayerProfileManager profileManager;
    private final PlayerStatBuff playerStatBuff;
    private final JavaPlugin plugin;

    private final Set<UUID> teleportingPlayers = new HashSet<>();

    public SpawnCommand(PlayerProfileManager profileManager, PlayerStatBuff playerStatBuff, JavaPlugin plugin) {
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

        UserProfile userProfile = profileManager.getProfile(player.getName());
        if (userProfile == null) {
            player.sendMessage("Profile not found.");
            return true;
        }

        // Prevent multiple teleports
        if (teleportingPlayers.contains(player.getUniqueId())) {
            player.sendMessage("You are already teleporting!");
            return true;
        }

        World world = Bukkit.getWorld("world");
        if (world == null) {
            player.sendMessage("The 'world' does not exist.");
            return true;
        }

        Location spawnLocation = world.getSpawnLocation();

        // HAS ENDER PEARL → INSTANT
        if (userProfile.getEnderPearl() >= 1) {
            userProfile.setEnderPearl(userProfile.getEnderPearl() - 1);
            player.teleport(spawnLocation);
            playerStatBuff.updatePlayerStatsToNormal(player);
            player.sendMessage("You teleported to overworld spawn area.");
            return true;
        }

        // NO ENDER PEARL → 5s CAST
        teleportingPlayers.add(player.getUniqueId());
        Location startLocation = player.getLocation().clone();
        player.sendMessage("No Ender Pearl detected. Stand still for 5 seconds to teleport...");

        new BukkitRunnable() {
            int secondsLeft = 5;

            @Override
            public void run() {

                if (!player.isOnline()) {
                    teleportingPlayers.remove(player.getUniqueId());
                    cancel();
                    return;
                }

                if (!player.getWorld().equals(startLocation.getWorld()) ||
                        player.getLocation().distance(startLocation) > 0.5) {
                    player.sendMessage("Teleport cancelled because you moved or changed world.");
                    teleportingPlayers.remove(player.getUniqueId());
                    cancel();
                    return;
                }

                if (secondsLeft <= 0) {
                    player.teleport(spawnLocation);
                    playerStatBuff.updatePlayerStatsToNormal(player);
                    player.sendMessage("You teleported to overworld spawn area.");
                    teleportingPlayers.remove(player.getUniqueId());
                    cancel();
                    return;
                }

                player.sendMessage("Teleporting in " + secondsLeft + "...");
                secondsLeft--;
            }
        }.runTaskTimer(plugin, 0L, 20L);

        return true;
    }
}



