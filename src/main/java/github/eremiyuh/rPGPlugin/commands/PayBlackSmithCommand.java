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

public class PayBlackSmithCommand implements CommandExecutor {
    private static final Map<Material, Integer> oresMap = new HashMap<>();
    private final PlayerProfileManager profileManager;

    static {
        // Populate the map with hunger values for each food type
        oresMap.put(Material.IRON_INGOT, 100);
        oresMap.put(Material.GOLD_INGOT, 200);
        oresMap.put(Material.EMERALD, 200);
        oresMap.put(Material.DIAMOND, 1000);

        // Add more food items with appropriate hunger values as needed
    }

    public PayBlackSmithCommand(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            convertOreToDura(player);
        }
        return true;
    }


    // Method to convert ores to durability
    public void convertOreToDura(Player player) {
        Inventory inventory = player.getInventory();
        int duraAdded = 0;

        // Loop through inventory to find ores
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                Material material = item.getType();

                // Check if the item is in the ores map
                if (oresMap.containsKey(material)) {
                    int duraRestored = oresMap.get(material);
                    int stackSize = item.getAmount();

                    // Add durability based on quantity of the item stack
                    duraAdded += duraRestored * stackSize;

                    // Remove the item from inventory after conversion
                    inventory.remove(item);
                }
            }
        }

        UserProfile userProfile = profileManager.getProfile(player.getName());

        // Get the current durability, add the new durability, and update it
        int currentDurability = userProfile.getDurability();
        int newDura = currentDurability + duraAdded;

        // Optionally, set a max durability limit if necessary
        // newDura = Math.min(newDura, MAX_DURABILITY);

        userProfile.setDurability(newDura);

        // Send feedback to player
        player.sendMessage("Blacksmith restored " + duraAdded + " durability using your ores!");
        player.sendMessage("Your total durability is now: " + newDura);
    }

}
