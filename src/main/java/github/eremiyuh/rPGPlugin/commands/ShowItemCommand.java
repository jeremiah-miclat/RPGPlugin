package github.eremiyuh.rPGPlugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

import java.util.HashMap;
import java.util.UUID;

public class ShowItemCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final HashMap<UUID, ItemStack> cachedItems = new HashMap<>();

    public ShowItemCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand == null || itemInHand.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "You must hold an item to use this command!");
            return true;
        }

        // Cache the item the player is holding
        cachedItems.put(player.getUniqueId(), itemInHand.clone());

        // Create a clickable chat message
        Component message = Component.text(ChatColor.GOLD + player.getName() + " says: " + ChatColor.YELLOW + "Check my ")
                .append(Component.text(ChatColor.AQUA + "item")
                        .clickEvent(ClickEvent.runCommand("/showitemgui " + player.getUniqueId())));

        // Send the message to all players
        Bukkit.broadcast(message);

        return true;
    }

    public void registerShowItemGuiCommand() {
        plugin.getCommand("showitemgui").setExecutor((sender, command, label, args) -> {
            if (args.length != 1) {
                sender.sendMessage(ChatColor.RED + "Invalid usage! This command is internal.");
                return true;
            }

            UUID targetUuid;
            try {
                targetUuid = UUID.fromString(args[0]);
            } catch (IllegalArgumentException e) {
                sender.sendMessage(ChatColor.RED + "Invalid player identifier.");
                return true;
            }

            // Retrieve the cached item
            ItemStack cachedItem = cachedItems.get(targetUuid);
            if (cachedItem == null || cachedItem.getType() == Material.AIR) {
                sender.sendMessage(ChatColor.RED + "The player's item is no longer available.");
                return true;
            }

            // Create a GUI to display the cached item
            Inventory gui = Bukkit.createInventory(null, InventoryType.CHEST, ChatColor.AQUA + "Player's Item");
            gui.setItem(13, cachedItem); // Center the item in the GUI

            if (sender instanceof Player) {
                Player player = (Player) sender;
                player.openInventory(gui);
            }

            return true;
        });
    }
}
