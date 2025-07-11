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
    private final List<CosmeticItem> cosmeticItems2 = new ArrayList<>();
    private final List<CosmeticItem> cosmeticItems3 = new ArrayList<>();

    public CosmeticStore(RPGPlugin plugin, PlayerProfileManager profileManager) {
        this.plugin = plugin;
        this.profileManager = profileManager;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        initializeCosmeticItems();
        initializeCosmeticItems2();
        initializeCosmeticItems3();
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
        cosmeticItems.add(new CosmeticItem(getSoulBoundBook(), 3, "netherite"));
        cosmeticItems.add(new CosmeticItem(getRubyFire(), 3, "netherite"));
        cosmeticItems.add(new CosmeticItem(getMirrorShield(), 1000, "iron"));

        cosmeticItems.add(new CosmeticItem(getAdamantiumHelmet(), 250, "gold"));
        cosmeticItems.add(new CosmeticItem(getAdamantiumChestPlate(), 250, "gold"));
        cosmeticItems.add(new CosmeticItem(getAdamantiumLeggings(), 250, "gold"));
        cosmeticItems.add(new CosmeticItem(getAdamantiumBoots(), 250, "gold"));

        cosmeticItems.add(new CosmeticItem(getChampHelm(), 250, "gold"));
        cosmeticItems.add(new CosmeticItem(getChampionChest(), 250, "gold"));
        cosmeticItems.add(new CosmeticItem(getChampLeg(), 250, "gold"));
        cosmeticItems.add(new CosmeticItem(getChampBoots(), 250, "gold"));

        cosmeticItems.add(new CosmeticItem(getDragHelm(), 250, "gold"));
        cosmeticItems.add(new CosmeticItem(getDragChest(), 250, "gold"));
        cosmeticItems.add(new CosmeticItem(getDragLeg(), 250, "gold"));
        cosmeticItems.add(new CosmeticItem(getDragBoots(), 250, "gold"));
    }

    private void initializeCosmeticItems2() {

        // ELLEGAARD
        cosmeticItems2.add(new CosmeticItem(getElleHelm(), 250, "gold"));
        cosmeticItems2.add(new CosmeticItem(getElleChest(), 250, "gold"));
        cosmeticItems2.add(new CosmeticItem(getElleLeg(), 250, "gold"));
        cosmeticItems2.add(new CosmeticItem(getElleBoots(), 250, "gold"));

        //netherite bow
        cosmeticItems2.add(new CosmeticItem(getNetherCrossBow(),5,"netherite"));

        // riesling crossbow
        cosmeticItems2.add(new CosmeticItem(getRieslingCrossBow(),100000,"activitypoints"));

        //grim dark netherite
        cosmeticItems2.add(new CosmeticItem(getGrimNethHelm(), 25000, "activitypoints"));
        cosmeticItems2.add(new CosmeticItem(getGrimNethChest(), 25000, "activitypoints"));
        cosmeticItems2.add(new CosmeticItem(getGrimNethLeg(), 25000, "activitypoints"));
        cosmeticItems2.add(new CosmeticItem(getGrimNethBoots(), 25000, "activitypoints"));

        //demonicblade
        cosmeticItems2.add(new CosmeticItem(getDemonicBlade(), 500000, "activitypoints"));

        //warbow
        cosmeticItems2.add(new CosmeticItem(getWarBow(), 500000, "activitypoints"));

        // mythic blade
        cosmeticItems2.add(new CosmeticItem(getMythicBlade(), 200, "diamond"));

        // toxic blade
        cosmeticItems2.add(new CosmeticItem(getTLS(), 3000, "emerald"));

        // creationsplitter
        cosmeticItems2.add(new CosmeticItem(getCreationSplitter(), 500000, "activitypoints"));

        //grim dark gold
        cosmeticItems2.add(new CosmeticItem(getGrimGHelm(), 25000, "activitypoints"));
        cosmeticItems2.add(new CosmeticItem(getGrimGChest(), 25000, "activitypoints"));
        cosmeticItems2.add(new CosmeticItem(getGrimGLeg(), 25000, "activitypoints"));
        cosmeticItems2.add(new CosmeticItem(getGrimGBoots(), 25000, "activitypoints"));

        //grim dark diamond
        cosmeticItems2.add(new CosmeticItem(getGrimDHelm(), 25000, "activitypoints"));
        cosmeticItems2.add(new CosmeticItem(getGrimDChest(), 25000, "activitypoints"));
        cosmeticItems2.add(new CosmeticItem(getGrimDLeg(), 25000, "activitypoints"));
        cosmeticItems2.add(new CosmeticItem(getGrimDBoots(), 25000, "activitypoints"));

        // kalam0n
        cosmeticItems2.add(new CosmeticItem(getKalPa(), 100000, "activitypoints"));
        cosmeticItems2.add(new CosmeticItem(getKalA(), 100000, "activitypoints"));
        cosmeticItems2.add(new CosmeticItem(getKalH(), 100000, "activitypoints"));
        cosmeticItems2.add(new CosmeticItem(getKalS(), 100000, "activitypoints"));
    }


    private void initializeCosmeticItems3() {

        // black n
        cosmeticItems3.add(new CosmeticItem(blackNHelm(), 2500000, "activitypoints"));
        cosmeticItems3.add(new CosmeticItem(blackNChest(), 2500000, "activitypoints"));
        cosmeticItems3.add(new CosmeticItem(blackNLeg(), 2500000, "activitypoints"));
        cosmeticItems3.add(new CosmeticItem(blackNBoots(), 2500000, "activitypoints"));

        // white n
        cosmeticItems3.add(new CosmeticItem(whiteNHelm(), 2500000, "activitypoints"));
        cosmeticItems3.add(new CosmeticItem(whiteNChest(), 2500000, "activitypoints"));
        cosmeticItems3.add(new CosmeticItem(whiteNLeg(), 2500000, "activitypoints"));
        cosmeticItems3.add(new CosmeticItem(whiteNBoots(), 2500000, "activitypoints"));

        // green n
        cosmeticItems3.add(new CosmeticItem(greenNHelm(), 2500000, "activitypoints"));
        cosmeticItems3.add(new CosmeticItem(greenNChest(), 2500000, "activitypoints"));
        cosmeticItems3.add(new CosmeticItem(greenNLeg(), 2500000, "activitypoints"));
        cosmeticItems3.add(new CosmeticItem(greenNBoots(), 2500000, "activitypoints"));

        // blue n
        cosmeticItems3.add(new CosmeticItem(blueNHelm(), 2500000, "activitypoints"));
        cosmeticItems3.add(new CosmeticItem(blueNChest(), 2500000, "activitypoints"));
        cosmeticItems3.add(new CosmeticItem(blueNLeg(), 2500000, "activitypoints"));
        cosmeticItems3.add(new CosmeticItem(blueNBoots(), 2500000, "activitypoints"));

        // red n
        cosmeticItems3.add(new CosmeticItem(redNHelm(), 2500000, "activitypoints"));
        cosmeticItems3.add(new CosmeticItem(redNChest(), 2500000, "activitypoints"));
        cosmeticItems3.add(new CosmeticItem(redNLeg(), 2500000, "activitypoints"));
        cosmeticItems3.add(new CosmeticItem(redNBoots(), 2500000, "activitypoints"));

        //assassin bow
        cosmeticItems3.add(new CosmeticItem(getAssassinBow(), 20000000, "activitypoints"));

        //creation weaver
        cosmeticItems3.add(new CosmeticItem(getCreationWeaver(), 20000000, "activitypoints"));

        //cyber katana
        cosmeticItems3.add(new CosmeticItem(getCyberKatana(), 20000000, "activitypoints"));

        //flamatic katana
        cosmeticItems3.add(new CosmeticItem(getFlamaticKatana(), 10000000, "activitypoints"));

        //cyber shield
        cosmeticItems3.add(new CosmeticItem(getCyberShield(), 10000000, "activitypoints"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 0 || args[0].equals("1")) {
                openCosmeticStore(player);
            } else if (args[0].equals("2")) {
                openCosmeticStore2(player);

            }
            else if (args[0].equals("3")) {
                openCosmeticStore3(player);
            }
            else {
                player.sendMessage(ChatColor.RED + "Invalid store number. Use /cosmeticstore 1, 2 or 3");
            }
            return true;
        } else {
            sender.sendMessage("This command can only be used by a player.");
            return true;
        }
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

    public void openCosmeticStore2(Player player) {
        Inventory cosmeticStore = Bukkit.createInventory(null, 27, Component.text("Cosmetic Store 2").color(TextColor.color(255, 0, 0)));

        for (int i = 0; i < cosmeticItems2.size(); i++) {
            CosmeticItem cosmeticItem = cosmeticItems2.get(i);
            // Use getDisplayItem() to show the item with lore in the GUI
            ItemStack item = cosmeticItem.getDisplayItem();
            cosmeticStore.setItem(i, item);
        }

        player.openInventory(cosmeticStore);
    }

    public void openCosmeticStore3(Player player) {
        Inventory cosmeticStore = Bukkit.createInventory(null, 54, Component.text("Cosmetic Store 3").color(TextColor.color(255, 0, 0)));

        for (int i = 0; i < cosmeticItems3.size(); i++) {
            CosmeticItem cosmeticItem = cosmeticItems3.get(i);
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

    @EventHandler
    public void onInventoryClick2(InventoryClickEvent event) {
        if (event.getView().title().equals(Component.text("Cosmetic Store 2").color(TextColor.color(255, 0, 0)))) {
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            UserProfile userProfile = profileManager.getProfile(player.getName());

            if (userProfile == null) {
                player.sendMessage(Component.text("Profile not found!").color(TextColor.color(255, 0, 0)));
                return;
            }

            int slot = event.getSlot();
            if (slot < 0 || slot >= cosmeticItems2.size()) return; // Ensure the slot is valid

            CosmeticItem cosmeticItem = cosmeticItems2.get(slot);
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

    @EventHandler
    public void onInventoryClick3(InventoryClickEvent event) {
        if (event.getView().title().equals(Component.text("Cosmetic Store 3").color(TextColor.color(255, 0, 0)))) {
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            UserProfile userProfile = profileManager.getProfile(player.getName());

            if (userProfile == null) {
                player.sendMessage(Component.text("Profile not found!").color(TextColor.color(255, 0, 0)));
                return;
            }

            int slot = event.getSlot();
            if (slot < 0 || slot >= cosmeticItems3.size()) return; // Ensure the slot is valid

            CosmeticItem cosmeticItem = cosmeticItems3.get(slot);
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
