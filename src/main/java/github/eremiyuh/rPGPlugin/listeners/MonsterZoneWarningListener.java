package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.UUID;

public class MonsterZoneWarningListener implements Listener {

    private final PlayerProfileManager profileManager;

    // Stores player UUIDs and their last known zone level
    private final HashMap<UUID, Integer> lastZoneLevel = new HashMap<>();

    // Stores last warning time to prevent spam
    private final HashMap<UUID, Long> lastWarningTime = new HashMap<>();
    private final long cooldownMillis = 10 * 1000; // 30 seconds

    public MonsterZoneWarningListener(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // Only react to horizontal movement
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
                event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        UUID uuid = player.getUniqueId();
        UserProfile profile = profileManager.getProfile(player.getName());
        if (profile == null) return;

        int playerLevel = profile.getLevel();

        // Determine monster level from horizontal coordinates
        int x = player.getLocation().getBlockX();
        int z = player.getLocation().getBlockZ();
        int monsterLevel = Math.max(Math.abs(x), Math.abs(z)) / 100;

        // Check if this is a new zone
        if (lastZoneLevel.containsKey(uuid) && lastZoneLevel.get(uuid) == monsterLevel) {
            return; // same zone, no need to warn again
        }

        // Cooldown check
        long now = System.currentTimeMillis();
        if (lastWarningTime.containsKey(uuid) && now - lastWarningTime.get(uuid) < cooldownMillis) {
            return; // still in cooldown
        }

        // Update zone and cooldown
        lastZoneLevel.put(uuid, monsterLevel);
        lastWarningTime.put(uuid, now);

        int levelDiff = monsterLevel - playerLevel;

        // Warnings based purely on level difference
        if (levelDiff > 30) {
            player.sendMessage(ChatColor.RED + "These monsters (level " + monsterLevel + ") are too powerful. You will not receive any drops while you're only level " + playerLevel + ".");
        } else if (levelDiff > 12) {
            player.sendMessage(ChatColor.RED + "Warning: These monsters are much stronger than you. Drop rewards will be heavily reduced.");
        } else if (levelDiff > 10) {
            player.sendMessage(ChatColor.YELLOW + "Caution: These monsters are slightly stronger. Drop rewards will be slightly reduced.");
        } else if (playerLevel - monsterLevel > 30) {
            player.sendMessage(ChatColor.RED + "These monsters are far too weak. No drop rewards will be given.");
        } else if (playerLevel - monsterLevel > 12) {
            player.sendMessage(ChatColor.RED + "Warning: These monsters are significantly below your level. Drop rewards will be heavily reduced.");
        } else if (playerLevel - monsterLevel > 10) {
            player.sendMessage(ChatColor.YELLOW + "Caution: These monsters are slightly below your level. Drop rewards will be slightly reduced.");
        }
    }
}
