package github.eremiyuh.rPGPlugin.commands;

import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class HealthScale implements CommandExecutor {

    // Constant for the displayed health to represent 10 hearts
    private static final double DISPLAY_HEALTH = 20.0;

    private final JavaPlugin plugin;

    public HealthScale(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private void scalePlayerHealth(Player player) {
        // Get the player's actual max health value
        double actualMaxHealth = Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();

        player.sendMessage("Your max health. " + actualMaxHealth);
        player.sendMessage("Your health is . " + player.getHealth());
        double scaleFactor = 20;
        player.setHealthScale(scaleFactor);
        player.setHealthScaled(true);

        // Send a message to the player (optional)
        player.sendMessage("Your health display has been scaled to show 10 hearts.");
    }

    // Method to reset player health display to normal
    private void resetPlayerHealthScale(Player player) {
        player.setHealthScaled(false);
        player.sendMessage("Your health display has been reset to default.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            // Check if scaling is currently enabled
            if (player.isHealthScaled()) {
                resetPlayerHealthScale(player);
            } else {
                scalePlayerHealth(player);
            }
            return true;
        } else {
            sender.sendMessage("Only players can use this command.");
            return false;
        }
    }

}
