package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class DeadMobListener implements Listener {
    private final PlayerProfileManager profileManager;
    private final Random random = new Random();

    public DeadMobListener(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @EventHandler
    public void onModifiedMobDeath(EntityDeathEvent event) {
        double RANDOMCHANCE = .1;

        if (!Objects.requireNonNull(event.getEntity().getLocation().getWorld()).getName().equals("world_rpg")) {
            return;
        }

        if ((event.getEntity() instanceof Monster || event.getEntity() instanceof Wolf || event.getEntity() instanceof IronGolem)
                && event.getEntity().getKiller() instanceof Player killer) {

            UserProfile killerProfile = profileManager.getProfile(killer.getName());
            String killerTeam = killerProfile.getTeam();
            List<ItemStack> drops = event.getDrops();

            LivingEntity mob = event.getEntity();
            if (mob.hasMetadata("extraDamage")) {
                double health = mob.getMetadata("extraDamage").get(0).asDouble() * 10;
                RANDOMCHANCE += health * 0.00045;
                int dropMultiplier = (int) (health * 0.01);

                String customName = mob.getCustomName();
                boolean isBoss = customName != null && customName.toLowerCase().contains("boss");

                if (isBoss) {
                    List<Player> nearbyPlayers = getNearbyTeamPlayers(mob, killerTeam, 40, 10);
                    for (Player player : nearbyPlayers) {
                        UserProfile playerProfile = profileManager.getProfile(player.getName());
                        applyRewards(player, playerProfile, health, RANDOMCHANCE, dropMultiplier);
                    }
                    return;
                }

                if (killerTeam.equals("none")) {
                    applyRewards(killer, killerProfile, health, RANDOMCHANCE, dropMultiplier);
                    distributeDrops(killer, drops, dropMultiplier);
                    return;
                }

                List<Player> nearbyTeamPlayers = getNearbyTeamPlayers(mob, killerTeam, 40, 10);
                for (Player player : nearbyTeamPlayers) {
                    UserProfile playerProfile = profileManager.getProfile(player.getName());
                    applyRewards(player, playerProfile, health, RANDOMCHANCE, dropMultiplier);
                    distributeDrops(player, drops, dropMultiplier);
                }
            }
        }
    }

    private List<Player> getNearbyTeamPlayers(LivingEntity mob, String team, double horizontalRange, double verticalRange) {
        List<Player> teamPlayers = new ArrayList<>();
        for (Player player : mob.getWorld().getPlayers()) {
            if (isPlayerInRange(player, mob, horizontalRange, verticalRange)) {
                UserProfile playerProfile = profileManager.getProfile(player.getName());
                if (playerProfile.getTeam().equals(team)) {
                    teamPlayers.add(player);
                }
            }
        }
        return teamPlayers;
    }

    private boolean isPlayerInRange(Player player, LivingEntity mob, double horizontalRange, double verticalRange) {
        return player.getLocation().distance(mob.getLocation()) <= horizontalRange &&
                Math.abs(player.getLocation().getY() - mob.getLocation().getY()) <= verticalRange;
    }

    private void applyRewards(Player player, UserProfile profile, double health, double chance, int multiplier) {
        player.giveExp((int) (health * 2));
        if (random.nextDouble() < chance) {
            applyRandomLoreToEquippedItem(player);
            profile.setDiamond(profile.getDiamond() + multiplier);
        }
    }

    private void distributeDrops(Player player, List<ItemStack> drops, int multiplier) {
        for (ItemStack item : drops) {
            ItemStack multipliedDrop = item.clone();
            multipliedDrop.setAmount(item.getAmount() * multiplier);

            if (!player.getInventory().addItem(multipliedDrop).isEmpty()) {
                player.getWorld().dropItemNaturally(player.getLocation(), multipliedDrop);
            }
        }
    }

    private void applyRandomLoreToEquippedItem(Player player) {
        // Define possible item slots
        ItemStack[] equipment = {
                player.getInventory().getHelmet(),
                player.getInventory().getChestplate(),
                player.getInventory().getLeggings(),
                player.getInventory().getBoots(),
                player.getInventory().getItemInMainHand(),
                player.getInventory().getItemInOffHand()
        };

        // Randomly select an item to potentially add lore to
        ItemStack selectedItem = equipment[random.nextInt(equipment.length)];

        // Check if the selected item is valid
        if (isValidLoreItem(selectedItem)) {
            Attribute selectedAttribute = getRandomAttribute();

            // Get the current item meta
            ItemMeta itemMeta = selectedItem.getItemMeta();
            if (itemMeta != null) {
                // Get the current lore
                List<String> lore = itemMeta.getLore() != null ? new ArrayList<>(itemMeta.getLore()) : new ArrayList<>();
                String loreEntryPrefix = selectedAttribute.getDisplayName() + ": ";

                // Check if the item already has any lore
                if (!lore.isEmpty()) {
                    // If it has lore, only check for the specific attribute
                    boolean attributeFound = false;

                    for (int i = 0; i < lore.size(); i++) {
                        String loreLine = lore.get(i);
                        if (loreLine.startsWith(loreEntryPrefix)) {
                            // If the attribute exists, increment the value
                            String[] parts = loreLine.split(": ");
                            if (parts.length == 2) {
                                try {
                                    int currentValue = Integer.parseInt(parts[1]);
                                    currentValue++; // Increment the current value
                                    lore.set(i, parts[0] + ": " + currentValue); // Update the lore
                                    attributeFound = true;
                                } catch (NumberFormatException e) {
                                    player.sendMessage("Error updating lore value for " + selectedAttribute.getDisplayName());
                                }
                            }
                            break; // Exit loop since we've found and updated the attribute
                        }
                    }

                } else {
                    // If there is no existing lore, add the new one
                    lore.add(loreEntryPrefix + "1");
                }

                // Set the updated lore back to the item
                itemMeta.setLore(lore);
                selectedItem.setItemMeta(itemMeta);

                // Feedback message
                if (!lore.isEmpty() && lore.get(lore.size() - 1).startsWith(loreEntryPrefix)) {
                    player.sendMessage("You have incremented: " + selectedAttribute.getDisplayName() + " on " + selectedItem.getType());
                }
            }
        }
    }


    private boolean isValidLoreItem(ItemStack item) {
        return item != null && !item.getType().isAir() && (isArmor(item) || isWeapon(item));
    }

    private boolean isArmor(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return false;
        }
        String itemType = item.getType().toString();
        return itemType.endsWith("_HELMET") || itemType.endsWith("_CHESTPLATE") ||
                itemType.endsWith("_LEGGINGS") || itemType.endsWith("_BOOTS");
    }

    private boolean isWeapon(ItemStack item) {
        return item.getType().toString().endsWith("_SWORD") || item.getType().toString().endsWith("_AXE") ||
                item.getType().toString().endsWith("_BOW") || item.getType().toString().endsWith("BOOK");
    }

    private Attribute getRandomAttribute() {
        return Attribute.values()[random.nextInt(Attribute.values().length)];
    }

    // Enum for attributes
    private enum Attribute {
        AGILITY("Agility"),
        DEXTERITY("Dexterity"),
        STRENGTH("Strength"),
        INTELLIGENCE("Intelligence"),
        VITALITY("Vitality"),
        LUCK("Luck");

        private final String displayName;

        Attribute(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
