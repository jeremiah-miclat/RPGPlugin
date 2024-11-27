package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import github.eremiyuh.rPGPlugin.buffs.PlayerStatBuff;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldSwitchCommand implements CommandExecutor {

    private final RPGPlugin plugin;
    private final PlayerStatBuff playerStatBuff;
    private final PlayerProfileManager profileManager;

    public WorldSwitchCommand(RPGPlugin plugin, PlayerStatBuff playerStatBuff, PlayerProfileManager profileManager) {
        this.plugin = plugin;
        this.playerStatBuff = playerStatBuff;

        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        UserProfile userProfile = profileManager.getProfile(player.getName());

        int playerEP = userProfile.getEnderPearl();

        if (playerEP <= 0) {
            player.sendMessage("Ender Pearl required. Get ender pearl and do /convermaterial enderpearl");
        }

        if (args.length < 1) {
            player.sendMessage("/warp a or ad or o | a for abyss | ad for abyss dungeon | o for Overworld");
            return false;
        }

        String worldType = args[0].toLowerCase();
        String targetWorldName;

        switch (worldType) {
            case "a":
                targetWorldName = "world_rpg";
                break;
            case "o":
                targetWorldName = "world";
                break;
            case "e":
                targetWorldName = "world_the_end";
                break;
            case "n":
                targetWorldName = "world_nether";
                break;
            case "ad":
                targetWorldName = "world_labyrinth2";
                break;
            case "r":
                targetWorldName = "world_resource";
                break;
            case "ro":
                targetWorldName = "resource_normal";
                break;
            case "rn":
                targetWorldName = "resource_nether";
                break;
            case "re":
                targetWorldName = "resource_end";
                break;
            default:
                player.sendMessage("/warp a or ad or o | a for abyss | ad for abyss dungeon | o for Overworld");
                return false;
        }

        if (player.getWorld().getName().equals(targetWorldName)) {
            player.sendMessage("You are already in the world.");
            return true;
        }

        World world = plugin.getServer().getWorld(targetWorldName);
        if (world == null) {

            player.sendMessage("Failed to load the world ");
            return true;

//            world = plugin.getServer().createWorld(new WorldCreator(targetWorldName));

        }

        Location spawnLocation = world.getSpawnLocation();

        if (targetWorldName.contains("resource") && (spawnLocation.getBlock().isSolid()
                && spawnLocation.getBlock().getType() != Material.BEDROCK
                && spawnLocation.clone().add(0, 1, 0).getBlock().getType() == Material.AIR
                && spawnLocation.clone().add(0, 2, 0).getBlock().getType() == Material.AIR) ) {

            Location safeLocation = findSafeLocation(world, spawnLocation);

            if (safeLocation == null) {
                player.sendMessage(ChatColor.RED + "No safe teleport location found near the spawn.");
                return true;
            }

            spawnLocation = safeLocation; // Update the spawn location to the safe location
        }


        if (player.teleport(spawnLocation)) {
            userProfile.setEnderPearl(playerEP-1);
        } else {
            player.sendMessage(ChatColor.RED + "Failed to teleport");
        }

        playerStatBuff.updatePlayerStatsToNormal(player);

        if (targetWorldName.contains("rpg") ||targetWorldName.contains("labyrinth")) {
            playerStatBuff.updatePlayerStatsToRPG(player);
        }



        return true;
    }

    private Location findSafeLocation(World world, Location spawnLocation) {
        int radius = 10;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Location checkLocation = spawnLocation.clone().add(x, 0, z);
                for (int y = world.getMinHeight(); y < world.getMaxHeight(); y++) {
                    checkLocation.setY(y);

                    // Check if the current block is solid and has 2 air blocks above it
                    if (checkLocation.getBlock().isSolid()
                            && checkLocation.getBlock().getType() != Material.BEDROCK
                            && checkLocation.clone().add(0, 1, 0).getBlock().getType() == Material.AIR
                            && checkLocation.clone().add(0, 2, 0).getBlock().getType() == Material.AIR) {
                        return checkLocation.clone().add(0, 1, 0); // Return the safe location
                    }
                }
            }
        }

        return null; // No safe location found
    }

}
