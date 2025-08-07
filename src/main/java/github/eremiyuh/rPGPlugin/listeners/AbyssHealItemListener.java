package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import github.eremiyuh.rPGPlugin.utils.ItemUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class AbyssHealItemListener implements Listener {

    @EventHandler
    public void onPlayerRightClick(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        Location loc = player.getLocation();
        String worldName = loc.getWorld().getName();
        if (worldName.contains("world_rpg")) {
            double x = loc.getX();
            double z = loc.getZ();
            if (Math.abs(x) > 17000 || Math.abs(z) > 17000) {
                // ❌ If the item applies Bad Omen (or is suspicious stew with effect), block it
                if (item.getType() == Material.SUSPICIOUS_STEW || item.getType() == Material.OMINOUS_BOTTLE) {
                    event.setCancelled(true);
                    player.sendMessage("§cYou cannot trigger a raid near or above the Level 80 zone.");
                    return;
                }
            }
        }


        // Check if the consumed item is the Abyss Potion
        if (item.isSimilar(ItemUtils.getAbyssPotion())) {
            event.setCancelled(true);

            if (player.getHealth()<=0) return;

            // Heal the player to full health
            player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).getValue());
            player.sendMessage("§aYou have been healed to full health!");

            // Reduce the stack size of the item in hand
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (itemInHand.isSimilar(item)) {
                int newAmount = itemInHand.getAmount() - 1;
                if (newAmount > 0) {
                    itemInHand.setAmount(newAmount);
                } else {
                    player.getInventory().setItemInMainHand(null); // Remove the item entirely
                    return;
                }
            }
            ItemStack itemInOffHand = player.getInventory().getItemInOffHand();
            if (itemInOffHand.isSimilar(item)) {
                int newAmount = itemInOffHand.getAmount() - 1;
                if (newAmount > 0) {
                    itemInOffHand.setAmount(newAmount);
                } else {
                    player.getInventory().setItemInOffHand(null); // Remove the item entirely
                }
            }
        }


        if (item.isSimilar(ItemUtils.getResistPotion())) {
            event.setCancelled(true);

            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 36_000, 0, true, true));

            // Reduce the stack size of the item in hand
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (itemInHand.isSimilar(item)) {
                int newAmount = itemInHand.getAmount() - 1;
                if (newAmount > 0) {
                    itemInHand.setAmount(newAmount);
                } else {
                    player.getInventory().setItemInMainHand(null); // Remove the item entirely
                    return;
                }
            }
            ItemStack itemInOffHand = player.getInventory().getItemInOffHand();
            if (itemInOffHand.isSimilar(item)) {
                int newAmount = itemInOffHand.getAmount() - 1;
                if (newAmount > 0) {
                    itemInOffHand.setAmount(newAmount);
                } else {
                    player.getInventory().setItemInOffHand(null); // Remove the item entirely
                }
            }
        }
    }
}

