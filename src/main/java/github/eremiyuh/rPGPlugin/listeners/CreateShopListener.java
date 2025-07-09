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

    private final Map<String, Location> playerChestMap = new HashMap<>();

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        if (event.getPlayer().getWorld().getName().contains("resource")
            || event.getPlayer().getWorld().getName().contains("rpg")
                || event.getPlayer().getWorld().getName().contains("labyrinth")
        ) {
            return;
        }
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
        Set<String> validCurrencies = Set.of("diamond", "emerald", "iron", "lapis", "gold", "enderpearl", "netherite", "copper","abysspoints","activitypoints");

        try {
            int numberOfItems = Integer.parseInt(lines[0].trim());
            String itemBeingTraded = lines[1].trim().toLowerCase();
            int numberOfItemsBeingTraded = Integer.parseInt(lines[2].trim());

            if (!validCurrencies.contains(itemBeingTraded)) {
                player.sendMessage("Invalid currency: " + itemBeingTraded);
                player.sendMessage("Currencies: diamond, emerald, iron, lapis, gold, enderpearl, netherite, copper, abysspoints, activitypoints");
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
                    List<Location> targetShops = shopsManager.listPlayerShops(player.getName());
                    if (targetShops.size()>=10) {
                        player.sendMessage(ChatColor.RED + "Sorry, only 10 shops are allowed per player");
                        return;
                    }
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
        Set<String> validCurrencies = Set.of("diamond", "emerald", "iron", "lapis", "gold", "enderpearl", "netherite", "copper", "abysspoints","activitypoints");
        if (!validCurrencies.contains(lines[1].trim().toLowerCase())) return false;

        // Third line must be a valid integer
        return lines[2].trim().matches("\\d+");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        // Handle sign breaking (check if attached to a chest owned by someone else)
        if (block.getState() instanceof Sign) {
            Block attachedBlock = getAttachedBlock(block);

            if (attachedBlock != null && attachedBlock.getType() == Material.CHEST) {
                BlockState state = attachedBlock.getState();
                if (state instanceof Chest) {
                    Chest chest = (Chest) state;
                    if (chest.hasMetadata("seller")) {
                        String ownerName = chest.getMetadata("seller").get(0).asString();

                        if (!player.getName().equals(ownerName)) {
                            event.setCancelled(true);
                            player.sendMessage(ChatColor.RED + "You cannot break this sign. It belongs to " + ownerName);
                            return;
                        }
                    }
                }
            }
        }

        // Handle chest breaking (same as your original)
        if (block.getType() == Material.CHEST) {
            Location blockLocation = block.getLocation();
            World world = block.getWorld();

            // Remove armor stands above the chest
            for (double yOffset = 0.25; yOffset <= 2.75; yOffset += 0.25) {
                Location checkLocation = blockLocation.clone().add(0.5, yOffset, 0.5);
                ArmorStand armorStand = findArmorStandAtLocation(world, checkLocation);
                if (armorStand != null) {
                    armorStand.remove();
                }
            }

            BlockState state = block.getState();
            if (state instanceof Chest) {
                Chest chest = (Chest) state;

                if (!chest.hasMetadata("seller")) return;

                String ownerName = chest.getMetadata("seller").get(0).asString();

                if (!player.getName().equals(ownerName)) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You are not the owner of this shop.");
                    return;
                }

                int shopID = getShopIDFromLocation(ownerName, blockLocation);
                if (shopID != -1) {
                    boolean removed = shopsManager.removeShopRecord(ownerName, shopID);
                    if (removed) {
                        player.sendMessage(ChatColor.GREEN + "Shop removed successfully!");
                    } else {
                        player.sendMessage(ChatColor.RED + "Failed to remove shop!");
                    }
                }

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
                event.getAction() == Action.RIGHT_CLICK_BLOCK) {

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
                if (player.getInventory().getItemInMainHand().getType().name().endsWith("_DYE") || player.getInventory().getItemInMainHand().getType() == Material.GLOW_INK_SAC) {
                    return;
                }
                event.setCancelled(true);
                ItemStack storedItem = (ItemStack) chestBlock.getMetadata("item").get(0).value();

                assert storedItem != null;
//                storedItem.setAmount(1);
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

            } else {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "This shop is not properly set up.");
            }
        }
    }

    private void openTransactionGui(Player player, ItemStack item, int itemAmount, String currency, int currencyAmount, Chest chest, int stock) {
        Inventory gui = Bukkit.createInventory(new ShopInventoryHolder(chest), 9, ChatColor.DARK_GREEN + "Shop Transaction");

        // Clone the item and get ItemMeta
        ItemStack itemDisplay = item.clone();
        ItemMeta meta = itemDisplay.getItemMeta();

        if (meta == null) {
            player.sendMessage(ChatColor.RED + "This was not set up properly by the owner.");
            player.sendMessage(ChatColor.RED + "To prevent this kind of error, follow the instructions below.");
            player.sendMessage(ChatColor.RED + "Destroy this shop and create a new one. Do not take the item after creating a shop.");
            player.sendMessage(ChatColor.RED + "Notify the owner to recreate the shop.");
            return;
        }

        // Get the display name (if any)
        String itemDisplayName = meta.hasDisplayName() ? meta.getDisplayName() : itemDisplay.getType().name();

        // Prepare the lore
        List<String> lore = new ArrayList<>();
        List<String> existingLore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        lore.addAll(existingLore);
        lore.add(""); // Empty line for separation
        lore.add(ChatColor.YELLOW + "Price: " + currencyAmount + " " + currency);
        lore.add(ChatColor.YELLOW + "For: " + itemAmount + " " + itemDisplayName);
        lore.add("Stock: " + stock);

        meta.setLore(lore);
        itemDisplay.setItemMeta(meta);

        // Place the item at the center slot of the GUI
        gui.setItem(4, itemDisplay);
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
                    assert storedItem != null;
//                    storedItem.setAmount(1);
                    int itemAmount = sellerChest.getMetadata("itemAmount").get(0).asInt(); // Amount given to player for purchase
                    String currency = sellerChest.getMetadata("currency").get(0).asString(); // Currency type
                    int currencyAmount = sellerChest.getMetadata("currencyAmount").get(0).asInt(); // Currency per item

                    // Retrieve the seller's and player's currency balances
                    double sellerCurrency = sellerProfile.getCurrency(currency);
                    double playerCurrency = playerProfile.getCurrency(currency);

                    // Calculate the total cost of the purchase (based on how many items the player wants)
                    double totalCost = currencyAmount;

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

    @EventHandler
    public void onChestPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();

        if (block.getType() != Material.CHEST) return;

        Player player = event.getPlayer();
        String playerName = player.getName();

        // Remove metadata from previous chest, if it exists
        if (playerChestMap.containsKey(playerName)) {
            Location previousLocation = playerChestMap.get(playerName);
            Block previousBlock = previousLocation.getBlock();

            if (previousBlock.getType() == Material.CHEST) {
                previousBlock.removeMetadata("owner", plugin);
            }
        }

        // Set metadata on new chest
        block.setMetadata("owner", new FixedMetadataValue(plugin, playerName));
        playerChestMap.put(playerName, block.getLocation());

//        player.sendMessage(ChatColor.YELLOW + "This chest is now protected while you set up your shop.");
    }

    @EventHandler
    public void onChestBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (block.getType() != Material.CHEST) return;

        Player player = event.getPlayer();

        if (block.hasMetadata("owner")) {
            String ownerName = block.getMetadata("owner").get(0).asString();

            if (!player.getName().equals(ownerName)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "This is protected till next server restart");
                return;
            }

            // If owner breaks their own chest, remove from tracking
            playerChestMap.remove(ownerName);
//            player.sendMessage(ChatColor.GRAY + "You broke your protected chest.");
        }
    }

    @EventHandler
    public void onChestOpen(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.CHEST) return;

        Player player = event.getPlayer();

        if (block.hasMetadata("owner")) {
            String ownerName = block.getMetadata("owner").get(0).asString();
            if (!player.getName().equals(ownerName)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "This is protected till next server restart" );
            }
        }
    }

}
