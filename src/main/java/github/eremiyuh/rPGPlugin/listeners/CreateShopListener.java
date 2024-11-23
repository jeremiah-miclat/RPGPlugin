package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import github.eremiyuh.rPGPlugin.manager.ChunkManager;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.manager.ShopsManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
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


//    @EventHandler
//    public void createShopEvent(EntityPlaceEvent event) {
//
//        Player player = event.getPlayer();
//        Chunk chunk = player.getChunk();
//
//        Set<String> validCurrencies = new HashSet<>();
//        validCurrencies.add("diamond");
//        validCurrencies.add("emerald");
//        validCurrencies.add("iron");
//        validCurrencies.add("lapis");
//        validCurrencies.add("gold");
//        validCurrencies.add("enderpearl");
//        validCurrencies.add("netherite");
//        validCurrencies.add("copper");
//
//        // Check if the chunk is owned by the player
//        if (chunkManager.getOwner(chunk) != null && chunkManager.getOwner(chunk).equalsIgnoreCase(player.getName())) {
//            player.sendMessage("Chunk owned");
//            // Check if the placed entity is an Armor Stand
//            if (event.getEntity() instanceof Boat boat) {
//                player.sendMessage("Boat placed");
//                // Get the block below the Armor Stand to check if it's a Barrel
//                Block blockBelow = event.getBlock().getLocation().getBlock();
//                if (blockBelow.getType() == Material.CHEST) {
//                    player.sendMessage("Block Below");
//                    Chest barrel = (Chest) blockBelow.getState();
//                    Inventory inventory = barrel.getInventory();
//
//                    String boatName = boat.getCustomName();
//                    if (boatName != null && boatName.matches("\\d+\\s+\\w+\\s+\\d+")) {
//                        player.sendMessage("Boat name");
//                        // Boat name is in the correct format, use it
//                        String[] boatDetails = boatName.split(" ");
//                        int numberOfItems = Integer.parseInt(boatDetails[0]); // Number of items inside the barrel that being traded
//                        String itemBeingTraded = boatDetails[1]; // Item being traded
//                        int numberOfItemsBeingTraded = Integer.parseInt(boatDetails[2]); // Number of items being traded
//
//
//                        if (!validCurrencies.contains(itemBeingTraded.toLowerCase())) {
//                            player.sendMessage("Invalid currency: " + itemBeingTraded);
//                            event.setCancelled(true);
//                            return; // Stop further processing if the currency is invalid
//                        }
//
//                        // Check only the first item in the Barrel
//                        ItemStack firstItem = inventory.getItem(0);
//                        if (firstItem != null) {
//                            player.sendMessage("First");
//                            event.setCancelled(true);
//                            String itemName = firstItem.getType().toString(); // Default to item type as name
//
//                            // Create floating text with item name and lore (if any)
//                            if (hasLore(firstItem)) {
//
//                                TextComponent customDisplay = (TextComponent) firstItem.getItemMeta().displayName();
//                                assert customDisplay != null;
//
//                                TextComponent boatTextComponent = Component.text(customDisplay.content() + " "
//                                        + numberOfItems + " " + itemBeingTraded + " " + numberOfItemsBeingTraded
//
//                                );
//                                String[] stack = {boatTextComponent.content() ,getItemLore(firstItem), customDisplay.content()};
//                                createHologramStack(boat.getWorld(), boat.getX(), boat.getY(), boat.getZ(), stack);
//                                shopsManager.saveShopForPlayer(player.getName(), firstItem,numberOfItems,numberOfItemsBeingTraded,itemBeingTraded, barrel.getLocation());
//                                setBarrelMetaData(barrel, player.getName(), firstItem, numberOfItems, itemBeingTraded, Integer.parseInt(String.valueOf(numberOfItemsBeingTraded)));
//                                return;
//                            }
//
//                                TextComponent boatTextComponent = Component.text(itemName + " "
//                                        + numberOfItems + " " + itemBeingTraded + " " + numberOfItemsBeingTraded
//
//                                );
//                                String[] stack = {boatTextComponent.content(),itemName};
//                                createHologramStack(boat.getWorld(), boat.getX(), boat.getY(), boat.getZ(), stack);
//                            shopsManager.saveShopForPlayer(player.getName(), firstItem,numberOfItems,numberOfItemsBeingTraded,itemBeingTraded, barrel.getLocation());
//                            setBarrelMetaData(barrel, player.getName(), firstItem, numberOfItems, itemBeingTraded, Integer.parseInt(String.valueOf(numberOfItemsBeingTraded)));
//                        }
//
//                    }
//
//                }
//            }
//        }
//    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        Block signBlock = event.getBlock();
        String[] lines = event.getLines();

        // Debugging: Print all lines to console
        for (int i = 0; i < lines.length; i++) {
            System.out.println("Line " + i + ": " + lines[i]);
        }

        // Count non-empty lines
        int nonEmptyLines = 0;
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                nonEmptyLines++;
            }
        }

        // Check if at least 3 lines are filled
        if (nonEmptyLines < 3) {
            player.sendMessage("You must fill at least 3 lines of the sign!");
            event.setCancelled(true);
            return;
        }

        Set<String> validCurrencies = Set.of("diamond", "emerald", "iron", "lapis", "gold", "enderpearl", "netherite", "copper");

        try {
            int numberOfItems = Integer.parseInt(lines[0].trim());
            String itemBeingTraded = lines[1].trim().toLowerCase();
            int numberOfItemsBeingTraded = Integer.parseInt(lines[2].trim());

            if (!validCurrencies.contains(itemBeingTraded)) {
                player.sendMessage("Invalid currency: " + itemBeingTraded);
                event.setCancelled(true);
                return;
            }

            player.sendMessage("Valid sign format detected");

            // Proceed with shop creation logic
            // Example: Check the block below the sign
            Block blockBelow = signBlock.getRelative(0, -1, 0);
            if (blockBelow.getType() == Material.CHEST) {
                Chest chest = (Chest) blockBelow.getState();
                Inventory inventory = chest.getInventory();

                ItemStack firstItem = inventory.getItem(0);
                if (firstItem != null) {

                    if (hasLore(firstItem)) {

                                TextComponent customDisplay = (TextComponent) firstItem.getItemMeta().displayName();
                                assert customDisplay != null;

                                TextComponent boatTextComponent = Component.text(customDisplay.content() + " "
                                        + numberOfItems + " " + itemBeingTraded + " " + numberOfItemsBeingTraded

                                );
                                String[] stack = {boatTextComponent.content() ,getItemLore(firstItem), customDisplay.content()};
                                createHologramStack(blockBelow.getWorld(), blockBelow.getX(), blockBelow.getY(), blockBelow.getZ(), stack);

                                shopsManager.saveShopForPlayer(player.getName(), firstItem,numberOfItems,numberOfItemsBeingTraded,itemBeingTraded, chest.getLocation());
                                setBarrelMetaData(chest, player.getName(), firstItem, numberOfItems, itemBeingTraded, Integer.parseInt(String.valueOf(numberOfItemsBeingTraded)));
                                signBlock.breakNaturally();
                                player.sendMessage("First item found in chest");
                                return;
                            }
                    String itemName = firstItem.getType().toString(); // Default to item type as name
                    TextComponent boatTextComponent = Component.text(itemName + " "
                                        + numberOfItems + " " + itemBeingTraded + " " + numberOfItemsBeingTraded

                                );
                                String[] stack = {boatTextComponent.content(),itemName};
                                createHologramStack(blockBelow.getWorld(), blockBelow.getX(), blockBelow.getY() + 1, blockBelow.getZ(), stack);
                            shopsManager.saveShopForPlayer(player.getName(), firstItem,numberOfItems,numberOfItemsBeingTraded,itemBeingTraded, chest.getLocation());
                            setBarrelMetaData( chest, player.getName(), firstItem, numberOfItems, itemBeingTraded, Integer.parseInt(String.valueOf(numberOfItemsBeingTraded)));


                    player.sendMessage("First item found in chest");


                    signBlock.breakNaturally();

                    player.sendMessage("Shop successfully created!");
                } else {
                    player.sendMessage("The chest is empty!");
                }
            } else {
                player.sendMessage("The sign must be placed on top of a chest!");
                event.setCancelled(true);
            }
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid number format! Ensure the first and third lines are numbers.");
            event.setCancelled(true);
        }
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
                event.getPlayer().sendMessage("Owner is: " + ownerName);
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
                chest.removeMetadata("numberOfItem", plugin);
                chest.removeMetadata("currency", plugin);
                chest.removeMetadata("amountOfCurrency", plugin);

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
    public void onPlayerRightClickBarrel(PlayerInteractEvent event) {
        // Ensure that the player clicked on a barrel with the necessary metadata
        if (event.getClickedBlock() != null &&
                event.getClickedBlock().getState() instanceof Chest barrel &&
                barrel.hasMetadata("seller") &&
                event.getAction() == Action.RIGHT_CLICK_BLOCK) {

            String seller = barrel.getMetadata("seller").get(0).asString(); // Safely retrieve metadata
            Player player = event.getPlayer();

            if (seller == null ) {
                event.setCancelled(true); // Cancel interaction for seller or invalid data
                return;
            }

            if (player.getName().equals(seller)) {return;}

            // Ensure that the barrel has the necessary metadata and the player is not the seller
            if (barrel.hasMetadata("item") && barrel.hasMetadata("numberOfItem") &&
                    barrel.hasMetadata("currency") && barrel.hasMetadata("amountOfCurrency")) {

                // Retrieve player profiles for the seller and the player
                UserProfile sellerProfile = profileManager.getProfile(seller);
                UserProfile playerProfile = profileManager.getProfile(player.getName());

                if (sellerProfile == null || playerProfile == null) {
                    player.sendMessage(ChatColor.RED + "Transaction failed due to missing profiles.");
                    return;
                }

                // Retrieve the item from metadata and other transaction details
                ItemStack storedItem = (ItemStack) barrel.getMetadata("item").get(0).value(); // Item being sold
                int itemAmount = barrel.getMetadata("numberOfItem").get(0).asInt(); // Amount given to player for purchase
                String currency = barrel.getMetadata("currency").get(0).asString(); // Currency type
                int currencyAmount = barrel.getMetadata("amountOfCurrency").get(0).asInt(); // Currency per item

                // Retrieve the seller's and player's currency balances
                double sellerCurrency = sellerProfile.getCurrency(currency);
                double playerCurrency = playerProfile.getCurrency(currency);

                // Calculate the total cost of the purchase (based on how many items the player wants)
                double totalCost = currencyAmount * itemAmount;

                // Check if the player has enough currency to buy the items
                if (playerCurrency >= totalCost) {
                    // Check if the seller has enough stock of the item
                    int sellerStock = getItemStock(barrel, storedItem); // Check the number of items in the barrel

                    if (sellerStock >= itemAmount) {
                        // Proceed with the transaction:
                        playerProfile.setCurrency(currency, playerCurrency - totalCost); // Player loses currency
                        sellerProfile.setCurrency(currency, sellerCurrency + totalCost); // Seller gains currency

                        // Add the items to the player's inventory
                        ItemStack itemToGive = storedItem.clone();
                        giveItemsToBuyer(player, itemToGive, itemAmount);

                        // Reduce the item stock in the barrel by the amount sold
                        reduceItemStock(barrel, storedItem, itemAmount);

                        // Send messages to both the seller and the player
                        player.sendMessage(ChatColor.GREEN + "You have successfully bought " + itemAmount + " " + storedItem.getType() + " for " + totalCost + " " + currency + ".");

                        // Notify the seller if online
                        Player sellerPlayer = Bukkit.getPlayer(seller);
                        if (sellerPlayer != null && sellerPlayer.isOnline()) {
                            sellerPlayer.sendMessage(ChatColor.GREEN + "You have successfully sold " + itemAmount + " " + storedItem.getType() + " for " + totalCost + " " + currency + ".");
                        }
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
                chest.removeMetadata("numberOfItem", plugin);
                chest.removeMetadata("currency", plugin);
                chest.removeMetadata("amountOfCurrency", plugin);
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
        barrel.setMetadata("numberOfItem", new FixedMetadataValue(plugin,numberOfItem));
        barrel.setMetadata("currency", new FixedMetadataValue(plugin,currency));
        barrel.setMetadata("amountOfCurrency", new FixedMetadataValue(plugin,amountOfCurrency));
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



}
