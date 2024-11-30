package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.buffs.PlayerStatBuff;
import github.eremiyuh.rPGPlugin.buffs.VampireBuffs;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
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


        if (profileManager.getProfile(playerName).getPassword().isEmpty()) {
            World world = Bukkit.getWorld("world");
            assert world != null;
            Location spawnLocation = world.getSpawnLocation();
            player.teleport(world.getSpawnLocation());

            // If the player is not logged in, trigger the blackout effect
            if (!profileManager.getProfile(playerName).isLoggedIn()) {
                // Make the player's screen black with a message
                player.sendTitle("§0§l§k⚜§r§6§lWelcome Adventurer!", "§7Use /register <password> <password> to start your adventure", 10, 200, 20);

                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§c§lHeed the Call: Register to Enter!"));

            }
            player.sendMessage("§6[§eSeizonSMP§6] §7Welcome, adventurer! Your profile hath been forged.");
            player.sendMessage("§6[§eSeizonSMP§6] §7Enter the command §e/register <password> <password>§7 to inscribe your credentials.");
            player.sendMessage("§6[§eSeizonSMP§6] §7Then enter the realm by using §e/login <password>§7.");

            event.setJoinMessage(ChatColor.GOLD + "⚜ Hail, " + ChatColor.AQUA + player.getName() + ChatColor.GOLD +
                    "! Welcome to the realm for the very first time!");

            return;
        }

        UserProfile profile = profileManager.getProfile(playerName);
        player.sendMessage("§6[§eSeizonSMP§6] §7Welcome back adventurer! Your profile has been loaded.");


        Location playerLocation = player.getLocation();
        Material blockType = playerLocation.clone().subtract(0, 1, 0).getBlock().getType(); // Check the block below the player

// Check if the player is in the air or standing on a non-solid block
        if (blockType == Material.AIR || !isSolidBlock(blockType)) {
            Location groundLocation = getGroundLocation(playerLocation, player);
            player.teleport(groundLocation);
        }

        playerStatBuff.updatePlayerStatsToNormal(player);

        if (Objects.requireNonNull(player.getLocation().getWorld()).getName().equals("world_rpg") ||
                player.getLocation().getWorld().getName().contains("labyrinth")) {
            playerStatBuff.updatePlayerStatsToRPG(player);
        }

        profile.setLoggedIn(false);

        player.sendMessage("§6[§eSeizonSMP§6] §cLog in to continue your quest.");
        player.sendTitle("§0§l§k⚜§r§6§lWelcome Back!", "§7Use /login <password> to continue your journey.", 10, 100, 20);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§7Use /login <password> to proceed."));


        event.setJoinMessage(ChatColor.DARK_GREEN + "⚔ Welcome back, " + ChatColor.AQUA + player.getName() +
                ChatColor.DARK_GREEN + ", please raid the abyss!");

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
