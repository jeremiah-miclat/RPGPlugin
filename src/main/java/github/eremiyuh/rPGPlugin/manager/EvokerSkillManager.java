package github.eremiyuh.rPGPlugin.manager;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EvokerSkillManager {

    private final JavaPlugin plugin;
    private final Map<UUID, Long> lastUsed = new HashMap<>();
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final Set<UUID> boasted = new HashSet<>();
    private final Map<UUID, List<LivingEntity>> spawnedVindicators = new HashMap<>();

    private static final String TARGET_WORLD = "world_rpg";

    public EvokerSkillManager(JavaPlugin plugin) {
        this.plugin = plugin;
        startSkillLoop();
    }

    private void startSkillLoop() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();

                for (World world : Bukkit.getWorlds()) {
                    String worldName = world.getName();

                    // Only run if it's world_rpg or has "_br" in the name
                    if (!worldName.equals("world_rpg") && !worldName.contains("_br")) {
                        continue;
                    }

                    for (LivingEntity entity : world.getLivingEntities()) {
                        if (!(entity instanceof Evoker evoker) || !evoker.isValid() || evoker.isDead()) {
                            UUID id = entity.getUniqueId();
                            lastUsed.remove(id);
                            cooldowns.remove(id);
                            boasted.remove(id);
                            spawnedVindicators.remove(id);
                            continue;
                        }

                        UUID id = evoker.getUniqueId();

                        // Assign cooldown if not present
                        cooldowns.putIfAbsent(id, (10 + new Random().nextInt(11)) * 1000L);

                        long last = lastUsed.getOrDefault(id, 0L);
                        long cd = cooldowns.get(id);
                        long elapsed = now - last;

                        if (elapsed >= cd) {
                            activateSkill(evoker);
                            lastUsed.put(id, now);
                            cooldowns.put(id, (10 + new Random().nextInt(11)) * 1000L); // randomize next use
                            boasted.remove(id);
                        } else if (elapsed >= cd - 5000 && !boasted.contains(id)) {
                            sendBoast(evoker);
                            boasted.add(id);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 20L); // Run every second
    }


    private void activateSkill(@NotNull Evoker evoker) {
        Location center = evoker.getLocation();
        World world = center.getWorld();
        if (world == null) return;

        double radius = 30;
        Location centerFlat = center.clone();
        centerFlat.setY(0);

        // VFX & Sound
        world.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 2f, 1f);
        world.spawnParticle(Particle.EXPLOSION, center.clone().add(0, 1, 0), 1);
        world.spawnParticle(Particle.CLOUD, center, 100, 3, 1, 3, 0.1);
        world.spawnParticle(Particle.ENCHANT, center.clone().add(0, 2, 0), 50, 1, 1, 1, 0);

        // Knockback + damage players
        for (Player player : world.getPlayers()) {
            Location playerLoc = player.getLocation().clone();
            playerLoc.setY(0);

            if (playerLoc.distanceSquared(centerFlat) <= radius * radius) {
                Vector push = player.getLocation().toVector().subtract(center.toVector()).normalize().multiply(2);
                push.setY(1.0);
                player.setVelocity(push);


                int damage = (int) (Objects.requireNonNull(evoker.getAttribute(Attribute.ATTACK_DAMAGE)).getValue() * 1.2);


                player.damage(damage);
                player.sendMessage("§a" + evoker.getName() + ": \"Shinra Tensei!\"");
            }
        }

        summonVindicators(evoker);
    }

    private void summonVindicators(@NotNull Evoker evoker) {
        UUID id = evoker.getUniqueId();
        Location base = evoker.getLocation().clone().add(evoker.getLocation().getDirection().setY(0).normalize().multiply(3));
        Vector right = new Vector(-evoker.getLocation().getDirection().getZ(), 0, evoker.getLocation().getDirection().getX());
        World world = evoker.getWorld();

        List<LivingEntity> vindicators = new ArrayList<>();

        for (int i = -1; i <= 2; i++) {
            Location spawnLoc = base.clone().add(right.clone().multiply(i));
            spawnLoc.setY(world.getHighestBlockYAt(spawnLoc) + 1);

            Vindicator vindicator = (Vindicator) world.spawnEntity(spawnLoc, EntityType.VINDICATOR);
            vindicator.setCustomNameVisible(true);
            vindicator.setPersistent(false);
            vindicator.setRemoveWhenFarAway(true);
            vindicator.setCanPickupItems(false);
            vindicators.add(vindicator);
        }

        spawnedVindicators.put(id, vindicators);

        new BukkitRunnable() {
            @Override
            public void run() {
                List<LivingEntity> toRemove = spawnedVindicators.remove(id);
                if (toRemove != null) {
                    for (LivingEntity e : toRemove) {
                        if (!e.isDead()) e.remove();
                    }
                }
            }
        }.runTaskLater(plugin, 6 * 20L); // 6 seconds later
    }

    private void sendBoast(@NotNull Evoker evoker) {
        evoker.getWorld().playSound(evoker.getLocation(), Sound.ENTITY_EVOKER_PREPARE_ATTACK, 1f, 0.8f);

        for (Player player : evoker.getWorld().getPlayers()) {
            if (player.getLocation().distanceSquared(evoker.getLocation()) <= 60 * 60) {
                player.sendMessage("§e" + evoker.getName() + ": \"This world shall know pain...\"");
            }
        }
    }

    @SuppressWarnings("unused")
    private void activateHeal(@NotNull Evoker evoker) {
        double maxHealth = evoker.getAttribute(Attribute.MAX_HEALTH).getValue();
        double current = evoker.getHealth();
        double healAmount = Math.min(1000, maxHealth - current);

        if (healAmount > 0) {
            evoker.setHealth(current + healAmount);
            evoker.getWorld().spawnParticle(Particle.HEART, evoker.getLocation().add(0, 1, 0), 30, 0.5, 0.5, 0.5);
            evoker.getWorld().playSound(evoker.getLocation(), Sound.ENTITY_EVOKER_PREPARE_WOLOLO, 1.5f, 1f);

            for (Player player : evoker.getWorld().getPlayers()) {
                if (player.getLocation().distanceSquared(evoker.getLocation()) <= 60 * 60) {
                    player.sendMessage("§a" + evoker.getName() + " chants: \"Life... returns to me.\"");
                }
            }
        }
    }
}
