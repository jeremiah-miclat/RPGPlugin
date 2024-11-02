package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.buffs.PlayerStatBuff;
import github.eremiyuh.rPGPlugin.buffs.VampireBuffs;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Objects;

public class PlayerJoinListener implements Listener {

    private final PlayerProfileManager profileManager;
    private final PlayerStatBuff playerStatBuff;

    public PlayerJoinListener(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
        this.playerStatBuff = new PlayerStatBuff(profileManager);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        UserProfile profile = profileManager.getProfile(playerName);

        // Ensure player spawns on solid ground
        Location spawnLocation = player.getLocation();
        spawnLocation = getGroundLocation(spawnLocation);
        player.teleport(spawnLocation);

        if (Objects.requireNonNull(spawnLocation.getWorld()).getName().equals("world_rpg")) {
            playerStatBuff.updatePlayerStatsToRPG(player);
        } else {
            playerStatBuff.updatePlayerStatsToNormal(player);
        }

        // If no profile was found, create a new one
        if (profile == null) {
            profileManager.createProfile(playerName);
            player.sendMessage("Welcome! Your profile has been created.");
        } else {
            // If profile exists, just load it (it should already be loaded)
            player.sendMessage("Welcome back! Your profile has been loaded.");
            player.sendMessage("Your chosen class is: " + profile.getChosenClass());
            player.sendMessage("Your chosen element is: " + profile.getSelectedElement());
            player.sendMessage("Your chosen race is: " + profile.getSelectedRace());

            playerStatBuff.onClassSwitchOrAttributeChange(player);

            // Apply race-specific effects based on the player's race
            String race = profile.getSelectedRace();
            switch (race) {
                case "vampire":
                    VampireBuffs.applyVampireSpeedBoost(player, true); // Apply speed boost to vampires
                    player.sendMessage("You are a vampire! Speed boost has been applied.");
                    break;
                case "human":
                case "elf":
                case "orc":
                case "dwarf":
                case "angel":
                case "demon":
                case "darkelf":
                    VampireBuffs.applyVampireSpeedBoost(player, false); // Reset to normal speed
                    break;
                default:
                    player.sendMessage("No race effects applied.");
                    break;
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        // Ensure player spawns on solid ground
        Location respawnLocation = event.getRespawnLocation();
        respawnLocation = getGroundLocation(respawnLocation);
        event.setRespawnLocation(respawnLocation);

        // Get the world from the player's location directly via the event
        String worldName = Objects.requireNonNull(respawnLocation.getWorld()).getName();

        // Check which world the player is respawning in
        if (worldName.equals("world_rpg")) {
            playerStatBuff.updatePlayerStatsToRPG(player);
        } else {
            playerStatBuff.updatePlayerStatsToNormal(player);
        }
    }

    private Location getGroundLocation(Location location) {
        // Find the highest block at the given location
        Location groundLocation = location.getWorld().getHighestBlockAt(location).getLocation();
        // Set the Y coordinate to the top of the highest block
        groundLocation.setY(groundLocation.getY() + 1);

        // Ensure it's a solid block below to avoid spawning inside of blocks
        if (groundLocation.getBlock().getType() == Material.AIR) {
            groundLocation.setY(groundLocation.getY() - 1);
        }

        return groundLocation;
    }
}
