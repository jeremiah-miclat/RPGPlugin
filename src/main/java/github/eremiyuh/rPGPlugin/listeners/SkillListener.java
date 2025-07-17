package github.eremiyuh.rPGPlugin.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class SkillListener implements Listener {

    private final HashMap<UUID, Long> lastUsed = new HashMap<>();
    private static final long COOLDOWN_MILLIS = 1500; // 1.5 seconds

    @EventHandler
    public void onToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (!player.getWorld().getName().contains("rpg")) return;

        // Must have Jump Boost effect
        if (!player.hasPotionEffect(PotionEffectType.JUMP_BOOST)) {
            return;
        }

        // Only if the player begins sneaking
        if (!event.isSneaking()) return;

        // Check if player is in the air
        if (!isAirborne(player)) return;

        // Check cooldown
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        if (lastUsed.containsKey(uuid) && now - lastUsed.get(uuid) < COOLDOWN_MILLIS) {
//            long secondsLeft = (COOLDOWN_MILLIS - (now - lastUsed.get(uuid))) / 1000;

            return;
        }

        // Activate skill
        event.setCancelled(true);            // Prevent sneak state from changing
        player.setSneaking(false);           // Reset sneak animation (prevents visual crouch)
        activateAeroLeap(player);
        lastUsed.put(uuid, now); // Start cooldown
    }

    private void activateAeroLeap(Player player) {
        Vector direction = player.getLocation().getDirection().normalize();
        Vector dash = direction.multiply(2.5).setY(0.3);
        player.setVelocity(dash);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 1.2f);
        player.spawnParticle(Particle.CLOUD, player.getLocation().add(0, 1, 0), 20, 0.2, 0.2, 0.2, 0.01);

    }

    private boolean isAirborne(Player player) {
        Location loc = player.getLocation();
        Material blockBelow = loc.clone().subtract(0, 0.1, 0).getBlock().getType();
        return blockBelow.isAir(); // In air if nothing underfoot
    }
}
