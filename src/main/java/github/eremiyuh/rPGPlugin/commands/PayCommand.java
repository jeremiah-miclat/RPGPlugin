package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class PayCommand implements CommandExecutor {

    private final PlayerProfileManager profileManager;
    private static final List<String> validCurrencies = List.of("gold", "emerald", "diamond","iron", "lapis", "enderpearl","netherite"
    , "copper", "abysspoints", "activitypoints"
    );



    public PayCommand(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player senderPlayer = (Player) sender;
        if (args.length != 3) {
            senderPlayer.sendMessage(ChatColor.RED + "Usage: /pay <playername> <currencyname> <amount>");
            return true;
        }

        String targetPlayerName = args[0];
        String currencyName = args[1].toLowerCase();
        int amount;  // Change to int instead of double

        try {
            amount = Integer.parseInt(args[2]);  // Use Integer.parseInt instead of Double.parseDouble
        } catch (NumberFormatException e) {
            senderPlayer.sendMessage(ChatColor.RED + "The amount must be a valid integer.");
            return true;
        }

        if (amount <= 0) {
            senderPlayer.sendMessage(ChatColor.RED + "You can only pay a positive amount.");
            return true;
        }

        Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
        if (targetPlayer == null || !targetPlayer.isOnline()) {
            senderPlayer.sendMessage(ChatColor.RED + "The specified player is not online.");
            return true;
        }

        if (targetPlayer.getName().equals(senderPlayer.getName())) {
            senderPlayer.sendMessage(ChatColor.RED + "You cannot pay yourself.");
            return true;
        }

        UserProfile senderProfile = profileManager.getProfile(senderPlayer.getName());
        UserProfile targetProfile = profileManager.getProfile(targetPlayer.getName());





        if (!isCurrencyValid(currencyName)) {
            senderPlayer.sendMessage(ChatColor.RED + "Valid currencies: diamond, emerald, iron, lapis, gold, enderpearl, netherite, copper, abysspoints");
            return true;
        }

        double senderBalance = 0;
        try {
            senderBalance = senderProfile.getCurrency(currencyName);
        } catch (IllegalArgumentException e) {
            senderPlayer.sendMessage(ChatColor.RED + "Invalid currency name: " + currencyName);
            return true;
        }
        if (senderBalance < amount) {
            senderPlayer.sendMessage(ChatColor.RED + "You do not have enough " + currencyName + " to complete this transaction.");
            return true;
        }

        // Perform the transaction
        senderProfile.setCurrency(currencyName, senderBalance - amount);
        targetProfile.setCurrency(currencyName, targetProfile.getCurrency(currencyName) + amount);

        // Save profiles to ensure persistence
        profileManager.saveProfile(senderPlayer.getName());
        profileManager.saveProfile(targetPlayer.getName());

        // Notify players
        senderPlayer.sendMessage(ChatColor.GREEN + "You have sent " + amount + " " + currencyName + " to " + targetPlayer.getName() + ".");
        targetPlayer.sendMessage(ChatColor.GREEN + senderPlayer.getName() + " has sent you " + amount + " " + currencyName + ".");

        return true;
    }

    /**
     * Checks if the given currency name is valid.
     * @param currencyName The name of the currency to check.
     * @return true if the currency is valid, false otherwise.
     */
    public boolean isCurrencyValid(String currencyName) {
        return validCurrencies.contains(currencyName);
    }
}
