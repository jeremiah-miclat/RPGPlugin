package github.eremiyuh.rPGPlugin.listeners;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
                anvilInventory.setRepairCost(10); // Example: 5 levels
            }
            return;
        }

        // Ensure both items are present and the first item has lore
        if (firstItem != null) {

            if (secondItem != null && secondItem.getType() != Material.AIR) {
                anvilInventory.setMaximumRepairCost(10000);

                if (firstItem.getItemMeta().hasLore() || firstItem.getItemMeta().hasItemModel()) {
                    anvilInventory.setRepairCost(10);
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


        if (secondItem != null && secondItem.hasItemMeta()) {
            if (firstItem.getType() == secondItem.getType()) {



            ItemMeta secondMeta = secondItem.getItemMeta();
            List<String> secondLore = secondMeta.getLore();

            // Debugging: Check if lore contains "Cosmetic"
                if (secondMeta.hasItemModel()) {
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
                            String displayName = secondMeta.getItemName();
                            System.out.println("Setting display name to: " + displayName);
                            resultMeta.setDisplayName(displayName);
                            cosmeticResult.setItemMeta(resultMeta);

                            // Update the anvil result
                            event.setResult(cosmeticResult);

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

        if (first == null || second ==null) {
            return;
        }


        if (hasBypassLore(first) && hasBypassLore(second)) {
            // Allow bypassing enchantment level restriction
            event.getView().bypassEnchantmentLevelRestriction(true);
        }

        if (first.getType() == Material.CROSSBOW && second.getType() == Material.ENCHANTED_BOOK) {
            ItemMeta crossbowMeta = first.getItemMeta();

            EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) second.getItemMeta();


            if (crossbowMeta != null && crossbowMeta.hasEnchant(Enchantment.POWER) && (bookMeta != null && bookMeta.hasStoredEnchant(Enchantment.POWER))) {
                int currentPower = crossbowMeta.getEnchantLevel(Enchantment.POWER);
                int powerLevel = bookMeta.getStoredEnchantLevel(Enchantment.POWER);
                if (powerLevel >=  currentPower) {
                    ItemStack result = first.clone();
                    ItemMeta resultMeta = result.getItemMeta();
                    if (powerLevel == currentPower) {
                        powerLevel += 1;
                    }
                    if (resultMeta != null) {
                        resultMeta.addEnchant(Enchantment.POWER, powerLevel, true); // 'true' makes it unsafe
                        result.setItemMeta(resultMeta);
                    }
                    event.getView().setRepairCost(10);
                    event.setResult(result);
                    return; // This stops everything below — BUT NOT ABOVE
                }
            }

            if (bookMeta != null && bookMeta.hasStoredEnchant(Enchantment.POWER)) {
                int powerLevel = bookMeta.getStoredEnchantLevel(Enchantment.POWER);
                // Clone the crossbow and its meta
                ItemStack result = first.clone();
                ItemMeta resultMeta = result.getItemMeta();

                if (resultMeta != null) {
                    resultMeta.addEnchant(Enchantment.POWER, powerLevel, true); // 'true' makes it unsafe
                    result.setItemMeta(resultMeta);
                }
                event.getView().setRepairCost(10);
                event.setResult(result);
            }

            if (bookMeta != null && bookMeta.hasStoredEnchant(Enchantment.INFINITY)) {
                int infinityLevel = bookMeta.getStoredEnchantLevel(Enchantment.INFINITY);

                // Clone the crossbow and its meta
                ItemStack result = first.clone();
                ItemMeta resultMeta = result.getItemMeta();

                if (resultMeta != null) {
                    resultMeta.addEnchant(Enchantment.INFINITY, infinityLevel, true); // 'true' makes it unsafe
                    result.setItemMeta(resultMeta);
                }
                event.getView().setRepairCost(10);
                event.setResult(result);
            }
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

            // Check if the result slot is clicked (slot 2 is the output slot in an anvil)
            if (event.getSlot() == 2) {
                ItemStack firstItem = event.getInventory().getItem(0);
                ItemStack secondItem = event.getInventory().getItem(1);

                // Cancel if first item has lore "Cosmetic"
                if (firstItem != null && firstItem.hasItemMeta()) {
                    ItemMeta firstMeta = firstItem.getItemMeta();
                    if (firstMeta.hasLore()) {
                        List<String> firstLore = firstMeta.getLore();
                        for (String line : firstLore) {
                            if (line.contains("Cosmetic")) {
                                event.getWhoClicked().sendMessage(ChatColor.RED + "⚠ Cosmetic items cannot be modified or upgraded. Do not retry, exp will not be refunded");
                                event.getInventory().close();
                                event.setCancelled(true);
                                return;
                            }
                        }
                    }
                }

                // If the result is picked up, consume one second item (e.g., a template or catalyst)
                if (firstItem != null && secondItem != null && secondItem.hasItemMeta()) {
                    ItemMeta secondMeta = secondItem.getItemMeta();
                    List<String> secondLore = secondMeta.getLore();

                    if (secondLore != null) {
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
