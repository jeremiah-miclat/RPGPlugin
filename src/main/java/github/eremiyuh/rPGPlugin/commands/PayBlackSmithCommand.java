package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import github.eremiyuh.rPGPlugin.utils.ItemUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PayBlackSmithCommand implements CommandExecutor {
    private static final Map<Material, Integer> oresMap = new HashMap<>();
    private final PlayerProfileManager profileManager;

    static {

        oresMap.put(Material.BONE, 10);
        oresMap.put(Material.IRON_INGOT, 100);
        oresMap.put(Material.COPPER_INGOT, 100);
        oresMap.put(Material.GOLD_INGOT, 200);
        oresMap.put(Material.EMERALD, 200);
        oresMap.put(Material.LAPIS_LAZULI, 100);
        oresMap.put(Material.NETHERITE_INGOT, 5000);
        oresMap.put(Material.DIAMOND, 1000);


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
            if (item != null && item.getType() != Material.AIR && !hasCustomLore(item)) {
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
        player.sendMessage("Blacksmith restored " + duraAdded + " durability using your materials!");
        player.sendMessage("Your total durability is now: " + newDura);
    }

    private boolean hasCustomLore(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasLore()) {
            return false;
        }

        // Get the lore and check for specific text (custom lore)
        List<Component> lore = meta.lore();
        for (Component line : lore) {
            if (line.toString().contains("Buy at /abyssstore for 100,000 abyss points.")) {
                return true; // The custom lore is present
            }
        }

        return false;
    }


}
