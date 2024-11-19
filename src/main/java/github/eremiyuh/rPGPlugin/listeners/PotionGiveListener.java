package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
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
                    potionEffect = new PotionEffect(PotionEffectType.POISON, 450, 1); // Duration of 450 ticks (22.5 seconds)
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
}

