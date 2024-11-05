package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeleteHomeCommand implements CommandExecutor {

    private final PlayerProfileManager profileManager;

    public DeleteHomeCommand(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage("Usage: /homedelete <homeName>");
            return true;
        }

        String homeName = args[0];
        UserProfile profile = profileManager.getProfile(player.getName());

        if (profile == null) {
            player.sendMessage("Your profile could not be found.");
            return true;
        }

        if (profile.removeHome(homeName)) {
            player.sendMessage("Home '" + homeName + "' has been deleted.");
            profileManager.saveProfile(player.getName()); // Save changes to the profile
        } else {
            player.sendMessage("Home '" + homeName + "' does not exist.");
        }
        return true;
    }
}
