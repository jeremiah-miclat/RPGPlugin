package github.eremiyuh.rPGPlugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import java.util.Random;

public class RTPCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final Random random = new Random();
    private final HashMap<Player, Long> cooldowns = new HashMap<>();  // To store cooldown times

    public RTPCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        player.sendMessage("feature disabled");

        // Check if player is on cooldown
//        if (isOnCooldown(player)) {
//            long remainingTime = getRemainingCooldownTime(player);
//            player.sendMessage("To reduce lag. You must wait " + remainingTime + " seconds before using this command again.");
//            return true;
//        }
//
//        World world = player.getWorld();
//
//        if (world.getEnvironment() == World.Environment.NETHER) {
//            player.sendMessage("Not allowed in the Nether.");
//            return true;
//        }
//
//        WorldBorder border = world.getWorldBorder();
//        double borderSize = border.getSize() / 2;  // Half-size radius from the center
//        double centerX = border.getCenter().getX();
//        double centerZ = border.getCenter().getZ();
//
//        // Attempt to find a safe location within world borders
//        Location randomLocation = getRandomSafeLocation(world, centerX, centerZ, borderSize);
//
//        if (randomLocation != null) {
//            player.teleport(randomLocation);
//            player.sendMessage("You have been randomly teleported!");
//
//            // Set cooldown for this player
//            setCooldown(player);
//        } else {
//            player.sendMessage("Could not find a safe location to teleport.");
//        }

        return true;
    }

    private Location getRandomSafeLocation(World world, double centerX, double centerZ, double borderSize) {
        for (int i = 0; i < 10; i++) {  // Try up to 10 times to find a safe spot
            double x = centerX + (random.nextDouble() * 2 - 1) * borderSize;
            double z = centerZ + (random.nextDouble() * 2 - 1) * borderSize;
            int y = world.getHighestBlockYAt((int) x, (int) z);
            Location location = new Location(world, x, y, z);

            // Check if the location is safe (not lava, water, etc.)
            Material blockType = location.getBlock().getType();
            if (blockType.isSolid()) {
                return location.add(0, 1, 0);
            }
        }
        return null;  // Return null if no safe spot is found
    }

    // Check if the player is on cooldown
    private boolean isOnCooldown(Player player) {
        if (cooldowns.containsKey(player)) {
            long lastUsed = cooldowns.get(player);
            long timePassed = System.currentTimeMillis() - lastUsed;
            // 60000 milliseconds = 1 minute
            return timePassed < 60000;
        }
        return false;
    }

    // Get the remaining time (in seconds) that the player must wait before using the command again
    private long getRemainingCooldownTime(Player player) {
        if (cooldowns.containsKey(player)) {
            long lastUsed = cooldowns.get(player);
            long timePassed = System.currentTimeMillis() - lastUsed;
            long remainingTime = 60000 - timePassed; // Remaining time in milliseconds
            return remainingTime / 1000; // Convert milliseconds to seconds
        }
        return 0;
    }

    // Set cooldown for the player
    private void setCooldown(Player player) {
        cooldowns.put(player, System.currentTimeMillis());
    }
}
