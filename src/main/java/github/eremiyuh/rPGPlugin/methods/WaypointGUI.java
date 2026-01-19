package github.eremiyuh.rPGPlugin.methods;

import github.eremiyuh.rPGPlugin.manager.WaypointManager;
import github.eremiyuh.rPGPlugin.utils.Waypoint;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class WaypointGUI {

    private static final int SLOTS_PER_PAGE = 9 * 3; // 3 rows
    private static final Material NEXT_BUTTON = Material.ARROW;

//    public static void open(Player player, WaypointManager waypointManager) {
//        open(player, waypointManager, 0);
//    }

//    public static void open(Player player, WaypointManager waypointManager, int page) {
//        List<Waypoint> waypoints = new ArrayList<>(waypointManager.getAllWaypoints());
//        if (waypoints.isEmpty()) {
//            player.sendMessage(ChatColor.RED + "No waypoints available.");
//            return;
//        }
//
//        int totalPages = (int) Math.ceil(waypoints.size() / (double) SLOTS_PER_PAGE);
//        if (page >= totalPages) page = totalPages - 1;
//        if (page < 0) page = 0;
//
//        Inventory inv = Bukkit.createInventory(null, 9 * 3, "Waypoints (Page " + (page + 1) + "/" + totalPages + ")");
//
//        int startIndex = page * SLOTS_PER_PAGE;
//        int endIndex = Math.min(startIndex + SLOTS_PER_PAGE, waypoints.size());
//
//        for (int i = startIndex; i < endIndex; i++) {
//            Waypoint wp = waypoints.get(i);
//
//            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
//            SkullMeta meta = (SkullMeta) skull.getItemMeta();
//
//            if (meta != null) {
//                OfflinePlayer owner = Bukkit.getOfflinePlayer(wp.getCreator()); // handle safely
//                meta.setOwningPlayer(owner);
//
//                // Set the display name to the waypoint name
//                meta.displayName(Component.text(wp.getName()).color(NamedTextColor.AQUA));
//
//                List<Component> lore = new ArrayList<>();
//                lore.add(Component.text("Owner: " + wp.getCreator()).color(NamedTextColor.GRAY));
//                lore.add(Component.text("World: " + wp.getWorld()).color(NamedTextColor.GRAY));
//                lore.add(Component.text("XYZ: " + wp.getX() + ", " + wp.getY() + ", " + wp.getZ()).color(NamedTextColor.GRAY));
//                meta.lore(lore);
//
//                skull.setItemMeta(meta);
//            }
//
//            inv.addItem(skull);
//        }
//
//        // Add next button if more pages
//        if (page < totalPages - 1) {
//            ItemStack next = new ItemStack(NEXT_BUTTON);
//            inv.setItem(26, next); // bottom right corner
//        }
//
//        // Open GUI
//        player.openInventory(inv);
//    }

    public static void open(Player player, WaypointManager waypointManager, int page) {
        List<Waypoint> waypoints = new ArrayList<>(waypointManager.getAllWaypoints());
        if (waypoints.isEmpty()) {
            player.sendMessage(ChatColor.RED + "No waypoints available.");
            return;
        }

        int totalPages = (int) Math.ceil(waypoints.size() / (double) SLOTS_PER_PAGE);
        if (page >= totalPages) page = totalPages - 1;
        if (page < 0) page = 0;

        Inventory inv = Bukkit.createInventory(null, 9 * 3,
                "Waypoints (Page " + (page + 1) + "/" + totalPages + ")");

        int startIndex = page * SLOTS_PER_PAGE;
        int endIndex = Math.min(startIndex + SLOTS_PER_PAGE, waypoints.size());

        // Add waypoint items
        for (int i = startIndex; i < endIndex; i++) {
            Waypoint wp = waypoints.get(i);

            Material icon = getWorldIcon(wp.getWorld());
            ItemStack item = new ItemStack(icon);

            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.displayName(Component.text(wp.getName()));

                List<Component> lore = new ArrayList<>();
                lore.add(Component.text("Owner: " + wp.getCreator()));
                lore.add(Component.text("World: " + wp.getWorld()));
                lore.add(Component.text("XYZ: " + wp.getX() + ", " + wp.getY() + ", " + wp.getZ()));
                meta.lore(lore);

                item.setItemMeta(meta);
            }

            inv.addItem(item);
        }

        // Add Next button
        if (page < totalPages - 1) {
            ItemStack next = new ItemStack(Material.ARROW);
            ItemMeta meta = next.getItemMeta();
            if (meta != null) {
                meta.displayName(Component.text("Next Page"));
                next.setItemMeta(meta);
            }
            inv.setItem(26, next);
        }

        // Add Previous button
        if (page > 0) {
            ItemStack prev = new ItemStack(Material.ARROW);
            ItemMeta meta = prev.getItemMeta();
            if (meta != null) {
                meta.displayName(Component.text("Previous Page"));
                prev.setItemMeta(meta);
            }
            inv.setItem(18, prev);
        }

        player.openInventory(inv);
    }

    private static Material getWorldIcon(String worldName) {
        String lower = worldName.toLowerCase();

        if (lower.contains("nether")) {
            return Material.NETHER_BRICKS;
        } else if (lower.contains("end")) {
            return Material.END_STONE;
        } else if (lower.contains("rpg")) {
            return Material.REDSTONE_BLOCK;
        } else {
            return Material.OAK_SIGN; // default for normal worlds
        }
    }

}
