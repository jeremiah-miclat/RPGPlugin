package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.buffs.PlayerStatBuff;
import github.eremiyuh.rPGPlugin.buffs.VampireBuffs;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
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

        if (Objects.requireNonNull(player.getLocation().getWorld()).getName().equals("world_rpg") ||
                player.getLocation().getWorld().getName().contains("labyrinth") ) {
            playerStatBuff.updatePlayerStatsToRPG(player);
        }

        // If no profile was found, create a new one
        if (profile == null) {
            World world = Bukkit.getWorld("world");
            assert world != null;
            Location spawnLocation = world.getSpawnLocation();
            player.teleport(world.getSpawnLocation());
            profileManager.createProfile(playerName);
            // If the player is not logged in, trigger the blackout effect
            if (!profileManager.getProfile(playerName).isLoggedIn() && !player.isOp()) {
                // Make the player's screen black with a message
                player.sendTitle("§0§lYou must log in", "§7Please use /login <password> or register using /register <password> <password>", 10, 70, 20); // Title + Subtitle

                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§cYou must log in first"));
                event.setJoinMessage(""); // Optional: Hide the join message for non-logged-in players
            }
            player.sendMessage("Welcome! Your profile has been created.");
        } else {
            // If profile exists, just load it (it should already be loaded)
            player.sendMessage("Welcome back! Your profile has been loaded.");


            if (Objects.requireNonNull(player.getLocation().getWorld()).getName().equals("world_rpg") || Objects.requireNonNull(player.getLocation().getWorld()).getName().equals("world_labyrinth")) {
                playerStatBuff.updatePlayerStatsToRPG(player);

            }

            // Apply race-specific effects based on the player's race
//            String race = profile.getSelectedRace();
//            switch (race) {
//                case "vampire":
//                    VampireBuffs.applyVampireSpeedBoost(player, true); // Apply speed boost to vampires
//                    player.sendMessage("You are a vampire! Speed boost has been applied.");
//                    break;
//                case "human":
//                case "elf":
//                case "orc":
//                case "dwarf":
//                case "angel":
//                case "demon":
//                case "darkelf":
//                    VampireBuffs.applyVampireSpeedBoost(player, false); // Reset to normal speed
//                    break;
//                default:
//                    player.sendMessage("No race effects applied.");
//                    break;
//            }

            if (!profileManager.getProfile(playerName).isLoggedIn()&& !player.isOp()) {
                // Make the player's screen black with a message
                player.sendTitle("§7lYou must log in!", "§7Please use /login <password>", 10, 70, 20); // Title + Subtitle

                // Apply an ActionBar message to prompt login as well
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§7You must log in first!"));

            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        World world = event.getRespawnLocation().getWorld();

        // Update player stats based on the respawn world
        playerStatBuff.updatePlayerStatsToNormal(player);

        // Check which world the player is respawning in
        assert world != null;
        String worldName = world.getName();
        if (worldName.equals("world_rpg") || worldName.equals("world_labyrinth")) {
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
            // If not in the Nether, search in a radius of 5
            for (int x = -5; x <= 5; x++) {
                for (int z = -5; z <= 5; z++) {
                    for (int y = world.getMaxHeight() - 1; y >= 0; y--) {
                        Location currentLocation = new Location(world, centerX + x, y, centerZ + z);
                        Material blockType = currentLocation.getBlock().getType();

                        // Check if the block is solid or water
                        if (blockType.isSolid() || blockType == Material.WATER) {
                            // Check if there are two air blocks above
                            if (currentLocation.clone().add(0, 1, 0).getBlock().getType() == Material.AIR &&
                                    currentLocation.clone().add(0, 2, 0).getBlock().getType() == Material.AIR) {
                                // Return location one block above the block
                                return currentLocation.add(0, 1, 0);
                            }
                        }
                    }
                }
            }
        }

        // If no valid ground location found, return the world's spawn location instead of null
        return world.getSpawnLocation(); // Always returns a valid location
    }







}
