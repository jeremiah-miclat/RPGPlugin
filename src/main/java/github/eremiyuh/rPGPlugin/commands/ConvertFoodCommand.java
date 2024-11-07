package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ConvertFoodCommand implements CommandExecutor {
    private static final Map<Material, Integer> foodHungerMap = new HashMap<>();
    private final PlayerProfileManager profileManager;

    static {
        // Populate the map with hunger values for each food type
        foodHungerMap.put(Material.CARROT, 10);
        foodHungerMap.put(Material.BREAD, 10);
        foodHungerMap.put(Material.BEEF, 30);
        foodHungerMap.put(Material.CHICKEN, 30);
        foodHungerMap.put(Material.COOKED_BEEF, 50);
        foodHungerMap.put(Material.COOKED_CHICKEN, 50);
        foodHungerMap.put(Material.MUTTON ,30);
        foodHungerMap.put(Material.COOKED_MUTTON, 50);
        foodHungerMap.put(Material.PORKCHOP ,30);
        foodHungerMap.put(Material.COOKED_PORKCHOP, 50);
        foodHungerMap.put(Material.RABBIT ,30);
        foodHungerMap.put(Material.RABBIT_STEW ,60);
        foodHungerMap.put(Material.COOKED_RABBIT, 50);
        foodHungerMap.put(Material.COD ,30);
        foodHungerMap.put(Material.COOKED_COD, 50);
        foodHungerMap.put(Material.SALMON ,30);
        foodHungerMap.put(Material.COOKED_SALMON, 50);
        foodHungerMap.put(Material.APPLE, 10);
        foodHungerMap.put(Material.BROWN_MUSHROOM, 5);
        foodHungerMap.put(Material.MUSHROOM_STEW, 20);
        foodHungerMap.put(Material.CHORUS_FRUIT, 10);
        foodHungerMap.put(Material.DRIED_KELP, 10);
        foodHungerMap.put(Material.MELON_SLICE, 10);
        foodHungerMap.put(Material.PUMPKIN_PIE, 10);
        foodHungerMap.put(Material.CAKE, 50);
        foodHungerMap.put(Material.COOKIE, 50);
        foodHungerMap.put(Material.SWEET_BERRIES, 5);
        foodHungerMap.put(Material.GLOW_BERRIES, 10);
        foodHungerMap.put(Material.HONEY_BOTTLE, 10);
        foodHungerMap.put(Material.PUFFERFISH, 10);
        foodHungerMap.put(Material.TROPICAL_FISH, 30);
        foodHungerMap.put(Material.BEETROOT, 10);
        foodHungerMap.put(Material.BEETROOT_SOUP, 20);
        foodHungerMap.put(Material.POTATO, 10);
        foodHungerMap.put(Material.BAKED_POTATO, 20);
        foodHungerMap.put(Material.WHEAT, 10);
        foodHungerMap.put(Material.ROTTEN_FLESH, 10);
        foodHungerMap.put(Material.SPIDER_EYE, 5);
        // Add more food items with appropriate hunger values as needed
    }

    public ConvertFoodCommand(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            convertFoodToStamina(player);
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

}
