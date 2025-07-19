package github.eremiyuh.rPGPlugin.listeners;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignCoordListener implements Listener {

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();

        // World name must contain "rpg"
        if (!world.getName().toLowerCase().contains("rpg")) return;

        // Player must be standing on a Sea Lantern
        Block blockBelow = player.getLocation().subtract(0, 1, 0).getBlock();
        if (blockBelow.getType() != Material.SEA_LANTERN) return;

        // Sign must have only one non-empty line
        String[] lines = event.getLines();
        String nonEmptyLine = null;
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                if (nonEmptyLine != null) return; // More than one non-empty line
                nonEmptyLine = line.trim();
            }
        }

        if (nonEmptyLine == null) return; // All lines are empty

        // Validate coordinates (expect 3 space-separated integers)
        String[] parts = nonEmptyLine.split(" ");
        if (parts.length != 3) return;

        try {
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            int z = Integer.parseInt(parts[2]);

            // Check Y range
            int minY = world.getMinHeight();
            int maxY = world.getMaxHeight();
            if (y < minY || y >= maxY) {
                player.sendMessage("§cY-coordinate must be between " + minY + " and " + (maxY - 1));
                return;
            }

            Location target = new Location(world, x + 0.5, y, z + 0.5);

            // Check against world border
            WorldBorder border = world.getWorldBorder();
            Location borderCenter = border.getCenter();
            double radius = border.getSize() / 2.0;
            double dx = Math.abs(target.getX() - borderCenter.getX());
            double dz = Math.abs(target.getZ() - borderCenter.getZ());
            if (dx > radius || dz > radius) {
                player.sendMessage("§cCannot teleport: location is outside world border.");
                return;
            }

            // Check if the chunk is loaded
            if (!world.isChunkGenerated(target.getBlockX() >> 4, target.getBlockZ() >> 4)) {
                player.sendMessage("§cCannot teleport: Unexplored destination.");
                return;
            }

            // Sound & particles at origin
            Location origin = player.getLocation();
            world.spawnParticle(Particle.PORTAL, origin, 100, 0.5, 1, 0.5, 0.1);
            world.playSound(origin, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);

            // Teleport
            player.teleport(target);
            player.sendMessage("§aTeleported to §f(" + x + ", " + y + ", " + z + ")");

            // Sound & particles at destination
            world.spawnParticle(Particle.PORTAL, target, 100, 0.5, 1, 0.5, 0.1);
            world.playSound(target, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.2f);

        } catch (NumberFormatException e) {
            player.sendMessage("§cInvalid coordinates.");
        }
    }
}
