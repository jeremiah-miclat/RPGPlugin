package github.eremiyuh.rPGPlugin.methods;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import org.bukkit.Chunk;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class ChunkBorderBlueVisualizer {
    private final RPGPlugin plugin;
    private final Map<Player, Map<Chunk, BukkitRunnable>> activeVisualizations = new HashMap<>();

    public ChunkBorderBlueVisualizer(RPGPlugin plugin) {
        this.plugin = plugin;
    }

    public void showChunkBoundary(Player player, Chunk chunk) {
        // Get or create the player's chunk map
        Map<Chunk, BukkitRunnable> playerVisualizations = activeVisualizations.computeIfAbsent(player, k -> new HashMap<>());

        // Check if visualization is already active for this chunk
        if (playerVisualizations.containsKey(chunk)) {
            return; // Exit if already visualizing this chunk
        }

        World world = chunk.getWorld();
        int chunkX = chunk.getX() * 16;
        int chunkZ = chunk.getZ() * 16;
        int y = player.getLocation().getBlockY();

        Particle particleType = Particle.SOUL_FIRE_FLAME;

        // Create and schedule the visualization task
        BukkitRunnable task = new BukkitRunnable() {
            int duration = 0;

            @Override
            public void run() {
                if (duration++ >= 3 * 20) {
                    // Visualization duration is over, cancel the task
                    cancel();
                    // Remove from active visualizations
                    playerVisualizations.remove(chunk);
                    return;
                }
                drawChunkBorder(world, particleType, chunkX, y, chunkZ);
            }
        };

        // Start the task and save it in the player's map
        task.runTaskTimer(plugin, 0, 5);
        playerVisualizations.put(chunk, task);
    }

    private void drawChunkBorder(World world, Particle particle, int chunkX, int startY, int chunkZ) {
        for (int y = startY; y < startY + 3; y++) {
            for (int i = 0; i <= 16; i++) {
                // Bottom edge (X)
                world.spawnParticle(particle, chunkX + i, y, chunkZ, 1, 0.1, 0.1, 0.1, 0);
                // Right edge (Z)
                world.spawnParticle(particle, chunkX + 16, y, chunkZ + i, 1, 0.1, 0.1, 0.1, 0);
                // Top edge (X)
                world.spawnParticle(particle, chunkX + 16 - i, y, chunkZ + 16, 1, 0.1, 0.1, 0.1, 0);
                // Left edge (Z)
                world.spawnParticle(particle, chunkX, y, chunkZ + 16 - i, 1, 0.1, 0.1, 0.1, 0);
            }
        }
    }
}
