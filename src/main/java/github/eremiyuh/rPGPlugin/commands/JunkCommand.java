package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class JunkCommand implements CommandExecutor, Listener {

    private final PlayerProfileManager profileManager;

    public JunkCommand(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Inventory junkInventory = Bukkit.createInventory(null, 36, ChatColor.RED + "Items will disappear");
        player.openInventory(junkInventory);

        return true;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(ChatColor.RED + "Items will disappear").equals(event.getView().getTitle())) return;

        Player player = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();
        int itemCount = 0;

        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                itemCount += item.getAmount();
            }
        }

        inventory.clear();
        UserProfile userProfile = profileManager.getProfile(player.getName());
        int userJunk = userProfile.getJunkPoints();
        userProfile.setJunkPoints(userJunk+itemCount);
        if (itemCount < 1) return;
        player.sendMessage(ChatColor.GREEN + "Received " + itemCount + " junk points.");
    }

//    @EventHandler
//    public void onInventoryClick(InventoryClickEvent event) {
//        // Ensure the player is interacting with the Junk Inventory or their own inventory
//        if (event.getView().getTitle().equals("Junk Inventory")) {
//            // Prevent interaction with the junk inventory (like placing or moving items)
//            if (event.getClickedInventory() != null && event.getClickedInventory().equals(event.getView().getTopInventory())) {
//                event.setCancelled(true);  // Cancel interaction in the Junk Inventory
//            }
//        }
//
//        // Allow interactions only in the player inventory (not in the junk inventory)
//        if (event.getClickedInventory() != null && event.getClickedInventory().equals(event.getView().getBottomInventory())) {
//            event.setCancelled(false); // Allow player inventory interactions
//        }
//    }
}
