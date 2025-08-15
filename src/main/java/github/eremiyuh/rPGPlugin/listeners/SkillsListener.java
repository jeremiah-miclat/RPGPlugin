package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class SkillsListener implements Listener {

    private final RPGPlugin plugin;
    private final PlayerProfileManager profileManager;
    private final Map<UUID,Long> cooldowns = new HashMap<>();

    public SkillsListener(RPGPlugin plugin, PlayerProfileManager profileManager) {
        this.plugin = plugin;
        this.profileManager = profileManager;
    }



    @EventHandler
    public void onPlayerUseSkill(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return; // Main hand only
        if (!event.getAction().name().contains("RIGHT_CLICK")) return;

        Player player = event.getPlayer();
        String worldName = player.getWorld().getName().toLowerCase();
        if (!worldName.contains("_rpg")) return;
        if (player.isFlying()) return;
        UserProfile profile = profileManager.getProfile(player.getName());
        if (profile == null) return;

        String clazz = profile.getChosenClass().toLowerCase();
        String skill = profile.getSelectedSkill(); // Now using full string like "Skill 1"
        String element = profile.getSelectedElement().toLowerCase();

        switch (clazz) {
            case "swordsman" -> handleSwordsman(player, profile, skill, element);
            case "archer" -> handleArcher(player, profile, skill, element);
            case "alchemist" -> handleAlchemist(player, profile, skill, element);
        }
    }

    private boolean isOnCooldown(Player player) {
        return cooldowns.getOrDefault(player.getUniqueId(), 0L) > System.currentTimeMillis();
    }

    private void setCooldown(Player player, long baseSeconds, Material item, UserProfile profile) {
        double reduction = profile.getCdr(); // cooldown reduction in seconds
        long finalSeconds = Math.max(1, Math.round(baseSeconds - reduction)); // min 1 sec

        // Special rule: Swordsman skill 2 must have at least 5 sec cooldown
        if (profile.getChosenClass().equalsIgnoreCase("swordsman") &&
                profile.getSelectedSkill().contains("2")) {
            finalSeconds = Math.max(5, finalSeconds+4);
        }

        if (profile.getChosenClass().equalsIgnoreCase("alchemist")) {
            finalSeconds = Math.max(10, finalSeconds+9);
        }

        // Store functional cooldown timer
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + (finalSeconds * 1000L));

        if ("archer".equalsIgnoreCase(profile.getChosenClass())) {
            // Archers: Only show cooldown visually on slot 8 if it has an item
            ItemStack slot8 = player.getInventory().getItem(8); // hotbar index 8 = slot #9
            if (slot8 != null && slot8.getType() != Material.AIR) {
                player.setCooldown(slot8.getType(), (int) (finalSeconds * 20));
            }
        } else {
            // Non-archers: Apply cooldown visual to the skill item
            if (item != null) {
                player.setCooldown(item, (int) (finalSeconds * 20));
            }
        }
    }



    // =======================
    // Swordsman Skills
    // =======================
    private void handleSwordsman(Player player, UserProfile profile, String skill, String element) {
        ItemStack main = player.getInventory().getItemInMainHand();
        ItemStack off = player.getInventory().getItemInOffHand();
        if (main.getType() == Material.AIR || !main.getType().name().endsWith("_SWORD")) return;

        if (skill.contains("1") && !isOnCooldown(player)) {
            // Play sound to indicate skill activation
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.5f, 1.0f);
            player.getWorld().spawnParticle(
                    Particle.SWEEP_ATTACK,
                    player.getLocation(),
                    50, 5, 1, 5, 0 // count, offsetX, offsetY, offsetZ, extra
            );
            player.getWorld().spawnParticle(
                    Particle.CRIT,
                    player.getLocation(),
                    80, 5, 1, 5, 0.2
            );
            for (Entity e : player.getNearbyEntities(5, 5, 5)) {
                if (e instanceof LivingEntity target && !target.equals(player)) {
                    if (target instanceof Player && !target.getWorld().getName().contains("_br")) continue;
                    double damage = profile.getMeleeDmg();
                    target.damage(0, player);
                    applyElementEffect(target, element);
                }
            }
            setCooldown(player, 30, main.getType(), profile);
        }

        if (skill.contains("2") && !isOnCooldown(player) && off.getType().name().endsWith("_SWORD")) {
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.2f, 1.2f);

            Location start = player.getLocation();
            Vector dir = start.getDirection().normalize();

            for (int i = 1; i <= 20; i++) { // Distance forward
                Location basePoint = start.clone().add(dir.clone().multiply(i));

                for (int y = -5; y <= 5; y++) { // 10 blocks tall (5 up, 5 down)
                    Location point = basePoint.clone().add(0, y, 0);

                    // Particle effects showing hit zone
                    player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, point, 1, 0, 0, 0, 0);
                    player.getWorld().spawnParticle(Particle.CRIT, point, 2, 0.1, 0.1, 0.1, 0.05);
                    player.getWorld().spawnParticle(
                            Particle.DUST_COLOR_TRANSITION, point, 1,
                            new Particle.DustTransition(Color.RED, Color.ORANGE, 1.5f)
                    );

                    // Damage entities in a 3-block horizontal radius
                    for (Entity e : player.getWorld().getNearbyEntities(point, 3, 0.5, 3)) {
                        if (e instanceof LivingEntity target && !target.equals(player)) {
                            if (target instanceof Player && !target.getWorld().getName().contains("_br")) continue;

                            double damage = profile.getMeleeDmg()*.5;
                            target.damage(0, player);

                            // Impact particles
                            target.getWorld().spawnParticle(Particle.SWEEP_ATTACK, target.getLocation().add(0, 1, 0), 5, 0.2, 0.2, 0.2, 0.05);
                        }
                    }
                }
            }

            setCooldown(player, 30, main.getType(), profile);
        }

        if (skill.contains("3") && !isOnCooldown(player)) {
            int amp = 0;
            for (Entity e : player.getNearbyEntities(10, 10, 10)) {
                if (e instanceof LivingEntity target && !target.equals(player)) {
                    amp++;
                    if (target instanceof Monster monster) {
                        monster.setTarget(player);
                    }
                }
            }
            player.addPotionEffect(new org.bukkit.potion.PotionEffect(PotionEffectType.STRENGTH, 200, amp));
            player.addPotionEffect(new org.bukkit.potion.PotionEffect(PotionEffectType.RESISTANCE, 200, amp));
            setCooldown(player, 30, main.getType(), profile);
        }
    }

    // =======================
    // Archer Skills
    // =======================
    private void handleArcher(Player player, UserProfile profile, String skill, String element) {
        ItemStack main = player.getInventory().getItemInMainHand();
        if (!(main.getType() == Material.BOW || main.getType() == Material.CROSSBOW)) return;

        if (skill.contains("1") && !isOnCooldown(player)) {
            Location center = player.getLocation();
            World world = center.getWorld();

            // 1️⃣ Quick particle ring
            double radius = 10;
            for (double angle = 0; angle < 360; angle += 10) {
                double rad = Math.toRadians(angle);
                double x = Math.cos(rad) * radius;
                double z = Math.sin(rad) * radius;
                world.spawnParticle(Particle.DRAGON_BREATH, center.clone().add(x, 1, z), 3, 0, 0, 0, 0);
            }

            // 2️⃣ Quick burst over all affected entities
            for (Entity e : player.getNearbyEntities(10, 5, 10)) {
                if (e instanceof LivingEntity target && !target.equals(player)) {
                    if (target instanceof Player && !target.getWorld().getName().contains("_br")) continue;

                    // Burst of crit particles
                    world.spawnParticle(Particle.CRIT, target.getLocation().add(0, 1, 0), 10, 0.5, 0.5, 0.5, 0.1);

                    // Damage & effects
                    double damage = profile.getLongDmg();
                    target.damage(damage, player);
                    applyElementEffect(target, element);

                    // Optional: retarget monsters
                    if (target instanceof Monster monster) {
                        Monster bossPriority = getNearbyBoss(target.getLocation(), 20);

                        // Make sure it doesn't target itself
                        if (bossPriority != null && !bossPriority.equals(monster)) {
                            monster.setTarget(bossPriority);
                        } else {
                            Monster randomMonster = getRandomMonster(target.getLocation(), 20);
                            if (randomMonster != null && !randomMonster.equals(monster)) {
                                monster.setTarget(randomMonster);
                            }
                        }
                    }
                }
            }

            // 3️⃣ Sounds
            world.playSound(center, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.5f, 0.5f);
            world.playSound(center, Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1.0f, 1.0f);

            // Cooldown
            setCooldown(player, 30, main.getType(), profile);
        }





        if (skill.contains("2") && !isOnCooldown(player)) {
            Location base = player.getLocation();
            World world = base.getWorld();

            if (world != null) {
                double startX = base.getX() - 7; // half of 15 blocks
                double startZ = base.getZ() - 7;

                for (int x = 0; x < 15; x++) {       // Cover each block in X
                    for (int z = 0; z < 15; z++) {   // Cover each block in Z
                        Location spawnLocation = new Location(world, startX + x, base.getY() + 100, startZ + z);
                        Arrow arrow = world.spawn(spawnLocation, Arrow.class);
                        arrow.setShooter(player);
                        arrow.setVelocity(new Vector(0, -0.1, 0)); // faster fall
                        arrow.setDamage(10);
                        arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                        Bukkit.getScheduler().runTaskLater(plugin, arrow::remove, 160L);
                    }
                }
            }

            setCooldown(player, 30, main.getType(), profile);
        }


        if (skill.contains("3") && !isOnCooldown(player)) {
            player.addPotionEffect(new org.bukkit.potion.PotionEffect(PotionEffectType.SPEED, 200, 1));
            setCooldown(player, 30, main.getType(), profile);
        }
    }

    // =======================
    // Alchemist Skills
    // =======================
    private void handleAlchemist(Player player, UserProfile profile, String skill, String element) {
        ItemStack main = player.getInventory().getItemInMainHand();
        if (main.getType() == Material.AIR) return;

        if (skill.contains("1") && !isOnCooldown(player)) {
            potionShower(player, PotionType.HARMING, 20);
            setCooldown(player, 30, main.getType(), profile);
        }
        if (skill.contains("2") && !isOnCooldown(player)) {
            potionShower(player, PotionType.STRENGTH, 20);
            setCooldown(player, 30, main.getType(), profile);
        }
        if (skill.contains("3") && !isOnCooldown(player)) {
            potionShower(player, PotionType.HEALING, 20);
            setCooldown(player, 30, main.getType(), profile);
        }
    }

    private void potionShower(Player player, PotionType type, int seconds) {
        Location origin = player.getLocation();
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= seconds * 20) {
                    cancel();
                    return;
                }
                ThrownPotion potion = player.getWorld().spawn(origin.clone().add(0, 3, 0), ThrownPotion.class);
                potion.setShooter(player);
                ItemStack item = new ItemStack(Material.SPLASH_POTION);
                PotionMeta meta = (PotionMeta) item.getItemMeta();
                meta.setBasePotionType(type);
                item.setItemMeta(meta);
                potion.setItem(item);
                potion.setVelocity(new Vector(0, -1, 0));
                ticks += 10;
            }
        }.runTaskTimer(plugin, 0, 10);
    }

    // =======================
    // Utilities
    // =======================
    private void applyElementEffect(LivingEntity target, String element) {
        switch (element) {
            case "fire" -> target.setFireTicks(200);
            case "water" -> target.addPotionEffect(new org.bukkit.potion.PotionEffect(PotionEffectType.WEAKNESS, 200, 9));
            case "ice" -> target.addPotionEffect(new org.bukkit.potion.PotionEffect(PotionEffectType.SLOWNESS, 200, 9));
        }
    }

    private Monster getRandomMonster(Location loc, double radius) {
        List<Monster> monsters = new ArrayList<>();
        for (Entity e : loc.getWorld().getNearbyEntities(loc, radius, radius, radius)) {
            if (e instanceof Monster m) monsters.add(m);
        }
        return monsters.isEmpty() ? null : monsters.get(new Random().nextInt(monsters.size()));
    }

    private Monster getNearbyBoss(Location loc, double radius) {
        for (Entity e : loc.getWorld().getNearbyEntities(loc, radius, radius, radius)) {
            if (e instanceof Monster m && m.getCustomName() != null && m.getCustomName().contains("Boss")) {
                return m;
            }
        }
        return null;
    }
}
