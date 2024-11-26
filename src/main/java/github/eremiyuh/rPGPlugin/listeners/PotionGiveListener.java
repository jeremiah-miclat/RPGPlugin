package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PotionGiveListener implements Listener {
    private final JavaPlugin plugin;
    private final PlayerProfileManager profileManager;

    public PotionGiveListener(JavaPlugin plugin, PlayerProfileManager profileManager) {
        this.plugin = plugin;
        this.profileManager = profileManager;
    }

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        if (!event.getPlayer().getWorld().getName().equals("world_rpg") && !event.getPlayer().getWorld().getName().contains("labyrinth")) {
            return;
        }

        Player player = event.getPlayer();
        UserProfile playerProfile = profileManager.getProfile(player.getName());


        if (!playerProfile.getChosenClass().equalsIgnoreCase("alchemist")) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_AIR &&
                event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return; // Only handle right-click actions
        }

        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        ItemStack offHandItem = player.getInventory().getItemInOffHand();

        // Check if main hand has a book and off-hand is empty
        if (mainHandItem.getType() == Material.BOOK && offHandItem.getType() == Material.AIR) {
            if (playerProfile.getPotion() < 1) {
                player.sendMessage("No potions");
                return;
            }
            // Cancel the event to prevent any default behavior
            event.setCancelled(true);

            // Create a potion item
            ItemStack potion = new ItemStack(Material.SPLASH_POTION); // Change to a different potion type if needed
            PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();

            if (potionMeta != null) {
                // Set the potion display name
                potionMeta.setDisplayName("Alchemist Potion");

                // Determine the potion effect based on the selected skill
                PotionEffect potionEffect = null;
                if (playerProfile.getSelectedSkill().equalsIgnoreCase("skill 1")) {
                    potionEffect = new PotionEffect(PotionEffectType.INSTANT_DAMAGE, 1, 1);
                } else if (playerProfile.getSelectedSkill().equalsIgnoreCase("skill 2")) {
                    // Create a list of potential PotionEffectTypes for "skill 2"
                    List<PotionEffectType> possibleEffects = Arrays.asList(
                            PotionEffectType.POISON,
                            PotionEffectType.WITHER,
                            PotionEffectType.SLOWNESS,
                            PotionEffectType.STRENGTH, // Strength
                            PotionEffectType.SPEED,
                            PotionEffectType.RESISTANCE // Resistance
                    );

                    // Randomly select an effect
                    Random random = new Random();
                    PotionEffectType selectedEffect = possibleEffects.get(random.nextInt(possibleEffects.size()));

                    // Create the potion effect with a duration of 20 ticks (1 second) and amplifier 1
                    potionEffect = new PotionEffect(selectedEffect, 100, 1);
                    player.sendMessage(ChatColor.BLUE + "Crafted Potion: " + formatPotionEffectName(selectedEffect));
                } else if (playerProfile.getSelectedSkill().equalsIgnoreCase("skill 3")) {
                    potionEffect = new PotionEffect(PotionEffectType.INSTANT_HEALTH, 1, 1);
                }

                // Add the custom effect to the potion
                if (potionEffect != null) {
                    potionMeta.addCustomEffect(potionEffect, true);
                }

                // Set the modified meta back to the potion
                potion.setItemMeta(potionMeta);
            }

            // Set the potion directly into the off-hand
            player.getInventory().setItemInOffHand(potion);
            playerProfile.setPotion(playerProfile.getPotion() - 1);
        }
    }

    private String formatPotionEffectName(PotionEffectType effectType) {
        if (effectType == null) return "Unknown Effect";
        // Convert the effect type name to a readable format
        String name = effectType.getName().toLowerCase().replace('_', ' ');
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}

