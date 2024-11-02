package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.buffs.PlayerStatBuff;
import github.eremiyuh.rPGPlugin.commands.FlyCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.entity.Player;

import java.util.Objects;

public class PlayerTeleportListener implements Listener {

    private final PlayerStatBuff playerStatBuff;
    private final FlyCommand flyCommand; // Reference to the FlyCommand

    public PlayerTeleportListener(PlayerStatBuff playerStatBuff, FlyCommand flyCommand) {
        this.playerStatBuff = playerStatBuff;
        this.flyCommand = flyCommand; // Initialize the FlyCommand reference
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        // Get the world from the player's new location
        String worldName = Objects.requireNonNull(Objects.requireNonNull(event.getTo()).getWorld()).getName();
        player.sendMessage("Moved to " + worldName);
        // Update player's stats based on the world they are entering
        if (worldName.equals("world_rpg")) {
            playerStatBuff.updatePlayerStatsToRPG(player); // Entering RPG world
        } if (worldName.equals("world"))  {
            playerStatBuff.updatePlayerStatsToNormal(player); // Entering Normal world
        }

        // Handle flying status when changing worlds
        flyCommand.onPlayerWorldChange(player); // Call the method to handle fly status
    }
}
