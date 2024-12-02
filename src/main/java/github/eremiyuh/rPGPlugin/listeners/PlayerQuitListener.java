package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.manager.VaultManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import java.util.Map;
import java.util.Random;

public class PlayerQuitListener implements Listener {

    private final PlayerProfileManager profileManager;
    private final VaultManager vaultManager;

    public PlayerQuitListener(PlayerProfileManager profileManager, VaultManager vaultManager) {
        this.profileManager = profileManager;
        this.vaultManager = vaultManager;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String playerName = event.getPlayer().getName();
        UserProfile playerProfile = profileManager.getProfile(playerName);
        playerProfile.setLoggedIn(false);
        profileManager.removeProfileOnLogout(playerName);
        vaultManager.saveAllVaultsForPlayer(player);

        // Customize the quit message
        String quitMessage = generateColoredQuitMessage(playerName);
        event.setQuitMessage(quitMessage); // Set the custom quit message
    }

    // Generate a fun and colored quit message
    private String generateColoredQuitMessage(String playerName) {
        // You can expand this with more creative or themed messages
        String[] messages = {
                ChatColor.GREEN + playerName + ChatColor.YELLOW + " has left the server! The adventure continues elsewhere...",
                ChatColor.AQUA + "Farewell, " + ChatColor.GOLD + playerName + ChatColor.AQUA + "! We'll miss your presence in the world!",
                ChatColor.LIGHT_PURPLE + playerName + ChatColor.GRAY + " logs off, but their legend lives on!",
                ChatColor.RED + playerName + ChatColor.DARK_GRAY + " left the server... until next time, hero!",
                ChatColor.DARK_GREEN + "The realm feels emptier now... " + ChatColor.WHITE + playerName + ChatColor.DARK_GREEN + " has departed."
        };

        // Select a random message
        Random random = new Random();
        return messages[random.nextInt(messages.length)];
    }
}
