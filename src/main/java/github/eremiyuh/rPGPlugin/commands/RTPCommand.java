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
import java.util.Random;

public class RTPCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final Random random = new Random();

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
        World world = player.getWorld();

        if (player.getWorld().getEnvironment() == World.Environment.NETHER ) {
            player.sendMessage("Not allowed");
            return true;
        }


        WorldBorder border = world.getWorldBorder();

        // Define border limits
        double borderSize = border.getSize() / 2;  // Half-size radius from the center
        double centerX = border.getCenter().getX();
        double centerZ = border.getCenter().getZ();

        // Attempt to find a safe location within world borders
        Location randomLocation = getRandomSafeLocation(world, centerX, centerZ, borderSize);

        if (randomLocation != null) {
            player.teleport(randomLocation);
            player.sendMessage("You have been randomly teleported!");
        } else {
            player.sendMessage("Could not find a safe location to teleport.");
        }

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
                return location.add(0,1,0);
            }
        }
        return null;  // Return null if no safe spot is found
    }
}