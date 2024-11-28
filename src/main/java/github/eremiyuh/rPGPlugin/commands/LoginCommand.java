package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LoginCommand implements CommandExecutor {
    private final RPGPlugin plugin;
    private final PlayerProfileManager profileManager;

    public LoginCommand(RPGPlugin plugin, PlayerProfileManager profileManager) {
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

        if (profile.getPassword().isEmpty()) {
            player.sendMessage("New account. You must register first. Enter: /register <password> <password>");
        }

        if (profile.isLoggedIn()) {
            player.sendMessage("You are already logged in.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("Usage: /login <password>");
            return true;
        }

        String password = args[0];
        if (profile.getPassword().equals(password)) {
            profile.setLoggedIn(true);
            player.sendMessage("Login successful!");
        } else {
            player.sendMessage("Incorrect password. Please try again.");
        }

        return true;
    }
}

