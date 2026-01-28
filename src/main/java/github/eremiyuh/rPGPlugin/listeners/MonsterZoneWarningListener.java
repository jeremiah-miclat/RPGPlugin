package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;


import java.util.HashMap;
import java.util.UUID;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class MonsterZoneWarningListener implements Listener {

    private final PlayerProfileManager profileManager;

    // Stores player UUIDs and their last known zone level
    private final HashMap<UUID, Integer> lastZoneLevel = new HashMap<>();

    // Stores last warning time to prevent spam
    private final HashMap<UUID, Long> lastWarningTime = new HashMap<>();
    private final long cooldownMillis = 10 * 1000; // 10 seconds

    public MonsterZoneWarningListener(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        Player player = event.getPlayer();
        if (player.getWorld().getName().contains("_br")) return;
        if (!player.getWorld().getName().contains("_rpg")) return;
        // Only react to horizontal movement
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
                event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        UUID uuid = player.getUniqueId();
        UserProfile profile = profileManager.getProfile(player.getName());
        if (profile == null) return;

        if (!profile.isBossIndicator()) return;

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
        String msg = null;

        if (levelDiff > 30) {
            msg = "§c⚠ Too strong! No drops (Mob Lv " + monsterLevel + " | You Lv " + playerLevel + ")";
        } else if (levelDiff > 12) {
            msg = "§c⚠ Much stronger! Drops heavily reduced";
        } else if (levelDiff > 10) {
            msg = "§e⚠ Slightly stronger. Drops reduced";
        } else if (playerLevel - monsterLevel > 30) {
            msg = "§c⚠ Too weak! Drops greatly reduced";
        } else if (playerLevel - monsterLevel > 12) {
            msg = "§c⚠ Far below your level. Drops heavily reduced";
        } else if (playerLevel - monsterLevel > 10) {
            msg = "§e⚠ Slightly below your level. Drops reduced";
        }

        if (msg != null) {
            player.spigot().sendMessage(
                    ChatMessageType.ACTION_BAR,
                    new TextComponent(msg)
            );
        }
    }
}
