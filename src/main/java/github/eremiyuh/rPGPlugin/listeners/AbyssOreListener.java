package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import github.eremiyuh.rPGPlugin.utils.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class AbyssOreListener implements Listener {

    private final PlayerProfileManager profileManager;

    public AbyssOreListener(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        // Ensure the event is a right-click and item is not null
        if (item != null
                && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                && item.isSimilar(ItemUtils.getAbyssPotion())) {

            event.setCancelled(true); // Prevent the default right-click action

            UserProfile profile = profileManager.getProfile(player.getName());
            if (profile != null) {
                profile.setAbyssPoints(profile.getAbyssPoints() + 1000);
                player.sendMessage("§aYou have gained 1000 Abyss Points!");
            } else {
                player.sendMessage("§cFailed to update Abyss Points. Profile not found.");
                return;
            }

            // Reduce the item count or remove it
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (itemInHand.isSimilar(item)) {
                int newAmount = itemInHand.getAmount() - 1;
                if (newAmount > 0) {
                    itemInHand.setAmount(newAmount);
                } else {
                    player.getInventory().setItemInMainHand(null);
                }
            }
        }
    }
}
