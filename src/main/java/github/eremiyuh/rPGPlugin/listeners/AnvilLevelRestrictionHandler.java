package github.eremiyuh.rPGPlugin.listeners;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.EquippableComponent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    private boolean hasBypassLore(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasLore()) return false;

        for (String line : meta.getLore()) {
            if (line.contains("ByPassLevel")) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void customEnchant(PrepareAnvilEvent event) {
//        if (event.getViewers().getFirst().isOp()) {
//            event.getView().bypassEnchantmentLevelRestriction(true);
//        }

        AnvilInventory inventory = event.getInventory();
        ItemStack first = inventory.getFirstItem();
        ItemStack second = inventory.getSecondItem();

        if (hasBypassLore(first) && hasBypassLore(second)) {
            // Allow bypassing enchantment level restriction
            event.getView().bypassEnchantmentLevelRestriction(true);
        }

        AnvilInventory anvilInventory = event.getInventory();
        ItemStack firstItem = anvilInventory.getItem(0);
        ItemStack secondItem = anvilInventory.getItem(1);

        // Ensure that both items exist
        if (firstItem == null || secondItem == null) return;

        // Clone the first item for modifications
        ItemStack result = firstItem.clone();

        if (firstItem.getType() == Material.NETHERITE_PICKAXE && secondItem.getType() == Material.ENCHANTED_BOOK) {
            // Get the item metas

            ItemMeta firstMeta = firstItem.getItemMeta();
            EnchantmentStorageMeta secondEnchantMeta = (EnchantmentStorageMeta) secondItem.getItemMeta();

            // Ensure both metas are not null
            if (firstMeta != null && secondEnchantMeta != null) {
                // Check if the pickaxe has Silk Touch
                if (firstMeta.hasEnchant(Enchantment.SILK_TOUCH)) {
                    // Check if the enchanted book has Fortune
                    if (secondEnchantMeta.hasStoredEnchant(Enchantment.FORTUNE)) {

                        // Remove Silk Touch from the pickaxe
                        firstMeta.removeEnchant(Enchantment.SILK_TOUCH);

                        // Add Fortune from the book (keep the level of Fortune)
                        int fortuneLevel = secondEnchantMeta.getStoredEnchantLevel(Enchantment.FORTUNE);
                        firstMeta.addEnchant(Enchantment.FORTUNE, fortuneLevel, true);

                        // Apply the updated meta to the result item
                        result.setItemMeta(firstMeta);

                        // Set the result and repair cost
                        event.setResult(result);
                        anvilInventory.setRepairCost(10);
                        return;
                    }
                }
            }

        }


        // Ensure the second item has metadata and lore
        ItemMeta secondMeta = secondItem.getItemMeta();
        if (secondMeta == null || !secondMeta.hasLore()) return;

        // Extract lore lines from the second item
        List<String> secondLore = secondMeta.getLore();
        if (secondLore.size() < 2) return;  // Ensure there are enough lore lines

        String secondLoreLine = secondLore.get(1);
        String secondLoreLine1 = secondLore.get(0);
        String firstItemName = firstItem.getType().name();

        // Extract the first lore line from second item (Enchanted Book)
        String secondLoreName = secondLoreLine != null ? secondLoreLine.toUpperCase() : null;

        // Check if we are dealing with the correct items (Fishing Rod and Enchanted Book with Lure5)
        if (firstItem.getType() == Material.FISHING_ROD && secondItem.getType() == Material.ENCHANTED_BOOK && secondLoreLine1.contains("Lure5")) {
            ItemMeta firstMeta = firstItem.getItemMeta();
            if (firstMeta != null) {
                firstMeta.removeEnchant(Enchantment.LURE);
                firstMeta.addEnchant(Enchantment.LURE, 5, true);
                result.setItemMeta(firstMeta);
                event.setResult(result);
                anvilInventory.setRepairCost(10);
            }
            return;
        }



        // Only proceed if the first item name matches the second lore line
        if (!firstItemName.contains(secondLoreLine1)) return;

        // Ensure the lore line is not null or empty
        if (secondLoreLine == null || secondLoreLine.isEmpty()) return;

        // Ensure we are working with an Enchanted Book
        if (secondLoreName != null && secondItem.getType() == Material.ENCHANTED_BOOK) {
            // Clone the first item meta for modification
            ItemMeta firstMeta = firstItem.getItemMeta();
            if (firstMeta == null) return; // If the first item has no metadata, exit

            // Create a result item metadata and lore list
            ItemMeta resultMeta = firstMeta.clone();
            List<String> resultLore = resultMeta.hasLore() ? resultMeta.getLore() : new ArrayList<>();

            // Process the lore line from the second item
            boolean found = false;
            for (int i = 0; i < resultLore.size(); i++) {
                String loreLine = resultLore.get(i);
                if (loreLine.startsWith(secondLoreLine.split(":")[0])) {  // Match the prefix
                    // Increment the number in the lore
                    try {
                        String[] parts = loreLine.split(":");
                        int currentValue = Integer.parseInt(parts[1].trim());
                        if ((loreLine.contains("OresHunter") && currentValue == 100) || loreLine.contains("StatDamage%") && currentValue > 100
                                || loreLine.contains("HP%") && currentValue > 100
                        ) {
                            return;
                        }
                        currentValue++; // Increment the value
                        resultLore.set(i, parts[0] + ": " + currentValue);  // Update the lore line
                    } catch (NumberFormatException e) {
                        return; // If the value is not a valid number, exit
                    }
                    found = true;
                    break;
                }
            }

            // If the lore line doesn't exist, add it to the result item's lore
            if (!found) {
                resultLore.add(secondLoreLine);  // Add the new lore line from the second item
            }


            // Set the modified lore back to the result item
            resultMeta.setLore(resultLore);
            result.setItemMeta(resultMeta);

            // Set the result of the anvil (optional)
            event.setResult(result);

            // Optionally, set the repair cost here if you want
            anvilInventory.setRepairCost(10);  // Example repair cost
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Check if the event happens in an anvil
        if (event.getInventory().getType() == InventoryType.ANVIL) {
            ItemStack result = event.getCurrentItem();
            if (result == null || result.getType() == Material.AIR) return;

            // Check if the result slot is clicked (usually slot 2)
            if (event.getSlot() == 2) {
                // Get the items in the anvil
                ItemStack firstItem = event.getInventory().getItem(0);
                ItemStack secondItem = event.getInventory().getItem(1);

                // If the result is picked up, consume the second item
                if (firstItem != null && secondItem != null) {
                    ItemMeta secondMeta = secondItem.getItemMeta();
                    List<String> secondLore = secondMeta.getLore();

                    // Check if lore contains "Cosmetic" to ensure it was involved in the crafting
                    if (secondLore != null ) {
                        // Decrease the amount of the second item only when the result is picked up
                        secondItem.setAmount(secondItem.getAmount() - 1);

                    }
                }
            }
        }
    }

    @EventHandler
    public void onApplyBypassLore(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!event.isLeftClick()) return; // Better compatibility (Java + Bedrock)
        if (event.getCursor() == null || event.getCurrentItem() == null) return;

        ItemStack cursor = event.getCursor();       // Dragged item (should be coal)
        ItemStack target = event.getCurrentItem();  // Target item to receive the lore

        if (cursor.getType() != Material.COAL) return;
        if (!hasBypassLore(cursor)) return; // Optional lore check on the coal

        ItemMeta targetMeta = target.getItemMeta();
        if (targetMeta == null) return;

        // Check if target has one of the allowed enchantments
        if (!hasAllowedEnchant(target)) return;

        // Check if lore already contains ByPassLevel
        List<String> lore = targetMeta.hasLore() ? new ArrayList<>(targetMeta.getLore()) : new ArrayList<>();
        if (lore.stream().anyMatch(l -> l.startsWith("ByPassLevel"))) return;

        // Apply the lore
        lore.add("ByPassLevel: 1");
        targetMeta.setLore(lore);
        target.setItemMeta(targetMeta);

        // Consume the coal item
        if (cursor.getAmount() > 1) {
            cursor.setAmount(cursor.getAmount() - 1);
        } else {
            event.setCursor(null);
        }

        event.setCurrentItem(target);
        ((Player) event.getWhoClicked()).sendMessage("§aByPassLevel applied!");
    }

    private boolean hasAllowedEnchant(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        // If it's an enchanted book
        if (meta instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) meta;
            Map<Enchantment, Integer> stored = bookMeta.getStoredEnchants();
            return stored.containsKey(Enchantment.SHARPNESS) ||   // Sharpness
                    stored.containsKey(Enchantment.POWER) || // Power
                    stored.containsKey(Enchantment.SMITE);  // Smite
        }

        // If it's a normal enchanted item
        return item.containsEnchantment(Enchantment.SHARPNESS) ||
                item.containsEnchantment(Enchantment.POWER) ||
                item.containsEnchantment(Enchantment.SMITE);
    }




}
