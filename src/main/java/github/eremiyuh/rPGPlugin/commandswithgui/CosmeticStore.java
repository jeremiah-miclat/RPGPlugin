package github.eremiyuh.rPGPlugin.commandswithgui;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.EquippableComponent;

import java.util.ArrayList;
import java.util.List;

import static github.eremiyuh.rPGPlugin.utils.ItemUtils.*;

public class CosmeticStore implements CommandExecutor, Listener {
    private final RPGPlugin plugin;
    private final PlayerProfileManager profileManager;


    public CosmeticStore(RPGPlugin plugin, PlayerProfileManager profileManager) {
        this.plugin = plugin;
        this.profileManager = profileManager;
        Bukkit.getPluginManager().registerEvents(this, plugin);
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
        // Create the Abyss Store inventory with 9 slots
        Inventory cosmeticStore = Bukkit.createInventory(null, 27, Component.text("Cosmetic Store").color(TextColor.color(255, 0, 0)));


        ItemStack compositeBow = getCompositeBow();
        ItemMeta compositeBowItemMeta = compositeBow.getItemMeta();

        if (compositeBowItemMeta != null) {
            // Add lore to show the cost
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Cost: 5,000 Copper ingot currency").color(TextColor.color(255, 215, 0))); // Gold color for cost
            compositeBowItemMeta.lore(lore); // Set the lore to the ItemMeta

            compositeBow.setItemMeta(compositeBowItemMeta);
        }
        cosmeticStore.setItem(0, compositeBow);


        ItemStack blacksaber = getBlackSaber();
        ItemMeta blacksaberItemMeta = blacksaber.getItemMeta();

        if (blacksaberItemMeta != null) {
            // Add lore to show the cost
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Cost: 5,000 Iron ingot currency").color(TextColor.color(255, 215, 0))); // Gold color for cost
            blacksaberItemMeta.lore(lore); // Set the lore to the ItemMeta

            blacksaber.setItemMeta(blacksaberItemMeta); // Apply the modified ItemMeta to the Ender Pearl
        }
        cosmeticStore.setItem(1, blacksaber);


        // HALO ARMORS - Add lore with price for each item
        ItemStack haloHelmet = getHaloHelmet();
        addLoreToItem(haloHelmet, "Cost: 2,000 Iron ingot currency");
        cosmeticStore.setItem(2, haloHelmet);

        ItemStack haloChestPlate = getHaloChestPlate();
        addLoreToItem(haloChestPlate, "Cost: 2,000 Iron ingot currency");
        cosmeticStore.setItem(3, haloChestPlate);

        ItemStack haloLeggings = getHaloLeggings();
        addLoreToItem(haloLeggings, "Cost: 2,000 Iron ingot currency");
        cosmeticStore.setItem(4, haloLeggings);

        ItemStack haloBoots = getHaloBoots();
        addLoreToItem(haloBoots, "Cost: 2,000 Iron ingot currency");
        cosmeticStore.setItem(5, haloBoots);


        ItemStack katana = getKatana();
        addLoreToItem(katana, "Cost: 5,000 Iron ingot currency");
        cosmeticStore.setItem(6, katana);

        ItemStack dragonSlayer = getDragonSlayer();
        addLoreToItem(dragonSlayer, "Cost: 30 Netherite ingot currency");
        cosmeticStore.setItem(7, dragonSlayer);

        // Open the Abyss Store GUI for the player
        player.openInventory(cosmeticStore);
    }

    private void addLoreToItem(ItemStack item, String costText) {
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null) {
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text(costText).color(TextColor.color(255, 215, 0))); // Gold color for cost
            itemMeta.lore(lore); // Set the lore to the ItemMeta
            item.setItemMeta(itemMeta); // Apply the modified ItemMeta
        }
    }



    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().title().equals(Component.text("Cosmetic Store").color(TextColor.color(255, 0, 0)))) {
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true); // Prevent interaction with other slots
            UserProfile userProfile = profileManager.getProfile(player.getName());

            // Ensure the player has a valid profile
            if (userProfile == null) {
                player.sendMessage(Component.text("Profile not found!").color(TextColor.color(255, 0, 0)));
                return;
            }


            if (event.getSlot() == 0 && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.BOW) {

                if (userProfile.getCurrency("copper") < 5000) {
                    player.sendMessage(ChatColor.RED +"Need 5k copper ingot currency");
                    return;
                }

                userProfile.setCurrency("copper",userProfile.getCopper()-5000);

                dropOrNotify(player, getCompositeBow(), "Successfully purchased Composite Bow!");
            }



            if (event.getSlot() == 1 && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.NETHERITE_SWORD) {

                if (userProfile.getCurrency("iron") < 5000) {
                    player.sendMessage(ChatColor.RED +"Need 5k iron ingot currency");
                    return;
                }

                userProfile.setCurrency("iron",userProfile.getIron()-5000);

                dropOrNotify(player, getBlackSaber(), "Successfully purchased Dark Blade!");
            }

            if (event.getSlot() == 2 && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.NETHERITE_HELMET) {

                if (userProfile.getCurrency("iron") < 2000) {
                    player.sendMessage(ChatColor.RED +"Need 1k iron ingot currency");
                    return;
                }

                userProfile.setCurrency("iron",userProfile.getIron()-2000);

                dropOrNotify(player, getHaloHelmet(), "Successfully purchased!");
            }


            if (event.getSlot() == 3 && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.NETHERITE_CHESTPLATE) {

                if (userProfile.getCurrency("iron") < 2000) {
                    player.sendMessage(ChatColor.RED +"Need 1k iron currency");
                    return;
                }

                userProfile.setCurrency("iron",userProfile.getIron()-2000);

                dropOrNotify(player, getHaloChestPlate(), "Successfully purchased!");
            }

            if (event.getSlot() == 4 && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.NETHERITE_LEGGINGS) {

                if (userProfile.getCurrency("iron") < 2000) {
                    player.sendMessage(ChatColor.RED +"Need 1k iron currency");
                    return;
                }

                userProfile.setCurrency("iron",userProfile.getIron()-2000);

                dropOrNotify(player, getHaloLeggings(), "Successfully purchased!");
            }

            if (event.getSlot() == 5 && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.NETHERITE_BOOTS) {

                if (userProfile.getCurrency("iron") < 2000) {
                    player.sendMessage(ChatColor.RED +"Need 1k iron currency");
                    return;
                }

                userProfile.setCurrency("iron",userProfile.getIron()-2000);

                dropOrNotify(player, getHaloBoots(), "Successfully purchased!");
            }

            if (event.getSlot() == 6 && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.NETHERITE_SWORD) {
                int cost = 5000;
                String currency = "iron";
                if (userProfile.getCurrency(currency) < cost) {
                    player.sendMessage(ChatColor.RED +"Need "+cost +" " + currency +" currency");
                    return;
                }

                userProfile.setCurrency(currency,userProfile.getCurrency(currency)-cost);

                dropOrNotify(player, getKatana(), "Successfully purchased!");
            }

            if (event.getSlot() == 7 && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.NETHERITE_SWORD) {
                int cost = 30;
                String currency = "netherite";
                if (userProfile.getCurrency(currency) < cost) {
                    player.sendMessage(ChatColor.RED +"Need "+cost +" " + currency +" currency");
                    return;
                }

                userProfile.setCurrency(currency,userProfile.getCurrency(currency)-cost);

                dropOrNotify(player, getDragonSlayer(), "Successfully purchased!");
            }

        }
    }

    private void dropOrNotify(Player player, ItemStack item, String successMessage) {
        if (player.getInventory().firstEmpty() != -1) {
            // Add item to inventory
            player.getInventory().addItem(item);
            player.sendMessage(Component.text(successMessage).color(TextColor.color(0, 255, 0)));
        } else {
            // Drop item at player's location
            player.getWorld().dropItemNaturally(player.getLocation(), item);
            player.sendMessage(Component.text("Your inventory is full! The item has been dropped near you.")
                    .color(TextColor.color(255, 255, 0)));
        }
    }



    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        // Prevent dragging in the Abyss Store inventory
        if (event.getInventory().getSize() == 9 && event.getView().getTitle().equals("Cosmetic Store")) {
            event.setCancelled(true); // Cancel any drag action in the Abyss Store
        }
    }

}
