package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.utils.TradeOffer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TradeAcceptCommand implements CommandExecutor {

    private final TradeCommand tradeCommand;

    public TradeAcceptCommand(TradeCommand tradeCommand) {
        this.tradeCommand = tradeCommand;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player recipient = (Player) sender;

            if (args.length != 2) {
                recipient.sendMessage("Usage: /ta <playerName> accept/reject");
                return false;
            }

            String senderName = args[0];
            String decision = args[1].toLowerCase();
            Player senderPlayer = recipient.getServer().getPlayer(senderName);

            if (senderPlayer == null || !senderPlayer.isOnline()) {
                recipient.sendMessage("The specified player is not online.");
                return false;
            }

            TradeOffer tradeOffer = tradeCommand.getActiveTrade(recipient.getUniqueId());

            if (tradeOffer == null || !tradeOffer.getSender().equals(senderPlayer)) {
                recipient.sendMessage("You have no active trade offer from " + senderName + ".");
                return false;
            }

            if ("accept".equals(decision)) {
                // Check if both players have required items
                if (!tradeOffer.hasRequiredItems()) {
                    recipient.sendMessage("Trade failed. One of you does not have the required items.");
                    senderPlayer.sendMessage("Trade failed. One of you does not have the required items.");
                    tradeCommand.removeTrade(recipient.getUniqueId());
                    return true;
                }

                // Process the trade
                tradeOffer.executeTrade();
                senderPlayer.sendMessage("Your trade with " + recipient.getName() + " was successful.");
                recipient.sendMessage("You have successfully traded with " + senderName + ".");
            } else if ("reject".equals(decision)) {
                senderPlayer.sendMessage(recipient.getName() + " has rejected your trade offer.");
                recipient.sendMessage("You have rejected the trade offer from " + senderName + ".");
            } else {
                recipient.sendMessage("Invalid response. Type either 'accept' or 'reject'.");
                return false;
            }

            tradeCommand.removeTrade(recipient.getUniqueId());

        } else {
            sender.sendMessage("This command can only be used by players.");
        }
        return true;
    }
}

