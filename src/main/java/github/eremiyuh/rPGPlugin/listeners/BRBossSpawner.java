package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalTime;
import java.util.*;

public class BRBossSpawner {

    private final Random random = new Random();
    private final RPGPlugin plugin;

    // Boss types you specified
    private final List<EntityType> bossTypes = Arrays.asList(
            EntityType.RAVAGER,
            EntityType.EVOKER,
            EntityType.WARDEN,
            EntityType.RAVAGER,
            EntityType.PIGLIN_BRUTE,
            EntityType.ZOMBIFIED_PIGLIN
    );

    // Tracks last mark we spawned a boss for each world
    private final Map<String, Integer> lastSpawnMark = new HashMap<>();

    // Stores the first run after restart to avoid instant spawn
    private boolean firstRun = true;

    public BRBossSpawner(RPGPlugin plugin) {
        this.plugin = plugin;
        startTask();
    }

    private void startTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                int currentMinute = LocalTime.now().getMinute();
                int currentMark = (currentMinute / 15) * 15; // 0, 15, 30, 45

                for (World world : Bukkit.getWorlds()) {
                    if (!world.getName().contains("_br")) continue;

                    // ✅ Skip if no players in the map
                    if (world.getPlayers().isEmpty()) continue;

                    boolean bossExists = world.getEntities().stream()
                            .anyMatch(e -> bossTypes.contains(e.getType()));

                    int lastMark = lastSpawnMark.getOrDefault(world.getName(), -1);

                    // ✅ Skip if boss already exists
                    if (bossExists) continue;

                    // ✅ Avoid instant spawn right after restart
                    if (firstRun) {
                        lastSpawnMark.put(world.getName(), currentMark);
                        continue;
                    }

                    // 1️⃣ Spawn exactly at mark if not already done
                    if (currentMinute % 15 == 0 && lastMark != currentMinute) {
                        spawnBoss(world);
                        lastSpawnMark.put(world.getName(), currentMinute);
                        continue;
                    }

                    // 2️⃣ Spawn if past mark and missed spawn
                    if (currentMinute != currentMark && lastMark != currentMark) {
                        spawnBoss(world);
                        lastSpawnMark.put(world.getName(), currentMark);
                    }
                }

                firstRun = false; // After first tick, allow normal behavior
            }
        }.runTaskTimer(plugin, 0L, 20L * 60L); // check every minute
    }

    private void spawnBoss(World world) {
        EntityType chosenBoss = bossTypes.get(random.nextInt(bossTypes.size()));
        world.spawnEntity(world.getSpawnLocation(), chosenBoss);
    }
}
