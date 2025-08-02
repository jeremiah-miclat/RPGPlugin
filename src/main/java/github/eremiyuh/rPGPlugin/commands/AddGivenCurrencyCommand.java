package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.profile.UserProfile;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddGivenCurrencyCommand implements CommandExecutor {

    private final PlayerProfileManager profileManager;

    public AddGivenCurrencyCommand(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.isOp()) {
            sender.sendMessage("§cYou do not have permission to use this command.");
            return true;
        }

        if (!sender.getName().equalsIgnoreCase("Eremiyuh") && !sender.getName().equalsIgnoreCase("DogWannaEat") ) {
            sender.sendMessage("§cYou do not have permission to use this command.");
            return true;
        }

        if (args.length != 3) {
            sender.sendMessage("§cUsage: /agc <player> <currency> <amount>");
            return true;
        }

        String targetName = args[0];
        String currency = args[1];
        double amount;

        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid amount. Must be a number.");
            return true;
        }

        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage("§cPlayer not found: " + targetName);
            return true;
        }

        UserProfile profile = profileManager.getProfile(target.getName());
        if (profile == null) {
            sender.sendMessage("§cCould not load profile for player " + targetName);
            return true;
        }

        try {
            double currentAmount = profile.getCurrency(currency);
            profile.setCurrency(currency, currentAmount + amount);

            sender.sendMessage("§aGave §e" + amount + " " + currency + "§a to §b" + targetName + "§a. New total: §e" + profile.getCurrency(currency));
            if (target.isOnline()) {
                target.sendMessage("§aYou have received §e" + amount + " " + currency);
            }

        } catch (Exception e) {
            sender.sendMessage("§cInvalid currency: " + currency);
        }

        return true;
    }
}
