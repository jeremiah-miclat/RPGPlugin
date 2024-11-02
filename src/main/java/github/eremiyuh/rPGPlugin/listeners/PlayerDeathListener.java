package github.eremiyuh.rPGPlugin.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

public class PlayerDeathListener implements Listener {

    private final String resourceWorldName = "world_resource"; // Name of the world where item loss is allowed

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        event.getDrops().clear();
        // Check if the player is in a world other than "world_resource"
        if (!Objects.requireNonNull(player.getLocation().getWorld()).getName().equals(resourceWorldName)) {
            event.setKeepInventory(true);
            // Optionally, you can store the items or restore them to the player's inventory
            player.sendMessage("You will not lose your items in this world.");
        }
    }
}
