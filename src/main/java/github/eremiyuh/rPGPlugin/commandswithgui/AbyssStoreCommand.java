package github.eremiyuh.rPGPlugin.commandswithgui;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import github.eremiyuh.rPGPlugin.utils.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
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
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static github.eremiyuh.rPGPlugin.utils.ItemUtils.getAbyssOre;
import static github.eremiyuh.rPGPlugin.utils.ItemUtils.getAbyssPotion;

public class AbyssStoreCommand implements CommandExecutor, Listener {
    private final RPGPlugin plugin;
    private final PlayerProfileManager profileManager;


    public AbyssStoreCommand(RPGPlugin plugin, PlayerProfileManager profileManager) {
        this.plugin = plugin;
        this.profileManager = profileManager;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            openAbyssStore(player);
            return true;
        }
        return false;
    }

    public void openAbyssStore(Player player) {
        // Create the Abyss Store inventory with 9 slots
        Inventory abyssStore = Bukkit.createInventory(null, 9, Component.text("Abyss Store").color(TextColor.color(255, 0, 0)));

        // Create the Abyss Mana Stone item
        ItemStack abyssIngot = getAbyssIngot();
        abyssStore.setItem(0, abyssIngot); // Place the Abyss Mana Stone in the center of the inventory

        // Create the Abyss Potion item
        ItemStack abyssPotion = getAbyssPotion();
        abyssStore.setItem(1, abyssPotion);

        // Create the Abyss Ore item
        ItemStack abyssOre = getAbyssOre();
        abyssStore.setItem(2, abyssOre);

        // Create the Ender Pearl item
        ItemStack enderPearl = new ItemStack(Material.ENDER_PEARL);
        ItemMeta enderPearlMeta = enderPearl.getItemMeta(); // Get the ItemMeta of the Ender Pearl

        if (enderPearlMeta != null) {
            // Add lore to show the cost
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Cost: 10,000 Abyss Points").color(TextColor.color(255, 215, 0))); // Gold color for cost
            enderPearlMeta.lore(lore); // Set the lore to the ItemMeta

            enderPearl.setItemMeta(enderPearlMeta); // Apply the modified ItemMeta to the Ender Pearl
        }

        abyssStore.setItem(3, enderPearl); // Place the Ender Pearl with the lore at the 7th slot


        // WARDEN SPAWN EGG
        ItemStack wardenSpawnEgg = new ItemStack(Material.WARDEN_SPAWN_EGG);
        ItemMeta wardenSEMeta = wardenSpawnEgg.getItemMeta();

        if (wardenSEMeta != null) {
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("World Boss Spawn Egg").color(TextColor.color(255, 215, 0)));
            lore.add(Component.text("Cost: 1,000 Emerald").color(TextColor.color(255, 215, 0)));
            lore.add(Component.text("Double abyss point reward").color(TextColor.color(255, 215, 0)));
            lore.add(Component.text("Doesn't despawn").color(TextColor.color(255, 215, 0)));
            wardenSEMeta.lore(lore);

            wardenSpawnEgg.setItemMeta(wardenSEMeta);
        }

        abyssStore.setItem(4, wardenSpawnEgg);

        // RAVAGER SPAWN EGG
        ItemStack ravagerSpawnEgg = new ItemStack(Material.RAVAGER_SPAWN_EGG);
        ItemMeta ravagerSEMeta = ravagerSpawnEgg.getItemMeta();

        if (ravagerSEMeta != null) {
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("World Boss Spawn Egg").color(TextColor.color(255, 215, 0)));
            lore.add(Component.text("Cost: 1,000 Emerald").color(TextColor.color(255, 215, 0)));
            lore.add(Component.text("x1.5 abyss point reward").color(TextColor.color(255, 215, 0)));
            lore.add(Component.text("Doesn't despawn").color(TextColor.color(255, 215, 0)));
            ravagerSEMeta.lore(lore);

            ravagerSpawnEgg.setItemMeta(ravagerSEMeta);
        }

        abyssStore.setItem(5, ravagerSpawnEgg);


        // EVOKER SPAWN EGG
        ItemStack evoSpawnEgg = new ItemStack(Material.EVOKER_SPAWN_EGG);
        ItemMeta evoSEMeta = evoSpawnEgg.getItemMeta();

        if (evoSEMeta != null) {
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("World Boss Spawn Egg").color(TextColor.color(255, 215, 0)));
            lore.add(Component.text("Cost: 1,000 Emerald").color(TextColor.color(255, 215, 0)));
            lore.add(Component.text("x1.5 abyss point reward").color(TextColor.color(255, 215, 0)));
            lore.add(Component.text("Doesn't despawn").color(TextColor.color(255, 215, 0)));
            evoSEMeta.lore(lore);

            evoSpawnEgg.setItemMeta(evoSEMeta);
        }

        abyssStore.setItem(6, evoSpawnEgg);

        // Wither SPAWN EGG
        ItemStack withSpawnEgg = new ItemStack(Material.WITHER_SPAWN_EGG);
        ItemMeta withMeta = withSpawnEgg.getItemMeta();

        if (withMeta != null) {
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("World Boss Spawn Egg").color(TextColor.color(255, 215, 0)));
            lore.add(Component.text("Cost: 1,000 Emerald").color(TextColor.color(255, 215, 0)));
            lore.add(Component.text("x1.5 abyss point reward").color(TextColor.color(255, 215, 0)));
            lore.add(Component.text("Doesn't despawn").color(TextColor.color(255, 215, 0)));
            withMeta.lore(lore);

            withSpawnEgg.setItemMeta(withMeta);
        }

        abyssStore.setItem(7, withSpawnEgg);



        // Open the Abyss Store GUI for the player
        player.openInventory(abyssStore);
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().title().equals(Component.text("Abyss Store").color(TextColor.color(255, 0, 0)))) {
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true); // Prevent interaction with other slots
            UserProfile userProfile = profileManager.getProfile(player.getName());

            // Ensure the player has a valid profile
            if (userProfile == null) {
                player.sendMessage(Component.text("Profile not found!").color(TextColor.color(255, 0, 0)));
                return;
            }

            // Handle item purchase logic
            if (event.getSlot() == 2 && event.getCurrentItem() != null && event.getCurrentItem().isSimilar(getAbyssOre())) {
                if (userProfile.getAbysspoints() >= 1000) {
                    userProfile.setAbysspoints(userProfile.getAbysspoints() - 1000); // Deduct Abyss Points
                    dropOrNotify(player, ItemUtils.getAbyssOre(), "Successfully purchased Abyss Ore!");
                } else {
                    player.sendMessage(Component.text("You do not have enough Abyss Points!").color(TextColor.color(255, 0, 0)));
                }
            }

            if (event.getSlot() == 0 && event.getCurrentItem() != null && event.getCurrentItem().isSimilar(getAbyssIngot())) {
                if (userProfile.getAbysspoints() >= 100000) {
                    userProfile.setAbysspoints(userProfile.getAbysspoints() - 100000); // Deduct Abyss Points
                    dropOrNotify(player, ItemUtils.getAbyssIngot(), "Successfully purchased Abyss Mana Stone!");
                } else {
                    player.sendMessage(Component.text("You do not have enough Abyss Points!").color(TextColor.color(255, 0, 0)));
                }
            }

            if (event.getSlot() == 1 && event.getCurrentItem() != null && event.getCurrentItem().isSimilar(getAbyssPotion())) {
                if (userProfile.getAbysspoints() >= 1000) {
                    userProfile.setAbysspoints(userProfile.getAbysspoints() - 1000); // Deduct Abyss Points
                    dropOrNotify(player, ItemUtils.getAbyssPotion(), "Successfully purchased Abyss Potion!");
                } else {
                    player.sendMessage(Component.text("You do not have enough Abyss Points!").color(TextColor.color(255, 0, 0)));
                }
            }

            // Ender Pearl (10,000 Abyss Points)
            if (event.getSlot() == 3 && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.ENDER_PEARL) {
                if (userProfile.getAbysspoints() >= 10000) {
                    userProfile.setAbysspoints(userProfile.getAbysspoints() - 10000); // Deduct Abyss Points
                    dropOrNotify(player, new ItemStack(Material.ENDER_PEARL), "Successfully purchased Ender Pearl!");
                } else {
                    player.sendMessage(Component.text("You do not have enough Abyss Points!").color(TextColor.color(255, 0, 0)));
                }
            }


            //WARDEN
            if (event.getSlot() == 4 && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.WARDEN_SPAWN_EGG) {
                if (userProfile.getEmerald() >= 1000) {
                    userProfile.setEmerald(userProfile.getEmerald() - 1000); // Deduct Abyss Points
                    dropOrNotify(player, new ItemStack(Material.WARDEN_SPAWN_EGG), "Successfully purchased!");
                } else {
                    player.sendMessage(Component.text("You do not have enough emerald!").color(TextColor.color(255, 0, 0)));
                }
            }

            //RAV
            if (event.getSlot() == 5 && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.RAVAGER_SPAWN_EGG) {
                if (userProfile.getEmerald() >= 1000) {
                    userProfile.setEmerald(userProfile.getEmerald() - 1000); // Deduct Abyss Points
                    dropOrNotify(player, new ItemStack(Material.RAVAGER_SPAWN_EGG), "Successfully purchased!");
                } else {
                    player.sendMessage(Component.text("You do not have enough emerald!").color(TextColor.color(255, 0, 0)));
                }
            }

            //EVOKER
            if (event.getSlot() == 6 && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.EVOKER_SPAWN_EGG) {
                if (userProfile.getEmerald() >= 1000) {
                    userProfile.setEmerald(userProfile.getEmerald() - 1000); // Deduct Abyss Points
                    dropOrNotify(player, new ItemStack(Material.EVOKER_SPAWN_EGG), "Successfully purchased!");
                } else {
                    player.sendMessage(Component.text("You do not have enough emerald!").color(TextColor.color(255, 0, 0)));
                }
            }

            //WITHER
            if (event.getSlot() == 7 && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.WITHER_SPAWN_EGG) {
                if (userProfile.getEmerald() >= 1000) {
                    userProfile.setEmerald(userProfile.getEmerald() - 1000); // Deduct Abyss Points
                    dropOrNotify(player, new ItemStack(Material.WITHER_SPAWN_EGG), "Successfully purchased!");
                } else {
                    player.sendMessage(Component.text("You do not have enough emerald!").color(TextColor.color(255, 0, 0)));
                }
            }
        }
    }

    /**
     * Drops the item near the player if their inventory is full, or adds it to their inventory.
     *
     * @param player The player receiving the item.
     * @param item   The item to give.
     * @param successMessage The message to send on successful purchase.
     */
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
        if (event.getInventory().getSize() == 9 && event.getView().getTitle().equals("Abyss Store")) {
            event.setCancelled(true); // Cancel any drag action in the Abyss Store
        }
    }

    private ItemStack getAbyssIngot() {
        // Create the Abyss Mana Stone item (Netherite Ingot)
        ItemStack abyssIngot = new ItemStack(Material.NETHERITE_INGOT);
        ItemMeta meta = abyssIngot.getItemMeta();

        if (meta != null) {
            meta.displayName(Component.text("Abyss Mana Stone").color(TextColor.color(250, 250, 8)));
            meta.lore(Arrays.asList(
                    Component.text("Cost: 100,000 Abyss Points"),
                    Component.text("Can be traded for equips"),
                    Component.text("Go to trading hall and look for Yabmat.")
            ));
            meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            meta.setEnchantmentGlintOverride(true);
            abyssIngot.setItemMeta(meta);
        }

        return abyssIngot;
    }
}
