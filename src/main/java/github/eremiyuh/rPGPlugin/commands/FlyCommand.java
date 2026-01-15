package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import github.eremiyuh.rPGPlugin.RPGPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class FlyCommand implements CommandExecutor {

    private final PlayerProfileManager profileManager;
    private final RPGPlugin plugin;
    private final String protectedWorldName = "world_rpg"; // Protected world name

    public FlyCommand(PlayerProfileManager profileManager, RPGPlugin plugin) {
        this.profileManager = profileManager;
        this.plugin = plugin;
    }

    private final Map<UUID, BukkitTask> flyTasks = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (player.getWorld().getName().contains("labyrinth")) {
            player.sendMessage("Not allowed here");
            return true;
        }

        UserProfile profile = profileManager.getProfile(player.getName());
        if (profile == null) {
            player.sendMessage("Profile not found.");
            return true;
        }

        UUID uuid = player.getUniqueId();

        // ======================
        // TURN OFF FLY
        // ======================
        if (player.getAllowFlight()) {
            disableFly(player);
            player.sendMessage("Fly mode disabled.");
            return true;
        }

        // ======================
        // PREMIUM = FREE FLY
        // ======================
        if (profile.getIsPremium()) {
            player.setAllowFlight(true);
            player.sendMessage("Fly mode enabled (Premium).");
            return true;
        }

        // ======================
        // NON-PREMIUM CHECK
        // ======================
        if (profile.getDiamond() < 10) {
            player.sendMessage("You do not have enough diamonds to enable fly mode.");
            player.sendMessage("Convert diamonds in your inventory to currency.");
            player.sendMessage("Enter /convertmaterial diamond");
            return true;
        }

        // Initial cost
        profile.setDiamond(profile.getDiamond() - 10);
        player.setAllowFlight(true);
        player.sendMessage("Fly mode enabled. Diamonds remaining: " + profile.getDiamond());

        // ======================
        // DRAIN TASK
        // ======================
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {

                if (!player.isOnline() || !player.getAllowFlight()) {
                    cancel();
                    flyTasks.remove(uuid);
                    return;
                }

                if (profile.getDiamond() >= 10) {
                    profile.setDiamond(profile.getDiamond() - 10);
                    player.sendMessage("Charged 10 diamonds. Remaining: " + profile.getDiamond());
                } else {
                    disableFly(player);
                    player.sendMessage("You have run out of diamonds for flying.");
                    cancel();
                    flyTasks.remove(uuid);
                }
            }
        }.runTaskTimer(plugin, 1200L, 1200L);

        flyTasks.put(uuid, task);
        return true;
    }

    private void disableFly(Player player) {
        player.setAllowFlight(false);
        player.setFlying(false);

        BukkitTask task = flyTasks.remove(player.getUniqueId());
        if (task != null) {
            task.cancel();
        }
    }

    // Called from PlayerChangedWorldEvent
    public void onPlayerWorldChange(Player player) {
        disableFly(player);
    }


}
