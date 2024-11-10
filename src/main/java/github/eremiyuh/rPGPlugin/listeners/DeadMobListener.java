package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.manager.BossDropItem;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.MetadataValue;

import java.util.*;

public class DeadMobListener implements Listener {
    private final PlayerProfileManager profileManager;
    private final Random random = new Random();

    private final List<BossDropItem> regularBossDrops = Arrays.asList(
            new BossDropItem(new ItemStack(Material.IRON_HELMET), random.nextInt(1, 2), 0.7),
            new BossDropItem(new ItemStack(Material.IRON_CHESTPLATE), random.nextInt(1, 2), 0.7),
            new BossDropItem(new ItemStack(Material.IRON_LEGGINGS), random.nextInt(1, 2), 0.7),
            new BossDropItem(new ItemStack(Material.IRON_BOOTS), random.nextInt(1, 2), 0.7),
            new BossDropItem(new ItemStack(Material.IRON_SWORD), random.nextInt(1, 2), 0.7),
            new BossDropItem(new ItemStack(Material.DIAMOND_HELMET), random.nextInt(1, 2), 0.2),
            new BossDropItem(new ItemStack(Material.DIAMOND_CHESTPLATE), random.nextInt(1, 2), 0.2),
            new BossDropItem(new ItemStack(Material.DIAMOND_LEGGINGS), random.nextInt(1, 2), 0.2),
            new BossDropItem(new ItemStack(Material.DIAMOND_BOOTS), random.nextInt(1, 2), 0.2),
            new BossDropItem(new ItemStack(Material.BOW), random.nextInt(1, 2), 0.1),
            new BossDropItem(new ItemStack(Material.CROSSBOW), random.nextInt(1, 2), 0.1),
            new BossDropItem(new ItemStack(Material.DIAMOND_SWORD), random.nextInt(1, 2), 0.1)
    );

    private final List<BossDropItem> worldBossDrops = Arrays.asList(
            new BossDropItem(new ItemStack(Material.DIAMOND_HELMET), random.nextInt(1, 2), 0.99),
            new BossDropItem(new ItemStack(Material.DIAMOND_CHESTPLATE), random.nextInt(1, 2), 0.99),
            new BossDropItem(new ItemStack(Material.DIAMOND_LEGGINGS), random.nextInt(1, 2), 0.99),
            new BossDropItem(new ItemStack(Material.DIAMOND_BOOTS), random.nextInt(1, 2), 0.99),
            new BossDropItem(new ItemStack(Material.DIAMOND_SWORD), random.nextInt(1, 2), 0.99),
            new BossDropItem(new ItemStack(Material.BOW), random.nextInt(1, 2), 0.99),
            new BossDropItem(new ItemStack(Material.CROSSBOW), random.nextInt(1, 2), 0.99),
            new BossDropItem(new ItemStack(Material.NETHERITE_HELMET), random.nextInt(1, 2), 0.01),
            new BossDropItem(new ItemStack(Material.NETHERITE_CHESTPLATE), random.nextInt(1, 2), 0.01),
            new BossDropItem(new ItemStack(Material.NETHERITE_LEGGINGS), random.nextInt(1, 2), 0.01),
            new BossDropItem(new ItemStack(Material.NETHERITE_BOOTS), random.nextInt(1, 2), 0.01),
            new BossDropItem(new ItemStack(Material.NETHERITE_SWORD), random.nextInt(1, 2), 0.01)
    );



    public DeadMobListener(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }






    @EventHandler
    public void onModifiedMobDeath(EntityDeathEvent event) {
        if (!Objects.requireNonNull(event.getEntity().getLocation().getWorld()).getName().equals("world_rpg")) {
            return;
        }

        double RANDOMCHANCE = .05;
        if ((event.getEntity() instanceof Monster || event.getEntity() instanceof Wolf || event.getEntity() instanceof IronGolem) && event.getEntity().getKiller() instanceof Player killer) {

            LivingEntity mob = event.getEntity();

            if (mob.hasMetadata("extraDamage")) {
                UserProfile killerProfile = profileManager.getProfile(killer.getName());
                String killerTeam = killerProfile.getTeam();

                double health = mob.getMetadata("extraDamage").get(0).asDouble() * 10;
                RANDOMCHANCE += (health * 0.00045);
                int dropMultiplier = (int) (1+(health * 0.01));

                String customName = mob.getCustomName();
                boolean isBoss = isBoss(mob);
                boolean isWorldBoss = isWorldBoss(mob);
                int bosslvl = getBossLevel(mob);

                List<String> attackerNames = (List<String>) mob.getMetadata("attackerList").get(0).value();
                List<Player> nearbyPlayers = new ArrayList<>();

                // Iterate over the list of attacker names and check if they are players nearby
                for (String attackerName : attackerNames) {
                    Player player = Bukkit.getPlayer(attackerName); // Get the player by name
                    if (player != null && isPlayerNearby(player, mob.getLocation(), 60, 10)) {
                        nearbyPlayers.add(player); // Add player to nearby players list
                    }
                }

                if ((isBoss || isWorldBoss) && bosslvl >= 10) {
                    for (Player player : nearbyPlayers) {
                        UserProfile playerProfile = profileManager.getProfile(player.getName());
                        BossDropItem dropItem = isBoss ? BossDropItem.getRandomBossDropItem(regularBossDrops) : BossDropItem.getRandomBossDropItem(worldBossDrops);
                        if (player.getName().equals(killer.getName())) {
                            applyRewards(player, playerProfile, health, 1, dropMultiplier);
                        } else {
                            applyRewards(player, playerProfile, health, RANDOMCHANCE, dropMultiplier);
                        }
                        distributeDrops(player, event, dropMultiplier);
                        if (dropItem != null) {
                            // Clone the item to ensure each player gets a separate instance
                            ItemStack itemToDrop = dropItem.getItem().clone();
                            // Add lore based on the boss level
                            dropItem.addLoreWithBossLevel(itemToDrop, bosslvl, isWorldBoss);
                            player.sendMessage("You received a boss reward for killing " + customName);
                            if (player.getInventory().firstEmpty() != -1) {
                                // If there is space in the inventory, add the item
                                player.getInventory().addItem(itemToDrop);
                            } else {
                                // If the inventory is full, drop the item in the world
                                player.getWorld().dropItem(player.getLocation(), itemToDrop);
                                player.sendMessage("Your inventory was full, so the item has been dropped on the ground.");
                            }
                        }

                }
                    event.getDrops().clear();
                    event.setDroppedExp(0);
                    return;
                }

                if (killerTeam.equals("none")) {
                    applyRewards(killer, killerProfile, health, 0, 0);
                    distributeDrops(killer, event, dropMultiplier);
                } else {
                    for (Player player : nearbyPlayers) {
                        UserProfile playerProfile = profileManager.getProfile(player.getName());
                        if (playerProfile.getTeam().equals(killerTeam)) {
                            applyRewards(player, playerProfile, health, 0, 0);
                            distributeDrops(player, event, dropMultiplier);
                        }
                    }
                }
                // Clear the original drops and experience
                event.getDrops().clear();
                event.setDroppedExp(0);
            }
        }
    }

    private void applyRewards(Player player, UserProfile profile, double healthForExp, double chanceForEquipLoreAndDias, int rewardCount) {
        player.giveExp((int) (healthForExp * 2));
        profile.setAbyssPoints(profile.getAbyssPoints()+(healthForExp * 2));
        if (random.nextDouble() < chanceForEquipLoreAndDias) {
            applyRandomLoreToEquippedItem(player);
        }
        double randomed = random.nextDouble();
        if (randomed < chanceForEquipLoreAndDias) {
            // Create a random instance
            Random random = new Random();

            // Select a random ore type
            OreType randomOre = OreType.values()[random.nextInt(OreType.values().length)];

            // Give rewardCount to the selected ore only
            switch (randomOre) {
                case DIAMOND:
                    profile.setDiamond(profile.getDiamond() + rewardCount);
                    break;
                case EMERALD:
                    profile.setEmerald(profile.getEmerald() + rewardCount);
                    break;
                case GOLD:
                    profile.setGold(profile.getGold() + rewardCount);
                    break;
                case IRON:
                    profile.setIron(profile.getIron() + rewardCount);
                    break;
                case LAPIS:
                    profile.setLapiz(profile.getLapiz() + rewardCount);
                    break;
            }
        }

    }

    enum OreType {
        DIAMOND,
        EMERALD,
        GOLD,
        IRON,
        LAPIS
    }


    private void distributeDrops(Player player, EntityDeathEvent event, int dropMultiplier) {
        for (ItemStack drop : event.getDrops()) {

            int originalAmount = drop.getAmount();
            int multipliedAmount = originalAmount * dropMultiplier;
            ItemStack newDrop = new ItemStack(drop.getType(), multipliedAmount);
            // Give the items directly to the player


            if (player.getInventory().firstEmpty() != -1) {
                // If there is space in the inventory, add the item
                player.getInventory().addItem(newDrop);
            } else {
                // If the inventory is full, drop the item in the world
                player.getWorld().dropItem(player.getLocation(), newDrop);

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

    private boolean isBoss(LivingEntity entity) {
        return entity.hasMetadata("boss");
    }

    private boolean isWorldBoss(LivingEntity entity) {
        return entity.hasMetadata("worldboss");
    }

    private int getBossLevel(LivingEntity entity) {
        if (entity.hasMetadata("lvl")) {
            return entity.getMetadata("lvl").get(0).asInt(); // Retrieve the level from metadata
        }
        return 1; // Default level if no metadata is set
    }

    // Method to check if the player is nearby within specified range
    private boolean isPlayerNearby(Player player, Location mobLocation, double horizontalRange, double verticalRange) {
        Location playerLocation = player.getLocation();
        double horizontalDistance = playerLocation.distance(mobLocation);
        double verticalDistance = Math.abs(playerLocation.getY() - mobLocation.getY());

        return horizontalDistance <= horizontalRange && verticalDistance <= verticalRange;
    }

}
