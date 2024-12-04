package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.buffs.PlayerStatBuff;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class HomeCommand implements CommandExecutor {

    private final PlayerProfileManager profileManager;
    private  final PlayerStatBuff playerStatBuff;

    public HomeCommand(PlayerProfileManager profileManager, PlayerStatBuff playerStatBuff) {
        this.profileManager = profileManager;
        this.playerStatBuff = playerStatBuff;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        UserProfile profile = profileManager.getProfile(player.getName());

        if (profile.getEnderPearl()<1) {
            player.sendMessage("Need ender pearl to teleport");
            return true;
        }

        if (profile == null) {
            player.sendMessage("Your profile could not be found.");
            return true;
        }

        if (args.length < 1) {
            // No home name provided; display list of homes
            Map<String, Location> homes = profile.getHomes();
            if (homes.isEmpty()) {
                player.sendMessage("You have not set any homes.");
            } else {
                player.sendMessage("Your homes: " + String.join(", ", homes.keySet()));
                player.sendMessage("Use /home <homeName> to teleport to a specific home.");
            }
            return true;
        }

        String homeName = args[0];
        Location homeLocation = profile.getHome(homeName);

        if (homeLocation == null) {
            Map<String, Location> homes = profile.getHomes();
            player.sendMessage("Home '" + homeName + "' does not exist.");
            player.sendMessage("Your homes: " + String.join(", ", homes.keySet()));
            player.sendMessage("Use /home <homeName> to teleport to a specific home.");
            return true;
        }

        if (player.teleport(homeLocation)) {
            player.sendMessage("Teleported to home '" + homeName + "'.");
            profile.setEnderPearl(profile.getEnderPearl()-1);
            playerStatBuff.updatePlayerStatsToNormal(player);

            if (homeLocation.getWorld().getName().contains("labyrinth") || homeLocation.getWorld().getName().contains("_rpg")) {
                playerStatBuff.updatePlayerStatsToRPG(player);
            }

        }
        else {
            player.sendMessage("Error: failed to teleport error");
        }

        return true;
    }
}
