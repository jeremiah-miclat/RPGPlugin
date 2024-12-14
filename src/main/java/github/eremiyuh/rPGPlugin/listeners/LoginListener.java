package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class LoginListener implements Listener {
    private final RPGPlugin plugin;
    private  final PlayerProfileManager profileManager;

    public LoginListener(RPGPlugin plugin, PlayerProfileManager profileManager) {
        this.plugin = plugin;
        this.profileManager = profileManager;
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        String playerName = event.getPlayerProfile().getName();
        if (playerName.equalsIgnoreCase("john")) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "You are already logged in on an infinite world server :)");
        }
        UserProfile profile = profileManager.getProfile(playerName);
        if (profile!=null && profile.isLoggedIn()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "You are already logged in from another location!");
            return;
        }
        // Check if server has finished loading
        if (!plugin.isServerLoaded()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    "Server is still starting up, please try again in a moment.");
        }
    }
}
