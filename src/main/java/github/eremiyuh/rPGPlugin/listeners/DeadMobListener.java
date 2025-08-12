package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import github.eremiyuh.rPGPlugin.manager.BossDropItem;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.methods.BossKillMessages;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import github.scarsz.discordsrv.DiscordSRV;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.*;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public class DeadMobListener implements Listener {
    private final RPGPlugin plugin;
    private final PlayerProfileManager profileManager;
    private final Random random = new Random();
    private final Map<UUID, Long> lastBrBossRewardTime = new HashMap<>();
    private static final long REWARD_COOLDOWN = 15 * 60 * 1000;

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
            new BossDropItem(new ItemStack(Material.BOOK), random.nextInt(1, 2), 0.99),
            new BossDropItem(new ItemStack(Material.CROSSBOW), random.nextInt(1, 2), 0.99),
            new BossDropItem(new ItemStack(Material.NETHERITE_HELMET), random.nextInt(1, 2), 0.01),
            new BossDropItem(new ItemStack(Material.NETHERITE_CHESTPLATE), random.nextInt(1, 2), 0.01),
            new BossDropItem(new ItemStack(Material.NETHERITE_LEGGINGS), random.nextInt(1, 2), 0.01),
            new BossDropItem(new ItemStack(Material.NETHERITE_BOOTS), random.nextInt(1, 2), 0.01),
            new BossDropItem(new ItemStack(Material.NETHERITE_SWORD), random.nextInt(1, 2), 0.01)
    );



    public DeadMobListener(RPGPlugin plugin, PlayerProfileManager profileManager) {
        this.plugin = plugin;
        this.profileManager = profileManager;
    }


    @EventHandler
    public void onCreeperExplode(EntityExplodeEvent event) {
        if (!event.getEntity().getWorld().getName().contains("_rpg")) return;
        if (!(event.getEntity() instanceof Creeper)) return;
        Location loc = event.getEntity().getLocation();
        loc.getWorld().spawnEntity(loc, EntityType.BLAZE);
    }

    @EventHandler
    public void entityDeathInRpg(EntityDeathEvent event) {
        if (!event.getEntity().getWorld().getName().contains("_rpg")) return;

        // Handle Zombie death
        if (event.getEntity() instanceof Zombie) {
            // 20% chance
            if (new Random().nextInt(100) < 20) {
                Zombie zombie = (Zombie) event.getEntity();
                Location loc = zombie.getLocation();
                zombie.getWorld().spawnEntity(loc, EntityType.SKELETON);
            }
        }

    }


    @EventHandler
    public void onModifiedMobDeath(EntityDeathEvent event) {



        if (!Objects.requireNonNull(event.getEntity().getLocation().getWorld()).getName().contains("_rpg")
        ) {

                if (event.getEntity().getKiller() instanceof Player player  ) {
                    UserProfile profile = profileManager.getProfile(player.getName());
                    int bonusPoints = profile.getHunter();
                    profile.setActivitypoints(profile.getActivitypoints()+1+bonusPoints);
                    if (event.getEntity() instanceof  Warden || event.getEntity() instanceof Wither) {
                        profile.setCurrency("activitypoints", profile.getCurrency("activitypoints") + (10000));
                    }

                    if (event.getEntity() instanceof EnderDragon) {
                        profile.setCurrency("activitypoints", profile.getCurrency("activitypoints") + (20000));
                    }
                }

                if ((event.getEntity().getKiller() instanceof Projectile proj && proj.getShooter() instanceof Player shooter)) {
                    UserProfile profile = profileManager.getProfile(shooter.getName());
                    int bonusPoints = profile.getHunter();
                    profile.setActivitypoints(profile.getActivitypoints()+1+bonusPoints);
                    if (event.getEntity() instanceof  Warden || event.getEntity() instanceof Wither) {
                        profile.setCurrency("activitypoints", profile.getCurrency("activitypoints") + (10000));
                    }
                    if (event.getEntity() instanceof EnderDragon) {
                        profile.setCurrency("activitypoints", profile.getCurrency("activitypoints") + (20000));;
                    }
                }


            return;
        }
        if (!(event.getEntity() instanceof Monster)) return;

        World world = event.getEntity().getWorld();
        int xValidRange = 60;
        int yValidRange = 20;
        double RANDOMCHANCE = .05;
        int level = 0;
        if (event.getEntity() instanceof Monster mob && event.getEntity().getKiller() instanceof Player killer) {


            // Extract the custom name of the mob
            String customName = mob.getCustomName();

            // Initialize the health variable
            double health = 0.0;
            int multiplier = 1;


            // Check if the custom name exists and contains "Lvl"
            if (customName != null) {
                if (customName.contains("Leader")) {multiplier*=10;}
                if (customName.contains("Boss") && !(customName.contains("World"))) {multiplier*=100;}
                if (customName.contains("World Boss")) {multiplier*=1000;}

                // Remove the health indicator (anything in the format [number])
                customName = customName.replaceAll("\\[\\d+\\]", "").trim(); // Removes [number] and trims any extra spaces

                // Check if the custom name contains "Lvl"
                if (customName.contains("Lvl")) {
                    try {
                        // Split the name at "Lvl" and extract the level
                        String[] parts = customName.split("Lvl"); // Split the name at "Lvl"
                        if (parts.length > 1) {
                            // Get the part after "Lvl", then split by spaces to get the level
                            String levelPart = parts[1].trim().split(" ")[0]; // Extract level number (before any space)
                            level = Integer.parseInt(levelPart); // Parse the level number

                            // Compute the health based on the level (level * 100)
                            health = level * 100 * multiplier;
                        }
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                        // If parsing fails, fallback to default health (can be set to 100 or another value)
                        health = 100.0; // Default value
                    }
                }
            }


            UserProfile killerProfile = profileManager.getProfile(killer.getName());
            String killerTeam = killerProfile.getTeam();

            int dropMultiplier = (int) (1+(health * .001));

                boolean isBoss = isBoss(mob);
                boolean isWorldBoss = isWorldBoss(mob);
                int bosslvl = level;
                double chance = (double) bosslvl /400;


                if (event.getEntity() instanceof Warden)  {
                    chance *=1.2;
                    health *=2;
                }

            if (event.getEntity() instanceof Wither)  {
                chance *=1.2;
                health *=2;
            }

            if (event.getEntity() instanceof Ravager)  {
                chance *=1.2;
                health *=1.5;
            }

            if (event.getEntity() instanceof Evoker)  {
                chance *=1.2;
                health *=1.5;
            }








                if ((isBoss || isWorldBoss) && bosslvl >= 1) {
                    List<String> attackerNames = (List<String>) mob.getMetadata("attackerList").get(0).value();
                    List<Player> nearbyPlayers = new ArrayList<>();
                    // Iterate over the list of attacker names and check if they are players nearby
                    assert attackerNames != null;
                    for (String attackerName : attackerNames) {
                        Player player = Bukkit.getPlayer(attackerName); // Get the player by name
                        if (player != null && mob.getWorld().getName().equals(player.getWorld().getName())) {
                            nearbyPlayers.add(player); // Add player to nearby players list
                        }
                    }


                    for (Player player : nearbyPlayers) {
                        String worldName = player.getWorld().getName();
                        // ✅ Only enforce cooldown in _br maps
                        if (worldName.contains("_br")) {
                            long lastTime = lastBrBossRewardTime.getOrDefault(player.getUniqueId(), 0L);
                            long now = System.currentTimeMillis();

                            if (now - lastTime < REWARD_COOLDOWN) {
                                long minutesLeft = ((REWARD_COOLDOWN - (now - lastTime)) / 1000) / 60;
                                player.sendMessage("§cYou can only receive boss rewards every 15 minutes in this kind of map. Try again in " + minutesLeft + " min(s).");
                                continue; // ⛔ Skip giving reward
                            }

                            // Record new reward time
                            lastBrBossRewardTime.put(player.getUniqueId(), now);
                        }
                        UserProfile playerProfile = profileManager.getProfile(player.getName());
                        applyRewards(player, playerProfile, health*3, chance+.2, dropMultiplier,level);
                        distributeDrops(player, event, dropMultiplier,level);

                        if (bosslvl > 9 && isWorldBoss) {

                            int dropCount = 2 + ((bosslvl - 1) / 50);

                            for (int i = 0; i < dropCount; i++) {
                                BossDropItem dropItem = isBoss ? BossDropItem.getRandomBossDropItem(regularBossDrops)
                                        : BossDropItem.getRandomBossDropItem(worldBossDrops);

                                if (dropItem != null) {
                                    ItemStack itemToDrop = dropItem.getItem().clone();
                                    dropItem.addLoreWithBossLevel(itemToDrop, bosslvl, playerProfile.getLevel(), isWorldBoss);

                                    player.sendMessage("You received a boss reward for killing " + customName);

                                    if (player.getInventory().firstEmpty() != -1) {
                                        player.getInventory().addItem(itemToDrop);
                                    } else {
                                        player.getWorld().dropItem(player.getLocation(), itemToDrop);
                                        player.sendMessage("Your inventory was full, so the item has been dropped on the ground.");
                                    }
                                }
                            }



                        }

                }
                    event.getDrops().clear();
                    event.setDroppedExp(0);
                                        if (isWorldBoss) {
                        try {
                            BossKillMessages.broadcastBossKill(killer.getName(), customName);
                        } catch (Exception e) {
                            Bukkit.getLogger().warning("[BossKill] Failed to broadcast kill: " + e.getMessage());
                            // Continue execution silently
                        }
                    }
                    return;
                }

            applyRewards(killer, killerProfile, health, 0, 0,level);
            distributeDrops(killer, event, dropMultiplier,level);
            if (!killerTeam.equals("none")) {
                UserProfile teamOwnerProfile = profileManager.getProfile(killerProfile.getTeam());
                List<String> killerTeamMatesNames = teamOwnerProfile.getTeamMembers();
                List<Player> teamMembers = new ArrayList<>();

                // Get the online players from the killer team
                for (String teamMemberName : killerTeamMatesNames) {
                    Player teamMember = Bukkit.getPlayer(teamMemberName); // Get the player by name
                    if (teamMember != null && teamMember.isOnline() && teamMember.getWorld()== mob.getWorld()) { // Check both null and online status
                        teamMembers.add(teamMember);
                    }
                }

                // Process the team members
                for (Player teamMember : teamMembers) {
                    // Skip the killer (you could compare the teamMember's name to killerProfile.getName())
                    if (teamMember.getName().equals(killer.getName())) {
                        continue; // Skip the killer to avoid duplicate rewards
                    }

                    // Check if the player is within the specified range of the mob
                    if (isPlayerNearby(teamMember, mob.getLocation(), xValidRange, yValidRange)) {

                        // Retrieve the player profile (assuming it’s a synchronous call)
                        UserProfile playerProfile = profileManager.getProfile(teamMember.getName());

                        // Make sure the profile is not null, and the team matches the killer's team
                        if (playerProfile != null && playerProfile.getTeam().equals(killerTeam)) {
                            // Apply rewards to the player
                            applyRewards(teamMember, playerProfile, health, 0, 0,level);

                            // Distribute drops (could potentially be made asynchronous)
                            distributeDrops(teamMember, event, dropMultiplier,level);
                        } else {
                            // Optional: log or handle the case where the profile or team doesn't match
                            System.out.println("Player " + teamMember.getName() + " has no valid profile or mismatched team.");
                        }
                    }
                }
            }


            // Clear the original drops and experience
                event.getDrops().clear();
                event.setDroppedExp(0);


        }
    }

    private void applyRewards(Player player, UserProfile profile, double healthForExp, double chanceForEquipLoreAndDias, int rewardCount, int monsterLevel) {
        int playerLevel = profile.getLevel();
        int levelDiff = monsterLevel - playerLevel;
        double currentAmount = profile.getEmerald();

        if (Math.abs(levelDiff) > 60) {
            if (profile.isBossIndicator()) {
                player.sendMessage("⚠ No rewards due 60 level difference. /sdw to turn off this warning");
            }
            return;
        }
        if (monsterLevel>=200) profile.setCurrency("emerald",currentAmount+1);
        // --- Reward Multiplier Logic ---
        double rewardMultiplier;
        if (Math.abs(levelDiff) > 30) {
            rewardMultiplier = 0.1; // No drops if monster too weak or too strong
        } else if (Math.abs(levelDiff) > 20) {
            rewardMultiplier = 0.5; // Heavily reduced drops
        } else if (Math.abs(levelDiff) > 10) {
            rewardMultiplier = 0.8; // Slightly reduced
        } else {
            rewardMultiplier = 1.0; // Full rewards
        }

        // Notify player if rewards are reduced
        if (rewardMultiplier < 1.0 && profile.isBossIndicator()) {
            player.sendMessage(ChatColor.RED + "⚠ Rewards reduced due to level gap. /sdw to turn off this warning");
        }

        // --- EXP Reward ---
        int expGained = (int) (healthForExp * 0.2 * rewardMultiplier);
        if (expGained > 0) {
            player.giveExp(expGained);
        }

        // --- Abyss Points Reward ---
        int baseMin = (int) (healthForExp / 12);
        int baseMax = (int) (healthForExp / 10);
        int rawAbyss = baseMin + (int) (Math.random() * (baseMax - baseMin));
        int finalAbyss = (int) (rawAbyss * rewardMultiplier);

        if (finalAbyss >= 1) {
            profile.setAbysspoints(profile.getAbysspoints() + finalAbyss);
            player.sendMessage(ChatColor.AQUA + "You gained " + finalAbyss + " Abyss Points!");
        }

        // --- Lore Reward ---
        if (random.nextDouble() < chanceForEquipLoreAndDias) {
            applyRandomLoreToEquippedItem(player);
        }

        // --- Ore Reward ---
        if (random.nextDouble() < chanceForEquipLoreAndDias) {
            OreType randomOre = OreType.values()[random.nextInt(OreType.values().length)];
            int baseAmount = 10 + (int) (Math.random() * ((double) rewardCount / 100));
            int scaledAmount = (int) (baseAmount * rewardMultiplier);
            int finalAmount = Math.min(scaledAmount, 300);

            if (finalAmount >= 1) {
                switch (randomOre) {
                    case DIAMOND -> {
                        profile.setDiamond(profile.getDiamond() + finalAmount);
                        player.sendMessage(ChatColor.GREEN + "You received " + finalAmount + " Diamond(s)!");
                    }
                    case EMERALD -> {
                        profile.setEmerald(profile.getEmerald() + finalAmount);
                        player.sendMessage(ChatColor.GREEN + "You received " + finalAmount + " Emerald(s)!");
                    }
                    case GOLD -> {
                        profile.setGold(profile.getGold() + finalAmount);
                        player.sendMessage(ChatColor.GREEN + "You received " + finalAmount + " Gold Ingot(s)!");
                    }
                    case IRON -> {
                        profile.setIron(profile.getIron() + finalAmount);
                        player.sendMessage(ChatColor.GREEN + "You received " + finalAmount + " Iron Ingot(s)!");
                    }
                    case LAPIS -> {
                        profile.setLapiz(profile.getLapiz() + finalAmount);
                        player.sendMessage(ChatColor.GREEN + "You received " + finalAmount + " Lapis Lazuli(s)!");
                    }
                    case COPPER -> {
                        profile.setCopper(profile.getCopper() + finalAmount);
                        player.sendMessage(ChatColor.GREEN + "You received " + finalAmount + " Copper Ingot(s)!");
                    }
                }
            }
        }
    }


    enum OreType {
        DIAMOND,
        EMERALD,
        GOLD,
        IRON,
        LAPIS,
        COPPER
    }


    private void distributeDrops(Player player, EntityDeathEvent event, int dropMultiplier, int monsterLevel) {
        Random random = new Random();
        UserProfile profile = profileManager.getProfile(player.getName());
        if (profile == null) return;

        int playerLevel = profile.getLevel();
        int levelDiff = monsterLevel - playerLevel;

        if (playerLevel - monsterLevel >= 60) {

            return;
        }

        // --- Reward Multiplier Logic ---
        double rewardMultiplier;
        if (Math.abs(levelDiff) > 30) {
            rewardMultiplier = 0.1; // No drops if monster too weak or too strong
        } else if (Math.abs(levelDiff) > 20) {
            rewardMultiplier = 0.5; // Heavily reduced drops
        } else if (Math.abs(levelDiff) > 10) {
            rewardMultiplier = 0.8; // Slightly reduced
        } else {
            rewardMultiplier = 1.0; // Full rewards
        }

//        if (rewardMultiplier < 1.0) {
//            player.sendMessage(ChatColor.YELLOW + "Drop rewards reduced due to level gap.");
//        }

        boolean receivedDrops = false;

        // --- Process Valid Drops ---
        for (ItemStack drop : event.getDrops()) {
            if (isEquipable(drop.getType())) continue;

            int baseAmount = drop.getAmount();
            int scaledAmount = (int) (baseAmount * (dropMultiplier / 100.0) * rewardMultiplier);

            if (scaledAmount < 1) continue;

            int randomizedAmount = 1 + random.nextInt(scaledAmount); // [1, scaledAmount]
            ItemStack newDrop = new ItemStack(drop.getType(), randomizedAmount);

            if (player.getInventory().firstEmpty() != -1) {
                player.getInventory().addItem(newDrop);
                receivedDrops = true;
            } else {
                // 10% chance to notify about full inventory
                if (random.nextInt(10) == 0) {
                    player.sendMessage(ChatColor.RED + "Inventory full! Some drops were lost.");
                }
            }
        }


    }



    // Helper method to determine if an item is equipable
    private boolean isEquipable(Material material) {
        return material.name().endsWith("_HELMET") ||
                material.name().endsWith("_CHESTPLATE") ||
                material.name().endsWith("_LEGGINGS") ||
                material.name().endsWith("_BOOTS") ||
                material.name().endsWith("_SWORD") ||
                material.name().endsWith("_AXE") ||
                material.name().endsWith("_SHOVEL") ||
                material.name().endsWith("_HOE") ||
                material.name().contains("CROSSBOW") ||
                material.name().contains("BOW") ||
                material.name().contains("SHIELD") ||
                material.name().contains("UNDYING") ||
                material.name().contains("SCULK") ||
                material.name().contains("SADDLE");
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
                    player.sendMessage("Item ascended after boss kill. +1 " + selectedAttribute.getDisplayName() + " on " + selectedItem.getType());
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
        return (entity.getCustomName().contains("Boss") && !entity.getCustomName().contains("World Boss") );
    }

    private boolean isWorldBoss(LivingEntity entity) {
        return entity.getCustomName().contains("World Boss");
    }

    private int getBossLevel(LivingEntity entity) {
        if (entity.hasMetadata("lvl")) {
            return entity.getMetadata("lvl").get(0).asInt();
        }
        return 1;
    }

    // Method to check if the player is nearby within specified range
    private boolean isPlayerNearby(Player player, Location mobLocation, double horizontalRange, double verticalRange) {
        if (player.getWorld()!= mobLocation.getWorld()) return false;

        Location playerLocation = player.getLocation();
        double horizontalDistance = playerLocation.distance(mobLocation);
        double verticalDistance = Math.abs(playerLocation.getY() - mobLocation.getY());

        return horizontalDistance <= horizontalRange && verticalDistance <= verticalRange;
    }
}
