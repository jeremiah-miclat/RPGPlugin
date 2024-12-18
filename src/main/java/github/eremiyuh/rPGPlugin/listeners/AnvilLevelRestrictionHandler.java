package github.eremiyuh.rPGPlugin.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AnvilLevelRestrictionHandler implements Listener {

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        AnvilInventory anvilInventory = event.getInventory();
        ItemStack firstItem = anvilInventory.getItem(0);
        ItemStack secondItem = anvilInventory.getItem(1);


        // Ensure both items are present and the first item has lore
        if (firstItem != null && firstItem.hasItemMeta() && firstItem.getItemMeta().hasLore()) {

            if (secondItem != null && secondItem.getType() != Material.AIR) {
                anvilInventory.setMaximumRepairCost(10000);

                // Set the result (combined item) and remove the level cost
                ItemStack result = event.getResult();
                if (result != null) {
                    // Adjust meta or lore of the result if necessary
                    ItemMeta meta = result.getItemMeta();
                    if (meta != null) {
                        result.setItemMeta(meta);
                    }

                }
            }
        }
    }
}
