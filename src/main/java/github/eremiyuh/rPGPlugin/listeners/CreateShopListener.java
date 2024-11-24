package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import github.eremiyuh.rPGPlugin.manager.ChunkManager;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.manager.ShopsManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import github.eremiyuh.rPGPlugin.utils.ShopInventoryHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.File;
import java.util.Set;
import java.util.HashSet;

import java.util.*;

import static github.eremiyuh.rPGPlugin.utils.HologramUtil.createHologramStack;
import static github.eremiyuh.rPGPlugin.utils.ItemUtils.getAbyssOre;
import static github.eremiyuh.rPGPlugin.utils.ItemUtils.getAbyssPotion;

public class CreateShopListener implements Listener {
    private final RPGPlugin plugin;
    private final ChunkManager chunkManager;
    private final PlayerProfileManager profileManager;
    private final ShopsManager shopsManager;
    public CreateShopListener(RPGPlugin plugin, ChunkManager chunkManager, PlayerProfileManager profileManager, ShopsManager shopsManager) {
        this.plugin = plugin;
        this.chunkManager = chunkManager;
        this.profileManager = profileManager;
        this.shopsManager = shopsManager;
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        Block signBlock = event.getBlock();
        String[] lines = event.getLines();
        Block chestBlock = getAttachedBlock(signBlock);

        // Allow regular sign placement if the format doesn't match shop creation
        if (!isShopFormat(lines)) {
            return; // Exit the event handler to allow normal sign placement
        }

        // Ensure the chest is part of the shop setup and doesn't already belong to another shop
        if (chestBlock != null && chestBlock.hasMetadata("seller")) {
            player.sendMessage("This chest is already being used as a shop!");
            event.setCancelled(true);
            return;
        }

        // List of valid currencies
        Set<String> validCurrencies = Set.of("diamond", "emerald", "iron", "lapis", "gold", "enderpearl", "netherite", "copper");

        try {
            int numberOfItems = Integer.parseInt(lines[0].trim());
            String itemBeingTraded = lines[1].trim().toLowerCase();
            int numberOfItemsBeingTraded = Integer.parseInt(lines[2].trim());

            if (!validCurrencies.contains(itemBeingTraded)) {
                player.sendMessage("Invalid currency: " + itemBeingTraded);
                player.sendMessage("Currencies: diamond, emerald, iron, lapis, gold, enderpearl, netherite, copper");
                event.setCancelled(true);
                return;
            }

            if (chestBlock != null && chestBlock.getType() == Material.CHEST) {
                Chest chest = (Chest) chestBlock.getState();
                Inventory inventory = chest.getInventory();

                ItemStack firstItem = inventory.getItem(0);
                if (firstItem != null) {
                    String itemName = firstItem.hasItemMeta() && firstItem.getItemMeta().hasDisplayName()
                            ? firstItem.getItemMeta().getDisplayName()
                            : firstItem.getType().toString();

                    shopsManager.saveShopForPlayer(
                            player.getName(), firstItem, numberOfItems, numberOfItemsBeingTraded, itemBeingTraded, chest.getLocation()
                    );
                    setBarrelMetaData(chest, player.getName(), firstItem, numberOfItems, itemBeingTraded, numberOfItemsBeingTraded);

                    signBlock.breakNaturally();
                    player.sendMessage("Shop successfully created!");
                } else {
                    player.sendMessage("The chest is either empty or the item you want to sell is not on first slot.");
                }
            } else {
                player.sendMessage("Plugin error, report to admin");
                event.setCancelled(true);
            }
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid number format! Ensure the first and third lines are numbers.");
            event.setCancelled(true);
        }
    }

    /**
     * Checks if the lines of the sign follow the expected shop format.
     *
     * @param lines the lines of the sign
     * @return true if the sign format is valid for creating a shop
     */
    private boolean isShopFormat(String[] lines) {
        if (lines.length < 3) return false;

        // First line must be a valid integer
        if (!lines[0].trim().matches("\\d+")) return false;

        // Second line must be a valid currency name (case-insensitive)
        Set<String> validCurrencies = Set.of("diamond", "emerald", "iron", "lapis", "gold", "enderpearl", "netherite", "copper");
        if (!validCurrencies.contains(lines[1].trim().toLowerCase())) return false;

        // Third line must be a valid integer
        return lines[2].trim().matches("\\d+");
    }




    // Helper method to check if the first item in the inventory has lore
    public boolean hasLore(ItemStack item) {
        // Check if the item is not null and has ItemMeta
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();

        // Check if the meta has lore
        return meta != null && meta.hasLore();
    }

    // Helper method to get the first line of lore from the item
    private String getItemLore(ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null && itemMeta.hasLore()) {
            TextComponent lore = (TextComponent) Objects.requireNonNull(itemMeta.lore()).getFirst();
            return lore.content();
        }
        return null; // No lore found
    }

    // Helper method to create floating text using an invisible ArmorStand
    private void createFloatingText(org.bukkit.Location location, String text, ItemStack item) {
        ArmorStand armorStand = location.getWorld().spawn(location.add(0.5, 2.5, 0.5), ArmorStand.class);

        // Set the Armor Stand to be invisible, no gravity, and no base plate (so it looks like text)
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setBasePlate(false);
        armorStand.setMarker(true);
        armorStand.setCustomName(text); // Set the text as the custom name
        armorStand.setCustomNameVisible(true); // Make the custom name visible

        // Set the color of the name if the item has color (for example, from item meta)
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            // Apply the same color as the item (if it's a colored item name)
            armorStand.setCustomName(item.getItemMeta().getDisplayName());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        // Check if the broken block is a Barrel or Chest
        if (block.getType() == Material.CHEST) {
            Location blockLocation = block.getLocation();
            World world = block.getWorld();

            // Check for ArmorStands above the chest/barrel within a certain vertical range
            for (double yOffset = 0.25; yOffset <= 2.75; yOffset += 0.25) {
                Location checkLocation = blockLocation.clone().add(0.5, yOffset, 0.5);
                ArmorStand armorStand = findArmorStandAtLocation(world, checkLocation);

                // If an ArmorStand is found at this location, remove it
                if (armorStand != null) {
                    armorStand.remove();
                }
            }



            // Clear metadata from the barrel or chest
            BlockState state = block.getState();
            if (state instanceof Chest) {
                Chest chest = (Chest) state;
                if (!chest.hasMetadata("seller")) return;

                String ownerName =chest.getMetadata("seller").getFirst().asString();

                int shopID = getShopIDFromLocation(ownerName, blockLocation); // Implement this method to find the shop ID

                if (shopID != -1) {
                    boolean removed = shopsManager.removeShopRecord(ownerName, shopID);
                    if (removed) {
                        event.getPlayer().sendMessage(ChatColor.GREEN + "Shop removed successfully!");
                    } else {
                        event.getPlayer().sendMessage(ChatColor.RED + "Failed to remove shop!");
                    }
                }
//                shopsManager.removeShopForPlayer(chest.getMetadata("seller").getFirst().asString(),blockLocation);
            }
            if (state instanceof Chest) {
                Chest chest = (Chest) state;

                chest.removeMetadata("seller", plugin);
                chest.removeMetadata("item", plugin);
                chest.removeMetadata("itemAmount", plugin);
                chest.removeMetadata("currency", plugin);
                chest.removeMetadata("currencyAmount", plugin);

            }
        }
    }


    private ArmorStand findArmorStandAtLocation(World world, Location location) {
        for (Entity entity : world.getEntities()) {
            if (entity instanceof ArmorStand) {
                ArmorStand armorStand = (ArmorStand) entity;
                // If the ArmorStand is at the exact location (considering precision), return it
                if (armorStand.getLocation().distance(location) < 0.5) {  // Tolerate a small distance threshold
                    return armorStand;
                }
            }
        }
        return null;  // No ArmorStand found at this location
    }

    @EventHandler
    public void onPlayerRightClickShop(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getClickedBlock() != null &&
                event.getClickedBlock().getType().name().endsWith("_WALL_SIGN")  &&
                event.getAction() == Action.RIGHT_CLICK_BLOCK && player.getInventory().getItemInMainHand().getType() == Material.PAPER) {

            Block chestBlock = getAttachedBlock(event.getClickedBlock());
            assert chestBlock != null;

            if (!chestBlock.hasMetadata("seller")) return;


            String seller = chestBlock.getMetadata("seller").get(0).asString(); // Retrieve metadata


            if (seller == null) {
                event.setCancelled(true);
                player.sendMessage("seller null");
                return;
            }

//            if (player.getName().equals(seller)) {
//                return;
//            }

            if (chestBlock.hasMetadata("item") && chestBlock.hasMetadata("itemAmount") &&
                    chestBlock.hasMetadata("currency") && chestBlock.hasMetadata("currencyAmount")) {

                ItemStack storedItem = (ItemStack) chestBlock.getMetadata("item").get(0).value();
                assert storedItem != null;
                int itemAmount = chestBlock.getMetadata("itemAmount").get(0).asInt();
                String currency = chestBlock.getMetadata("currency").get(0).asString();
                int currencyAmount = chestBlock.getMetadata("currencyAmount").get(0).asInt();
//
//

                if (chestBlock.getState() instanceof Chest chest) {
                    openTransactionGui(player, storedItem, itemAmount, currency, currencyAmount, chest, getItemStock(chest ,storedItem));
                } else {
                    player.sendMessage(ChatColor.RED + "This shop does not have a valid chest!");
                }
                event.setCancelled(true);
            } else {
                player.sendMessage(ChatColor.RED + "This shop is not properly set up.");
            }
        }
    }

    private void openTransactionGui(Player player, ItemStack item, int itemAmount, String currency, int currencyAmount, Chest chest, int stock) {
        Inventory gui = Bukkit.createInventory(new ShopInventoryHolder(chest), 9, ChatColor.DARK_GREEN + "Shop Transaction");

        // Configure the item to display in the GUI
        ItemStack itemDisplay = item.clone();
        TextComponent itemDisplayName = (TextComponent) itemDisplay.getItemMeta().displayName();
        itemDisplayName = itemDisplayName != null ? itemDisplayName : Component.text(itemDisplay.getType().name());
        ItemMeta meta = itemDisplay.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.YELLOW + "Amount: " + itemAmount);
        assert itemDisplayName != null;
        lore.add(ChatColor.YELLOW + "Price: " + currencyAmount + " " + currency + " per " +
                itemAmount + " " +  itemDisplayName.content()
                );
        lore.add("Stock: " + stock);
        meta.setLore(lore);
        itemDisplay.setItemMeta(meta);

        gui.setItem(4, itemDisplay); // Place the item at the center slot
        player.openInventory(gui);
    }

    // Handle inventory clicks in the shop GUI
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.DARK_GREEN + "Shop Transaction") && event.getWhoClicked() instanceof Player player) {
            event.setCancelled(true);
            if (event.getCurrentItem() != null && event.getSlot() == 4){
                ShopInventoryHolder holder = (ShopInventoryHolder) event.getView().getTopInventory().getHolder();
                if (holder==null) return;
                Chest sellerChest = holder.getSellerChest();

                if (sellerChest.hasMetadata("item") && sellerChest.hasMetadata("itemAmount") &&
                        sellerChest.hasMetadata("currency") && sellerChest.hasMetadata("currencyAmount")) {
                    String seller = sellerChest.getMetadata("seller").get(0).asString();

                    if (player.getName().equals(seller)) {player.sendMessage(ChatColor.RED + "$_$"); return;}

                    // Retrieve player profiles for the seller and the player
                    UserProfile sellerProfile = profileManager.getProfile(seller);
                    UserProfile playerProfile = profileManager.getProfile(player.getName());

                    if (sellerProfile == null || playerProfile == null) {
                        player.sendMessage(ChatColor.RED + "Transaction failed due to missing profiles.");
                        return;
                    }

                    // Retrieve the item from metadata and other transaction details
                    ItemStack storedItem = (ItemStack) sellerChest.getMetadata("item").get(0).value(); // Item being sold
                    int itemAmount = sellerChest.getMetadata("itemAmount").get(0).asInt(); // Amount given to player for purchase
                    String currency = sellerChest.getMetadata("currency").get(0).asString(); // Currency type
                    int currencyAmount = sellerChest.getMetadata("currencyAmount").get(0).asInt(); // Currency per item

                    // Retrieve the seller's and player's currency balances
                    double sellerCurrency = sellerProfile.getCurrency(currency);
                    double playerCurrency = playerProfile.getCurrency(currency);

                    // Calculate the total cost of the purchase (based on how many items the player wants)
                    double totalCost = currencyAmount * itemAmount;

                    // Check if the player has enough currency to buy the items
                    if (playerCurrency >= totalCost) {
                        // Check if the seller has enough stock of the item
                        int sellerStock = getItemStock(sellerChest, storedItem); // Check the number of items in the barrel

                        if (sellerStock >= itemAmount) {
                            // Proceed with the transaction:
                            playerProfile.setCurrency(currency, playerCurrency - totalCost); // Player loses currency
                            sellerProfile.setCurrency(currency, sellerCurrency + totalCost); // Seller gains currency

                            // Add the items to the player's inventory
                            ItemStack itemToGive = storedItem.clone();
                            giveItemsToBuyer(player, itemToGive, itemAmount);

                            // Reduce the item stock in the barrel by the amount sold
                            reduceItemStock(sellerChest, storedItem, itemAmount);

                            // Send messages to both the seller and the player
                            player.sendMessage(ChatColor.GREEN + "You have successfully bought " + itemAmount + " " + storedItem.getType() + " for " + totalCost + " " + currency + ".");

                            // Notify the seller if online
                            Player sellerPlayer = Bukkit.getPlayer(seller);
                            if (sellerPlayer != null && sellerPlayer.isOnline()) {
                                sellerPlayer.sendMessage(ChatColor.GREEN + "You have successfully sold " + itemAmount + " " + storedItem.getType() + " for " + totalCost + " " + currency + ".");
                            }

                            // **Update the stock in the player's GUI**
                            int newStock = sellerStock - itemAmount;  // Updated stock after purchase
                            openTransactionGui(player, storedItem, itemAmount, currency, currencyAmount, sellerChest, newStock);  // Refresh GUI with new stock

                        } else {
                            player.sendMessage(ChatColor.RED + "The seller does not have enough stock of the item.");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "You do not have enough " + currency + " to complete this purchase.");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "This shop is not properly set up.");
                }
            }

        }
    }


    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();

        // If the placed block is a Chest or Barrel, clear its metadata
        if (block.getType() == Material.CHEST ) {
            BlockState state = block.getState();
            if (state instanceof Chest) {
                Chest chest = (Chest) state;
                // Ensure no leftover metadata exists when placing a new chest/barrel
                chest.removeMetadata("seller", plugin);
                chest.removeMetadata("item", plugin);
                chest.removeMetadata("itemAmount", plugin);
                chest.removeMetadata("currency", plugin);
                chest.removeMetadata("currencyAmount", plugin);
            }
        }
    }


    /**
     * Get the current stock of the item in the barrel's inventory.
     * Handles stackable items like potions correctly.
     */
    private int getItemStock(Chest barrel, ItemStack item) {
        int stock = 0;

        // Iterate over the barrel's inventory and count how many of the item are available
        Inventory inventory = barrel.getInventory();
        for (ItemStack stack : inventory.getContents()) {
            if (stack != null && stack.isSimilar(item)) {
                stock += stack.getAmount();
            }
        }

        return stock;
    }

    private void reduceItemStock(Chest barrel, ItemStack item, int amountToReduce) {
        Inventory inventory = barrel.getInventory();

        // Iterate over the barrel's inventory to reduce the stock of the specified item
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack stack = inventory.getItem(i);

            // Check if the stack is similar to the specified item
            if (stack != null && stack.isSimilar(item)) {
                int currentStackAmount = stack.getAmount();

                // Determine the amount to deduct from this stack
                int amountToDeduct = Math.min(currentStackAmount, amountToReduce);

                // Update the remaining amount to reduce
                amountToReduce -= amountToDeduct;

                // Update or remove the stack
                if (currentStackAmount - amountToDeduct <= 0) {
                    inventory.setItem(i, null); // Remove the item if the stack is emptied
                } else {
                    stack.setAmount(currentStackAmount - amountToDeduct); // Update the stack amount
                    inventory.setItem(i, stack);
                }

                // If all required items have been deducted, stop the loop
                if (amountToReduce <= 0) {
                    break;
                }
            }
        }

        // If the loop ends and amountToReduce is still greater than 0, not enough items were available
        if (amountToReduce > 0) {
            throw new IllegalArgumentException("Not enough items in the barrel to reduce by the requested amount.");
        }


    }



    private void giveItemsToBuyer(Player buyer, ItemStack item, int amountToAdd) {
        // Get the buyer's inventory
        Inventory inventory = buyer.getInventory();
        World world = buyer.getWorld();
        Location dropLocation = buyer.getLocation();

        // Add items one by one
        for (int i = 0; i < amountToAdd; i++) {
            ItemStack singleItem = item.clone();
            singleItem.setAmount(1); // Ensure only one item is being added at a time

            // Try to add the item to the player's inventory
            HashMap<Integer, ItemStack> leftovers = inventory.addItem(singleItem);

            // If leftovers exist (item couldn't be added), drop the item on the ground
            if (!leftovers.isEmpty()) {
                for (ItemStack leftoverItem : leftovers.values()) {
                    world.dropItemNaturally(dropLocation, leftoverItem); // Drop the item at the buyer's location
                }
            }
        }
    }






    private ArmorStand getFloatingTextAbove(Block block) {
        // Check if there is an Armor Stand one block above the barrel
        org.bukkit.Location location = block.getLocation().add(0, 0.25, 0);
        for (org.bukkit.entity.Entity entity : location.getWorld().getEntities()) {
            if (entity instanceof ArmorStand) {
                ArmorStand armorStand = (ArmorStand) entity;
                // If the Armor Stand has a custom name, assume it's floating text
                if (armorStand.isInvisible()) {
                    return armorStand;
                }
            }
        }
        return null; // No floating text found
    }

    public void setBarrelMetaData(Chest barrel,String sellerName ,ItemStack item, int numberOfItem, String currency, int amountOfCurrency) {
        barrel.setMetadata("seller", new FixedMetadataValue(plugin,sellerName));
        barrel.setMetadata("item", new FixedMetadataValue(plugin,item));
        barrel.setMetadata("itemAmount", new FixedMetadataValue(plugin,numberOfItem));
        barrel.setMetadata("currency", new FixedMetadataValue(plugin,currency));
        barrel.setMetadata("currencyAmount", new FixedMetadataValue(plugin,amountOfCurrency));
    }

    private int getShopIDFromLocation(String playerName, Location location) {
        File playerShopsFile = new File(plugin.getDataFolder(), playerName + "_shops.yml");
        if (!playerShopsFile.exists()) return -1;

        YamlConfiguration shopsConfig = YamlConfiguration.loadConfiguration(playerShopsFile);
        ConfigurationSection shopsSection = shopsConfig.getConfigurationSection("shops");
        if (shopsSection == null) return -1;

        for (String key : shopsSection.getKeys(false)) {
            ConfigurationSection shopSection = shopsSection.getConfigurationSection(key);
            if (shopSection != null) {
                String world = shopSection.getString("id.world");
                double x = shopSection.getDouble("id.x");
                double y = shopSection.getDouble("id.y");
                double z = shopSection.getDouble("id.z");

                if (location.getWorld().getName().equals(world) &&
                        location.getX() == x &&
                        location.getY() == y &&
                        location.getZ() == z) {
                    return Integer.parseInt(key.replace("shop", ""));
                }
            }
        }
        return -1; // Return -1 if no matching shop is found
    }

    /**
     * Gets the block a sign is attached to.
     *
     * @param signBlock The block containing the sign.
     * @return The block the sign is attached to, or null if not applicable.
     */
    private Block getAttachedBlock(Block signBlock) {
        if (!(signBlock.getState() instanceof Sign)) {
            return null; // Not a sign
        }

        // Check if it's a wall sign or a standing sign
        if (signBlock.getType().name().contains("WALL_SIGN")) {
            // Wall signs are attached to the block they face
            return signBlock.getRelative(((org.bukkit.block.data.type.WallSign) signBlock.getBlockData()).getFacing().getOppositeFace());
        } else if (signBlock.getType().name().contains("SIGN")) {
            // Standing signs are usually placed on the block below them
            return signBlock.getRelative(org.bukkit.block.BlockFace.DOWN);
        }
        return null;
    }

    public void openShop(Player player, Block signBlock) {
        Block chestBLock = getAttachedBlock(signBlock);

        if (!(chestBLock != null && chestBLock.hasMetadata("seller"))) return;

        String sellerName = chestBLock.getMetadata("seller").get(0).asString();
        UserProfile sellerProfile = profileManager.getProfile(sellerName);
        ItemStack item = (ItemStack) chestBLock.getMetadata("item").get(0);
        if (sellerProfile == null ) return;

        Inventory sellerShop = Bukkit.createInventory(null, 9, Component.text(sellerName + "'s"+ " Shop").color(TextColor.color(255, 0, 0)));
//        ItemStack abyssIngot = getAbyssIngot();
//
//
//
//        sellerShop.setItem(3, abyssIngot);
//
//
//        player.openInventory(sellerShop);
//
//
//        ItemStack abyssPotion = getAbyssPotion();
//        sellerShop.setItem(5, abyssPotion);


        ItemStack abyssOre = getAbyssOre();
        sellerShop.setItem(1, abyssOre);
    }

}
