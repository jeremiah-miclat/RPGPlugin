package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class HungerListener implements Listener {
    private final PlayerProfileManager profileManager;

    public HungerListener(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @EventHandler
    public void onHungerChange(FoodLevelChangeEvent event) {
        if (!event.getEntity().getWorld().getName().contains("rpg")) return;
        if (!(event.getEntity() instanceof Player player)) return;

        // ⛔ Only trigger when hunger is decreasing
        if (event.getFoodLevel() >= player.getFoodLevel()) return;

        UserProfile playerProfile = profileManager.getProfile(player.getName());
        if (playerProfile == null) return;

        if (playerProfile.getStamina() > 0) {
            int newStamina = Math.max(0, playerProfile.getStamina() - 1);
            playerProfile.setStamina(newStamina);
            event.setCancelled(true); // Cancel natural hunger drop
        }
    }
}
