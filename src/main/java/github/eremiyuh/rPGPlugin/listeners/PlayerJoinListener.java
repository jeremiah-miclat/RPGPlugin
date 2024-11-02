package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.buffs.PlayerStatBuff;
import github.eremiyuh.rPGPlugin.buffs.VampireBuffs;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
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
        String playerName = event.getPlayer().getName();
        UserProfile profile = profileManager.getProfile(playerName);
        if (Objects.requireNonNull(player.getLocation().getWorld()).getName().equals("world_rpg")) {
            playerStatBuff.updatePlayerStatsToRPG(player);
        } else {
            playerStatBuff.updatePlayerStatsToNormal(player);
        }
        // If no profile was found, create a new one
        if (profile == null) {
            profileManager.createProfile(playerName);
            event.getPlayer().sendMessage("Welcome! Your profile has been created.");
        } else {

            // If profile exists, just load it (it should already be loaded)
            event.getPlayer().sendMessage("Welcome back! Your profile has been loaded.");

            // Optionally display the player's chosen class or any other information
            event.getPlayer().sendMessage("Your chosen class is: " + profile.getChosenClass());
            event.getPlayer().sendMessage("Your chosen element is: " + profile.getSelectedElement());
            event.getPlayer().sendMessage("Your chosen race is: " + profile.getSelectedRace());

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
        // Get the world from the player's location directly via the event
        String worldName = Objects.requireNonNull(Objects.requireNonNull(event.getRespawnLocation()).getWorld()).getName();

        // Check which world the player is respawning in
        if (worldName.equals("world_rpg")) {
            playerStatBuff.updatePlayerStatsToRPG(player);
        } else {
            playerStatBuff.updatePlayerStatsToNormal(player);
        }
    }

}
