package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class LoginCommand implements CommandExecutor {
    private final RPGPlugin plugin;
    private final PlayerProfileManager profileManager;

    public LoginCommand(RPGPlugin plugin, PlayerProfileManager profileManager) {
        this.plugin = plugin;
        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        UserProfile profile = profileManager.getProfile(player.getName());

        if (profile.getPassword().isEmpty()) {
            player.sendMessage("New account. You must register first. Enter: /register <password> <password>");
        }

        if (profile.isLoggedIn()) {
            player.sendMessage("You are already logged in.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("Usage: /login <password>");
            return true;
        }

        String password = args[0];
        if (profile.getPassword().equals(password)) {
            profile.setLoggedIn(true);
            player.sendMessage("Login successful!");
        } else {
            player.sendMessage("Incorrect password. Please try again.");
            return true;
        }

//        // List of resource world names and descriptions
//        List<String> worldNames = Arrays.asList("resource_normal", "resource_nether", "resource_end");
//        List<String> worldDescriptions = Arrays.asList(
//                "A standard overworld for resource gathering.",
//                "The Nether dimension for rare resources.",
//                "The End dimension for unique treasures."
//        );
//
//        // Message header
//        player.sendMessage(Component.text("=== Resource World Seeds Today ===")
//                .color(NamedTextColor.GOLD)
//                .decorate(TextDecoration.BOLD));
//
//        // Generate clickable seed messages
//        for (int i = 0; i < worldNames.size(); i++) {
//            String worldName = worldNames.get(i);
//            String description = worldDescriptions.get(i);
//
//            String worldCodeName = "";
//
//            if (worldName.equals("resource_normal")) worldCodeName = "ro";
//            if (worldName.equals("resource_nether")) worldCodeName = "rn";
//            if (worldName.equals("resource_end")) worldCodeName = "re";
//
//            World world = Bukkit.getWorld(worldName);
//            if (world != null) {
//                long seed = world.getSeed();
//
//
//                // Create clickable world name
//                Component worldNameComponent = Component.text(worldName)
//                        .color(NamedTextColor.GREEN)
//                        .hoverEvent(HoverEvent.showText(Component.text(description)))
//                        .clickEvent(ClickEvent.runCommand("/warp " + worldCodeName));
//
//                // Create clickable seed
//                Component seedComponent = Component.text(String.valueOf(seed))
//                        .color(NamedTextColor.AQUA)
//                        .hoverEvent(HoverEvent.showText(Component.text("Click to copy the seed!")))
//                        .clickEvent(ClickEvent.copyToClipboard(String.valueOf(seed)));
//
//                // Combine world name and seed
//                Component message = Component.text("• ")
//                        .color(NamedTextColor.GRAY)
//                        .append(worldNameComponent)
//                        .append(Component.text(" Seed: ").color(NamedTextColor.WHITE))
//                        .append(seedComponent);
//
//                player.sendMessage(message);
//
//            }
//        }
//        player.sendMessage(Component.text("Hover over the names to see descriptions. ")
//                .color(NamedTextColor.GRAY)
//                .append(Component.text("Click the name to teleport, or the seed to copy. Cost: 1 enderpearl"))
//                .color(NamedTextColor.YELLOW)
//                .decorate(TextDecoration.ITALIC));
        return true;
    }
}

