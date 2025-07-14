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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static github.eremiyuh.rPGPlugin.utils.ItemUtils.getAbyssOre;

public class ActivitiyShop implements CommandExecutor, Listener {
    private final RPGPlugin plugin;
    private final PlayerProfileManager profileManager;

    public ActivitiyShop(RPGPlugin plugin, PlayerProfileManager profileManager) {
        this.plugin = plugin;
        this.profileManager = profileManager;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            activityStore(player);
            return true;
        }
        return false;
    }

    public void activityStore(Player player) {
        // Create the Abyss Store inventory with 9 slots
        Inventory activityPointStore = Bukkit.createInventory(null, 9, Component.text("Activity Point Store").color(TextColor.color(255, 0, 0)));

        // Create the Ender Pearl item
        ItemStack enderPearl = new ItemStack(Material.ENDER_PEARL);
        ItemMeta enderPearlMeta = enderPearl.getItemMeta();
        if (enderPearlMeta != null) {
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Cost: 5,000 Active Points").color(TextColor.color(255, 215, 0)));
            enderPearlMeta.lore(lore);
            enderPearl.setItemMeta(enderPearlMeta);
        }
        activityPointStore.setItem(0, enderPearl);

        // Create the Villager Spawn Egg item
        ItemStack villagerEgg = new ItemStack(Material.VILLAGER_SPAWN_EGG);
        ItemMeta villagerEggMeta = villagerEgg.getItemMeta();
        if (villagerEggMeta != null) {
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Cost: 10,000 Active Points").color(TextColor.color(255, 215, 0)));
            villagerEggMeta.lore(lore);
            villagerEgg.setItemMeta(villagerEggMeta);
        }
        activityPointStore.setItem(1, villagerEgg);

        // Create the Zombie Spawn Egg item
        ItemStack zombieEgg = new ItemStack(Material.ZOMBIE_SPAWN_EGG);
        ItemMeta zombieEggMeta = zombieEgg.getItemMeta();
        if (zombieEggMeta != null) {
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Cost: 10,000 Active Points").color(TextColor.color(255, 215, 0)));
            zombieEggMeta.lore(lore);
            zombieEgg.setItemMeta(zombieEggMeta);
        }
        activityPointStore.setItem(2, zombieEgg);

        // Create the Shulker Spawn Egg item
        ItemStack shulkerEgg = new ItemStack(Material.SHULKER_SPAWN_EGG);
        ItemMeta shulkerEggMeta = shulkerEgg.getItemMeta();
        if (shulkerEggMeta != null) {
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Cost: 100,000 Active Points").color(TextColor.color(255, 215, 0)));
            shulkerEggMeta.lore(lore);
            shulkerEgg.setItemMeta(shulkerEggMeta);
        }
        activityPointStore.setItem(3, shulkerEgg);

        // Create the Piglin Spawn Egg item
        ItemStack piglinEgg = new ItemStack(Material.PIGLIN_SPAWN_EGG);
        ItemMeta piglinEggMeta = piglinEgg.getItemMeta();
        if (piglinEggMeta != null) {
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Cost: 50,000 Active Points").color(TextColor.color(255, 215, 0)));
            piglinEggMeta.lore(lore);
            piglinEgg.setItemMeta(piglinEggMeta);
        }
        activityPointStore.setItem(4, piglinEgg);




        // Create the activity point item
        ItemStack activePoints = new ItemStack(Material.WOODEN_PICKAXE);
        ItemMeta activePointsMeta = activePoints.getItemMeta();
        if (activePointsMeta != null) {
            activePointsMeta.customName(Component.text("1000 Activity Points"));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Cost: 1 diamond currency").color(TextColor.color(255, 215, 0)));
            activePointsMeta.lore(lore);
            activePoints.setItemMeta(activePointsMeta);
        }
        activityPointStore.setItem(8, activePoints);

        player.openInventory(activityPointStore);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().title().equals(Component.text("Activity Point Store").color(TextColor.color(255, 0, 0)))) {
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            UserProfile userProfile = profileManager.getProfile(player.getName());

            if (userProfile == null) {
                player.sendMessage(Component.text("Profile not found!").color(TextColor.color(255, 0, 0)));
                return;
            }
            if (event.getCurrentItem() == null) return;
            ItemMeta itemmeta = event.getCurrentItem().getItemMeta();
            if (!itemmeta.hasLore()) return;

            // Handle item purchase logic
            if (event.getSlot() == 0 && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.ENDER_PEARL) {
                if (userProfile.getActivitypoints() >= 5000) {
                    userProfile.setCurrency("activitypoints", userProfile.getActivitypoints() - 5000);
                    dropOrNotify(player, new ItemStack(Material.ENDER_PEARL), "Successfully purchased.");
                } else {
                    player.sendMessage(Component.text("You do not have enough Points!").color(TextColor.color(255, 0, 0)));
                }
            } else if (event.getSlot() == 1 && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.VILLAGER_SPAWN_EGG) {
                if (userProfile.getActivitypoints() >= 10000) {
                    userProfile.setCurrency("activitypoints", userProfile.getActivitypoints() - 10000);
                    dropOrNotify(player, new ItemStack(Material.VILLAGER_SPAWN_EGG), "Successfully purchased.");
                } else {
                    player.sendMessage(Component.text("You do not have enough Points!").color(TextColor.color(255, 0, 0)));
                }
            } else if (event.getSlot() == 2 && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.ZOMBIE_SPAWN_EGG) {
                if (userProfile.getActivitypoints() >= 10000) {
                    userProfile.setCurrency("activitypoints", userProfile.getActivitypoints() - 10000);
                    dropOrNotify(player, new ItemStack(Material.ZOMBIE_SPAWN_EGG), "Successfully purchased.");
                } else {
                    player.sendMessage(Component.text("You do not have enough Points!").color(TextColor.color(255, 0, 0)));
                }
            } else if (event.getSlot() == 3 && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.SHULKER_SPAWN_EGG) {
                if (userProfile.getActivitypoints() >= 100000) {
                    userProfile.setCurrency("activitypoints", userProfile.getActivitypoints() - 100000);
                    dropOrNotify(player, new ItemStack(Material.SHULKER_SPAWN_EGG), "Successfully purchased.");
                } else {
                    player.sendMessage(Component.text("You do not have enough Points!").color(TextColor.color(255, 0, 0)));
                }
            } else if (event.getSlot() == 4 && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.PIGLIN_SPAWN_EGG) {
                if (userProfile.getActivitypoints() >= 50000) {
                    userProfile.setCurrency("activitypoints", userProfile.getActivitypoints() - 50000);
                    dropOrNotify(player, new ItemStack(Material.PIGLIN_SPAWN_EGG), "Successfully purchased.");
                } else {
                    player.sendMessage(Component.text("You do not have enough Points!").color(TextColor.color(255, 0, 0)));
                }
            } else if (event.getSlot() == 8 && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.WOODEN_PICKAXE) {
                if (userProfile.getDiamond() >= 1) {
                    userProfile.setCurrency("diamond", userProfile.getDiamond() - 1);
                    userProfile.setCurrency("activitypoints", userProfile.getActivitypoints() + 1000);
                } else {
                    player.sendMessage(Component.text("You do not have enough Diamond currency! Do /convertmaterial diamond").color(TextColor.color(255, 0, 0)));
                }
            }
        }
    }

    private void dropOrNotify(Player player, ItemStack item, String successMessage) {
        if (player.getInventory().firstEmpty() != -1) {
            // Add item to inventory
            player.getInventory().addItem(item);
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
        if (event.getInventory().getSize() == 9 && event.getView().getTitle().equals("Activity Point Store")) {
            event.setCancelled(true);
        }
    }
}
