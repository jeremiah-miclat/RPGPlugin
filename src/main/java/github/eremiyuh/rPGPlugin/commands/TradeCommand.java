package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.utils.TradeOffer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class TradeCommand implements CommandExecutor {

    private final HashMap<UUID, TradeOffer> activeTrades;
    private final JavaPlugin plugin;

    // Constant for trade expiration time in ticks (3 minutes)
    private static final long TRADE_EXPIRY_TIME = 3600L; // 3 minutes = 3600 ticks

    public TradeCommand(HashMap<UUID, TradeOffer> activeTrades, JavaPlugin plugin) {
        this.activeTrades = activeTrades;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return false;
        }

        Player senderPlayer = (Player) sender;

        // Check for correct number of arguments
        if (args.length != 5) {
            senderPlayer.sendMessage("Usage: /tm <playerName> <materialNameToTrade> <amountToTrade> <materialNameToReceive> <amountToReceive>");
            return false;
        }

        // Retrieve and validate the recipient player
        Player recipient = Bukkit.getPlayer(args[0]);
        if (recipient == null || !recipient.isOnline()) {
            senderPlayer.sendMessage("The specified player is not online.");
            return false;
        }

        if (recipient.getName().equals(senderPlayer.getName())) {
            senderPlayer.sendMessage("You forgot to drink your meds.");
        }

        // Check if sender already has an active trade
        if (activeTrades.containsKey(senderPlayer.getUniqueId())) {
            senderPlayer.sendMessage("You already have an active trade.");
            return false;
        }

        // Parse and validate the materials to trade
        Material materialToTrade = Material.matchMaterial(args[1].toUpperCase());
        Material materialToReceive = Material.matchMaterial(args[3].toUpperCase());
        if (materialToTrade == null || materialToReceive == null) {
            senderPlayer.sendMessage("Invalid material specified. Use correct material names. Enter /iteminfo while holding your item to check.");
            return false;
        }

        // Parse and validate the trade amounts
        int amountToTrade;
        int amountToReceive;
        try {
            amountToTrade = Integer.parseInt(args[2]);
            amountToReceive = Integer.parseInt(args[4]);
            if (amountToTrade <= 0 || amountToReceive <= 0) {
                senderPlayer.sendMessage("Amount must be greater than zero.");
                return false;
            }
        } catch (NumberFormatException e) {
            senderPlayer.sendMessage("Amount to trade and amount to receive must be valid numbers.");
            return false;
        }

        // Check if sender has enough items to trade
        if (!hasEnoughItems(senderPlayer, materialToTrade, amountToTrade)) {
            senderPlayer.sendMessage("You do not have enough " + materialToTrade.name() + " to complete this trade.");
            return false;
        }

        // Create the trade offer and add it to active trades
        TradeOffer tradeOffer = new TradeOffer(senderPlayer, recipient, materialToTrade, amountToTrade, materialToReceive, amountToReceive);
        activeTrades.put(recipient.getUniqueId(), tradeOffer);

        // Notify both players
        senderPlayer.sendMessage("Trade offer sent to " + recipient.getName() + ". Waiting for response...");
        recipient.sendMessage(senderPlayer.getName() + " has sent you a trade offer: " + amountToTrade + " " + materialToTrade.name() + " for " + amountToReceive + " " + materialToReceive.name());
        recipient.sendMessage("Type /ta " + senderPlayer.getName() + " accept to accept or /ta " + senderPlayer.getName() + " reject to reject.");

        // Schedule trade expiration after the defined time (3 minutes)
        new BukkitRunnable() {
            @Override
            public void run() {
                // If the trade offer is still active, expire it
                if (activeTrades.containsKey(recipient.getUniqueId())) {
                    activeTrades.remove(recipient.getUniqueId());
                    senderPlayer.sendMessage("Your trade offer to " + recipient.getName() + " has expired.");
                    recipient.sendMessage("The trade offer from " + senderPlayer.getName() + " has expired.");
                }
            }
        }.runTaskLater(plugin, TRADE_EXPIRY_TIME); // 3600L ticks = 3 minutes

        return true;
    }

    // Utility method to check if the player has enough of the specified item
    private boolean hasEnoughItems(Player player, Material material, int amount) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && !item.getType().isAir() && item.getType() == material) {
                count += item.getAmount();
                if (count >= amount) {
                    return true;
                }
            }
        }
        return false;
    }

    // Get an active trade offer for a specific player by their UUID
    public TradeOffer getActiveTrade(UUID playerId) {
        return activeTrades.get(playerId);
    }

    // Remove an active trade offer for a specific player by their UUID
    public void removeTrade(UUID playerId) {
        activeTrades.remove(playerId);
    }
}
