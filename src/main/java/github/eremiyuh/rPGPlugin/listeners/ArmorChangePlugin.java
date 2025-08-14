package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import github.eremiyuh.rPGPlugin.buffs.PlayerStatBuff;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChangedMainHandEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

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
        UserProfile profile = profileManager.getProfile(player.getName());
            if (profile.isAscending()) {
                profile.setAscending(false);
                player.sendMessage("Equip Ascension disabled");
            }



        String worldName = Objects.requireNonNull(player.getLocation().getWorld()).getName();

        if (worldName.contains("world_rpg")) {
            if (player.getHealth() <= 0) return;
            try {
                playerStatBuff.updatePlayerStatsToRPG(player);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            try {
                if (player.getHealth() <= 0) return;
                playerStatBuff.updatePlayerStatsToNormal(player);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

//        new BukkitRunnable() {
//            @Override
//            public void run() {
//
//            }
//        }.runTaskLater(plugin, 1);
    }


    @EventHandler
    public void onWeaponChange(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        String worldName = Objects.requireNonNull(player.getLocation().getWorld()).getName();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.getHealth() <= 0) return;

                try {
                    if (worldName.contains("world_rpg")) {
                        UserProfile profile = profileManager.getProfile(player.getName());
                        ItemStack item = player.getInventory().getItemInMainHand();

                        if (item.getType() != Material.AIR && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                            String displayName = item.getItemMeta().getDisplayName();

                            if (displayName.contains("S1") || displayName.toLowerCase().contains("skill 1")) {
                                profile.setSelectedSkill("Skill 1");
                                player.sendMessage("Switched to Skill 1");
                            }

                            if (displayName.contains("S2") || displayName.toLowerCase().contains("skill 2")) {
                                profile.setSelectedSkill("Skill 2");
                                player.sendMessage("Switched to Skill 2");
                            }

                            if (displayName.contains("S3") || displayName.toLowerCase().contains("skill 3")) {
                                profile.setSelectedSkill("Skill 3");
                                player.sendMessage("Switched to Skill 3");
                            }

                            if (displayName.toLowerCase().contains("fire")) {
                                profile.setSelectedElement("fire");
                                player.sendMessage("Switched to fire element");
                            }

                            if (displayName.toLowerCase().contains("ice")) {
                                profile.setSelectedElement("ice");
                                player.sendMessage("Switched to ice element");
                            }

                            if (displayName.toLowerCase().contains("water")) {
                                profile.setSelectedElement("water");
                                player.sendMessage("Switched to water element");
                            }
                        }

                        // ✅ Always update RPG stats, even if no skill/element is changed
                        playerStatBuff.updatePlayerStatsToRPG(player);

                    } else {
                        playerStatBuff.updatePlayerStatsToNormal(player);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskLater(plugin, 1);
    }

}
