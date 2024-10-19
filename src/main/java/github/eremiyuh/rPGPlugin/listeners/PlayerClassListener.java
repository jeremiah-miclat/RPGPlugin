package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.manager.PlayerClassManager;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerClassListener implements Listener {

    private final PlayerClassManager playerClassManager;

    public PlayerClassListener(PlayerClassManager playerClassManager) {
        this.playerClassManager = playerClassManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Select Your Class")) {
            event.setCancelled(true); // Prevent item pickup

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null && clickedItem.hasItemMeta()) {
                String className = clickedItem.getItemMeta().getDisplayName();
                switch (className) {
                    case "§aSwordsman":
                        playerClassManager.savePlayerClass(player, "Swordsman");
                        player.sendMessage("You have selected the Swordsman class!");
                        break;
                    case "§aArcher":
                        playerClassManager.savePlayerClass(player, "Archer");
                        player.sendMessage("You have selected the Archer class!");
                        break;
                    case "§aAlchemist":
                        playerClassManager.savePlayerClass(player, "Alchemist");
                        player.sendMessage("You have selected the Alchemist class!");
                        break;
                    default:
                        player.sendMessage("Invalid class selection.");
                        break;
                }

                player.closeInventory(); // Close the GUI after selection
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            String playerClass = playerClassManager.loadPlayerClass(player);
            double originalDamage = event.getDamage(); // Store the original damage

            if (playerClass != null) {
                // Add a default case if necessary
                if (playerClass.equals("Swordsman")) {
                    if (player.getInventory().getItemInMainHand().getType().toString().endsWith("_SWORD")) {
                        double newDamage = originalDamage * 1.2; // 20% more damage
                        event.setDamage(newDamage); // Set the new damage
                        player.sendMessage("Swordsman, dealt " + newDamage + " (previous damage: " + originalDamage + ")");
                    }
                }
            }
        } else if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();
            if (projectile instanceof Arrow) {
                // Get the shooter of the arrow
                ProjectileSource shooter = projectile.getShooter();
                if (shooter instanceof Player) {
                    Player player = (Player) shooter;
                    String playerClass = playerClassManager.loadPlayerClass(player);
                    double originalDamage = event.getDamage();

                    if ("Archer".equals(playerClass)) {
                        double newDamage = originalDamage * 1.2; // 20% more damage for Archer class
                        event.setDamage(newDamage); // Set the new damage
                        player.sendMessage("Archer, dealt " + newDamage + " with your arrow (previous damage: " + originalDamage + ")");
                    }

                }
            } else if (event.getDamager() instanceof ThrownPotion) {
                ThrownPotion thrownPotion = (ThrownPotion) event.getDamager();

                ProjectileSource thrower = thrownPotion.getShooter();
                if (thrower instanceof Player) {
                    Player player = (Player) thrower;
                    String playerClass = playerClassManager.loadPlayerClass(player);
                    double originalDamage = event.getDamage();

                    if ("Alchemist".equals(playerClass)) {
                        double newDamage = originalDamage * 1.2; // 20% more damage for Archer class
                        event.setDamage(newDamage); // Set the new damage
                        player.sendMessage("Alchemist, dealt " + newDamage + " with your potion (previous damage: " + originalDamage + ")");
                    }

                }


            }
        }}

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        ThrownPotion thrownPotion = event.getPotion();
        ProjectileSource thrower = thrownPotion.getShooter();

        // Check if the thrower is a player
        if (thrower instanceof Player) {
            Player player = (Player) thrower;

            // Get the player's class (assumes you have a method to retrieve it)
            String playerClass = playerClassManager.loadPlayerClass(player);

            // Check if the player is an Alchemist
            if ("Alchemist".equalsIgnoreCase(playerClass)) {

                // Iterate through potion effects and boost them
                for (PotionEffect effect : thrownPotion.getEffects()) {
                    PotionEffectType effectType = effect.getType();

                    // Boost effect depending on its type
                    switch (effectType.getKey().getKey()) {  // Use toString() instead of getName()
                        case "instant_health":
                            // Boost healing (increase healing power)
                            boostHealing(event, player, effect);
                            break;

                        case "speed":
                        case "increase_damage": // For strength
                        case "jump_boost":
                        case "regeneration":
                            // For these effects, we will increase the duration or amplifier
                            boostEffect(event, effectType, player, effect);
                            break;

                        default:
                            // Handle other potions if necessary
                            player.sendMessage("Potion effect " + effectType.getKey().getKey()
                                    + " boosted.");
                            break;
                    }
                }
            }
        }
    }

    // Method to boost healing potions
    private void boostHealing(PotionSplashEvent event, Player player, PotionEffect effect) {
        int amplifier = effect.getAmplifier(); // Get current amplifier
        double originalHealingAmount = amplifier;
        double healingFactor = 1.2; // 20% boost
        double amplifiedHealingAmount = originalHealingAmount * healingFactor;

        // Notify player
        player.sendMessage("Boosted healing with amplifier " + amplifier);
        player.sendMessage("Amplified healing amount: " + amplifiedHealingAmount);

        // Apply increased healing to all affected entities
        event.getAffectedEntities().forEach(entity -> {

            if (entity instanceof Player) {
                Player affectedPlayer = (Player) entity;
                double maxHealth = affectedPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                double newHealth = Math.min(affectedPlayer.getHealth() + amplifiedHealingAmount, maxHealth);
                affectedPlayer.setHealth(newHealth);
            } else {
                // Handle other entities (e.g., mobs) that can receive healing
                if (entity instanceof LivingEntity) {
                    LivingEntity livingEntity = (LivingEntity) entity;
                    // Assuming the same max health logic applies to all living entities
                    double maxHealth = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                    double newHealth = Math.min(livingEntity.getHealth() + amplifiedHealingAmount, maxHealth);
                    livingEntity.setHealth(newHealth);
                }
            }
        });
    }

    // Method to boost other potion effects (like Speed, Strength, etc.)
    private void boostEffect(PotionSplashEvent event, PotionEffectType effectType, Player player, PotionEffect effect) {
        int amplifier = effect.getAmplifier();
        int originalDuration = effect.getDuration(); // In ticks (20 ticks = 1 second)

        // Increase the effect's amplifier and duration
        int newAmplifier = amplifier + 1; // Increase amplifier by 1
        int newDuration = (int) (originalDuration * 5); // Increase duration by 500%

        player.sendMessage("Boosted " + effectType.getKey().getKey() + " potion. New amplifier: " + newAmplifier);
        player.sendMessage("New duration: " + newDuration / 20 + " seconds.");

        // Apply new boosted effect to affected players
        event.getAffectedEntities().forEach(entity -> {
            if (entity instanceof Player) {
                Player affectedPlayer = (Player) entity;

                // Apply new effect with boosted amplifier and duration
                affectedPlayer.addPotionEffect(new PotionEffect(effectType, newDuration, newAmplifier));
            } else {
                // Handle other entities (e.g., mobs) that can receive healing
                if (entity instanceof LivingEntity) {
                    LivingEntity livingEntity = (LivingEntity) entity;
                    livingEntity.addPotionEffect(new PotionEffect(effectType, newDuration, newAmplifier));
                }
            }
        });
    }
}