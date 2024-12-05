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
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PayBlackSmithCommand implements CommandExecutor, Listener {
    private static final Map<Material, Integer> durabilityMap = new HashMap<>();
    private final PlayerProfileManager profileManager;

    static {

        // Ores and general materials
        durabilityMap.put(Material.BONE, 10);
        durabilityMap.put(Material.IRON_INGOT, 100);
        durabilityMap.put(Material.COPPER_INGOT, 100);
        durabilityMap.put(Material.GOLD_INGOT, 200);
        durabilityMap.put(Material.EMERALD, 200);
        durabilityMap.put(Material.LAPIS_LAZULI, 100);
        durabilityMap.put(Material.NETHERITE_INGOT, 5000);
        durabilityMap.put(Material.DIAMOND, 1000);
        // Equipment: Chainmail and Iron (Armor: 300, Weapons: 500)
        durabilityMap.put(Material.CHAINMAIL_HELMET, 300);
        durabilityMap.put(Material.CHAINMAIL_CHESTPLATE, 400);
        durabilityMap.put(Material.CHAINMAIL_LEGGINGS, 300);
        durabilityMap.put(Material.CHAINMAIL_BOOTS, 300);
        durabilityMap.put(Material.IRON_HELMET, 300);
        durabilityMap.put(Material.IRON_CHESTPLATE, 400);
        durabilityMap.put(Material.IRON_LEGGINGS, 300);
        durabilityMap.put(Material.IRON_BOOTS, 300);
        durabilityMap.put(Material.IRON_SWORD, 400);

        // Gold (Armor: 600, Weapons: 1200)
        durabilityMap.put(Material.GOLDEN_HELMET, 600);
        durabilityMap.put(Material.GOLDEN_CHESTPLATE, 1000);
        durabilityMap.put(Material.GOLDEN_LEGGINGS, 600);
        durabilityMap.put(Material.GOLDEN_BOOTS, 600);
        durabilityMap.put(Material.GOLDEN_SWORD, 1000);

        // Diamond (Armor: 3000, Weapons: 5000)
        durabilityMap.put(Material.DIAMOND_HELMET, 1500);
        durabilityMap.put(Material.DIAMOND_CHESTPLATE, 1500);
        durabilityMap.put(Material.DIAMOND_LEGGINGS, 1500);
        durabilityMap.put(Material.DIAMOND_BOOTS, 1500);
        durabilityMap.put(Material.DIAMOND_SWORD, 2000);

        // Netherite (Armor: 15000, Weapons: 25000)
        durabilityMap.put(Material.NETHERITE_HELMET, 6000);
        durabilityMap.put(Material.NETHERITE_CHESTPLATE, 6000);
        durabilityMap.put(Material.NETHERITE_LEGGINGS, 6000);
        durabilityMap.put(Material.NETHERITE_BOOTS, 6000);
        durabilityMap.put(Material.NETHERITE_SWORD, 7000);

        // Bows and Crossbows
        durabilityMap.put(Material.BOW, 10);
        durabilityMap.put(Material.CROSSBOW, 10);

        // Arrows
        durabilityMap.put(Material.ARROW, 10);

        durabilityMap.put(Material.LEATHER_HELMET, 50);
        durabilityMap.put(Material.LEATHER_CHESTPLATE, 50);
        durabilityMap.put(Material.LEATHER_LEGGINGS, 50);
        durabilityMap.put(Material.LEATHER_BOOTS, 50);

        durabilityMap.put(Material.STONE_SWORD, 100);
        durabilityMap.put(Material.WOODEN_SWORD, 50);

        durabilityMap.put(Material.SHIELD, 200);

        durabilityMap.put(Material.IRON_PICKAXE, 300);
        durabilityMap.put(Material.IRON_AXE, 300);
        durabilityMap.put(Material.IRON_SHOVEL, 300);
        durabilityMap.put(Material.IRON_HOE, 300);

        durabilityMap.put(Material.GOLDEN_PICKAXE, 400);
        durabilityMap.put(Material.GOLDEN_AXE, 400);
        durabilityMap.put(Material.GOLDEN_SHOVEL, 400);
        durabilityMap.put(Material.GOLDEN_HOE, 400);

        durabilityMap.put(Material.DIAMOND_PICKAXE, 1000);
        durabilityMap.put(Material.DIAMOND_AXE, 1000);
        durabilityMap.put(Material.DIAMOND_SHOVEL, 1000);
        durabilityMap.put(Material.DIAMOND_HOE, 1000);

        durabilityMap.put(Material.NETHERITE_PICKAXE, 6000);
        durabilityMap.put(Material.NETHERITE_AXE, 6000);
        durabilityMap.put(Material.NETHERITE_SHOVEL, 6000);
        durabilityMap.put(Material.NETHERITE_HOE, 6000);

        durabilityMap.put(Material.FISHING_ROD, 150);

        durabilityMap.put(Material.IRON_HORSE_ARMOR, 5000);
        durabilityMap.put(Material.GOLDEN_HORSE_ARMOR, 10000);
        durabilityMap.put(Material.DIAMOND_HORSE_ARMOR, 20000);

        durabilityMap.put(Material.SADDLE, 300);

        durabilityMap.put(Material.ELYTRA, 10000);
        durabilityMap.put(Material.TURTLE_HELMET, 100);

        durabilityMap.put(Material.STONE, 1);
        durabilityMap.put(Material.STICK, 1);
        durabilityMap.put(Material.STRING, 1);
        durabilityMap.put(Material.FLINT, 10);
        durabilityMap.put(Material.FLINT_AND_STEEL, 100);
        durabilityMap.put(Material.COAL, 50);
        durabilityMap.put(Material.CHARCOAL, 50);
        durabilityMap.put(Material.BLAZE_ROD, 90);
        durabilityMap.put(Material.BLAZE_POWDER, 30);
        durabilityMap.put(Material.REDSTONE, 10);
        durabilityMap.put(Material.REDSTONE_BLOCK, 90);
        durabilityMap.put(Material.MINECART, 300);
        durabilityMap.put(Material.HOPPER_MINECART, 500);
        durabilityMap.put(Material.CHEST, 100);
        durabilityMap.put(Material.HEART_OF_THE_SEA, 10000);
        durabilityMap.put(Material.WITHER_SKELETON_SKULL, 10000);
        durabilityMap.put(Material.NETHER_STAR, 100000);
        durabilityMap.put(Material.BUCKET, 200);

    }

    public PayBlackSmithCommand(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            openBlacksmithInventory(player);
        }
        return true;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();
        // Ensure the event is from our custom Blacksmith inventory
        if (!Component.text("Items will be lost for durability.").content().equals(event.getView().getTitle())) return;


            // Loop through all the items in the blacksmith inventory
        int duraAdded = 0;
            for (ItemStack item : inventory.getContents()) {
                if (item != null && item.getType() != Material.AIR && !hasCustomLore(item)) {
                    if (durabilityMap.containsKey(item.getType())) {
                        // If the item is a valid ore, convert it to durability
                        int durabilityValue = durabilityMap.get(item.getType()) * item.getAmount();
                        duraAdded += durabilityValue;
                        inventory.remove(item);
                    }
                }
            }

            // If any items were valid and converted, update the player's durability
            if (duraAdded > 0) {
                UserProfile userProfile = profileManager.getProfile(player.getName());
                int newDurability = userProfile.getDurability() + duraAdded;
                userProfile.setDurability(newDurability);

                // Send feedback to the player
                player.sendMessage("You gained " + duraAdded + " durability!");
                player.sendMessage("Your total durability is now: " + newDurability);
            }

            // After processing, return invalid items to the player's inventory
            for (ItemStack item : inventory.getContents()) {
                if (item != null && item.getType() != Material.AIR ) {
                    // Return invalid items back to the player's inventory
                    player.getInventory().addItem(item);
                }
            }
            profileManager.saveProfile(player.getName());
            // Clear the blacksmith inventory after processing
            inventory.clear();

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
                if (durabilityMap.containsKey(material)) {
                    int duraRestored = durabilityMap.get(material);
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

    public void openBlacksmithInventory(Player player) {
        Inventory blacksmithInventory = Bukkit.createInventory(null, 27, "Items will be lost for durability.");
        // Open the inventory for the player
        player.openInventory(blacksmithInventory);
    }

}
