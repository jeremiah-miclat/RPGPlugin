package github.eremiyuh.rPGPlugin.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Random;

public class RTPCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final Random random = new Random();

    // Global cooldown (anyone cannot start RTP while another is using it)
    private long lastRTPTime = 0;
    private String lastPlayerName = "None";
    private final int globalCooldown = 20; // seconds

    // Individual player cooldown
    private final HashMap<Player, Long> selfCooldowns = new HashMap<>();
    private final int selfCooldown = 60; // seconds

    public RTPCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        World world = player.getWorld();
        if (world.getEnvironment() == World.Environment.NETHER) {
            player.sendMessage("Not allowed in the Nether.");
            return true;
        }

        // ---------- Self cooldown ----------
        if (selfCooldowns.containsKey(player)) {
            long timePassed = (System.currentTimeMillis() - selfCooldowns.get(player)) / 1000;
            if (timePassed < selfCooldown) {
                long remaining = selfCooldown - timePassed;
                player.sendMessage("You must wait " + remaining + " seconds before using /rtp again.");
                return true;
            }
        }

        // ---------- Global cooldown ----------
        long timeSinceLastRTP = (System.currentTimeMillis() - lastRTPTime) / 1000;
        if (timeSinceLastRTP < globalCooldown) {
            long remaining = globalCooldown - timeSinceLastRTP;
            player.sendMessage("Someone is currently using /rtp! Last used by " + lastPlayerName + ". Wait " + remaining + " seconds.");
            return true;
        }

        final int maxRadius = 10000; // 10k radius
        final Location startLocation = player.getLocation().clone();
        player.sendMessage("Stand still for 5 seconds to randomly teleport...");

        new BukkitRunnable() {
            int secondsLeft = 5;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }

                // Player moved
                Location current = player.getLocation();
                if (current.getBlockX() != startLocation.getBlockX() ||
                        current.getBlockY() != startLocation.getBlockY() ||
                        current.getBlockZ() != startLocation.getBlockZ()) {
                    player.sendMessage("Teleport canceled because you moved.");
                    cancel();
                    return;
                }

                if (secondsLeft <= 0) {
                    // Find safe random location
                    Location randomLocation = null;
                    for (int i = 0; i < 50; i++) {
                        double x = (Math.random() * 2 - 1) * maxRadius;
                        double z = (Math.random() * 2 - 1) * maxRadius;
                        int y = world.getHighestBlockYAt((int) x, (int) z);
                        Location loc = new Location(world, x + 0.5, y, z + 0.5);
                        if (isSafeLocation(loc)) {
                            randomLocation = loc;
                            break;
                        }
                    }

                    if (randomLocation != null) {
                        player.teleport(randomLocation);
                        player.sendMessage("You have been randomly teleported!");

                        // Update cooldowns
                        lastRTPTime = System.currentTimeMillis();
                        lastPlayerName = player.getName();
                        selfCooldowns.put(player, System.currentTimeMillis());
                    } else {
                        player.sendMessage("Could not find a safe location. Try again.");
                    }

                    cancel();
                    return;
                }

                player.sendActionBar(Component.text("Teleporting in " + secondsLeft + "..."));
                secondsLeft--;
            }
        }.runTaskTimer(plugin, 0L, 20L);

        return true;
    }

    private boolean isSafeLocation(Location loc) {
        if (loc == null) return false;
        Block block = loc.getBlock();
        Block below = block.getRelative(0, -1, 0);

        return below.getType().isSolid() &&
                block.getType() == Material.AIR &&
                block.getRelative(0, 1, 0).getType() == Material.AIR;
    }
}
