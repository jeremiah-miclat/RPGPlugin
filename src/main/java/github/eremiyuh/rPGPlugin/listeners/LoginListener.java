package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
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

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage().toLowerCase();

        if (containsBadWord(message)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "⚠ Your message was blocked due to inappropriate language.");

            // Optional: log to console or take further action
            Bukkit.getLogger().warning("[ChatFilter] " + event.getPlayer().getName() + " tried to say: " + message);

            // Optional: kick or ban
            // event.getPlayer().kickPlayer("You were kicked for using offensive language.");
            // Bukkit.getBanList(BanList.Type.NAME).addBan(event.getPlayer().getName(), "Banned for profanity", null, null);
        }
    }

    private boolean containsBadWord(String message) {
        String[] bannedWords = {
                // Tagalog
                "bobo", "tanga", "gago", "ulol", " puta ", "pakyu", "tang i","tangina","siraulo", "sira ulo","puki","puke",
                "tits","titi","tite","burat","bembang","kantot","kantut"," pota ","bubu",
                // English
                "fuck", "shit", "asshole", "idiot", "bastard"," ass ","nigga"," fuk ","dick","pussy"
        };

        for (String word : bannedWords) {
            if (message.contains(word)) {
                return true;
            }
        }
        return false;
    }


}
