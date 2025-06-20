package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DiscordLinkCommand implements CommandExecutor {

    private final RPGPlugin plugin;

    public DiscordLinkCommand(RPGPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        String discordLink = "https://discord.gg/5h695G2Edx";

        // Create the clickable and hoverable message
        Component discordMessage = Component.text("Click here to join our Discord!")
                .color(NamedTextColor.AQUA)
                .decorate(TextDecoration.BOLD)
                .hoverEvent(HoverEvent.showText(Component.text("Join our Discord community!").color(NamedTextColor.GREEN)))
                .clickEvent(ClickEvent.openUrl(discordLink));

        Component prefix = Component.text("[Discord] ")
                .color(NamedTextColor.BLUE)
                .decorate(TextDecoration.BOLD);

        Component finalMessage = prefix.append(discordMessage);

        // Send the formatted message to the player
        player.sendMessage(finalMessage);
        return true;
    }
}
