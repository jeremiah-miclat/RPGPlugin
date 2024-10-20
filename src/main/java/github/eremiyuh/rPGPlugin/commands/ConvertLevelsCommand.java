package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ConvertLevelsCommand implements CommandExecutor {

    private final PlayerProfileManager profileManager;

    public ConvertLevelsCommand(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        int playerLevels = player.getLevel();

        if (playerLevels < 100) {
            player.sendMessage(ChatColor.RED + "You need at least 100 experience levels to convert to 1 attribute point.");
            return true;
        }

        // Deduct 100 levels from the player
        player.setLevel(playerLevels - 100);

        // Get the player's profile and add 1 attribute point
        UserProfile profile = profileManager.getProfile(player.getName());
        profile.setCurrentAttributePoints(profile.getCurrentAttributePoints() + 1);

        // Save the profile
        profileManager.saveProfile(player.getName());

        player.sendMessage(ChatColor.GREEN + "You have converted 100 levels into 1 attribute point!");

        return true;
    }
}