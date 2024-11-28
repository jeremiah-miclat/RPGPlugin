package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.block.BlockExplodeEvent;

import java.util.Objects;

public class OverworldBlastProtectionListener implements Listener {

    private static final int SEA_LEVEL = 63; // Default sea level in Minecraft is at y=63

    public OverworldBlastProtectionListener(RPGPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    // Handle entity-based explosions (e.g., TNT, creepers, wither skulls)
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getLocation().getWorld().getName().contains("rpg")) {
            event.blockList().removeIf(block -> block.getY() > -65);
        }


        if (Objects.requireNonNull(event.getLocation().getWorld()).getEnvironment() == World.Environment.NORMAL && !event.getLocation().getWorld().getName().contains("resource")) {
            event.blockList().removeIf(block -> block.getY() > SEA_LEVEL);
        }
    }

    // Handle block-based explosions (e.g., beds, respawn anchors)
    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        if (event.getBlock().getWorld().getName().contains("rpg")) {
            event.blockList().removeIf(block -> block.getY() > -65);
        }

        if (event.getBlock().getWorld().getEnvironment() == World.Environment.NORMAL && !event.getBlock().getWorld().getName().contains("resource"))  {
            event.blockList().removeIf(block -> block.getY() > SEA_LEVEL);
        }
    }
}
