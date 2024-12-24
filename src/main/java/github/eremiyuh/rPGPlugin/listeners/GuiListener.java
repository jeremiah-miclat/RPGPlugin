package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class GuiListener implements Listener {

    private final RPGPlugin plugin;

    public GuiListener(RPGPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        String inventoryTitle = event.getView().getTitle();

        // Check if the inventory is the "Player's Item" GUI
        if (inventoryTitle.equals(ChatColor.AQUA + "Player's Item")) {
            event.setCancelled(true); // Prevent any interaction
        }
    }
}
