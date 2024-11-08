package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegisterCommand implements CommandExecutor {
    private final RPGPlugin plugin;
    private final PlayerProfileManager profileManager;

    public RegisterCommand(RPGPlugin plugin, PlayerProfileManager profileManager) {
        this.plugin = plugin;
        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        UserProfile profile = profileManager.getProfile(player.getName());

        // Check if the player is already registered
        if (!profile.getPassword().isEmpty()) {
            player.sendMessage("You are already registered.");
            return true;
        }

        // Check if the player provided two password arguments
        if (args.length != 2) {
            player.sendMessage("Usage: /register <password> <password>");
            return true;
        }

        String password1 = args[0];
        String password2 = args[1];

        // Verify that both passwords match
        if (!password1.equals(password2)) {
            player.sendMessage("Passwords do not match. Please try again with /register <password> <password>.");
            return true;
        }

        profile.setPassword(password1);
        player.sendMessage("Registration successful. Use /login <password> to log in.");

        return true;
    }
}

