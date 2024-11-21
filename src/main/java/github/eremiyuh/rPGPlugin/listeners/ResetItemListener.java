package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import github.eremiyuh.rPGPlugin.utils.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class ResetItemListener implements Listener {
    private final PlayerProfileManager profileManager;

    public ResetItemListener(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @EventHandler
    public void onPlayerRightClick(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        // Check if item matches the reset item
        if (item.isSimilar(ItemUtils.getResetItem())) {
            // Assuming you have a method to get the player's UserProfile
            UserProfile profile = profileManager.getProfile(player.getName());
            int allocatedTotal = profile.getTotalAllocatedPoints();
            int pointsToRefund = allocatedTotal;

            profile.setCurrentAttributePoints(profile.getCurrentAttributePoints()+pointsToRefund);
            // Reset each class's attributes
            profile.getArcherClassInfo().resetAttributes();
            profile.getSwordsmanClassInfo().resetAttributes();
            profile.getAlchemistClassInfo().resetAttributes();



            // Provide feedback to the player
            player.sendMessage("All class attributes have been reset, and points refunded.");

            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (itemInHand.isSimilar(item)) {
                int newAmount = itemInHand.getAmount() - 1;
                if (newAmount > 0) {
                    itemInHand.setAmount(newAmount);
                } else {
                    player.getInventory().setItemInMainHand(null);
                }
            }
            profileManager.saveProfile(player.getName());
            // Prevent further interactions
            event.setCancelled(true);
        }
    }


}
