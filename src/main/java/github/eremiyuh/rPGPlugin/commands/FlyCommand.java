package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import github.eremiyuh.rPGPlugin.RPGPlugin; // Import your main plugin class

public class FlyCommand implements CommandExecutor {

    private final PlayerProfileManager profileManager;
    private final RPGPlugin plugin; // Reference to your main plugin
    private BukkitTask flyTask; // Store the running task

    public FlyCommand(PlayerProfileManager profileManager, RPGPlugin plugin) {
        this.profileManager = profileManager;
        this.plugin = plugin; // Initialize the plugin instance
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        UserProfile profile = profileManager.getProfile(player.getName());

        // Check if player is allowed to fly
        if (player.getAllowFlight()) {
            // Disable flight
            player.setAllowFlight(false);
            player.setFlying(false);
            player.sendMessage("Fly mode disabled.");
            cancelFlyTask(); // Cancel the task if flying is turned off
        } else {
            // Check if player has diamonds
            if (profile.getDiamond() > 0) {
                profile.setDiamond(profile.getDiamond() - 10); // Deduct 1 diamond for enabling fly mode
                player.setAllowFlight(true);
                player.sendMessage("Fly mode enabled. You have " + profile.getDiamond() + " diamonds remaining.");

                // Start a repeating task to deduct diamonds every minute
                flyTask = new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (profile.getDiamond() > 10) {
                            profile.setDiamond(profile.getDiamond() - 10);
                            player.sendMessage("You have been charged 10 diamond for flying. Diamonds remaining: " + profile.getDiamond());
                        } else {
                            player.setAllowFlight(false); // Disable flight
                            player.setFlying(false);
                            player.sendMessage("You have run out of diamonds for flying. You have been teleported to the ground.");

                            // Teleport player to the ground
                            Location groundLocation = player.getWorld().getHighestBlockAt(player.getLocation()).getLocation();
                            player.teleport(groundLocation);
                            cancel(); // Stop the task
                        }
                    }
                }.runTaskTimer(plugin, 1200L, 1200L); // 1200 ticks = 60 seconds
            } else {
                player.sendMessage("You do not have enough diamonds to enable fly mode.");
            }
        }
        return true;
    }

    // Method to cancel the flying task
    private void cancelFlyTask() {
        if (flyTask != null) {
            flyTask.cancel();
            flyTask = null; // Clear the reference to the canceled task
        }
    }
}
