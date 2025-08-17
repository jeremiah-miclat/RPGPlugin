package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import io.papermc.paper.event.player.PlayerTradeEvent;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerBucketEntityEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;


public class ActivityListener implements Listener {
    private final PlayerProfileManager profileManager;
    private final World world;
    private final int x1 = -1923, z1 = -76;
    private final int x2 = -1802, z2 = -168;

    public ActivityListener(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
        this.world = Bukkit.getWorld("world");
    }

    private boolean inFishingArea(int x, int z) {
        return x >= Math.min(x1, x2) && x <= Math.max(x1, x2) &&
                z >= Math.min(z1, z2) && z <= Math.max(z1, z2);
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        UserProfile userProfile = profileManager.getProfile(event.getPlayer().getName());
        int bonusBreakPoints = userProfile.getDestroyer();
        userProfile.setActivitypoints(userProfile.getActivitypoints()+1+bonusBreakPoints);
    }

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        UserProfile userProfile = profileManager.getProfile(event.getPlayer().getName());
        int bonusBuildPoints = userProfile.getBuilder();
        userProfile.setActivitypoints(userProfile.getActivitypoints()+1+bonusBuildPoints);
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        // Retrieve the player's profile
        UserProfile userProfile = profileManager.getProfile(event.getPlayer().getName());

        int additionalPoints = 0;
        double multiplyChance = .1;
        double multiplier = 0;

        // Get the caught entity
        Entity entity = event.getCaught();

        // Ensure the caught entity is an instance of Item
        if (entity instanceof Item item) {
            // Get the item type name
            String itemName = item.getName();
            Player player = event.getPlayer();

            // Retrieve the player's fishing rod (if held in main hand)
            ItemStack fishingRod = event.getPlayer().getInventory().getItemInMainHand();

            // Check if the item is a fishing rod and has lore
            if (fishingRod != null && fishingRod.getType().toString().contains("FISHING_ROD") && fishingRod.hasItemMeta()) {
                ItemMeta meta = fishingRod.getItemMeta();
                if (meta != null && meta.hasLore()) {
                    List<String> lore = meta.getLore();

                    // Check for "Fisherman" tag in the lore and parse the value
                    for (String line : lore) {
                        if (line.contains("PointsBonus:")) {
                            try {
                                // Extract the additional points from the lore
                                additionalPoints = Integer.parseInt(line.split(":")[1].trim());
                            } catch (NumberFormatException e) {
                                // If lore parsing fails, fallback to 0 additional points
                                additionalPoints = 0;
                            }
                        }

                        if (line.contains("ExtraFish:")) {
                            try {
                                // Extract the additional points from the lore
                                multiplier = Math.min(99, Integer.parseInt(line.split(":")[1].trim()));
                            } catch (NumberFormatException e) {
                                // If lore parsing fails, fallback to 0 additional points
                                multiplier = 0;
                            }
                        }
                    }
                }
            }

            // Create the item lore with player's name and the fish details
            List<String> fishLore = new ArrayList<>();
            fishLore.add("Caught by: " + player.getName()); // Player's name
            ItemStack itemStack = item.getItemStack();
            ItemMeta fishItemmeta = item.getItemStack().getItemMeta();
            fishItemmeta.setLore(fishLore);
            itemStack.setItemMeta(fishItemmeta);


            if (inFishingArea(player.getLocation().getBlockX(), player.getLocation().getBlockZ())
                    && (player.getWorld().getName().equals(world.getName()))) {
                additionalPoints+=50;
            }

            // Update the currency and fish lore based on the type of fish caught
            assert itemName != null;
            if (itemName.contains("Cod")) {
                if (Math.random() < multiplyChance && multiplier>0) {
                    dropOrNotify(player, new ItemStack(Material.COD, (int) (multiplier)), fishLore);
                }
                userProfile.setCurrency("activitypoints", userProfile.getCurrency("activitypoints") + (5 + additionalPoints));
            } else if (itemName.contains("Salmon")) {
                if (Math.random() < multiplyChance && multiplier>0) {
                    dropOrNotify(player, new ItemStack(Material.SALMON, (int) (multiplier)), fishLore);
                }
                userProfile.setCurrency("activitypoints", userProfile.getCurrency("activitypoints") + (15 + additionalPoints));
            } else if (itemName.contains("Tropical")) {
                if (Math.random() < multiplyChance && multiplier>0) {
                    dropOrNotify(player, new ItemStack(Material.TROPICAL_FISH, (int) (multiplier)), fishLore);
                }
                userProfile.setCurrency("activitypoints", userProfile.getCurrency("activitypoints") + (25 + additionalPoints));
            } else if (itemName.contains("Puffer")) {
                if (Math.random() < multiplyChance && multiplier>0) {
                    dropOrNotify(player, new ItemStack(Material.PUFFERFISH, (int) (multiplier)), fishLore);
                }
                userProfile.setCurrency("activitypoints", userProfile.getCurrency("activitypoints") + (50 + additionalPoints));
            } else if (itemName.contains("Nautilus")) {
                userProfile.setCurrency("activitypoints", userProfile.getCurrency("activitypoints") + (100 + additionalPoints*2));
            }

            else {
                userProfile.setCurrency("activitypoints", userProfile.getCurrency("activitypoints") + (1 + additionalPoints));
            }
        }
    }


    @EventHandler
    public void onTrade(PlayerTradeEvent event) {
        UserProfile userProfile = profileManager.getProfile(event.getPlayer().getName());
        int bonusPoint = userProfile.getTrader();
        userProfile.setActivitypoints(userProfile.getActivitypoints()+1+bonusPoint);
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        UserProfile userProfile = profileManager.getProfile(event.getPlayer().getName());
        userProfile.setActivitypoints(userProfile.getActivitypoints()+1);
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack craftedItem = event.getCurrentItem();
        UserProfile profile = profileManager.getProfile(player.getName());

        if (craftedItem != null ) {
            int craftAmount = craftedItem.getAmount();
            int bonusPoint = profile.getCrafter();
            profile.setActivitypoints(profile.getActivitypoints()+craftAmount+bonusPoint);
        }
    }

    @EventHandler
    public void onAnvilResultTake(InventoryClickEvent event) {
        // Check if the inventory is an anvil
        if (event.getInventory() instanceof AnvilInventory) {
            AnvilInventory anvilInventory = (AnvilInventory) event.getInventory();
            ItemStack firstItem = anvilInventory.getItem(0);
            ItemStack secondItem = anvilInventory.getItem(1);
            ItemStack resultItem = anvilInventory.getItem(2); // Slot 2 is the result slot
            if (firstItem == null || secondItem == null )return;
            // Ensure the player is taking the item from the result slot (slot 2)
            if (event.getSlot() == 2 && resultItem != null && resultItem.getType() != Material.AIR) {
                if (secondItem.getType() == Material.ENCHANTED_BOOK) {
                    Player player = (Player) event.getWhoClicked();
                    UserProfile profile = profileManager.getProfile(player.getName());
                    int bonusPoint = profile.getCrafter();
                    profile.setActivitypoints(profile.getActivitypoints()+50+(bonusPoint*5));

                }
            }
        }
    }



    private void dropOrNotify(Player player, ItemStack item, List<String> lore) {
        // Add custom lore to the item
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setLore(lore); // Set the custom lore
            item.setItemMeta(meta);
        }

        // Check if the player has space in their inventory
        if (player.getInventory().firstEmpty() != -1) {
            // Add item to inventory
            player.getInventory().addItem(item);
        } else {
            // Drop item at player's location
            player.getWorld().dropItemNaturally(player.getLocation(), item);
        }
    }
}
