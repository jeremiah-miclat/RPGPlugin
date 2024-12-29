package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import io.papermc.paper.event.player.PlayerTradeEvent;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class ActivityListener implements Listener {
    private final PlayerProfileManager profileManager;

    public ActivityListener(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        UserProfile userProfile = profileManager.getProfile(event.getPlayer().getName());
        userProfile.setActivitypoints(userProfile.getActivitypoints()+1);
    }

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event) {
        UserProfile userProfile = profileManager.getProfile(event.getPlayer().getName());
        userProfile.setActivitypoints(userProfile.getActivitypoints()+1);
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        // Retrieve the player's profile
        UserProfile userProfile = profileManager.getProfile(event.getPlayer().getName());

        // Get the caught entity
        Entity entity = event.getCaught();

        // Ensure the caught entity is an instance of Item
        if (entity instanceof Item item) {
            // Get the item type name
            String itemName = item.getName();

            // Update the currency based on the type of fish caught
            assert itemName != null;
            if (itemName.contains("Cod")) {
                userProfile.setCurrency("activitypoints", userProfile.getCurrency("activitypoints") + 5);
            } else if (itemName.contains("Salmon")) {
                userProfile.setCurrency("activitypoints", userProfile.getCurrency("activitypoints") + 15);
            } else if (itemName.contains("Tropical")) {
                userProfile.setCurrency("activitypoints", userProfile.getCurrency("activitypoints") + 25);
            } else if (itemName.contains("Puffer")) {
                userProfile.setCurrency("activitypoints", userProfile.getCurrency("activitypoints") + 50);
            }
        }
    }


    @EventHandler
    public void onTrade(PlayerTradeEvent event) {
        UserProfile userProfile = profileManager.getProfile(event.getPlayer().getName());
        userProfile.setActivitypoints(userProfile.getActivitypoints()+1);
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        UserProfile userProfile = profileManager.getProfile(event.getPlayer().getName());
        userProfile.setActivitypoints(userProfile.getActivitypoints()+1);
    }
}
