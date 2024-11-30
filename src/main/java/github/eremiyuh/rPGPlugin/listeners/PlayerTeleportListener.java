package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import github.eremiyuh.rPGPlugin.buffs.PlayerStatBuff;
import github.eremiyuh.rPGPlugin.commands.FlyCommand;
import io.papermc.paper.event.entity.EntityPortalReadyEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.entity.Player;

import java.util.Objects;

public class PlayerTeleportListener implements Listener {

    private final PlayerStatBuff playerStatBuff;
    private final FlyCommand flyCommand; // Reference to the FlyCommand
    private  final RPGPlugin plugin;

    public PlayerTeleportListener(PlayerStatBuff playerStatBuff, FlyCommand flyCommand, RPGPlugin plugin) {
        this.playerStatBuff = playerStatBuff;
        this.flyCommand = flyCommand; // Initialize the FlyCommand reference
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        // Handle flying status when changing worlds
        flyCommand.onPlayerWorldChange(player); // Call the method to handle fly status
        playerStatBuff.updatePlayerStatsToNormal(player);
        if (player.getWorld().getName().contains("rpg") || player.getWorld().getName().contains("labyrinth")) {
            playerStatBuff.updatePlayerStatsToRPG(player);
        }



    }

    @EventHandler
    public void onPortalEvent(EntityPortalEvent event) {
        if (!isRestrictedWorld(event.getEntity().getWorld().getName())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPortalEvent(EntityPortalEvent event) {
        if (!isRestrictedWorld(event.getEntity().getWorld().getName())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPortalPlayerEvent(EntityPortalReadyEvent event) {
        if (!isRestrictedWorld(event.getEntity().getWorld().getName())) return;
        event.setCancelled(true);
    }

    // Utility method to check if the world name is restricted
    private boolean isRestrictedWorld(String worldName) {
        return worldName.contains("resource") || worldName.contains("labyrinth") || worldName.contains("rpg");
    }
}
