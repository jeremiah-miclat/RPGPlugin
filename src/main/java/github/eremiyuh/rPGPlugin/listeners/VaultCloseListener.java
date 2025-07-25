package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.manager.VaultManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.entity.Player;

public class VaultCloseListener implements Listener {

    private final VaultManager vaultManager;

    public VaultCloseListener(VaultManager vaultManager) {
        this.vaultManager = vaultManager;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        // Check if the inventory being closed is a vault
        Inventory inventory = event.getInventory();
        String title = event.getView().getTitle(); // or getView().getTitle() depending on the API version
        Player player = (Player) event.getPlayer();

//        if (title.startsWith(player.getName() + "'s ")) {
//            try {
//                // Extract the vault number from the title
//                int vaultNumber = Integer.parseInt(title.replace("Vault ", "").trim());
//
//                // Save the player's vault
//                vaultManager.saveVault(player.getName(), vaultNumber);
//                player.sendMessage("Your vault has been saved!");
//            } catch (NumberFormatException e) {
//                Bukkit.getLogger().warning("Failed to parse vault number from inventory title: " + title);
//            }
//        }
    }
}
