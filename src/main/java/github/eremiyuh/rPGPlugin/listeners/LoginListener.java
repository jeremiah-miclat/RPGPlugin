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
        Player player = event.getPlayer();
        String message = event.getMessage().toLowerCase();

        // 1. Check for bad words
        if (containsBadWord(message)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "⚠ Your message was blocked due to inappropriate language.");
            Bukkit.getLogger().warning("[ChatFilter] " + player.getName() + " tried to say: " + message);
            return;
        }

        // 2. Block specific words/commands in _rpg worlds
        if (player.getWorld().getName().toLowerCase().contains("_rpg")) {
            String[] blockedWords = {"/sit", "/spin", "/crawl", "/lay", "/gsit", "/bellyflop"};
            for (String word : blockedWords) {
                if (message.contains(word)) { // strong contains check
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "⚠ This action is disabled in abyss worlds.");
                    Bukkit.getLogger().info("[ChatFilter] Blocked " + player.getName() + " from saying: " + message);
                    return;
                }
            }
        }
    }


    private boolean containsBadWord(String message) {
        String[] bannedWords = {
                // Tagalog
                "bobo ", "tanga ", "gago ", "ulol ", " puta ", "pakyu", "tang i","tangina","siraulo", "sira ulo"," puki ","puke",
                "tits","titi","tite","burat","bembang","kantot","kantut"," pota "," bubu ",
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
