package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Random;

public class ItemAscensionListener implements Listener {

    private final PlayerProfileManager profileManager;
    private final Random random = new Random();
    private static final int COST_PER_ATTEMPT = 100;  // The cost per attempt

    public ItemAscensionListener(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @EventHandler
    public void onItemDragIntoAnother(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) return;
        UserProfile userProfile = profileManager.getProfile(player.getName());
        if (!userProfile.isAscending()) {
            return;
        }


        ItemStack draggedItem = event.getCursor();
        if (draggedItem == null || draggedItem.getType() == Material.AIR) {
            return;
        }

        ItemStack targetItem = event.getCurrentItem();
        if (targetItem == null || targetItem.getType() == Material.AIR) {
            return;
        }


        if (!draggedItem.hasItemMeta() || !targetItem.hasItemMeta()) {
            player.sendMessage("One or both items do not have valid attribute.");
            userProfile.setAscending(false);
            player.sendMessage("Ascension failed, turn it on again to retry.");
            return;
        }


        if (!areSameEquipmentType(draggedItem, targetItem)) {
            player.sendMessage("Items are not of the same equipment type or material. Ascension not allowed.");
            userProfile.setAscending(false);
            player.sendMessage("Ascension failed, turn it on again to retry.");
            return;
        }

        ItemMeta draggedMeta = draggedItem.getItemMeta();
        ItemMeta targetMeta = targetItem.getItemMeta();


        List<String> draggedLores = draggedMeta.getLore();
        List<String> targetLores = targetMeta.getLore();

        if (draggedLores == null || targetLores == null) {
            return;
        }


        player.sendMessage("Dragged Item attribute: " + draggedLores);
        player.sendMessage("Target Item attribute: " + targetLores);


        String attribute = getMatchingAttribute(draggedLores, targetLores);
        if (attribute == null) {
            player.sendMessage("No matching attribute found in the equipments.");
            userProfile.setAscending(false);
            player.sendMessage("Ascension failed, turn it on again to retry.");
            return;
        }


        player.sendMessage("Matching attribute: " + attribute);

        // Get the attribute value from the dragged item
        int attributeValue = getAttributeValue(draggedLores, attribute);
        if (attributeValue <= 0) {
            player.sendMessage("Attribute value is 0 or less.");
            userProfile.setAscending(false);
            player.sendMessage("Ascension failed, turn it on again to retry.");
            return;
        }

        int diamondsRequired = COST_PER_ATTEMPT * attributeValue;
        if (userProfile.getDiamond() < diamondsRequired) {
            player.sendMessage("You need at least " + diamondsRequired + " diamonds to attempt item ascension.");
            userProfile.setAscending(false);
            player.sendMessage("Ascension failed, turn it on again to retry.");
            return;
        }

        player.sendMessage("Diamond cost for ascension: " + diamondsRequired);

        // Deduct diamonds for the ascension attempt
        userProfile.setDiamond(userProfile.getDiamond() - diamondsRequired);
        player.sendMessage(diamondsRequired + " diamonds have been deducted for the ascension attempts.");

        // Attempt the ascension with a 30% success chance per attribute value
        boolean successfulAscension = false;

        for (int i = 0; i < attributeValue; i++) {
            if (random.nextDouble() <= 0.50) {  // 60% chance
                successfulAscension = true;
                incrementAttribute(targetLores, attribute);
            }
        }

        // Final feedback to the player
        if (successfulAscension) {
            targetMeta.setLore(targetLores);
            targetItem.setItemMeta(targetMeta);
            player.sendMessage("Ascension successful! " + attribute + " increased.");
            userProfile.setAscending(false);
            player.sendMessage("Ascension turned off.");
        } else {
            player.sendMessage("Ascension attempt failed.");
            userProfile.setAscending(false);
            player.sendMessage("Ascension turned off.");
        }

        // Cancel the event (preventing item placement)
        event.setCancelled(true);

        // Remove the dragged item from the cursor
        player.setItemOnCursor(null);
    }

    private boolean areSameEquipmentType(ItemStack item1, ItemStack item2) {
        // Ensure both items are valid and non-null
        if (item1 == null || item2 == null) return false;

        Material type1 = item1.getType();
        Material type2 = item2.getType();

        // Check for armor pieces (boots, leggings, chestplate, helmet)
        if (type1.name().endsWith("BOOTS") || type1.name().endsWith("LEGGINGS") ||
                type1.name().endsWith("CHESTPLATE") || type1.name().endsWith("HELMET")) {
            // Check if both items are of the same armor type (boots, leggings, chestplate, helmet)
            if (!type2.name().endsWith(type1.name().substring(type1.name().length() - 6))) {
                return false;  // Items are not the same armor type (e.g., boots vs chestplate)
            }
            // Allow combination of items of the same type (boots with boots, leggings with leggings, etc.)
            return true;  // Items are of the same armor type, regardless of material
        }


        if (type1.name().endsWith("SWORD")) {
            return type2.name().endsWith("SWORD");
        }


        // Check for bows (BOW)
        if (type1 == Material.BOW) {
            return type2 == Material.BOW;
        }

        // Check for crossbows (CROSSBOW)
        if (type1 == Material.CROSSBOW) {
            return type2 == Material.CROSSBOW;
        }

        // Check for books (ENCHANTED_BOOK)
        if (type1 == Material.BOOK) {
            return type2 == Material.BOOK;
        }

        // If we haven't matched any of the above categories, return false
        return false;
    }


    private String getMatchingAttribute(List<String> lore1, List<String> lore2) {
        // Check if both lore lists contain the same attribute (e.g., "Vitality")
        String[] attributes = {"Strength", "Agility", "Luck", "Vitality", "Dexterity", "Intelligence"};
        for (String attr : attributes) {
            if (containsAttribute(lore1, attr) && containsAttribute(lore2, attr)) {
                return attr;
            }
        }
        return null;
    }

    private boolean containsAttribute(List<String> lore, String attribute) {
        for (String line : lore) {
            if (line.contains(attribute + ":")) return true;
        }
        return false;
    }

    private int getAttributeValue(List<String> lore, String attribute) {
        for (String line : lore) {
            if (line.contains(attribute + ":")) {
                String[] parts = line.split(": ");
                try {
                    return Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    return 0;  // Return 0 if the value cannot be parsed
                }
            }
        }
        return 0;
    }

    private void incrementAttribute(List<String> lore, String attribute) {
        for (int i = 0; i < lore.size(); i++) {
            String line = lore.get(i);
            if (line.contains(attribute + ":")) {
                String[] parts = line.split(": ");
                int currentValue = Integer.parseInt(parts[1]);
                lore.set(i, attribute + ": " + (currentValue + 1));
                return;
            }
        }
    }
}
