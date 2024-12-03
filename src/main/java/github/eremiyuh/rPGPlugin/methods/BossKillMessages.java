package github.eremiyuh.rPGPlugin.methods;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BossKillMessages {

    // Minecraft messages (with colors and symbols)
    private static final List<String> MC_BOSS_KILL_MESSAGES = Arrays.asList(
            ChatColor.GOLD + "🏆 [Seizon SMP] %player% has slain the mighty %boss%! Legends will speak of this day!",
            ChatColor.RED + "⚔️ [Seizon SMP] Against all odds, %player% has conquered the fearsome %boss%!",
            ChatColor.BLUE + "🌟 [Seizon SMP] %boss%, the terror of the lands, has been defeated by %player%! Glory to the hero!",
            ChatColor.LIGHT_PURPLE + "🎉 [Seizon SMP] A great victory! %player% has brought down %boss% and claimed the spoils!",
            ChatColor.YELLOW + "🔥 [Seizon SMP] The battle is over, and %boss% has fallen to %player%. Balance is restored to the realm."
    );

    // Discord messages (plain text, suitable for Markdown)
    private static final List<String> DISCORD_BOSS_KILL_MESSAGES = Arrays.asList(
            "**🏆 [Seizon SMP] %player% has slain the mighty %boss%! Legends will speak of this day!**",
            "**⚔️ [Seizon SMP] Against all odds, %player% has conquered the fearsome %boss%!**",
            "**🌟 [Seizon SMP] %boss%, the terror of the lands, has been defeated by %player%! Glory to the hero!**",
            "**🎉 [Seizon SMP] A great victory! %player% has brought down %boss% and claimed the spoils!**",
            "**🔥 [Seizon SMP] The battle is over, and %boss% has fallen to %player%. Balance is restored to the realm.**"
    );

    // Method to broadcast a random message to both Minecraft and Discord
    public static void broadcastBossKill(String playerName, String bossName) {
        Random random = new Random();

        // Select random Minecraft message
        String mcMessage = MC_BOSS_KILL_MESSAGES.get(random.nextInt(MC_BOSS_KILL_MESSAGES.size()));
        mcMessage = mcMessage.replace("%player%", playerName).replace("%boss%", bossName);

        // Select random Discord message
        String discordMessage = DISCORD_BOSS_KILL_MESSAGES.get(random.nextInt(DISCORD_BOSS_KILL_MESSAGES.size()));
        discordMessage = discordMessage.replace("%player%", playerName).replace("%boss%", bossName);

        // Trim Minecraft-specific formatting for Discord
        String cleanedDiscordMessage = cleanForDiscord(discordMessage);

        // Broadcast the Minecraft message
        Bukkit.broadcastMessage(mcMessage);

        // Send the cleaned message to Discord
        sendMessageToDiscord(cleanedDiscordMessage);
    }


    // Method to send the message to Discord
    private static void sendMessageToDiscord(String message) {
        // Check if DiscordSRV is loaded
        if (DiscordSRV.getPlugin() != null) {
            // Get the default channel for Discord (you can specify a channel ID if needed)
            TextChannel channel = DiscordSRV.getPlugin().getJda().getTextChannelById("1312945610969841705");

            if (channel != null) {
                // Send the message to the Discord channel
                channel.sendMessage(message).queue();
            } else {
                Bukkit.getLogger().warning("Discord channel not found. Please check your channel ID.");
            }
        } else {
            Bukkit.getLogger().warning("DiscordSRV is not available on this server.");
        }
    }

    // Method to remove color codes and unwanted placeholders
    private static String cleanForDiscord(String message) {
        // Remove Minecraft color codes (§x) using a regex
        message = message.replaceAll("§[0-9a-fk-or]", "");

        // Remove placeholders like [0] using another regex
        message = message.replaceAll("\\[\\d+\\]", "");

        return message;
    }
}