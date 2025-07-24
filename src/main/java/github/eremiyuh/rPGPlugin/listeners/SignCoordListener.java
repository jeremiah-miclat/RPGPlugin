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

        if (nonEmptyLine == null) return;

        // Validate coordinates (expect 3 space-separated integers)
        String[] parts = nonEmptyLine.split(" ");
        if (parts.length != 3) return;

        try {
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]); // still parsed, but unused
            int z = Integer.parseInt(parts[2]);

            // World border check
            WorldBorder border = world.getWorldBorder();
            Location borderCenter = border.getCenter();
            double radius = border.getSize() / 2.0;
            double dx = Math.abs(x + 0.5 - borderCenter.getX());
            double dz = Math.abs(z + 0.5 - borderCenter.getZ());
            if (dx > radius || dz > radius) {
                player.sendMessage("§cCannot teleport: location is outside world border.");
                return;
            }

            // Check if chunk is generated
            int chunkX = x >> 4;
            int chunkZ = z >> 4;
            if (!world.isChunkGenerated(chunkX, chunkZ)) {
                player.sendMessage("§cCannot teleport: Unexplored destination.");
                return;
            }

            // Search downward from world max height
            int maxY = world.getMaxHeight();
            int minY = world.getMinHeight();
            Location target = null;

            for (int ySearch = maxY - 1; ySearch > minY; ySearch--) {
                Block airBlock = world.getBlockAt(x, ySearch, z);
                Block belowBlock = world.getBlockAt(x, ySearch - 1, z);

                if (airBlock.getType() == Material.AIR && belowBlock.getType() != Material.AIR) {
                    target = new Location(world, x + 0.5, ySearch, z + 0.5);
                    break;
                }
            }

            if (target == null) {
                player.sendMessage("§cNo valid air-above-ground location found.");
                return;
            }

            // Teleport effect: origin
            Location origin = player.getLocation();
            world.spawnParticle(Particle.PORTAL, origin, 100, 0.5, 1, 0.5, 0.1);
            world.playSound(origin, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);

            // Teleport
            player.teleport(target);
            player.sendMessage("§aTeleported to §f(" + x + ", " + target.getBlockY() + ", " + z + ")");

            // Teleport effect: destination
            world.spawnParticle(Particle.PORTAL, target, 100, 0.5, 1, 0.5, 0.1);
            world.playSound(target, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.2f);

        } catch (NumberFormatException e) {
            player.sendMessage("§cInvalid coordinates.");
        }
    }
}
