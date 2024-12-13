package github.eremiyuh.rPGPlugin.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class CustomItemRecipeListener implements Listener {




    @EventHandler
    public void onSmithingTableUse(SmithItemEvent event) {
        ItemStack firstItem = event.getInventory().getItem(0);
        ItemStack secondItem = event.getInventory().getItem(1);
        ItemStack thirdItem = event.getInventory().getItem(2);
        assert thirdItem != null;
//        event.getWhoClicked().sendMessage(thirdItem.displayName());
        // Check if any of the items has the custom lore
        if (hasCustomLore(firstItem) || hasCustomLore(secondItem) || hasCustomLore(thirdItem)) {
            event.setCancelled(true); // Cancel the Smithing Table operation
            event.getWhoClicked().sendMessage("This item cannot be used in the Smithing Table.");
        }
    }

    private boolean hasCustomLore(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasLore()) {
            return false;
        }

        // Get the lore and check for specific text (custom lore)
        List<Component> lore = meta.lore();
        for (Component line : lore) {
            if (line.toString().contains("Buy at /abyssstore for 100,000 abyss points.")) {
                return true; // The custom lore is present
            }
        }

        return false;
    }

}
