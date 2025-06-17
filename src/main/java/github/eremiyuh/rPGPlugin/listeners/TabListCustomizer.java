package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class TabListCustomizer {

    private final RPGPlugin plugin;
    private final PlayerProfileManager profileManager;

    public TabListCustomizer(RPGPlugin plugin, PlayerProfileManager profileManager) {
        this.plugin = plugin;
        this.profileManager = profileManager;
        startTabListUpdater();
    }

    // Start a repeating task to update the tab list every second
    private void startTabListUpdater() {
        new BukkitRunnable() {
            @Override
            public void run() {
                updateTabList();
            }
        }.runTaskTimer(plugin, 0L, 20L); // Updates every second (20 ticks)
    }

    private void updateTabList() {
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        int onlinePlayersCount = Bukkit.getOnlinePlayers().size();
        // Recalculate memory usage
        long totalMemory = Runtime.getRuntime().totalMemory() / (1024 * 1024); // in MB
        long maxMemory = Runtime.getRuntime().maxMemory() / (1024 * 1024); // in MB
        long freeMemory = Runtime.getRuntime().freeMemory() / (1024 * 1024); // in MB
        long usedMemory = totalMemory - freeMemory; // used memory in MB

        // Set memory usage color (red if more than 80% used)
        TextColor memoryColor = (usedMemory > (maxMemory * 0.8)) ? TextColor.fromHexString("#FF0000") : TextColor.fromHexString("#FFFFFF");

        // Sort players by name (alphabetically)
        onlinePlayers.sort((player1, player2) -> player1.getName().compareToIgnoreCase(player2.getName()));

        // Set header and footer dynamically (set once for all players)
        TextComponent header = Component.newline().append(Component.text("                                §eYeye's Server                                ")
                        .color(TextColor.fromCSSHexString("#53ff1a")).append(Component.newline())).append(Component.newline()).append(Component.text("Online Players: " + onlinePlayersCount).color(TextColor.fromCSSHexString("#00ff55")))
                .append(Component.newline());
        TextComponent footer =
                Component.newline().append(Component.newline())
                        .append(Component.text(String.format("Memory Usage: %d MB / %d MB", usedMemory, maxMemory))
                                .color(memoryColor)).append(Component.newline()).append(Component.newline())
                        .append(Component.text("                                §eDiscord: discord.gg/tNJrWP4E5W                           ")
                                .color(TextColor.fromCSSHexString("#53ff1a"))).decorate(TextDecoration.ITALIC)
                        .clickEvent(ClickEvent.openUrl("https://discord.gg/tNJrWP4E5W"))
                        .hoverEvent(HoverEvent.showText(Component.text("Click to join our Discord!")))
        .append(Component.newline());

        // Set the header and footer for all players once
        for (Player player : onlinePlayers) {
            player.sendPlayerListHeaderAndFooter(header, footer);
        }


        for (Player player : onlinePlayers) {
            TextComponent rank = Component.text(getPlayerRank(player));  // Get the player's rank
            TextComponent health = Component.text("HP: " + (int) player.getHealth());  // Get player's health as a string
            TextComponent world = Component.text(player.getWorld().toString());



            Component formattedName = formatPlayerName(player, rank, health, world);


            player.playerListName(formattedName);
        }
    }

    /**
     * Retrieve a player's rank and allocate colors dynamically.
     *
     * @param player The player to get the rank for
     * @return Formatted player rank string
     */
    private String getPlayerRank(Player player) {
        UserProfile profile = profileManager.getProfile(player.getName());
        String profileClass = profile.getChosenClass();
        String profileClassPoints = "";

        switch (profileClass.toLowerCase()) {
            case "alchemist":
                profileClassPoints = String.valueOf(profile.getTotalAlchemistAllocatedPoints());
                break;
            case "archer":
                profileClassPoints = String.valueOf(profile.getTotalArcherAllocatedPoints());
                break;
            case "swordsman":
                profileClassPoints = String.valueOf(profile.getTotalSwordsmanAllocatedPoints());
                break;
            default:
                profileClassPoints = "0";
                break;
        }

        return " - " + profileClass + " ";
//                + profileClassPoints;
    }

    /**
     * Formats the player name with colors based on their rank and health.
     *
     * @param player The player to format
     * @param rank   The player's rank
     * @param health The player's health
     * @return The formatted player name
     */
    private Component formatPlayerName(Player player, TextComponent rank, TextComponent health, TextComponent world) {
        // Determine color based on the player's rank
        TextColor color = TextColor.fromHexString("#ffffff");
        UserProfile userProfile = profileManager.getProfile(player.getName());

        // Green for swordsman, red for alchemist, blue for archer
        color = switch (userProfile.getChosenClass().toLowerCase()) {
            case "alchemist" -> TextColor.fromHexString("#ff1a1a");  // Red for alchemist
            case "archer" -> TextColor.fromHexString("#0066ff");  // Blue for archer
            case "swordsman" -> TextColor.fromHexString("#00ff55");
            default -> color;
        };

        // Construct the formatted player name component
        return Component.text()
                .append(Component.text(player.getName()).color(color))
                .append(Component.text(rank.content()).color(color))

//                .append(health)
                .build();
    }
}
