package github.eremiyuh.rPGPlugin.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class RWSeedCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        // List of resource world names and descriptions
        List<String> worldNames = Arrays.asList("resource_normal", "resource_nether", "resource_end");
        List<String> worldDescriptions = Arrays.asList(
                "A standard overworld for resource gathering.",
                "The Nether dimension for rare resources.",
                "The End dimension for unique treasures."
        );

        // Message header
        player.sendMessage(Component.text("=== Resource World Seeds ===")
                .color(NamedTextColor.GOLD)
                .decorate(TextDecoration.BOLD));

        // Generate clickable seed messages
        for (int i = 0; i < worldNames.size(); i++) {
            String worldName = worldNames.get(i);
            String description = worldDescriptions.get(i);

            String worldCodeName = "";

            if (worldName.equals("resource_normal")) worldCodeName = "ro";
            if (worldName.equals("resource_nether")) worldCodeName = "rn";
            if (worldName.equals("resource_end")) worldCodeName = "re";

            World world = Bukkit.getWorld(worldName);
            if (world != null) {
                long seed = world.getSeed();



                // Create clickable world name
                Component worldNameComponent = Component.text(worldName)
                        .color(NamedTextColor.GREEN)
                        .hoverEvent(HoverEvent.showText(Component.text(description)))
                        .clickEvent(ClickEvent.runCommand("/warp " + worldCodeName));

                // Create clickable seed
                Component seedComponent = Component.text(String.valueOf(seed))
                        .color(NamedTextColor.AQUA)
                        .hoverEvent(HoverEvent.showText(Component.text("Click to copy the seed!")))
                        .clickEvent(ClickEvent.copyToClipboard(String.valueOf(seed)));

                // Combine world name and seed
                Component message = Component.text("• ")
                        .color(NamedTextColor.GRAY)
                        .append(worldNameComponent)
                        .append(Component.text(" Seed: ").color(NamedTextColor.WHITE))
                        .append(seedComponent);

                player.sendMessage(message);
            } else {
                player.sendMessage(Component.text("• " + worldName + " is not loaded.")
                        .color(NamedTextColor.RED));
            }
        }

        // Footer instructions
        player.sendMessage(Component.text("Hover over the names to see descriptions. ")
                .color(NamedTextColor.GRAY)
                .append(Component.text("Click the name to teleport, or the seed to copy."))
                .color(NamedTextColor.YELLOW)
                .decorate(TextDecoration.ITALIC));

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null; // No tab completion needed for this command
    }
}
