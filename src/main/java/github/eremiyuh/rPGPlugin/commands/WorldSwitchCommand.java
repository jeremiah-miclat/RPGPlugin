package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldSwitchCommand implements CommandExecutor {

    private final RPGPlugin plugin;

    public WorldSwitchCommand(RPGPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        // Check for proper arguments
        if (args.length < 1) {
            player.sendMessage("Specify the world to switch to: /switchworld <rpg|normal>");
            return false;
        }

        // Determine the target world based on input
        String worldType = args[0].toLowerCase();
        String targetWorldName;

        switch (worldType) {
            case "rpg":
                targetWorldName = "world_rpg";
                break;
            case "normal":
                targetWorldName = "world";
                break;
            default:
                player.sendMessage("Invalid world specified. Use 'rpg' or 'normal'.");
                return false;
        }

        // Check if the player is already in the target world
        if (player.getWorld().getName().equals(targetWorldName)) {
            player.sendMessage("You are already in " + targetWorldName + " world.");
            return true;
        }

        // Load the target world if it is not already loaded
        World world = plugin.getServer().getWorld(targetWorldName);
        if (world == null) {
            player.sendMessage("Failed to load the world " + targetWorldName + ".");
            return true;
        }

        // Teleport the player to the spawn location of the target world
        Location spawnLocation = world.getSpawnLocation();
        player.teleport(spawnLocation);
        player.sendMessage("Teleported to " + targetWorldName + "!");
        return true;
    }
}
