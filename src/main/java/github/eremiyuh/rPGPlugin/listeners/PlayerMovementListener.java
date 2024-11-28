package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.entity.Player;

public class PlayerMovementListener implements Listener {
    private final RPGPlugin plugin;
    private final PlayerProfileManager profileManager;

    public PlayerMovementListener(RPGPlugin plugin, PlayerProfileManager profileManager) {
        this.plugin = plugin;
        this.profileManager = profileManager;
    }

    private boolean isNotLoggedIn(Player player) {
        UserProfile profile = profileManager.getProfile(player.getName());
        return profile != null && !profile.isLoggedIn();
    }


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (isNotLoggedIn(player)) {
            event.setCancelled(true);
            player.sendMessage("You must log in first using /login <password>.");
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (isNotLoggedIn(player)) {
            event.setCancelled(true);
            player.sendMessage("You must log in first using /login <password>.");
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (isNotLoggedIn(player)) {
            event.setCancelled(true);
            player.sendMessage("You must log in first to interact with your inventory.");
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (isNotLoggedIn(player)) {
            event.setCancelled(true);
            player.sendMessage("You must log in first to drop items.");
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (isNotLoggedIn(player)) {
            event.setCancelled(true);
            player.sendMessage("You must log in first to place blocks.");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (isNotLoggedIn(player)) {
            event.setCancelled(true);
            player.sendMessage("You must log in first to break blocks.");
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (isNotLoggedIn(player)) {
            event.setCancelled(true);
            player.sendMessage("You must log in first to chat.");
        }
    }

    @EventHandler
    public void onItemSwap(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (isNotLoggedIn(player)) {
            event.setCancelled(true);
            player.sendMessage("You must log in first to swap items.");
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        UserProfile profile = profileManager.getProfile(playerName);

        // Check if the player is not logged in
        if ((profile == null || !profile.isLoggedIn())) {
            // Get the command entered by the player
            String command = event.getMessage().toLowerCase();

            // If the command is not /login or /register, cancel it
            if (!command.startsWith("/login") && !command.startsWith("/register")) {
                event.setCancelled(true);
                player.sendMessage("§cYou must log in first! Use /login <password> or /register <password> <password>.");
            }
        }
    }

}
