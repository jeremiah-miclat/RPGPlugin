package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import github.eremiyuh.rPGPlugin.buffs.PlayerStatBuff;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChangedMainHandEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class ArmorChangePlugin  implements Listener {
    private final PlayerStatBuff playerStatBuff;
    private final PlayerProfileManager profileManager;
    private final RPGPlugin plugin;

    public ArmorChangePlugin(PlayerProfileManager profileManager, RPGPlugin plugin) {
        this.profileManager = profileManager;
        this.playerStatBuff = new PlayerStatBuff(profileManager);
        this.plugin = plugin;
    }

    @EventHandler
    public void onArmorChange(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;

        Player player = (Player) event.getPlayer();
            player.sendMessage("You closed you inventory");
        playerStatBuff.updatePlayerStatsToRPG(player);
    }

    @EventHandler
    public void onWeaponChange(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        // Debug message to confirm the event is firing
        player.sendMessage("Item held changed to slot: " + event.getNewSlot());

        String worldName = Objects.requireNonNull(player.getLocation().getWorld()).getName();
        player.sendMessage("Current world: " + worldName);

        if (worldName.equals("world_rpg")) {
            ItemStack newItem = player.getInventory().getItem(event.getNewSlot());


            plugin.getServer().getScheduler().runTask(plugin, () -> {
                    playerStatBuff.updatePlayerStatsToRPG(player);
                });

        } if (worldName.equals("world")) {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                playerStatBuff.updatePlayerStatsToNormal(player);
            });
        }
    }
}
