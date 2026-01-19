package github.eremiyuh.rPGPlugin.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;

public class Waypoint {

    private final String name;
    private final String creator;

    private final String world;
    private final double x;
    private final double y;
    private final double z;

    private final float yaw;
    private final float pitch;

    private final BlockFace facing;

    public Waypoint(String name, String creator, Location location, BlockFace facing) {
        this.name = name;
        this.creator = creator;

        this.world = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();

        this.yaw = location.getYaw();
        this.pitch = location.getPitch();

        this.facing = facing;
    }

    /* ================= GETTERS ================= */

    public String getName() {
        return name;
    }

    public String getCreator() {
        return creator;
    }

    public String getWorld() {
        return world;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public BlockFace getFacing() {
        return facing;
    }

    /* ================= UTILITY ================= */

    public Location toLocation() {
        World w = Bukkit.getWorld(world);
        if (w == null) return null;

        return new Location(w, x, y, z, yaw, pitch);
    }
}
