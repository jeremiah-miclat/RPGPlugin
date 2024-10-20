package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class OptimizedVampireSunlightListener implements Listener {

    private final PlayerProfileManager profileManager;
    private final Set<UUID> vampireCooldown = new HashSet<>(); // To limit sunlight checks
    private final RPGPlugin plugin; // Reference to the main plugin

    public OptimizedVampireSunlightListener(PlayerProfileManager profileManager, RPGPlugin plugin) {
        this.profileManager = profileManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UserProfile profile = profileManager.getProfile(player.getName());

        // Only proceed if the player's race is Vampire
        if (profile != null && "vampire".equalsIgnoreCase(profile.getSelectedRace())) {

            // Only trigger the sunlight check if the player moves to a new block
            if (hasMovedToNewBlock(event)) {
                UUID playerId = player.getUniqueId();

                // Check if player is already in cooldown (e.g., sunlight check every 5 seconds)
                if (!vampireCooldown.contains(playerId)) {
                    checkForSunlight(player);

                    // Add player to cooldown
                    vampireCooldown.add(playerId);

                    // Remove from cooldown after 5 seconds
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            vampireCooldown.remove(playerId);
                        }
                    }.runTaskLater(plugin, 100L); // 100 ticks = 5 seconds
                }
            }
        }
    }

    private void checkForSunlight(Player player) {
        World world = player.getWorld();
        Block block = player.getLocation().getBlock();

        // Check if it is daytime and the player is in direct sunlight
        if (world.getTime() > 0 && world.getTime() < 12300 && !block.getRelative(0, 1, 0).getType().isOccluding()) {
            if (world.getHighestBlockAt(player.getLocation()).getY() <= player.getLocation().getY()) {
                player.setFireTicks(100);
            }
        }
    }

    private boolean hasMovedToNewBlock(PlayerMoveEvent event) {
        // Only consider significant movement between blocks
        return event.getFrom().getBlockX() != event.getTo().getBlockX() ||
                event.getFrom().getBlockY() != event.getTo().getBlockY() ||
                event.getFrom().getBlockZ() != event.getTo().getBlockZ();
    }
}
