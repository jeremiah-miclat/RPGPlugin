package github.eremiyuh.rPGPlugin.listeners;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.EquippableComponent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AnvilLevelRestrictionHandler implements Listener {

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        AnvilInventory anvilInventory = event.getInventory();
        ItemStack firstItem = anvilInventory.getItem(0);
        ItemStack secondItem = anvilInventory.getItem(1);
        ItemStack result = event.getResult();

        if (firstItem==null || secondItem==null) return;

        if (firstItem.getType() == Material.BOOK && secondItem.getType() == Material.BOOK) {
            ItemMeta secondMeta = secondItem.getItemMeta();
            if (secondMeta != null && secondMeta.getLore() != null && secondMeta.getLore().contains("Cosmetic")) {

                // Clone the first item to apply modifications
                ItemStack resultItem = firstItem.clone();
                ItemMeta resultMeta = resultItem.getItemMeta();

                // Apply display name from the second item
                if (secondMeta.hasDisplayName()) {
                    resultMeta.setDisplayName(secondMeta.getDisplayName());
                }

                // Apply cosmetic model data (optional)
                if (secondMeta.hasItemModel()) {
                    resultMeta.setItemModel(secondMeta.getItemModel());
                }

                // Set the modified meta back to the result item
                resultItem.setItemMeta(resultMeta);

                // Set the result item in the anvil
                event.setResult(resultItem);

                // Optional: Set the level cost of the anvil operation
                anvilInventory.setRepairCost(30); // Example: 5 levels
            }
            return;
        }

        // Ensure both items are present and the first item has lore
        if (firstItem != null) {

            if (secondItem != null && secondItem.getType() != Material.AIR) {
                anvilInventory.setMaximumRepairCost(10000);

                if (firstItem.getItemMeta().hasLore() || firstItem.getItemMeta().hasItemModel()) {
                    anvilInventory.setRepairCost(30);
                }

                if (result != null) {
                    // Adjust meta or lore of the result if necessary
                    ItemMeta meta = result.getItemMeta();
                    if (meta != null) {
                        result.setItemMeta(meta);
                    }

                }
            }
        }


        if (secondItem != null && secondItem.hasItemMeta() && secondItem.getItemMeta().hasLore()) {
            if (firstItem.getType() == secondItem.getType()) {



            ItemMeta secondMeta = secondItem.getItemMeta();
            List<String> secondLore = secondMeta.getLore();

            // Debugging: Check if lore contains "Cosmetic"
            if (secondLore != null) {
                System.out.println("Second item lore found: " + secondLore);
                if (secondLore.contains("Cosmetic")) {
                    System.out.println("Second item contains 'Cosmetic' lore.");

                    // Create a modified version of the result item
                    ItemStack cosmeticResult = firstItem.clone(); // Clone the first item
                    if (cosmeticResult != null) {
                        ItemMeta resultMeta = cosmeticResult.getItemMeta();

                        if (resultMeta != null) {
                            // Set the custom model data
                            @Nullable NamespacedKey customModelData = secondMeta.getItemModel();


                            resultMeta.setItemModel(customModelData);

                            EquippableComponent equippableComponent = secondMeta.getEquippable();

                            resultMeta.setEquippable(equippableComponent);

                            resultMeta.setEnchantmentGlintOverride(false);

                            if (secondMeta.hasDisplayName()) {
                                String displayName = secondMeta.getDisplayName();
                                System.out.println("Setting display name to: " + displayName);
                                resultMeta.setDisplayName(displayName); // Apply the name from the second item
                            }

                            cosmeticResult.setItemMeta(resultMeta);

                            // Update the anvil result
                            event.setResult(cosmeticResult);
                        }
                    }
                }
            }
            }
        }

    }
}
