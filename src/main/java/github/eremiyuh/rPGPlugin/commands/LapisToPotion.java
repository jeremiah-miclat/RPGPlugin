package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LapisToPotion implements CommandExecutor {

    private final PlayerProfileManager profileManager;


    public LapisToPotion(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        UserProfile userProfile = profileManager.getProfile(player.getName());

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /craftpotion <amount of Lapis>. 20 potions per lapis ");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[0]);
            if (amount <= 0) {
                player.sendMessage(ChatColor.RED + "Please enter a positive number.");
                return true;
            }
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid number format.");
            return true;
        }

        if (userProfile.getLapiz() < amount) {
            player.sendMessage(ChatColor.RED + "You don't have enough lapis.");
            return true;
        }

        // Deduct lapis and add to potion points
        userProfile.setLapiz(userProfile.getLapiz() - amount);
        userProfile.setPotion(userProfile.getPotion() + amount*20);

        player.sendMessage(ChatColor.GREEN + "Successfully converted " + amount + " lapis into potion points.");
        return true;
    }
}
