package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.manager.WaypointManager;
import github.eremiyuh.rPGPlugin.methods.WaypointGUI;
import github.eremiyuh.rPGPlugin.utils.Waypoint;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class WaypointClickListener implements Listener {

    private final WaypointManager waypointManager;

    public WaypointClickListener(WaypointManager waypointManager) {
        this.waypointManager = waypointManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;

        if (!event.getView().getTitle().startsWith("Waypoints")) return;

        event.setCancelled(true); // Prevent taking items

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        Player player = (Player) event.getWhoClicked();

        ItemMeta meta = clicked.getItemMeta();
        // Handle Next button
        if (clicked.getType() == Material.ARROW && meta != null && meta.displayName() != null) {
            String name = PlainTextComponentSerializer.plainText().serialize(meta.displayName());
            String title = event.getView().getTitle();
            int currentPage = Integer.parseInt(title.split("Page ")[1].split("/")[0]) - 1;

            if (name.equalsIgnoreCase("Next Page")) {
                WaypointGUI.open(player, waypointManager, currentPage + 1);
            } else if (name.equalsIgnoreCase("Previous Page")) {
                WaypointGUI.open(player, waypointManager, currentPage - 1);
            }
            return;
        }

        // Handle waypoint teleport
        if (clicked == null || meta == null || meta.displayName() == null) return;

// Convert Component to plain text
        String wpName = PlainTextComponentSerializer.plainText().serialize(meta.displayName());

// Find the waypoint by name
        Waypoint wp = waypointManager.getAllWaypoints().stream()
                .filter(w -> w.getName().equalsIgnoreCase(wpName))
                .findFirst()
                .orElse(null);

        if (wp == null) {
            player.sendMessage("§cWaypoint no longer exists.");
            player.closeInventory();
            return;
        }

// Teleport safely
        Location loc = wp.toLocation();
        if (loc == null || loc.getWorld() == null) {
            player.sendMessage("§cFailed to teleport. Waypoint location is invalid.");
            return;
        }

        player.teleport(loc.add(0, 0, 0));

        // Send a chat message (optional)
        player.sendMessage("§aTeleported to waypoint §e" + wp.getName());

// Send a title on screen
        player.sendTitle(
                "§aWaypoint Reached!",          // Title text
                "§e" + wp.getName(),            // Subtitle text (waypoint name)
                10, 70, 20                      // FadeIn, Stay, FadeOut in ticks
        );

        player.closeInventory();



    }

}

