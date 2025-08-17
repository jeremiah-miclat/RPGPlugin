package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportToHighestBlock implements CommandExecutor {
    private final PlayerProfileManager profileManager;

    public TeleportToHighestBlock(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (((Player) sender).getWorld().toString().contains("labyrinth")) {
            sender.sendMessage("Can not teleport to highest block.");
            return true;
        }


        UserProfile profile = profileManager.getProfile(sender.getName());
        if (profile.getEnderPearl() < 1) {
            sender.sendMessage("You need an ender pearl to teleport.");
            return true;
        }

        Player player = (Player) sender;
        World world = player.getWorld();
        Location playerLocation = player.getLocation();
        Location targetLocation = null;

        // Set height boundaries for the search
        int heightLimit = world.getMaxHeight();
        int upperBound = world.getEnvironment() == World.Environment.NETHER ? 120 : heightLimit;  // Avoid Nether roof
        int lowerBound = 0;  // Start searching from ground level
        // Loop through each block in a 5x5 radius around the player's X and Z position
        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                Location location = playerLocation.clone().add(x, 0, z);

                if (world.getEnvironment() == World.Environment.NETHER) {
                    // In the Nether, search from upperBound down to lowerBound to avoid the roof
                    for (int y = upperBound; y >= lowerBound; y--) {
                        location.setY(y);

                        // Check if this block is solid and has two air blocks above it
                        if (location.getBlock().getType().isSolid() &&
                                world.getBlockAt(location.clone().add(0, 1, 0)).getType() == Material.AIR &&
                                world.getBlockAt(location.clone().add(0, 2, 0)).getType() == Material.AIR) {

                            if (targetLocation == null || location.getY() > targetLocation.getY()) {
                                targetLocation = location.clone().add(0, 1, 0); // One block above solid ground
                            }
                            break; // Found a valid block, no need to continue this column
                        }
                    }
                } else {
                    // For non-Nether worlds, use the highest block at this location up to the height limit
                    Location highestBlock = world.getHighestBlockAt(location).getLocation();
                    if (highestBlock.getY() > heightLimit) {
                        continue;
                    }

                    Block above1 = world.getBlockAt(highestBlock.clone().add(0, 1, 0));
                    Block above2 = world.getBlockAt(highestBlock.clone().add(0, 2, 0));

                    boolean passable1 = above1.getType() == Material.AIR || above1.getType() == Material.SNOW;
                    boolean passable2 = above2.getType() == Material.AIR || above2.getType() == Material.SNOW;

                    if (passable1 && passable2) {
                        if (targetLocation == null || highestBlock.getY() > targetLocation.getY()) {
                            targetLocation = highestBlock.clone().add(0, 1, 0);
                        }
                    }
                }
            }
        }

        if (targetLocation != null) {
            profile.setEnderPearl(profile.getEnderPearl() - 1);
            player.teleport(targetLocation);
            player.sendMessage("Teleported to the highest block nearby!");
        } else {
            player.sendMessage("No suitable block found nearby.");
        }

        return true;
    }
}
