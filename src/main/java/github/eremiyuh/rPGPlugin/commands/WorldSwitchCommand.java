package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import github.eremiyuh.rPGPlugin.buffs.PlayerStatBuff;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static org.bukkit.Bukkit.getServer;

public class WorldSwitchCommand implements CommandExecutor, Listener {

    private final RPGPlugin plugin;
    private final PlayerStatBuff playerStatBuff;
    private final PlayerProfileManager profileManager;
    private final Map<UUID, Long> lastBossRewardTime = new HashMap<>();
    public WorldSwitchCommand(RPGPlugin plugin, PlayerStatBuff playerStatBuff, PlayerProfileManager profileManager) {
        this.plugin = plugin;
        this.playerStatBuff = playerStatBuff;

        this.profileManager = profileManager;
    }



    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        // No args: open GUI
        if (args.length == 0) {
            openWorldSwitchGUI(player);
            return true;
        }

        String input = args[0].toLowerCase();

// ==== /warp a ====
        if (input.equals("a")) {
            teleportPlayerToWorld(player, "world_rpg", "Adventure World");
            return true;
        }

// ==== /warp o ====
        if (input.equals("o")) {
            teleportPlayerToWorld(player, "world", "Overworld");
            return true;
        }

        // ==== /warp a<number> ====
        if (input.matches("a\\d+")) {
            int num = Integer.parseInt(input.substring(1));
            if (num % 10 == 0 && num >= 10 && num <= 300) {
                String folderWorldName = "world_rpg_br_" + num;
                String displayName = "Abyss Warzone " + num;

                // Load world if missing
                if (Bukkit.getWorld(folderWorldName) == null) {
                    loadWorld(folderWorldName, -1, -1, -1, -1, -1, 0, 0, 150, 18000,
                            GameRule.DO_DAYLIGHT_CYCLE, false,
                            World.Environment.NORMAL, null);
                }

                // Use existing teleport function
                teleportPlayerToWorld(player, folderWorldName, displayName);
                return true;
            }
        }

        player.sendMessage(ChatColor.RED + "Invalid warp command.");
        return true;
    }






    private void openWorldSwitchGUI(Player player) {
        // Create an inventory with 27 slots (3 rows of 9)
        Inventory gui = Bukkit.createInventory(null, 27, "Select World to Teleport");

        // Create Ender Pearl items for each world
        ItemStack abyssItem = createWorldSwitchItem("Abyss", "a", true);
//        ItemStack abyssDungeonItem = createWorldSwitchItem("Abyss Dungeon", "ad", true);
        ItemStack overworldItem = createWorldSwitchItem("Overworld", "o", false);
//        ItemStack resourceOverworldItem = createWorldSwitchItem("Resource Overworld", "ro", false);
//        ItemStack resourceNetherItem = createWorldSwitchItem("Resource Nether", "rn", false);
//        ItemStack resourceEndItem = createWorldSwitchItem("Resource End", "re", false);

        // Set the items in the inventory
        gui.setItem(0, overworldItem);
//        gui.setItem(1, resourceOverworldItem);
//        gui.setItem(2, resourceNetherItem);
//        gui.setItem(3, resourceEndItem);
        // Abyss and Abyss Dungeon should be at the bottom row (6th, 7th, and 8th slots)
        gui.setItem(18, abyssItem);
//        gui.setItem(19, abyssDungeonItem);

        // Open the GUI for the player
        player.openInventory(gui);
    }



    private ItemStack createWorldSwitchItem(String displayName, String worldType, boolean isDifficult) {
        ItemStack item = new ItemStack(Material.ENDER_PEARL);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + displayName);
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Click to teleport to the " + displayName);
            lore.add(ChatColor.YELLOW + "/warp " + worldType);
            lore.add(ChatColor.YELLOW + "Cost: Ender Pearl (currency)");

            // Add difficulty information if the world is difficult
            if (isDifficult) {
                lore.add(ChatColor.RED + "WARNING: This world is difficult!");
                lore.add(ChatColor.RED + "Ensure your gear is fully enchanted before entering.");
            }

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getView().getTitle().equals("Select World to Teleport")) return;

        event.setCancelled(true); // Prevent the player from moving items in the inventory

        int slot = event.getSlot();

        // Handle teleportation based on the clicked slot
        switch (slot) {
            case 0:
                teleportPlayerToWorld(player, "world", "Overworld"); // Overworld
                break;
//            case 1:
//                teleportPlayerToWorld(player, "resource_normal", "Resource Overworld"); // Resource Overworld
//                break;
//            case 2:
//                teleportPlayerToWorld(player, "resource_nether", "Resource Nether"); // Resource Nether
//                break;
//            case 3:
//                teleportPlayerToWorld(player, "resource_end", "Resource End"); // Resource End
//                break;
            case 18:
                teleportPlayerToWorld(player, "world_rpg", "Abyss Overworld"); // Abyss
                break;
//            case 19:
//                teleportPlayerToWorld(player, "world_labyrinth2", "Abyss Dungeon"); // Abyss Dungeon
//                break;
            default:
                player.sendMessage(ChatColor.RED + "Invalid selection.");
                break;
        }
    }


    private void teleportPlayerToWorld(Player player, String worldName, String displayName) {
        UserProfile profile = profileManager.getProfile(player.getName());

        if (profile.getEnderPearl() < 1) {
            player.sendMessage(ChatColor.RED + "Failed to teleport. Tip: /convertmaterial enderpearl or /status");
            return;
        }

        World world = plugin.getServer().getWorld(worldName);



        if (world != null) {
            Location spawnLocation = world.getSpawnLocation();
            if (worldName.startsWith("world_rpg_br_")) {
                spawnLocation = getRandomWarzoneSpawn(world);
            }

            if (worldName.contains("resource")
                    &&
                    !(spawnLocation.getBlock().isSolid()
                            && spawnLocation.getBlock().getType() != Material.BEDROCK
                            && spawnLocation.clone().add(0, 1, 0).getBlock().getType() == Material.AIR
                            && spawnLocation.clone().add(0, 2, 0).getBlock().getType() == Material.AIR)
            ) {
                spawnLocation = spawnLocation.add(10,0,10);
                // Loop through a 3x3x3 cube around the spawn location
                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {
                        for (int z = -1; z <= 1; z++) {
                            assert spawnLocation != null;
                            Location blockLocation = spawnLocation.clone().add(x, y, z);

                            // Set to obsidian if it's not above the spawn
                            if (y <= 0) {
                                if (world.getEnvironment() == World.Environment.NETHER) blockLocation.getBlock().setType(Material.NETHERRACK);
                                if (world.getEnvironment() == World.Environment.NORMAL) blockLocation.getBlock().setType(Material.GRASS_BLOCK);
                                if (world.getEnvironment() == World.Environment.THE_END) blockLocation.getBlock().setType(Material.END_STONE);
                            }
                        }
                    }
                }

                // Clear the two blocks above the spawn location
                spawnLocation.clone().add(0, 1, 0).getBlock().setType(Material.AIR);
                spawnLocation.clone().add(0, 2, 0).getBlock().setType(Material.AIR);
                spawnLocation = spawnLocation.add(0,1,0);
            }



            if (player.teleport(spawnLocation)) {
                if (!worldName.contains("rpg") && !worldName.contains("labyrinth")) {
                    playerStatBuff.updatePlayerStatsToNormal(player);
                }
                if (worldName.contains("rpg") || worldName.contains("labyrinth")) {
                    playerStatBuff.updatePlayerStatsToRPG(player);
                }
                profile.setEnderPearl(profile.getEnderPearl()-1);
                player.sendMessage(ChatColor.GREEN + "Teleported to " + displayName);
            } else {
                player.sendMessage(ChatColor.RED + "Failed to teleport.");
            }
        } else {
            player.sendMessage(ChatColor.RED + "World not found.");
        }
    }


    private void loadWorld(String worldName, int sx, int sy, int sz, int syaw,  int spitch ,int bcx, int bcz, int bs, long time, GameRule<Boolean> rule, boolean grValue, World.Environment env, Biome biome) {
        // Check if the world is already loaded
        World world = getServer().getWorld(worldName);
        if (world == null) {
            world = getServer().createWorld(new WorldCreator(worldName).environment(env));
            assert world != null;

            // Set the biome for a region (example: 100x100 area at coordinates (0, 64, 0))
            if (biome != null) {
                for (int x = 0; x < 100; x++) {
                    for (int z = 0; z < 100; z++) {
                        // Assuming y is set at 64 (you might want to adjust this depending on your needs)
                        world.setBiome(x, 64, z, biome);
                    }
                }
            }

            world.setGameRule(GameRule.SPAWN_RADIUS, 0);
            if (world == null) {

                return;
            }

        } else {
            world.setGameRule(GameRule.SPAWN_RADIUS, 0);

        }

        if (sx != -1) {
            Location spawnLocation = new Location(
                    Bukkit.getWorld(worldName), // Specify the world
                    sx, sy, sz,             // Coordinates
                    syaw, spitch                     // Yaw (90° East), Pitch (0° level)
            );
//            world.setSpawnLocation(spawnLocation);
        }

        if (bcx != -1) {
            WorldBorder worldBorder = world.getWorldBorder();
            worldBorder.setCenter(bcx, bcz);
            worldBorder.setSize(bs);
        }

        if (rule != null) {
            world.setGameRule(rule, grValue);
            world.setTime(time);
        }
    }

    private Location getRandomWarzoneSpawn(World world) {
        WorldBorder border = world.getWorldBorder();
        double halfSize = border.getSize() / 2.0;
        Location center = border.getCenter();

        Random rand = new Random();

        // Choose which side of the border: 0 = north, 1 = south, 2 = west, 3 = east
        int side = rand.nextInt(4);

        double x = center.getX();
        double z = center.getZ();

        double offset = 1 + rand.nextInt(5); // 1–5 blocks inside the border
        double randomWithin = rand.nextDouble() * (border.getSize() - (offset * 2)) - (border.getSize() / 2 - offset);

        switch (side) {
            case 0: // north
                z = center.getZ() - halfSize + offset;
                x = center.getX() + randomWithin;
                break;
            case 1: // south
                z = center.getZ() + halfSize - offset;
                x = center.getX() + randomWithin;
                break;
            case 2: // west
                x = center.getX() - halfSize + offset;
                z = center.getZ() + randomWithin;
                break;
            case 3: // east
                x = center.getX() + halfSize - offset;
                z = center.getZ() + randomWithin;
                break;
        }

        // Find highest solid block at X,Z
        int y = world.getHighestBlockYAt((int) x, (int) z);
        Location spawn = new Location(world, x + 0.5, y, z + 0.5);

        // Ensure block is safe
        if (spawn.getBlock().getType().isSolid()) {
            spawn.add(0, 1, 0); // Move above solid block
        }

        return spawn;
    }


}
