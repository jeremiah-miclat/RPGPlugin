package github.eremiyuh.rPGPlugin.commandswithgui;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
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
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CustomEnchant implements CommandExecutor, Listener {
    private final RPGPlugin plugin;
    private final PlayerProfileManager profileManager;

    public CustomEnchant(RPGPlugin plugin, PlayerProfileManager profileManager) {
        this.plugin = plugin;
        this.profileManager = profileManager;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            openCeStore(player);
            return true;
        }
        return false;
    }


    public void openCeStore(Player player) {
        Inventory ceStore = Bukkit.createInventory(null, 27, Component.text("CE Store").color(TextColor.color(255, 0, 0)));


        // OresHunter
        ItemStack enchant = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta enchatMeta = enchant.getItemMeta();

        if (enchatMeta != null) {
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("PICKAXE"));
            lore.add(Component.text("OresHunter: 1").style(Style.style(TextDecoration.ITALIC,TextColor.color(255,0,0))));
            lore.add(Component.text("Cost: 10000 activity points, 10 redstones, 10 blaze rods"));
            enchatMeta.lore(lore);

            enchant.setItemMeta(enchatMeta);
        }

        ceStore.setItem(0, enchant);

        // PointsBonus
        ItemStack PointsBonus = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta PointsBonusMeta = PointsBonus.getItemMeta();

        if (PointsBonusMeta != null) {
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("FISHING_ROD"));
            lore.add(Component.text("PointsBonus: 1").style(Style.style(TextDecoration.ITALIC,TextColor.color(255,0,0))));
            lore.add(Component.text("Cost: 10000 activity points, 1 sponge"));
            PointsBonusMeta.lore(lore);

            PointsBonus.setItemMeta(PointsBonusMeta);
        }

        ceStore.setItem(1, PointsBonus);

        // ExtraFish
        ItemStack FishMultiplier = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta FishMultiPlierMeta = FishMultiplier.getItemMeta();

        if (FishMultiPlierMeta != null) {
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("FISHING_ROD"));
            lore.add(Component.text("ExtraFish: 1").style(Style.style(TextDecoration.ITALIC,TextColor.color(255,0,0))));
            lore.add(Component.text("Cost: 10000 activity points, 1 sponge"));
            FishMultiPlierMeta.lore(lore);

            FishMultiplier.setItemMeta(FishMultiPlierMeta);
        }

        ceStore.setItem(2, FishMultiplier);


        // Lure5
        ItemStack Lure5 = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta Lure5Meta = Lure5.getItemMeta();

        if (Lure5Meta != null) {
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Lure5").style(Style.style(TextDecoration.ITALIC,TextColor.color(255,0,0))));
            lore.add(Component.text("For fishing rods"));
            lore.add(Component.text("Cost: 100000 activity points, 10 Nautilus Shell"));
            Lure5Meta.lore(lore);

            Lure5.setItemMeta(Lure5Meta);
        }

        ceStore.setItem(3, Lure5);

        // BonusStatDamage
        ItemStack BonusStatDamage = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta BonusStatDamageMeta = BonusStatDamage.getItemMeta();

        if (BonusStatDamageMeta != null) {
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("SWORD"));
            lore.add(Component.text("StatDamage%: 1").style(Style.style(TextDecoration.ITALIC, TextColor.color(171, 0, 255))));
            lore.add(Component.text("Cost: 1M activity points, 1 Nether Star"));
            BonusStatDamageMeta.lore(lore);

            BonusStatDamage.setItemMeta(BonusStatDamageMeta);
        }


        ceStore.setItem(4, BonusStatDamage);


        // HP% Enchantment
        ItemStack HPEnchantment = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta HPEnchantmentMeta = HPEnchantment.getItemMeta();

        if (HPEnchantmentMeta != null) {
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("NETHERITE"));
            lore.add(Component.text("HP%: 1").style(Style.style(TextDecoration.ITALIC, TextColor.color(0, 171, 255))));
            lore.add(Component.text("Cost: 1M activity points, 2 Heart of the Sea"));
            HPEnchantmentMeta.lore(lore);

            HPEnchantment.setItemMeta(HPEnchantmentMeta);
        }
        ceStore.setItem(5, HPEnchantment);


        // BonusStatDamage BOWS
        ItemStack BonusStatDamage2 = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta BonusStatDamageMeta2 = BonusStatDamage.getItemMeta();
        if (BonusStatDamageMeta2 != null) {
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("BOW"));
            lore.add(Component.text("StatDamage%: 1").style(Style.style(TextDecoration.ITALIC, TextColor.color(171, 0, 255))));
            lore.add(Component.text("Cost: 1M activity points, 1 Nether Star"));
            BonusStatDamageMeta2.lore(lore);

            BonusStatDamage2.setItemMeta(BonusStatDamageMeta2);


        }

        ceStore.setItem(6, BonusStatDamage2);

        // BonusStatDamage BOOK
        ItemStack BonusStatDamage3 = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta BonusStatDamageMeta3 = BonusStatDamage.getItemMeta();
        if (BonusStatDamageMeta3 != null) {
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("BOOK"));
            lore.add(Component.text("StatDamage%: 1").style(Style.style(TextDecoration.ITALIC, TextColor.color(171, 0, 255))));
            lore.add(Component.text("Cost: 1M activity points, 1 Nether Star"));
            BonusStatDamageMeta3.lore(lore);

            BonusStatDamage3.setItemMeta(BonusStatDamageMeta3);
        }


        ceStore.setItem(7, BonusStatDamage3);

        player.openInventory(ceStore);
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Check if the inventory view title matches "CE Store"
        if (event.getView().title().equals(Component.text("CE Store").color(TextColor.color(255, 0, 0)))) {
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true); // Prevent interaction with other slots

            UserProfile userProfile = profileManager.getProfile(player.getName());

            // Safely check if current item is null
            ItemStack currentItem = event.getCurrentItem();
            if (currentItem == null || !currentItem.hasItemMeta()) return;

            ItemMeta itemMeta = currentItem.getItemMeta();
            if (itemMeta == null || !itemMeta.hasLore()) return;

            // Ensure lore has enough elements
            List<String> lore = itemMeta.getLore();
            if (lore == null || lore.size() < 3) return;

            // Check if the third lore line contains "Icon"
            boolean icon = lore.get(2).contains("Cost:");

            // Handle the case for the "ORES HUNTER" item when slot is 0 and the item is an ENCHANTED_BOOK
            if (event.getSlot() == 0 && currentItem.getType() == Material.ENCHANTED_BOOK && icon)  {
                double cost = 10000;
                String activityPoint = " active points ";
                int requiredRedstoneAmount = 10;
                int requiredBR = 10;

                // Check if the player has enough activity points
                if (userProfile.getActivitypoints() < cost) {
                    player.sendMessage(ChatColor.RED + "Needs " + ((int) cost) + activityPoint);
                    return;
                }

                // Check if the player has enough redstone in their inventory
                if (player.getInventory().containsAtLeast(new ItemStack(Material.REDSTONE), requiredRedstoneAmount) && player.getInventory().containsAtLeast(new ItemStack(Material.BLAZE_ROD), requiredBR)) {
                    // Deduct redstone from the player's inventory
                    player.getInventory().removeItem(new ItemStack(Material.REDSTONE, requiredRedstoneAmount));
                    player.getInventory().removeItem(new ItemStack(Material.BLAZE_ROD, requiredBR));
                    // Deduct activity points
                    userProfile.setCurrency("activitypoints", userProfile.getActivitypoints() - cost);

                    // Clone the item and notify the player
                    ItemStack item = event.getCurrentItem().clone();
                    dropOrNotify(player, item, "Successfully purchased.");
                } else {
                    player.sendMessage(ChatColor.RED + "Requirements not met.");
                }
            }

            // POINT BONUS FISHING ROD
            if (event.getSlot() == 1 && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.ENCHANTED_BOOK
                    && icon
            ) {
                double cost = 10000;
                String activityPoint = " active points ";
                int requiredSponge = 1;


                if (userProfile.getActivitypoints() < cost) {
                    player.sendMessage(ChatColor.RED + "Needs " + ((int) cost) + activityPoint);
                    return;
                }
                if (player.getInventory().containsAtLeast(new ItemStack(Material.SPONGE), requiredSponge)) {

                    player.getInventory().removeItem(new ItemStack(Material.SPONGE, requiredSponge));


                    userProfile.setCurrency("activitypoints", userProfile.getActivitypoints() - cost);


                    ItemStack item = event.getCurrentItem().clone();
                    dropOrNotify(player, item, "Successfully purchased.");
                } else {
                    player.sendMessage(ChatColor.RED + "You need " + requiredSponge + " sponge to make this purchase.");
                }
            }


            //FISH MULTIPLIER
            if (event.getSlot() == 2 && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.ENCHANTED_BOOK
                    && icon
            ) {
                double cost = 10000;
                String activityPoint = " active points ";
                int requiredSponge = 1;


                if (userProfile.getActivitypoints() < cost) {
                    player.sendMessage(ChatColor.RED + "Needs " + ((int) cost) + activityPoint);
                    return;
                }
                if (player.getInventory().containsAtLeast(new ItemStack(Material.SPONGE), requiredSponge)) {

                    player.getInventory().removeItem(new ItemStack(Material.SPONGE, requiredSponge));


                    userProfile.setCurrency("activitypoints", userProfile.getActivitypoints() - cost);


                    ItemStack item = event.getCurrentItem().clone();
                    dropOrNotify(player, item, "Successfully purchased.");
                } else {
                    player.sendMessage(ChatColor.RED + "You need " + requiredSponge + " sponge to make this purchase.");
                }
            }


            //LURE 5
            if (event.getSlot() == 3 && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.ENCHANTED_BOOK
                    && icon
            ) {
                double cost = 100000;
                String activityPoint = " active points ";
                int requiredShell = 10;


                if (userProfile.getActivitypoints() < cost) {
                    player.sendMessage(ChatColor.RED + "Needs " + ((int) cost) + activityPoint);
                    return;
                }
                if (player.getInventory().containsAtLeast(new ItemStack(Material.NAUTILUS_SHELL), requiredShell)) {

                    player.getInventory().removeItem(new ItemStack(Material.NAUTILUS_SHELL, requiredShell));


                    userProfile.setCurrency("activitypoints", userProfile.getActivitypoints() - cost);


                    ItemStack item = event.getCurrentItem().clone();
                    dropOrNotify(player, item, "Successfully purchased.");
                } else {
                    player.sendMessage(ChatColor.RED + "You need " + requiredShell + " nautilus shell to make this purchase.");
                }
            }

            // BonusStatDamage
            if (event.getSlot() == 4 && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.ENCHANTED_BOOK
                    && icon) {
                double cost = 1000000;
                String activityPoint = " activity points ";
                int requiredStar = 1;

                if (userProfile.getActivitypoints() < cost) {
                    player.sendMessage(ChatColor.RED + "Needs " + ((int) cost) + activityPoint);
                    return;
                }
                if (player.getInventory().containsAtLeast(new ItemStack(Material.NETHER_STAR), requiredStar)) {

                    player.getInventory().removeItem(new ItemStack(Material.NETHER_STAR, requiredStar));

                    userProfile.setCurrency("activitypoints", userProfile.getActivitypoints() - cost);

                    ItemStack item = event.getCurrentItem().clone();
                    dropOrNotify(player, item, "Successfully purchased.");
                } else {
                    player.sendMessage(ChatColor.RED + "You need " + requiredStar + " nether star to make this purchase.");
                }
            }

            // HP% Enchantment Purchase
            if (event.getSlot() == 5 && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.ENCHANTED_BOOK
                    && icon) {
                double cost = 1000000;
                String activityPoint = " activity points ";
                int requiredHearts = 2;

                if (userProfile.getActivitypoints() < cost) {
                    player.sendMessage(ChatColor.RED + "Needs " + ((int) cost) + activityPoint);
                    return;
                }
                if (player.getInventory().containsAtLeast(new ItemStack(Material.HEART_OF_THE_SEA), requiredHearts)) {

                    player.getInventory().removeItem(new ItemStack(Material.HEART_OF_THE_SEA, requiredHearts));

                    userProfile.setCurrency("activitypoints", userProfile.getActivitypoints() - cost);

                    ItemStack item = event.getCurrentItem().clone();
                    dropOrNotify(player, item, "Successfully purchased.");
                } else {
                    player.sendMessage(ChatColor.RED + "You need " + requiredHearts + " Heart of the Sea to make this purchase.");
                }
            }

            // BonusStatDamage
            if (event.getSlot() == 6 && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.ENCHANTED_BOOK
                    && icon) {
                double cost = 1000000;
                String activityPoint = " activity points ";
                int requiredStar = 1;

                if (userProfile.getActivitypoints() < cost) {
                    player.sendMessage(ChatColor.RED + "Needs " + ((int) cost) + activityPoint);
                    return;
                }
                if (player.getInventory().containsAtLeast(new ItemStack(Material.NETHER_STAR), requiredStar)) {

                    player.getInventory().removeItem(new ItemStack(Material.NETHER_STAR, requiredStar));

                    userProfile.setCurrency("activitypoints", userProfile.getActivitypoints() - cost);

                    ItemStack item = event.getCurrentItem().clone();
                    dropOrNotify(player, item, "Successfully purchased.");
                } else {
                    player.sendMessage(ChatColor.RED + "You need " + requiredStar + " nether star to make this purchase.");
                }
            }

            // BonusStatDamage BOOK
            if (event.getSlot() == 7 && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.ENCHANTED_BOOK
                    && icon) {
                double cost = 1000000;
                String activityPoint = " activity points ";
                int requiredStar = 1;

                if (userProfile.getActivitypoints() < cost) {
                    player.sendMessage(ChatColor.RED + "Needs " + ((int) cost) + activityPoint);
                    return;
                }
                if (player.getInventory().containsAtLeast(new ItemStack(Material.NETHER_STAR), requiredStar)) {

                    player.getInventory().removeItem(new ItemStack(Material.NETHER_STAR, requiredStar));

                    userProfile.setCurrency("activitypoints", userProfile.getActivitypoints() - cost);

                    ItemStack item = event.getCurrentItem().clone();
                    dropOrNotify(player, item, "Successfully purchased.");
                } else {
                    player.sendMessage(ChatColor.RED + "You need " + requiredStar + " nether star to make this purchase.");
                }
            }

        }
    }



    private void dropOrNotify(Player player, ItemStack item, String successMessage) {
        if (player.getInventory().firstEmpty() != -1) {
            // Check if the item has item meta and lore
            if (item.hasItemMeta()) {
                ItemMeta itemMeta = item.getItemMeta();
                if (itemMeta != null && itemMeta.hasLore()) {
                    List<String> lore = itemMeta.getLore();

                    // Ensure there are at least 3 lines of lore
                    if (lore.size() >= 3) {
                        // Get and remove the third lore line (index 2)
                        lore.remove(2); // Remove the third line (index 2)

                        // Set the updated lore back to the item
                        itemMeta.setLore(lore);
                        item.setItemMeta(itemMeta);
                    }
                }
            }

            // Add item to inventory and notify the player
            player.getInventory().addItem(item);
            player.sendMessage(Component.text(successMessage).color(TextColor.color(0, 255, 0)));
        } else {
            // Drop item at player's location if inventory is full
            player.getWorld().dropItemNaturally(player.getLocation(), item);
            player.sendMessage(Component.text("Your inventory is full! The item has been dropped near you.")
                    .color(TextColor.color(255, 255, 0)));
        }
    }



    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        // Prevent dragging in the Abyss Store inventory
        if (event.getInventory().getSize() == 27 && event.getView().getTitle().equals("CE Store")) {
            event.setCancelled(true); // Cancel any drag action in the Abyss Store
        }
    }

}
