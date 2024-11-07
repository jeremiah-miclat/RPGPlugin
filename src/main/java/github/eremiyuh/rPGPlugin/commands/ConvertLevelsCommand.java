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

        // Get the player's profile and total allocated attribute points
        UserProfile profile = profileManager.getProfile(player.getName());
        double playerClassTotalAttrib = profile.getTotalAllocatedPoints();
        double abyssPoints = profile.getAbyssPoints();

        if (playerClassTotalAttrib==10000) {
            player.sendMessage("Max attributes reached.");
            return true;
        }

        // Determine how many levels are required to convert to 1 attribute point
        int levelsRequired = getLevelsRequiredForConversion(playerClassTotalAttrib);

        // Check if the player has enough levels
        if (playerLevels < levelsRequired) {
            player.sendMessage(ChatColor.RED + "You need at least " + levelsRequired + " experience levels to convert to 1 attribute point.");
            return true;
        }

        // Allocate 1 attribute point and reduce experience levels
        profile.setCurrentAttributePoints(profile.getCurrentAttributePoints() + 1);
        player.setLevel(playerLevels - levelsRequired);

        // Save the profile
        profileManager.saveProfile(player.getName());

        player.sendMessage(ChatColor.GREEN + "You have converted " + levelsRequired + " levels into 1 attribute point!");

        return true;
    }

    /**
     * This method determines how many levels are required to convert to 1 attribute point,
     * based on the total allocated attribute points.
     *
     * @param totalAttrib The total allocated attribute points.
     * @return The number of levels required for conversion.
     */
    private int getLevelsRequiredForConversion(double totalAttrib) {
        // Adjust the conversion rate based on total allocated attribute points
        if (totalAttrib < 100) {
            return 10;  // this is equal to 170 abyss points



        } else if (totalAttrib < 1000) {
            return 100;  // this is equal to 29315 abyss points
        } else if (totalAttrib < 2000) {
            return 200;  // total to 155015 abyss points
        } else
            return 300; // total to 380715 abyss points
    }
}
