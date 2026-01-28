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

import java.util.*;

public class PotionGiveListener implements Listener {
    private final JavaPlugin plugin;
    private final PlayerProfileManager profileManager;

    public PotionGiveListener(JavaPlugin plugin, PlayerProfileManager profileManager) {
        this.plugin = plugin;
        this.profileManager = profileManager;
    }

    private final Map<UUID, Long> alchemistCooldowns = new HashMap<>();

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!player.getWorld().getName().contains("world_rpg")) {
            return;
        }

        UserProfile playerProfile = profileManager.getProfile(player.getName());
        if (!playerProfile.getChosenClass().equalsIgnoreCase("alchemist")) return;

        if (event.getAction() != Action.RIGHT_CLICK_AIR &&
                event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        ItemStack offHandItem = player.getInventory().getItemInOffHand();

        // ✅ Cooldown check (1 sec for book, 2.5 sec otherwise)
        long now = System.currentTimeMillis();
        long cooldown = (mainHandItem.getType() == Material.BOOK) ? 1000 : 2500;

        if (alchemistCooldowns.containsKey(uuid)) {
            long lastUse = alchemistCooldowns.get(uuid);
            long remaining = cooldown - (now - lastUse);
            if (remaining > 0) {
                return;
            }
        }

        if (mainHandItem.getType() != Material.AIR && offHandItem.getType() == Material.AIR) {
            if (playerProfile.getPotion() < 1) {
                player.sendActionBar("No potions");
                return;
            }

            event.setCancelled(true);

            ItemStack potion = new ItemStack(Material.SPLASH_POTION);
            PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();

            if (potionMeta != null) {
                potionMeta.setDisplayName("Alchemist Potion");

                PotionEffect potionEffect = null;
                String skill = playerProfile.getSelectedSkill().trim().toLowerCase();

                if (skill.equals("skill 1")) {
                    potionEffect = new PotionEffect(PotionEffectType.INSTANT_DAMAGE, 1, 1);
                } else if (skill.equals("skill 2")) {
                    List<PotionEffectType> possibleEffects = Arrays.asList(
//                            PotionEffectType.POISON,
//                            PotionEffectType.WITHER,
                            PotionEffectType.SLOWNESS,
                            PotionEffectType.STRENGTH,
//                            PotionEffectType.SPEED,
                            PotionEffectType.RESISTANCE
                    );

                    Random random = new Random();
                    PotionEffectType selectedEffect = possibleEffects.get(random.nextInt(possibleEffects.size()));
                    potionEffect = new PotionEffect(selectedEffect, 0, 0); // ⛔ untouched duration
                    player.sendActionBar(ChatColor.BLUE + "Crafted Potion: " + formatPotionEffectName(selectedEffect));
                } else if (skill.equals("skill 3")) {
                    potionEffect = new PotionEffect(PotionEffectType.INSTANT_HEALTH, 1, 0);
                }

                if (potionEffect != null) {
                    potionMeta.addCustomEffect(potionEffect, true);
                }

                potion.setItemMeta(potionMeta);
            }

            // 🧪 Give potion to off-hand
            player.swingMainHand();
            player.getInventory().setItemInOffHand(potion);
            player.updateInventory();

            // Update profile + cooldown
            playerProfile.setPotion(playerProfile.getPotion() - 1);
            alchemistCooldowns.put(uuid, now); // ✅ start cooldown
        }
    }

    private String formatPotionEffectName(PotionEffectType effectType) {
        if (effectType == null) return "Unknown Effect";
        String name = effectType.getName().toLowerCase().replace('_', ' ');
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

}

