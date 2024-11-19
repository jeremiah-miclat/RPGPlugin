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

import java.util.Arrays;

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



        abyssStore.setItem(4, abyssIngot); // Place the Abyss Mana Stone in the center of the inventory

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
                player.sendMessage("Profile not found!");
                return;
            }

            // Check if the clicked item is the Abyss Mana Stone (slot 4)
            if (event.getSlot() == 4 && event.getCurrentItem() != null && event.getCurrentItem().isSimilar(getAbyssIngot())) {
                // Check if the player has enough Abyss Points
                if (userProfile.getAbyssPoints() >= 100000) {
                    userProfile.setAbyssPoints(userProfile.getAbyssPoints() - 100000); // Deduct Abyss Points
                    player.getInventory().addItem(ItemUtils.getAbyssIngot()); // Give the player the Abyss Mana Stone
                    player.sendMessage(Component.text("Successfully purchased Abyss Mana Stone!").color(TextColor.color(0, 255, 0)));
                } else {
                    player.sendMessage(Component.text("You do not have enough Abyss Points!").color(TextColor.color(255, 0, 0)));
                }
            }
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
