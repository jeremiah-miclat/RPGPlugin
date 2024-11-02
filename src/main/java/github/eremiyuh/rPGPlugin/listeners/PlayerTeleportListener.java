package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.buffs.PlayerStatBuff;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.entity.Player;

import java.util.Objects;

public class PlayerTeleportListener implements Listener {

    private final PlayerStatBuff playerStatBuff;

    public PlayerTeleportListener(PlayerStatBuff playerStatBuff) {
        this.playerStatBuff = playerStatBuff;
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        // Get the world from the player's new location
        String worldName = Objects.requireNonNull(event.getTo()).getWorld().getName();

        // Update player's stats based on the world they are entering
        if (worldName.equals("world_rpg")) {
            playerStatBuff.updatePlayerStatsToRPG(player); // Entering RPG world
        } else {
            playerStatBuff.updatePlayerStatsToNormal(player); // Entering Normal world
        }
    }
}
