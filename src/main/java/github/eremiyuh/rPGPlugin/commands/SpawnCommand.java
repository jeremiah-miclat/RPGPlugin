package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.buffs.PlayerStatBuff;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {

    private final PlayerProfileManager profileManager;
    private final PlayerStatBuff playerStatBuff;

    public SpawnCommand(PlayerProfileManager profileManager, PlayerStatBuff playerStatBuff) {
        this.profileManager = profileManager;
        this.playerStatBuff = playerStatBuff;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        UserProfile userProfile = profileManager.getProfile(player.getName());

        if (userProfile.getEnderPearl()<1) {
            player.sendMessage("Need ender pearl to teleport");
            return true;
        }

        World world = Bukkit.getWorld("world"); // Get the "world" by name

        if (world == null) {
            player.sendMessage("The 'world' does not exist.");
            return true;
        }

        Location spawnLocation = world.getSpawnLocation(); // Get the spawn location of the "world"

        // Teleport the player to the spawn location of "world"
        if (player.teleport(spawnLocation))
        {
            userProfile.setEnderPearl(userProfile.getEnderPearl() - 1);

            playerStatBuff.updatePlayerStatsToNormal(player);
            player.sendMessage("You teleported to overworld spawn area.");
            return true;
        }
        else {
            player.sendMessage("Teleport failed. Report to admin");
            return true;
        }
    }
}

