package github.eremiyuh.rPGPlugin.manager;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class TpAllowManager {
    private final RPGPlugin plugin;
    private File tpAllowFile;
    private YamlConfiguration tpAllowConfig;

    public TpAllowManager(RPGPlugin plugin) {
        this.plugin = plugin;
        this.tpAllowFile = new File(plugin.getDataFolder(), "tpallowfile.yml");
        this.tpAllowConfig = YamlConfiguration.loadConfiguration(tpAllowFile);
    }

    // Add player to the allowed list
    public void allowTeleport(Player player, String targetPlayerName) {
        String playerName = player.getName();

        // Check if the player already has a list, if not create a new one
        if (!tpAllowConfig.contains(playerName)) {
            tpAllowConfig.createSection(playerName);
        }

        // Get current allowed list (if any)
        List<String> allowedPlayers = tpAllowConfig.getStringList(playerName);

        // Add the target player to the list if not already added
        if (!allowedPlayers.contains(targetPlayerName)) {
            allowedPlayers.add(targetPlayerName);
            tpAllowConfig.set(playerName, allowedPlayers);
            player.sendMessage(ChatColor.GREEN + "Player " + targetPlayerName + " has been added to your auto tp allow list.");
        } else {
            player.sendMessage(ChatColor.YELLOW + "Player " + targetPlayerName + " is already on your auto tp allow list.");
        }

        // Save the config file after changes
        saveConfig();
    }

    // Check if a player is allowed to teleport to another
    public boolean isPlayerAllowedToTeleport(String playerName, String targetPlayerName) {
        // Check if the player has a list
        if (!tpAllowConfig.contains(playerName)) {
            return false; // Player has no allow list
        }

        List<String> allowedPlayers = tpAllowConfig.getStringList(playerName);
        return allowedPlayers.contains(targetPlayerName);
    }

    // List the allowed players for the player
    public void listAllowedPlayers(Player player) {
        String playerName = player.getName();

        // Get the allowed list for the player
        List<String> allowedPlayers = tpAllowConfig.getStringList(playerName);

        if (allowedPlayers.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Your teleport allow list is empty.");
        } else {
            StringBuilder message = new StringBuilder(ChatColor.GREEN + "Your allowed teleport list: ");
            for (String targetPlayer : allowedPlayers) {
                message.append(targetPlayer).append(", ");
            }
            // Remove the last comma and space
            message.setLength(message.length() - 2);
            player.sendMessage(message.toString());
        }
    }

    // Remove a player from the allow list
    public void removeAllowedPlayer(Player player, String targetPlayerName) {
        String playerName = player.getName();

        // Get current allowed list (if any)
        List<String> allowedPlayers = tpAllowConfig.getStringList(playerName);

        // Check if the player is on the list
        if (allowedPlayers.contains(targetPlayerName)) {
            allowedPlayers.remove(targetPlayerName);
            tpAllowConfig.set(playerName, allowedPlayers);
            player.sendMessage(ChatColor.GREEN + "Player " + targetPlayerName + " has been removed from your auto tp allow list.");
            saveConfig();
        } else {
            player.sendMessage(ChatColor.RED + "Player " + targetPlayerName + " is not on your auto tp allow list.");
        }
    }

    // Save the configuration file after changes
    private void saveConfig() {
        try {
            tpAllowConfig.save(tpAllowFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
