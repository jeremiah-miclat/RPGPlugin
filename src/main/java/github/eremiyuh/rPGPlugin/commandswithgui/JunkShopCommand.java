package github.eremiyuh.rPGPlugin.commandswithgui;

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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class JunkShopCommand implements CommandExecutor, Listener {
    private final PlayerProfileManager profileManager;

    public JunkShopCommand(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            openJunkShopGui(player);
            return true;
        }
        return false;
    }

    private void openJunkShopGui(Player player) {
        Inventory gui = Bukkit.createInventory(null, 9, "Junk Shop");

        // Add the "Sell All Junk" button to the inventory
        ItemStack sellAllItem = getSellAllJunkItem(player);
        gui.setItem(4, sellAllItem);

        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Junk Shop")) {
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true); // Prevent interaction with other slots

            // Get the player's profile
            UserProfile userProfile = profileManager.getProfile(player.getName());

            // Ensure the player has a valid profile
            if (userProfile == null) {
                player.sendMessage(Component.text("Profile not found!").color(TextColor.color(255, 0, 0)));
                return;
            }

            int userJunk = userProfile.getJunkPoints();

            // Check if the clicked item is the "Sell All Junk" item (diamond)
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.DIAMOND) {
                // Ensure the player has junk points to sell
                if (userJunk > 0) {
                    // Calculate the number of diamonds based on junk points
                    int diamondsToGive = userJunk / 1000; // For example, 1000 junk points = 1 diamond

                    // Deduct all the junk points
                    userProfile.setJunkPoints(0);

                    // Create a stack of diamonds to give to the player
                    ItemStack diamonds = new ItemStack(Material.DIAMOND, diamondsToGive);

                    // Try to add diamonds to the player's inventory
                    if (!player.getInventory().addItem(diamonds).isEmpty()) {
                        // If there is no space in the player's inventory, drop the diamonds on the ground
                        player.getWorld().dropItem(player.getLocation(), diamonds);
                        player.sendMessage(Component.text("Your inventory was full, so your diamonds were dropped on the ground.").color(TextColor.color(255, 0, 0)));
                    } else {
                        player.sendMessage(Component.text("You sold all your junk points for " + diamondsToGive + " diamonds!").color(TextColor.color(0, 255, 0)));
                    }
                } else {
                    player.sendMessage(Component.text("You do not have enough junk points to sell.").color(TextColor.color(255, 0, 0)));
                }
            }
        }
    }


    private ItemStack getSellAllJunkItem(Player player) {
        ItemStack diamondFromJunk = new ItemStack(Material.DIAMOND);
        ItemMeta meta = diamondFromJunk.getItemMeta();
        UserProfile profile = profileManager.getProfile(player.getName());
        int junk= profile.getJunkPoints();
        if (meta != null) {
            meta.displayName(Component.text("Click to sell all junk points for diamonds").color(TextColor.color(250, 250, 8)));
            meta.lore(Arrays.asList(
                    Component.text("Sell all your junk points in exchange for diamonds.").color(TextColor.color(200, 200, 200)),
                    Component.text("1000 : 1.").color(TextColor.color(200, 200, 200)),
                    Component.text("Current points: " + junk).color(TextColor.color(200, 200, 200))
            ));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES); // Hide unnecessary attributes like attack damage
            diamondFromJunk.setItemMeta(meta);
        }

        return diamondFromJunk;
    }
}
