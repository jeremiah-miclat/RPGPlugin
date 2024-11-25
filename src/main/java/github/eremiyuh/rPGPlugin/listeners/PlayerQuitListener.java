package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.manager.VaultManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import java.util.Map;

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
    }
}
