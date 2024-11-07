package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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

        UserProfile profile = profileManager.getProfile(sender.getName());
        if (profile.getEnderPearl()<1) {
            sender.sendMessage("Need ender pearl to teleport.");
            return true;
        }

        Player player = (Player) sender;
        World world = player.getWorld();
        Location playerLocation = player.getLocation();
        Location targetLocation = null;



        // Define height limit if in the Nether (ignore blocks above Y=127)
        int heightLimit = world.getEnvironment() == World.Environment.NETHER ? 110 : world.getMaxHeight();

        // Loop through each block in a 5x5 radius around the player's X and Z position
        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                Location location = playerLocation.clone().add(x, 0, z);
                Location highestBlock = world.getHighestBlockAt(location).getLocation();

                // Skip blocks above the height limit
                if (highestBlock.getY() > heightLimit) {
                    continue;
                }

                // Check if there are two air blocks above the highest block
                if (world.getBlockAt(highestBlock.clone().add(0, 1, 0)).getType() == Material.AIR &&
                        world.getBlockAt(highestBlock.clone().add(0, 2, 0)).getType() == Material.AIR) {

                    // Update the target location to the highest block if it's the highest point found
                    if (targetLocation == null || highestBlock.getY() > targetLocation.getY()) {
                        targetLocation = highestBlock.clone().add(0, 1, 0); // One block above the highest block
                    }
                }
            }
        }

        if (targetLocation != null) {
            profile.setEnderPearl(profile.getEnderPearl()-1);
            player.teleport(targetLocation);
            player.sendMessage("Teleported to the highest block nearby!");
        } else {
            player.sendMessage("No suitable block found nearby.");
        }

        return true;
    }
}
