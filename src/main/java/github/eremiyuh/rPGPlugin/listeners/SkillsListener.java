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
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class SkillsListener implements Listener {

    private final RPGPlugin plugin;
    private final PlayerProfileManager profileManager;
    private final Map<UUID,Long> cooldowns = new HashMap<>();
    private final Map<UUID, Boolean> skillStates = new HashMap<>();


    public SkillsListener(RPGPlugin plugin, PlayerProfileManager profileManager) {
        this.plugin = plugin;
        this.profileManager = profileManager;
    }



    @EventHandler
    public void onPlayerUseSkill(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return; // Main hand only
        if (!event.getAction().name().contains("RIGHT_CLICK")) return;

        Player player = event.getPlayer();

        double cd = player.getAttackCooldown();
        if (cd<1) return;

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


        if (profile.getChosenClass().equalsIgnoreCase("swordsman") &&
                profile.getSelectedSkill().contains("3")) {
            finalSeconds = Math.max(20, finalSeconds+20);
        }

        if (profile.getChosenClass().equalsIgnoreCase("alchemist")) {
            finalSeconds = Math.max(10, finalSeconds+10);
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
        skillStates.remove(player.getUniqueId());
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
            player.swingMainHand();
            for (Entity e : player.getNearbyEntities(5, 5, 5)) {
                if (e instanceof LivingEntity target && !target.equals(player)) {
                    if (target instanceof Player && !target.getWorld().getName().contains("_br")) continue;
                    double damage = profile.getMeleeDmg();
                    skillStates.put(player.getUniqueId(), true);
                    target.damage(damage*.5, player);
                    applyElementEffect(target, element);
                }
            }
            setCooldown(player, 30, main.getType(), profile);
        }

//        if (skill.contains("2") && !isOnCooldown(player) && off.getType().name().endsWith("_SWORD")) {
//            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.2f, 1.2f);
//
//            Location start = player.getLocation();
//            Vector dir = start.getDirection().normalize();
//
//            for (int i = 1; i <= 20; i++) { // Distance forward
//                Location basePoint = start.clone().add(dir.clone().multiply(i));
//
//                for (int y = -5; y <= 5; y++) { // 10 blocks tall (5 up, 5 down)
//                    Location point = basePoint.clone().add(0, y, 0);
//
//                    // Particle effects showing hit zone
//                    player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, point, 1, 0, 0, 0, 0);
//                    player.getWorld().spawnParticle(Particle.CRIT, point, 2, 0.1, 0.1, 0.1, 0.05);
//                    player.getWorld().spawnParticle(
//                            Particle.DUST_COLOR_TRANSITION, point, 1,
//                            new Particle.DustTransition(Color.RED, Color.ORANGE, 1.5f)
//                    );
//
//                    // Damage entities in a 3-block horizontal radius
//                    for (Entity e : player.getWorld().getNearbyEntities(point, 3, 0.5, 3)) {
//                        if (e instanceof LivingEntity target && !target.equals(player)) {
//                            if (target instanceof Player && !target.getWorld().getName().contains("_br")) continue;
//
//                            double damage = profile.getMeleeDmg()*.5;
//                            isSkill = true;
//                            target.damage(0, player);
//
//                            // Impact particles
//                            target.getWorld().spawnParticle(Particle.SWEEP_ATTACK, target.getLocation().add(0, 1, 0), 5, 0.2, 0.2, 0.2, 0.05);
//                        }
//                    }
//                }
//            }
//
//            setCooldown(player, 30, main.getType(), profile);
//        }

        if (skill.contains("2") && !isOnCooldown(player) && off.getType().name().endsWith("_SWORD")) {
            boolean isSkill = skillStates.getOrDefault(player.getUniqueId(), false);
            if (isSkill) return;
            skillStates.put(player.getUniqueId(), true);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.2f, 1.2f);

            // maybe play a "charge-up" particle or sound here
            player.getWorld().spawnParticle(Particle.LARGE_SMOKE, player.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.1);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 100, false, false));
            // Delay 1.5s (30 ticks) before triggering sweep
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (!player.isOnline() || player.isDead()) {
                    skillStates.remove(player.getUniqueId());
                    return;
                }

                Location start = player.getLocation();
                Vector dir = start.getDirection().normalize();

                double forwardDistance = 20;
                double halfWidth = 3;
                double halfHeight = 5;

                // --- Collect entities once ---
                Set<LivingEntity> hitEntities = new HashSet<>();
                Location center = start.clone().add(dir.clone().multiply(forwardDistance / 2));

                for (Entity e : player.getWorld().getNearbyEntities(center, forwardDistance / 2 + halfWidth, halfHeight, forwardDistance / 2 + halfWidth)) {
                    if (e instanceof LivingEntity target && !target.equals(player)) {
                        if (target instanceof Player && !target.getWorld().getName().contains("_br")) continue;

                        Vector toEntity = target.getLocation().toVector().subtract(start.toVector());
                        double dot = dir.dot(toEntity.normalize());
                        if (dot > 0.5) {
                            hitEntities.add(target);
                        }
                    }
                }

                // --- Apply damage & impact particles ---
                player.swingOffHand();

                for (LivingEntity target : hitEntities) {
                    double damage = profile.getMeleeDmg() * 2;

                    target.damage(damage, player);

                    target.getWorld().spawnParticle(Particle.SWEEP_ATTACK, target.getLocation().add(0, 1, 0), 5, 0.3, 0.3, 0.3, 0.05);
                }
                setCooldown(player, 30, main.getType(), profile);
                // --- Show sweep particle effect ---
                int steps = 20;
                int verticals = 5;
                for (int i = 1; i <= steps; i++) {
                    Location base = start.clone().add(dir.clone().multiply(i));

                    for (int y = -verticals; y <= verticals; y += 2) {
                        for (int x = (int) -halfWidth; x <= halfWidth; x += 2) {
                            Location p = base.clone().add(x, y, 0);

                            player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, p, 1, 0, 0, 0, 0);
                            player.getWorld().spawnParticle(Particle.CRIT, p, 1, 0.1, 0.1, 0.1, 0.01);
                            player.getWorld().spawnParticle(
                                    Particle.DUST_COLOR_TRANSITION, p, 1,
                                    new Particle.DustTransition(Color.RED, Color.ORANGE, 1.2f)
                            );
                        }
                    }
                }

            }, 30L); // 1.5 seconds


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
            player.swingMainHand();
            player.addPotionEffect(new org.bukkit.potion.PotionEffect(PotionEffectType.STRENGTH, 200, amp+profile.getArLvl(),true,true));
            player.addPotionEffect(new org.bukkit.potion.PotionEffect(PotionEffectType.RESISTANCE, 200, amp,true,true));
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
            player.swingMainHand();
            for (Entity e : player.getNearbyEntities(10, 5, 10)) {
                if (e instanceof LivingEntity target && !target.equals(player)) {
                    if (target instanceof Player && !target.getWorld().getName().contains("_br")) continue;

                    // Burst of crit particles
                    world.spawnParticle(Particle.CRIT, target.getLocation().add(0, 1, 0), 10, 0.5, 0.5, 0.5, 0.1);

                    // Damage & effects
                    double damage = profile.getLongDmg();
                    skillStates.put(player.getUniqueId(), true);
                    target.damage(damage*.5, player);
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
            player.swingMainHand();
            if (world != null) {
                double startX = base.getBlockX() - 15; // half of 30
                double startZ = base.getBlockZ() - 15;

                for (int x = 0; x < 30; x++) {
                    for (int z = 0; z < 30; z++) {
                        Location spawnLocation = new Location(world, startX + x + 0.5, base.getY() + 50, startZ + z + 0.5);
                        Arrow arrow = world.spawn(spawnLocation, Arrow.class);
                        arrow.setShooter(player);
                        arrow.setVelocity(new Vector(0, -2, 0)); // faster downward velocity
                        arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                        Bukkit.getScheduler().runTaskLater(plugin, arrow::remove, 160L);
                    }
                }

            }

            setCooldown(player, 30, main.getType(), profile);
        }


        if (skill.contains("3") && !isOnCooldown(player)) {
            int duration = 5 * 20; // 5 seconds in ticks
            int interval = 10;     // 0.5s = 10 ticks
            int shots = duration / interval; // 10 shots

            // Speed boost effect (optional)
            player.swingMainHand();
            player.addPotionEffect(new org.bukkit.potion.PotionEffect(PotionEffectType.SPEED, duration, 1));

            // Capture the locked direction once
            Location eyeLoc = player.getEyeLocation();
            Vector lockedDirection = eyeLoc.getDirection().normalize().multiply(5); // super fast arrow
            Location spawnBase = eyeLoc.clone();

            Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
                int count = 0;

                @Override
                public void run() {
                    if (count >= shots || !player.isOnline()) {
                        return;
                    }

                    // Spawn arrow slightly in front of eyes
                    Location fireLocation = spawnBase.clone().add(lockedDirection.clone().multiply(0.2));
                    Arrow arrow = player.getWorld().spawn(fireLocation, Arrow.class);
                    arrow.setShooter(player);
                    arrow.setVelocity(lockedDirection);
                    arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                    arrow.setPierceLevel(10);

                    // ⚡ Particles at firing spot
                    player.getWorld().spawnParticle(Particle.CRIT, fireLocation, 10, 0.1, 0.1, 0.1, 0.05);
                    player.getWorld().spawnParticle(Particle.FLAME, fireLocation, 5, 0.05, 0.05, 0.05, 0.01);

                    // 🔊 Play sound at arrow spawn
                    player.getWorld().playSound(fireLocation, Sound.ENTITY_ARROW_SHOOT, 1.0f, 1.5f);

                    // ⚡ Trail effect for the arrow
                    Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
                        @Override
                        public void run() {
                            if (arrow.isDead() || arrow.isOnGround()) {
                                this.cancel();
                                return;
                            }
                            arrow.getWorld().spawnParticle(Particle.CRIT, arrow.getLocation(), 2, 0.05, 0.05, 0.05, 0.01);
                        }

                        private void cancel() {
                            // no direct cancel in Runnable, so wrap with task
                        }
                    }, 1L, 1L);

                    count++;
                }
            }, 0L, interval);

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
            player.swingMainHand();
            potionShower(player, PotionEffectType.INSTANT_DAMAGE, 20);
            setCooldown(player, 30, main.getType(), profile);
        }
        if (skill.contains("2") && !isOnCooldown(player)) {
            player.swingMainHand();
            // List of possible effects
            PotionEffectType[] effects = {
                    PotionEffectType.STRENGTH, // Strength
                    PotionEffectType.RESISTANCE, // Resistance
            };

            // Pick one at random
            PotionEffectType chosen = effects[(int)(Math.random() * effects.length)];

            potionShower(player, chosen, 20); // 20 = duration (ticks or seconds depending on your method)
            setCooldown(player, 30, main.getType(), profile);
        }
        if (skill.contains("3") && !isOnCooldown(player)) {
            player.swingMainHand();
            potionShower(player, PotionEffectType.INSTANT_HEALTH, 20);
            setCooldown(player, 30, main.getType(), profile);
        }
    }

    private void potionShower(Player player, PotionEffectType type, int seconds) {
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
                PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
                PotionEffect potionEffect = null;
                potionEffect = new PotionEffect(type, 0, 0);

                potionMeta.addCustomEffect(potionEffect, true);
                item.setItemMeta(potionMeta);
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
            case "ice" -> target.addPotionEffect(new org.bukkit.potion.PotionEffect(PotionEffectType.SLOWNESS, 200, 1));
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

    public boolean getIsSkill(Player player) {

        return skillStates.getOrDefault(player.getUniqueId(), false);
    }

    @EventHandler
    public void isCharging(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!player.getWorld().getName().contains("_rpg")) return;

        PotionEffect slowEffect = player.getPotionEffect(PotionEffectType.SLOWNESS);
        if (slowEffect != null) {
            int slowLevel = slowEffect.getAmplifier();
            if (slowLevel >= 100) {
                event.setCancelled(true);
            }
        }
    }
}
