package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.manager.WaypointManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import github.eremiyuh.rPGPlugin.utils.ItemUtils;
import github.eremiyuh.rPGPlugin.utils.Waypoint;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class WaypointCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final WaypointManager waypointManager;
    private final PlayerProfileManager profileManager;

    public WaypointCommand(JavaPlugin plugin, WaypointManager waypointManager, PlayerProfileManager profileManager) {
        this.plugin = plugin;
        this.waypointManager = waypointManager;
        this.profileManager = profileManager;

        plugin.getLogger().info("WaypointCommand initialized. Manager = " + waypointManager);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }


        if (args.length == 0) {
            // List all waypoints created by this player
            List<Waypoint> playerWaypoints = waypointManager.getAllWaypoints().stream()
                    .filter(wp -> wp.getCreator().equalsIgnoreCase(player.getName()))
                    .toList();

            if (playerWaypoints.isEmpty()) {
                player.sendMessage("§eYou have no waypoints yet.");
                player.sendMessage("§eTo create: /waypoint add <waypoint name>");
                return true;
            }

            player.sendMessage("§aYour waypoints:");
            for (Waypoint wp : playerWaypoints) {
                player.sendMessage("§7- §e" + wp.getName() + " §7[World: " + wp.getWorld() + ", XYZ: " +
                        (int) wp.getX() + ", " + (int) wp.getY() + ", " + (int) wp.getZ() + "]");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("delete")) {
            if (args.length != 2) {
                player.sendMessage("§eUsage: /waypoint delete <name>");
                return true;
            }

            String name = args[1];

            Waypoint wp = waypointManager.getAllWaypoints().stream()
                    .filter(w -> w.getName().equalsIgnoreCase(name))
                    .findFirst()
                    .orElse(null);

            if (wp == null) {
                player.sendMessage("§cWaypoint §e" + name + " §cdoes not exist.");
                return true;
            }

            if (!wp.getCreator().equalsIgnoreCase(player.getName())) {
                player.sendMessage("§cYou are not the owner of this waypoint.");
                return true;
            }

            boolean deleted = waypointManager.delete(name, player.getName());
            if (deleted) {
                player.sendMessage("§aWaypoint §e" + name + " §adeleted successfully.");
            } else {
                player.sendMessage("§cFailed to delete waypoint §e" + name);
            }

            return true;
        }



        if (args.length != 2 || !args[0].equalsIgnoreCase("add")) {
            player.sendMessage("§eUsage: /waypoint add <name>");
            return true;
        }

        String name = args[1].toLowerCase();

        if (waypointManager.exists(name)) {
            player.sendMessage("§cThat waypoint already exists.");
            return true;
        }

        // Get player location
        Location loc = player.getLocation();

        Waypoint nearby = getNearestWaypoint(loc, 500);
        if (nearby != null) {
            double distance = nearby.toLocation().distance(loc);
            player.sendMessage("§cYou cannot create a waypoint here.");
            player.sendMessage("§7Nearest waypoint: §e" + nearby.getName() + " §7at " + (int) distance + " blocks away.");
            return true;
        }

// Must be standing on solid block
        Block below = loc.clone().subtract(0, 1, 0).getBlock();
        if (!below.getType().isSolid()) {
            player.sendMessage("§cYou must stand on a solid block.");
            return true;
        }

// Must not be looking too far up or down
        float pitch = loc.getPitch();
        if (pitch > 60 || pitch < -60) {
            player.sendMessage("§cYou must face forward (not up or down).");
            return true;
        }

// Determine cardinal facing
        BlockFace facing = getCardinalFacing(loc.getYaw());
        if (facing == null) {
            player.sendMessage("§cUnable to determine facing direction.");
            return true;
        }

        // Center player location on block
        Location centerLoc = loc.clone();
        centerLoc.setX(loc.getBlockX() + 0.5);
        centerLoc.setZ(loc.getBlockZ() + 0.5);

// Facing-adjusted obsidian locations
        Location feetBlockLoc = centerLoc.clone().add(facing.getDirection());
        Location headBlockLoc = feetBlockLoc.clone().add(0, 1, 0);

// Check if space is replaceable
        if (!isReplaceable(feetBlockLoc.getBlock()) || !isReplaceable(headBlockLoc.getBlock())) {
            player.sendMessage("§cSpace in front of you is blocked.");
            return true;
        }

        UserProfile playerProfile = profileManager.getProfile(player.getName());

        if (playerProfile.getEmerald() < 100) {
            player.sendMessage("§cNeed 100 emeralds. Collect 100 emeralds, then enter /convertmaterial emerald.");
            return true;
        }

// Place obsidian in front (feet + head)
        feetBlockLoc.getBlock().setType(Material.OBSIDIAN);
        headBlockLoc.getBlock().setType(Material.OBSIDIAN);

// Create waypoint with centered location and player's yaw/pitch
        Waypoint wp = new Waypoint(
                name,
                player.getName(),
                centerLoc,
                facing
        );

// Add waypoint to manager (memory + file)
        waypointManager.add(wp);

        player.sendMessage("§aWaypoint §e" + name + " §acreated successfully.");
        playerProfile.setCurrency("emerald",playerProfile.getEmerald()-100);

        return true;
    }

    /* ================= HELPERS ================= */

    private boolean isReplaceable(Block block) {
        return block.getType() == Material.AIR;
    }

    private BlockFace getCardinalFacing(float yaw) {
        yaw = yaw % 360;
        if (yaw < 0) yaw += 360;

        if (yaw >= 315 || yaw < 45) return BlockFace.SOUTH;
        if (yaw < 135) return BlockFace.WEST;
        if (yaw < 225) return BlockFace.NORTH;
        if (yaw < 315) return BlockFace.EAST;

        return null;
    }

    private boolean isNearOtherWaypoint(Location loc, double minDistance) {
        for (Waypoint wp : waypointManager.getAllWaypoints()) {
            Location wpLoc = wp.toLocation();
            if (wpLoc == null) continue; // just in case
            if (wpLoc.getWorld().equals(loc.getWorld()) && wpLoc.distance(loc) < minDistance) {
                return true; // found a waypoint too close
            }
        }
        return false;
    }

    /**
     * Returns the nearest waypoint within maxDistance of the given location.
     * If no waypoint is found within the radius, returns null.
     */
    private Waypoint getNearestWaypoint(Location loc, double maxDistance) {
        Waypoint nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (Waypoint wp : waypointManager.getAllWaypoints()) {
            Location wpLoc = wp.toLocation();
            if (wpLoc == null) continue; // skip invalid waypoints

            if (!wpLoc.getWorld().equals(loc.getWorld())) continue; // different world

            double distance = wpLoc.distance(loc);
            if (distance <= maxDistance && distance < nearestDistance) {
                nearest = wp;
                nearestDistance = distance;
            }
        }

        return nearest;
    }
}