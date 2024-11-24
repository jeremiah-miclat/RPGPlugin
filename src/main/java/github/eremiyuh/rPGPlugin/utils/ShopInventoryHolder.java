package github.eremiyuh.rPGPlugin.utils;

import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class ShopInventoryHolder implements InventoryHolder {
    private final Chest sellerChest;

    public ShopInventoryHolder(Chest sellerChest) {
        this.sellerChest = sellerChest;
    }

    @Override
    public Inventory getInventory() {
        // Return null or the actual chest inventory if needed, but we don't use this directly here
        return null;
    }

    public Chest getSellerChest() {
        return sellerChest;
    }
}
