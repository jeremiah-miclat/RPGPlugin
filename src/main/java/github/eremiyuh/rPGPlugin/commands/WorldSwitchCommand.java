package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import github.eremiyuh.rPGPlugin.buffs.PlayerStatBuff;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldSwitchCommand implements CommandExecutor {

    private final RPGPlugin plugin;
    private final PlayerStatBuff playerStatBuff;
    public WorldSwitchCommand(RPGPlugin plugin, PlayerStatBuff playerStatBuff) {
        this.plugin = plugin;

        this.playerStatBuff = playerStatBuff;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage("Specify the world to switch to: /switchworld <abyss|normal>");
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
//            case "l":
//                targetWorldName = "world_labyrinth";
//                break;
//            case "e":
//                targetWorldName = "world_the_end";
//                break;
            case "ad":
                targetWorldName = "world_labyrinth2";
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
        player.teleport(spawnLocation);

        playerStatBuff.updatePlayerStatsToNormal(player);

        if (targetWorldName.contains("rpg") ||targetWorldName.contains("labyrinth")) {
            playerStatBuff.updatePlayerStatsToRPG(player);
        }



        return true;
    }
}
