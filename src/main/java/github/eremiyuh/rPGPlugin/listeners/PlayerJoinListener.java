package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.buffs.PlayerStatBuff;
import github.eremiyuh.rPGPlugin.buffs.VampireBuffs;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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

    // Helper method to determine if a block is solid
    private boolean isSolidBlock(Material material) {
        return material.isSolid() && material != Material.LAVA && material != Material.BEDROCK;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        UserProfile profile = profileManager.getProfile(playerName);

        Location playerLocation = player.getLocation();
        Material blockType = playerLocation.clone().subtract(0, 1, 0).getBlock().getType(); // Check the block below the player

        // Check if the player is in the air or standing on a non-solid block
        if (blockType == Material.AIR || !isSolidBlock(blockType)) {
            Location groundLocation = getGroundLocation(playerLocation, player);
            player.teleport(groundLocation);
        }

        playerStatBuff.updatePlayerStatsToNormal(player);

        if (Objects.requireNonNull(player.getLocation().getWorld()).getName().equals("world_rpg")) {
            playerStatBuff.updatePlayerStatsToRPG(player);
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

            if (Objects.requireNonNull(player.getLocation().getWorld()).getName().equals("world_rpg")) {
                playerStatBuff.updatePlayerStatsToRPG(player);
                player.sendMessage("on player join else listener");
            }

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
        String worldName = Objects.requireNonNull(event.getRespawnLocation().getWorld()).getName();

        playerStatBuff.updatePlayerStatsToNormal(player);

        // Check which world the player is respawning in
        if (worldName.equals("world_rpg")) {
            playerStatBuff.updatePlayerStatsToRPG(player);
        }
    }


    private Location getGroundLocation(Location location, Player player) {
        World world = location.getWorld();
        int centerX = location.getBlockX();
        int centerY = location.getBlockY();
        int centerZ = location.getBlockZ();

        // Check if the player is in the Nether
        if (world.getEnvironment() == World.Environment.NETHER) {
            // Check a 60x60 area around the player's location (adjusted from 20x20)
            for (int x = -30; x <= 30; x++) {
                for (int z = -30; z <= 30; z++) {
                    Location currentLocation = new Location(world, centerX + x, centerY - 1, centerZ + z);
                    Material blockType = currentLocation.getBlock().getType();

                    // Check if the block is solid but not bedrock
                    if (blockType.isSolid() && blockType != Material.BEDROCK) {
                        // Check two air blocks above
                        if (currentLocation.clone().add(0, 1, 0).getBlock().getType() == Material.AIR &&
                                currentLocation.clone().add(0, 2, 0).getBlock().getType() == Material.AIR) {
                            // Return location one block above the found solid block
                            return currentLocation.add(0, 1, 0); // Adjust Y coordinate to be on top of the solid block
                        }
                    }
                }
            }
        } else {
            // If not in the Nether, find the highest solid block at the player's current location
            for (int y = world.getMaxHeight() - 1; y >= 0; y--) {
                Location currentLocation = new Location(world, centerX, y, centerZ);
                Material blockType = currentLocation.getBlock().getType();

                // Check if the block is solid
                if (blockType.isSolid()) {
                    // Return location one block above the highest solid block
                    return currentLocation.add(0, 1, 0);
                }
            }
        }

        // If no valid ground location found, return the world's spawn location instead of null
        return world.getSpawnLocation(); // Always returns a valid location
    }






}
