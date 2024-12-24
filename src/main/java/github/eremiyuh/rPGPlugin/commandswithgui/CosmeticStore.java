package github.eremiyuh.rPGPlugin.commandswithgui;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

import java.util.List;


import static github.eremiyuh.rPGPlugin.utils.ItemUtils.*;

public class CosmeticStore implements CommandExecutor, Listener {
    private final RPGPlugin plugin;
    private final PlayerProfileManager profileManager;
    private final List<CosmeticItem> cosmeticItems = new ArrayList<>();


    public CosmeticStore(RPGPlugin plugin, PlayerProfileManager profileManager) {
        this.plugin = plugin;
        this.profileManager = profileManager;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        initializeCosmeticItems();
    }

    private void initializeCosmeticItems() {
        // Initialize cosmetic items
        cosmeticItems.add(new CosmeticItem(getCompositeBow(), 5000, "copper"));
        cosmeticItems.add(new CosmeticItem(getBlackSaber(), 5000, "iron"));
        cosmeticItems.add(new CosmeticItem(getHaloHelmet(), 2000, "iron"));
        cosmeticItems.add(new CosmeticItem(getHaloChestPlate(), 2000, "iron"));
        cosmeticItems.add(new CosmeticItem(getHaloLeggings(), 2000, "iron"));
        cosmeticItems.add(new CosmeticItem(getHaloBoots(), 2000, "iron"));
        cosmeticItems.add(new CosmeticItem(getKatana(), 5000, "iron"));
        cosmeticItems.add(new CosmeticItem(getDragonSlayer(), 10, "netherite"));
        cosmeticItems.add(new CosmeticItem(getFoxHelmet(), 1000, "iron"));
        cosmeticItems.add(new CosmeticItem(getFoxChestPlate(), 1000, "iron"));
        cosmeticItems.add(new CosmeticItem(getFoxLeggings(), 1000, "iron"));
        cosmeticItems.add(new CosmeticItem(getFoxBoots(), 1000, "iron"));

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            openCosmeticStore(player);
            return true;
        }
        return false;
    }

    public void openCosmeticStore(Player player) {
        Inventory cosmeticStore = Bukkit.createInventory(null, 27, Component.text("Cosmetic Store").color(TextColor.color(255, 0, 0)));

        for (int i = 0; i < cosmeticItems.size(); i++) {
            CosmeticItem cosmeticItem = cosmeticItems.get(i);
            // Use getDisplayItem() to show the item with lore in the GUI
            ItemStack item = cosmeticItem.getDisplayItem();
            cosmeticStore.setItem(i, item);
        }

        player.openInventory(cosmeticStore);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().title().equals(Component.text("Cosmetic Store").color(TextColor.color(255, 0, 0)))) {
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            UserProfile userProfile = profileManager.getProfile(player.getName());

            if (userProfile == null) {
                player.sendMessage(Component.text("Profile not found!").color(TextColor.color(255, 0, 0)));
                return;
            }

            int slot = event.getSlot();
            if (slot < 0 || slot >= cosmeticItems.size()) return; // Ensure the slot is valid

            CosmeticItem cosmeticItem = cosmeticItems.get(slot);
            ItemStack clickedItem = event.getCurrentItem();

            // Check if the clicked item is not null and matches the expected cosmetic item
            if (clickedItem == null || !clickedItem.isSimilar(cosmeticItem.getDisplayItem())) {
                return;
            }

            int cost = cosmeticItem.getCost();
            String currency = cosmeticItem.getCurrency();

            if (userProfile.getCurrency(currency) < cost) {
                player.sendMessage(ChatColor.RED + "Need " + cost + " " + currency + " currency");
                return;
            }

            userProfile.setCurrency(currency, userProfile.getCurrency(currency) - cost);
            dropOrNotify(player, cosmeticItem.getItem(), "Successfully purchased.");  // Give the original item
        }
    }

    private void dropOrNotify(Player player, ItemStack item, String successMessage) {
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(item);
            player.sendMessage(Component.text(successMessage).color(TextColor.color(0, 255, 0)));
        } else {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
            player.sendMessage(Component.text("Your inventory is full! The item has been dropped near you.")
                    .color(TextColor.color(255, 255, 0)));
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getSize() == 9 && event.getView().getTitle().equals("Cosmetic Store")) {
            event.setCancelled(true);
        }
    }
}

class CosmeticItem {
    private final ItemStack item;  // Original item
    private final int cost;
    private final String currency;

    public CosmeticItem(ItemStack item, int cost, String currency) {
        this.item = item;
        this.cost = cost;
        this.currency = currency;
    }

    public ItemStack getItem() {
        return item;  // Return the original item
    }

    public int getCost() {
        return cost;
    }

    public String getCurrency() {
        return currency;
    }

    // Optionally, you can add a method to get a modified version for GUI
    public ItemStack getDisplayItem() {
        ItemStack displayItem = item.clone();  // Clone to avoid modifying the original
        ItemMeta itemMeta = displayItem.getItemMeta();
        if (itemMeta != null) {
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Cost: " + cost + " " + currency + " currency").color(TextColor.color(255, 215, 0)));
            itemMeta.lore(lore);
            displayItem.setItemMeta(itemMeta);
        }
        return displayItem;
    }
}
