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
            EntityType.WITHER,
            EntityType.PIGLIN_BRUTE,
            EntityType.ZOMBIFIED_PIGLIN
    );

    // Tracks last minute we spawned a boss in a given world
    private final Map<String, Integer> lastSpawnMinute = new HashMap<>();

    public BRBossSpawner(RPGPlugin plugin) {
        this.plugin = plugin;
        startTask();
    }

    private void startTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                int currentMinute = LocalTime.now().getMinute();

                for (World world : Bukkit.getWorlds()) {
                    if (!world.getName().contains("_br")) continue;

                    boolean bossExists = world.getEntities().stream()
                            .anyMatch(e -> bossTypes.contains(e.getType()));

                    // If world is freshly loaded & no boss → spawn immediately
                    if (!lastSpawnMinute.containsKey(world.getName()) && !bossExists) {
                        spawnBoss(world);
                        lastSpawnMinute.put(world.getName(), currentMinute);
                        continue;
                    }

                    // Spawn only on 15-minute multiples, avoid re-spawning same minute
                    if (currentMinute % 15 == 0 &&
                            (!bossExists) &&
                            lastSpawnMinute.getOrDefault(world.getName(), -1) != currentMinute) {

                        spawnBoss(world);
                        lastSpawnMinute.put(world.getName(), currentMinute);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L * 60L); // Check every 60 seconds
    }

    private void spawnBoss(World world) {
        EntityType chosenBoss = bossTypes.get(random.nextInt(bossTypes.size()));
        world.spawnEntity(world.getSpawnLocation(), chosenBoss);
    }
}
