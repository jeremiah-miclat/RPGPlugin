package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;


public class FishSell implements CommandExecutor, Listener {

    private final PlayerProfileManager profileManager;

    public FishSell(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        UserProfile userProfile = profileManager.getProfile(player.getName());
        int bonusPointsFromPassive = userProfile.getFisherman();
        if (bonusPointsFromPassive < 1) {player.sendMessage(ChatColor.RED+ "" +ChatColor.ITALIC + "Level up fisherman skill to start selling fish"); return true;}
        Inventory junkInventory = Bukkit.createInventory(null, 36, ChatColor.RED + "Fish will disappear");
        player.openInventory(junkInventory);

        return true;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        // Check if the inventory title is the one we're interested in
        if (!(ChatColor.RED + "Fish will disappear").equals(event.getView().getTitle())) return;

        Player player = (Player) event.getPlayer(); // The player triggering the event (converter)

        // Retrieve the user's profile and update activity points
        UserProfile userProfile = profileManager.getProfile(player.getName());
        int bonusPointsFromPassive = userProfile.getFisherman();
        if (bonusPointsFromPassive < 1) {player.sendMessage("Level up fisherman skill to start selling fish. /passiveskills"); return;}
        Inventory inventory = event.getInventory();
        int itemCount = 0;
        List<ItemStack> unconvertedItems = new ArrayList<>();

        // Loop through each item in the inventory
        for (ItemStack item : inventory.getContents()) {
            // Ensure the item is not null and not air
            if (item != null && item.getType() != Material.AIR) {
                // Check the item's lore to see if it has the "Caught By: playerName" lore
                List<String> lore = item.getItemMeta() != null ? item.getItemMeta().getLore() : null;

                boolean isConverted = false;

                if (lore != null && lore.stream().anyMatch(line -> line.equals("Caught by: " + player.getName()))) {
                    // Calculate points based on the item type
                    if (item.getType() == Material.COD) {
                        itemCount += (item.getAmount()*(10 + bonusPointsFromPassive));
                        isConverted = true;
                    }
                    else if (item.getType() == Material.SALMON) {
                        itemCount += (item.getAmount() *(20 + bonusPointsFromPassive));
                        isConverted = true;
                    }
                    else if (item.getType() == Material.TROPICAL_FISH) {
                        itemCount += (item.getAmount() *(30 + bonusPointsFromPassive));
                        isConverted = true;
                    }
                    else if (item.getType() == Material.PUFFERFISH) {
                        itemCount += (item.getAmount() *(40 + bonusPointsFromPassive));
                        isConverted = true;
                    }
                    else if (item.getType() == Material.NAUTILUS_SHELL) {
                        itemCount += (item.getAmount() *(100 + bonusPointsFromPassive));
                        isConverted = true;
                    }
                    else {
                        itemCount += item.getAmount()*bonusPointsFromPassive;
                        isConverted = true;
                    }
                }

                // If the item wasn't converted, add it to the list to give it back to the player
                if (!isConverted) {
                    unconvertedItems.add(item);
                }
            }
        }

        // Clear the inventory after processing
        inventory.clear();

        double activitypoints = userProfile.getActivitypoints();
        userProfile.setActivitypoints(activitypoints + itemCount);

        // Only send the message if the player has earned at least one activity point
        if (itemCount >= 1) {
            player.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "Received " + itemCount + " activity points.");
        }

        // Return unconverted items to the player
        if (!unconvertedItems.isEmpty()) {
            player.getInventory().addItem(unconvertedItems.toArray(new ItemStack[0]));
            player.sendMessage(ChatColor.RED + "Some items couldn't be converted and have been returned to you.");
        }
    }




}
