package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.RPGPlugin;
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
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class PlayerJoinListener implements Listener {

    private final PlayerProfileManager profileManager;
    private final PlayerStatBuff playerStatBuff;

    private final List<String> suggestions = Arrays.asList(
            "Ready for abyss?",
            "Team up now.",
            "Throw your junk into /junk and sell them for diamonds on /junkshop.",
            "Visit a shop now using /tpshop.",
            "Hunt mighty bosses for useless rewards",
            "Earn Abyss Points and shop at /abyssstore.",
            "Boost your stats using /addstat to dominate battles.",
            "Try other maps now! /warp",
            "Want the resource maps seed? /rwseed",
            "May your adventures be epic and your loot plentiful!",
            "Good luck, adventurer! The world is full of challenges and treasures awaiting you.",
            "Step boldly into the unknown!",
            "Gear up and dive in! May fortune favor your journey.",
            "The abyss awaits! Strength and cunning will be your allies.",
            "Good luck, adventurer! May you find allies, treasures, and glory.",
            "May your swords stay sharp and your potions plentiful. Adventure awaits!",
            "Every great story starts with a single step. Begin yours!",
            "Welcome, hero! The legends of this server are waiting for your name to be etched into them.",
            "Your destiny calls! May luck and skill guide you."
    );

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

        World world = Bukkit.getWorld("world");
        assert world != null;
        Location spawnLocation = world.getSpawnLocation();

        if (profile == null || profile.getPassword() == null || profile.getPassword().isEmpty()) {
            // Handle new players
            if (profile == null) {
                profileManager.createProfile(playerName); // Create a new profile
            }

            player.teleport(spawnLocation);
//            player.sendTitle("§0§l§k⚜§r§6§lWelcome Adventurer!",
//                    "§7Use /register <password> <password> to start your adventure",
//                    10, 200, 20);
//            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
//                    new TextComponent("§c§lHeed the Call: Register to Enter!"));
            player.sendMessage("§6[§Server§6] §7Welcome, adventurer! Your profile hath been forged.");
//            player.sendMessage("§6[§Server§6] §7Enter the command §e/register <password> <password>§7 to inscribe your credentials.");
//            player.sendMessage("§6[§Server§6] §7Then enter the realm by using §e/login <password>§7.");

            event.setJoinMessage(ChatColor.GOLD + "⚜ Hail, " + ChatColor.AQUA + playerName + ChatColor.GOLD +
                    "! Welcome to the realm for the very first time!");
        } else {
            // Handle returning players
            String suggestion = suggestRandomThing();
            player.sendMessage("§6[§eServer§6] §7Welcome back adventurer! Your profile has been loaded.");
            profile.setLoggedIn(false);
//            player.sendMessage("§6[§Server§6] §cLog in to continue your quest.");
//            player.sendMessage("§6[§Server§6] §cEnter /login <your password>");
//            player.sendTitle("§0§l§k⚜§r§6§lWelcome Back!",
//                    "§7Use /login <password> to continue your journey.",
//                    10, 100, 20);
//            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
//                    new TextComponent("§7Use /login <password> to proceed."));
            event.setJoinMessage(ChatColor.DARK_GREEN + "⚔ Welcome back, " + ChatColor.AQUA + playerName +". " +
                    ChatColor.DARK_GREEN + suggestion);
        }

        // Ensure the player is teleported to solid ground if necessary
        Location playerLocation = player.getLocation();
        Material blockType = playerLocation.clone().subtract(0, 1, 0).getBlock().getType();
        if (blockType == Material.AIR || !isSolidBlock(blockType)) {
            Location groundLocation = getGroundLocation(playerLocation, player);
            player.teleport(groundLocation);
        }




        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = board.registerNewObjective(
                "HEALTH", Criteria.HEALTH, ChatColor.RED + "❤ "
        );
        objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        player.setScoreboard(board);


        // Apply appropriate buffs
        playerStatBuff.updatePlayerStatsToNormal(player);
        String worldName = Objects.requireNonNull(player.getLocation().getWorld()).getName();
        if (worldName.equals("world_rpg") || worldName.contains("labyrinth")) {
            playerStatBuff.updatePlayerStatsToRPG(player);
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



    private String suggestRandomThing() {
        Random random = new Random();
        return suggestions.get(random.nextInt(suggestions.size()));
    }



}
