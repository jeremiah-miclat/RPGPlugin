package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import github.eremiyuh.rPGPlugin.buffs.PlayerStatBuff;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.*;
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

import java.util.ArrayList;
import java.util.List;

public class WorldSwitchCommand implements CommandExecutor, Listener {

    private final RPGPlugin plugin;
    private final PlayerStatBuff playerStatBuff;
    private final PlayerProfileManager profileManager;

    public WorldSwitchCommand(RPGPlugin plugin, PlayerStatBuff playerStatBuff, PlayerProfileManager profileManager) {
        this.plugin = plugin;
        this.playerStatBuff = playerStatBuff;

        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        UserProfile userProfile = profileManager.getProfile(player.getName());

        int playerEP = userProfile.getEnderPearl();


        if (args.length < 1) {
            openWorldSwitchGUI(player);
            return true;
        }

        if (args.length < 1) {
            player.sendMessage("/warp a or ad or o | a for abyss | ad for abyss dungeon | o for Overworld");
            return false;
        }

        String worldType = args[0].toLowerCase();
        String targetWorldName;

        switch (worldType) {
            case "a":
                targetWorldName = "world_rpg"; // abyss overworld
                break;
            case "o":
                targetWorldName = "world"; // overworld
                break;
//            case "e":
//                targetWorldName = "world_the_end";
//                break;
//            case "n":
//                targetWorldName = "world_nether";
//                break;
            case "ad":
                targetWorldName = "world_labyrinth2"; // abyss dungeon
                break;
            case "ro":
                targetWorldName = "resource_normal"; // resource overworld
                break;
            case "rn":
                targetWorldName = "resource_nether"; // resource nether
                break;
            case "re":
                targetWorldName = "resource_end"; // resource end
                break;
            default:
                player.sendMessage("/warp a or ad or o | a for abyss | ad for abyss dungeon | o for Overworld");
                return false;
        }

        if (player.getWorld().getName().equals(targetWorldName)) {
            player.sendMessage("You are already in the world.");
            return true;
        }

        World world = plugin.getServer().getWorld(targetWorldName);
        if (world == null) {

            player.sendMessage("Failed to load the world ");
            return true;


        }

        if (playerEP < 1) {
            player.sendMessage("Ender Pearl currency required. Get ender pearl and enter /convertmaterial enderpearl");
            return true;
        }

        Location spawnLocation = world.getSpawnLocation();

        if (targetWorldName.contains("resource")



        ) {

            if ( !(spawnLocation.getBlock().isSolid()
                    && spawnLocation.getBlock().getType() != Material.BEDROCK
                    && spawnLocation.clone().add(0, 1, 0).getBlock().getType() == Material.AIR
                    && spawnLocation.clone().add(0, 2, 0).getBlock().getType() == Material.AIR))
            {
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
        }



        if (player.teleport(spawnLocation)) {
            userProfile.setEnderPearl(playerEP-1);
        } else {
            player.sendMessage(ChatColor.RED + "Failed to teleport");
        }

        playerStatBuff.updatePlayerStatsToNormal(player);

        if (targetWorldName.contains("rpg") ||targetWorldName.contains("labyrinth")) {
            playerStatBuff.updatePlayerStatsToRPG(player);
        }



        return true;
    }

    private void openWorldSwitchGUI(Player player) {
        // Create an inventory with 9 slots (adjust size as needed)
        Inventory gui = Bukkit.createInventory(null, 9, "Select World to Teleport");

        // Create Ender Pearl items for each world
        ItemStack abyssItem = createWorldSwitchItem("Abyss", "a");
        ItemStack abyssDungeonItem = createWorldSwitchItem("Abyss Dungeon", "ad");
        ItemStack overworldItem = createWorldSwitchItem("Overworld", "o");
        ItemStack resourceOverworldItem = createWorldSwitchItem("Resource Overworld", "ro");
        ItemStack resourceNetherItem = createWorldSwitchItem("Resource Nether", "rn");
        ItemStack resourceEndItem = createWorldSwitchItem("Resource End", "re");

        // Set the items in the inventory
        gui.setItem(0, abyssItem);
        gui.setItem(1, abyssDungeonItem);
        gui.setItem(2, overworldItem);
        gui.setItem(6, resourceOverworldItem);
        gui.setItem(7, resourceNetherItem);
        gui.setItem(8, resourceEndItem);

        // Open the GUI for the player
        player.openInventory(gui);
    }



    private ItemStack createWorldSwitchItem(String displayName, String worldType) {
        ItemStack item = new ItemStack(Material.ENDER_PEARL);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + displayName);
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Click to teleport to the " + displayName);
            lore.add(ChatColor.YELLOW + "/warp " + worldType);
            lore.add(ChatColor.YELLOW + "Cost: enderpearl (currency)");

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
                teleportPlayerToWorld(player, "world_rpg", "Abyss Overworld"); // Abyss
                break;
            case 1:
                teleportPlayerToWorld(player, "world_labyrinth2", "Abyss Dungeon"); // Abyss Dungeon
                break;
            case 2:
                teleportPlayerToWorld(player, "world", "Overworld"); // Overworld
                break;
            case 6:
                teleportPlayerToWorld(player, "resource_normal", "Resource Overworld"); // Resource Overworld
                break;
            case 7:
                teleportPlayerToWorld(player, "resource_nether", "Resource Nether"); // Resource Nether
                break;
            case 8:
                teleportPlayerToWorld(player, "resource_end", "Resource End"); // Resource End
                break;
            default:
                player.sendMessage(ChatColor.RED + "Invalid selection.");
                break;
        }
    }

    private void teleportPlayerToWorld(Player player, String worldName, String displayName) {
        UserProfile profile = profileManager.getProfile(player.getName());

        if (profile.getEnderPearl() < 1) {
            player.sendMessage(ChatColor.RED + "Failed to teleport. Tip: /convertmaterial enderpearl or /checkstatus");
            return;
        }

        World world = plugin.getServer().getWorld(worldName);



        if (world != null) {
            Location spawnLocation = world.getSpawnLocation();

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





}
