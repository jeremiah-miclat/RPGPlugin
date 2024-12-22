package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import github.eremiyuh.rPGPlugin.utils.ItemUtils;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class AbyssHealItemListener implements Listener {

    @EventHandler
    public void onPlayerRightClick(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        // Check if the consumed item is the Abyss Potion
        if (item.isSimilar(ItemUtils.getAbyssPotion())) {
            event.setCancelled(true);

            if (player.getHealth()<=0) return;

            // Heal the player to full health
            player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).getValue());
            player.sendMessage("§aYou have been healed to full health!");

            // Reduce the stack size of the item in hand
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (itemInHand.isSimilar(item)) {
                int newAmount = itemInHand.getAmount() - 1;
                if (newAmount > 0) {
                    itemInHand.setAmount(newAmount);
                } else {
                    player.getInventory().setItemInMainHand(null); // Remove the item entirely
                }
            }
        }
    }
}

