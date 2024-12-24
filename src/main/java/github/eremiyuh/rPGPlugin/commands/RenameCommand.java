package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RenameCommand implements CommandExecutor {

    private final PlayerProfileManager profileManager;

    public RenameCommand(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by a player.");
            return false;
        }

        Player player = (Player) sender;
        UserProfile userProfile = profileManager.getProfile(player.getName());

        if (userProfile == null) return true;

        // Ensure the player has an item in hand
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand == null || itemInHand.getType().isAir()) {
            player.sendMessage("You must be holding an item.");
            return true;
        }

        // Check emerald cost
        if (userProfile.getCurrency("emerald") < 300) {
            player.sendMessage("You need at least 300 emeralds to rename an item.");
            return true;
        }

        if (args.length < 3 || args.length % 3 != 0) {
            player.sendMessage(ChatColor.RED + "Usage: /rename <name> <color> <format> [<name> <color> <format> ...]");
            player.sendMessage(ChatColor.YELLOW + "Available colors: black, dark_blue, dark_green, dark_aqua, dark_red, dark_purple, gold, gray, dark_gray, blue, green, aqua, red, light_purple, yellow, white");
            player.sendMessage(ChatColor.YELLOW + "Available formats: obfuscated, bold, strikethrough, underline, italic, normal");
            return true;
        }

        StringBuilder finalNameBuilder = new StringBuilder();

        for (int i = 0; i < args.length; i += 3) {
            String itemName = args[i].replace("_", " ");
            String color = args[i + 1].toLowerCase();
            String format = args[i + 2].toLowerCase();

            // Validate color
            ChatColor chatColor = getColorFromString(color);
            if (chatColor == null) {
                player.sendMessage(ChatColor.RED + "Invalid color: " + color);
                player.sendMessage(ChatColor.YELLOW + "Available colors: black, dark_blue, dark_green, dark_aqua, dark_red, dark_purple, gold, gray, dark_gray, blue, green, aqua, red, light_purple, yellow, white");
                return true;
            }

            // Validate format
            String formatCode = getFormat(format);
            if (formatCode == null) {
                player.sendMessage(ChatColor.RED + "Invalid format: " + format);
                player.sendMessage(ChatColor.YELLOW + "Available formats: obfuscated, bold, strikethrough, underline, italic, normal");
                return true;
            }

            // Append formatted name part
            finalNameBuilder.append(chatColor).append(formatCode).append(itemName);
        }

        String finalName = finalNameBuilder.toString();

        // Apply the name to the item
        ItemMeta meta = itemInHand.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(finalName);
            itemInHand.setItemMeta(meta);

            // Deduct emerald cost
            userProfile.setCurrency("emerald", userProfile.getCurrency("emerald") - 300);
            player.sendMessage(ChatColor.GREEN + "Item renamed to: " + finalName);
        }

        return true;
    }

    private String getFormat(String format) {
        switch (format) {
            case "obfuscated":
                return "§k";
            case "bold":
                return "§l";
            case "strikethrough":
                return "§m";
            case "underline":
                return "§n";
            case "italic":
                return "§o";
            case "normal": // Reset formatting
                return "§r";
            default:
                return null;
        }
    }

    private ChatColor getColorFromString(String color) {
        switch (color) {
            case "black":
                return ChatColor.BLACK;
            case "dark_blue":
                return ChatColor.DARK_BLUE;
            case "dark_green":
                return ChatColor.DARK_GREEN;
            case "dark_aqua":
                return ChatColor.DARK_AQUA;
            case "dark_red":
                return ChatColor.DARK_RED;
            case "dark_purple":
                return ChatColor.DARK_PURPLE;
            case "gold":
                return ChatColor.GOLD;
            case "gray":
                return ChatColor.GRAY;
            case "dark_gray":
                return ChatColor.DARK_GRAY;
            case "blue":
                return ChatColor.BLUE;
            case "green":
                return ChatColor.GREEN;
            case "aqua":
                return ChatColor.AQUA;
            case "red":
                return ChatColor.RED;
            case "light_purple":
                return ChatColor.LIGHT_PURPLE;
            case "yellow":
                return ChatColor.YELLOW;
            case "white":
                return ChatColor.WHITE;
            default:
                return null;
        }
    }
}
