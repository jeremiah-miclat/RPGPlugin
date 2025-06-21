package github.eremiyuh.rPGPlugin.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ArmorEquipListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        ItemStack cursorItem = event.getCursor(); // Item being moved by the cursor
        if (cursorItem == null || cursorItem.getType() == Material.AIR) return;

        // Check if the destination is an equipment slot (e.g., armor, offhand)
        InventoryType.SlotType slotType = event.getSlotType();

        boolean equipping =
                slotType == InventoryType.SlotType.ARMOR ||
                        (event.getSlot() == 40); // Offhand slot

        // Also check if player is shift-clicking a cosmetic item into equipment slot
        boolean shiftEquipping = event.isShiftClick() &&
                hasCosmeticLore(event.getCurrentItem()) &&
                isArmorOrOffhand(event.getCurrentItem());

        if ((equipping && hasCosmeticLore(cursorItem)) || shiftEquipping) {
            event.setCancelled(true);
            player.sendMessage("§cYou can't equip cosmetic items!");
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return; // avoid duplicate call from OFF_HAND
        ItemStack item = event.getItem();
        if (item == null || item.getType() == Material.AIR) return;

        if (hasCosmeticLore(item)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("You can't equip cosmetic items!");
        }
    }

    private boolean hasCosmeticLore(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore()) return false;

        if (meta.getLore().contains("Cosmetic")) {
            return true;
        }
        return false;
    }

    private boolean isArmorOrOffhand(ItemStack item) {
        if (item == null) return false;
        Material type = item.getType();

        return type.name().endsWith("_HELMET") ||
                type.name().endsWith("_CHESTPLATE") ||
                type.name().endsWith("_LEGGINGS") ||
                type.name().endsWith("_BOOTS") ||
                type == Material.SHIELD;
    }
}

