package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ConvertFoodCommand implements CommandExecutor, Listener {
    private static final Map<Material, Integer> foodHungerMap = new HashMap<>();
    private final PlayerProfileManager profileManager;

    static {
        // Populate the map with hunger values for each food type
        foodHungerMap.put(Material.CARROT, 20);
        foodHungerMap.put(Material.BREAD, 20);
        foodHungerMap.put(Material.BEEF, 30);
        foodHungerMap.put(Material.CHICKEN, 30);
        foodHungerMap.put(Material.COOKED_BEEF, 70);
        foodHungerMap.put(Material.COOKED_CHICKEN, 50);
        foodHungerMap.put(Material.MUTTON ,30);
        foodHungerMap.put(Material.COOKED_MUTTON, 55);
        foodHungerMap.put(Material.PORKCHOP ,30);
        foodHungerMap.put(Material.COOKED_PORKCHOP, 55);
        foodHungerMap.put(Material.RABBIT ,30);
        foodHungerMap.put(Material.RABBIT_STEW ,60);
        foodHungerMap.put(Material.COOKED_RABBIT, 50);
        foodHungerMap.put(Material.COD ,30);
        foodHungerMap.put(Material.COOKED_COD, 50);
        foodHungerMap.put(Material.SALMON ,30);
        foodHungerMap.put(Material.COOKED_SALMON, 50);
        foodHungerMap.put(Material.APPLE, 30);
        foodHungerMap.put(Material.BROWN_MUSHROOM, 5);
        foodHungerMap.put(Material.MUSHROOM_STEW, 20);
        foodHungerMap.put(Material.CHORUS_FRUIT, 30);
        foodHungerMap.put(Material.DRIED_KELP, 30);
        foodHungerMap.put(Material.MELON_SLICE, 20);
        foodHungerMap.put(Material.PUMPKIN_PIE, 20);
        foodHungerMap.put(Material.CAKE, 50);
        foodHungerMap.put(Material.COOKIE, 50);
        foodHungerMap.put(Material.SWEET_BERRIES, 5);
        foodHungerMap.put(Material.GLOW_BERRIES, 10);
        foodHungerMap.put(Material.HONEY_BOTTLE, 30);
        foodHungerMap.put(Material.PUFFERFISH, 10);
        foodHungerMap.put(Material.TROPICAL_FISH, 30);
        foodHungerMap.put(Material.BEETROOT, 10);
        foodHungerMap.put(Material.BEETROOT_SOUP, 20);
        foodHungerMap.put(Material.POTATO, 20);
        foodHungerMap.put(Material.BAKED_POTATO, 30);
        foodHungerMap.put(Material.WHEAT, 10);
        foodHungerMap.put(Material.ROTTEN_FLESH, 10);
        foodHungerMap.put(Material.SPIDER_EYE, 5);
        foodHungerMap.put(Material.GOLDEN_APPLE, 200);
        foodHungerMap.put(Material.ENCHANTED_GOLDEN_APPLE, 10000);
        foodHungerMap.put(Material.SUSPICIOUS_STEW, 40);
        foodHungerMap.put(Material.MILK_BUCKET, 100);
        foodHungerMap.put(Material.GLISTERING_MELON_SLICE, 150);
        foodHungerMap.put(Material.GOLDEN_CARROT, 150);
    }

    public ConvertFoodCommand(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            openStaminaInventory(player);
        }
        return true;
    }


    public void convertFoodToStamina(Player player) {
        Inventory inventory = player.getInventory();
        int staminaAdded = 0;

        // Loop through inventory to find food items
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                Material material = item.getType();

                // Check if the item is in the food hunger map
                if (foodHungerMap.containsKey(material)) {
                    int hungerRestored = foodHungerMap.get(material);

                    // Add stamina based on quantity of the item stack
                    int stackSize = item.getAmount();
                    staminaAdded += hungerRestored * stackSize;

                    // Remove the item from inventory after conversion
                    inventory.remove(item);
                }
            }
        }

        UserProfile userProfile = profileManager.getProfile(player.getName());

        // Get the current stamina, add the new stamina, and update it
        int currentStamina = userProfile.getStamina();
        int newStamina = currentStamina + staminaAdded;

        // Optionally, set a max stamina limit if necessary (e.g., 100 stamina)
        // newStamina = Math.min(newStamina, MAX_STAMINA);

        userProfile.setStamina(newStamina);

        // Send feedback to player
        player.sendMessage("You converted " + staminaAdded + " stamina from your food!");
        player.sendMessage("Your total stamina is now: " + newStamina);
    }


    public void openStaminaInventory(Player player) {
        Inventory staminaInventory = Bukkit.createInventory(null, 27, "Restore your stamina.");
        // Open the inventory for the player
        player.openInventory(staminaInventory);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!Component.text("Restore your stamina.").content().equals(event.getView().getTitle())) return;
        Player player = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();
        // Ensure the event is from our custom Blacksmith inventory



        int staminaAdded = 0;

        // Loop through inventory to find food items
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                Material material = item.getType();

                // Check if the item is in the food hunger map
                if (foodHungerMap.containsKey(material)) {
                    int hungerRestored = foodHungerMap.get(material);

                    // Add stamina based on quantity of the item stack
                    int stackSize = item.getAmount();
                    staminaAdded += hungerRestored * stackSize;

                    // Remove the item from inventory after conversion
                    inventory.remove(item);
                }
            }
        }

        if (staminaAdded >0) {
            UserProfile userProfile = profileManager.getProfile(player.getName());

            // Get the current stamina, add the new stamina, and update it
            int currentStamina = userProfile.getStamina();
            int newStamina = currentStamina + staminaAdded;

            // Optionally, set a max stamina limit if necessary (e.g., 100 stamina)
            // newStamina = Math.min(newStamina, MAX_STAMINA);

            userProfile.setStamina(newStamina);

            // Send feedback to player
            player.sendMessage("You converted " + staminaAdded + " stamina from your food!");
            player.sendMessage("Your total stamina is now: " + newStamina);
        }


        // After processing, return invalid items to the player's inventory
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() != Material.AIR ) {
                // Return invalid items back to the player's inventory
                player.getInventory().addItem(item);
            }
        }

        inventory.clear();
    }
}
