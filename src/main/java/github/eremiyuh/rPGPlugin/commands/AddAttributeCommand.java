package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class AddAttributeCommand implements CommandExecutor {

    private final PlayerProfileManager profileManager;

    public AddAttributeCommand(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        UserProfile userProfile = profileManager.getProfile(player.getName());

        if (args.length != 2) {
            player.sendMessage("Usage: /add <attributeName> <amount>");
            return true;
        }

        String attribute = args[0].toLowerCase();
        int points;

        try {
            points = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("The amount must be a number.");
            return true;
        }

        if (points <= 0) {
            player.sendMessage("You must add a positive number of points.");
            return true;
        }

        if (userProfile.getCurrentAttributePoints() < points) {
            player.sendMessage("You don't have enough attribute points to allocate.");
            return true;
        }

        UserProfile profile = profileManager.getProfile(player.getName());

        if (Objects.equals(profile.getChosenClass(), "default")) {
            player.sendMessage("You cannot allocate points while in the default class.");
            return true;
        }

        double allocatedPoints = 0;

        if (profile.getChosenClass().equalsIgnoreCase("archer")) allocatedPoints = profile.getTotalArcherAllocatedPoints();
        if (profile.getChosenClass().equalsIgnoreCase("alchemist")) allocatedPoints = profile.getTotalAlchemistAllocatedPoints();
        if (profile.getChosenClass().equalsIgnoreCase("swordsman")) allocatedPoints = profile.getTotalSwordsmanAllocatedPoints();

        if (points + allocatedPoints > 10000) {
            player.sendMessage("Max attribute allocation per class reached.");
            return true;
        }

        try {
            // Check current class and update attributes
            String currentClass = userProfile.getChosenClass();
            switch (currentClass.toLowerCase()) {
                case "archer":
                    userProfile.addPointsToClass(userProfile.getArcherClassInfo(), attribute, points);
                    break;
                case "swordsman":
                    userProfile.addPointsToClass(userProfile.getSwordsmanClassInfo(), attribute, points);
                    break;
                case "alchemist":
                    userProfile.addPointsToClass(userProfile.getAlchemistClassInfo(), attribute, points);
                    break;
                default:
                    userProfile.addPointsToClass(userProfile.getDefaultClassInfo(), attribute, points);
                    break;
            }

            // Subtract the allocated points from the user's available pool
            userProfile.setCurrentAttributePoints(userProfile.getCurrentAttributePoints() - points);

            player.sendMessage("Successfully added " + points + " points to " + attribute + " for the " + currentClass + " class.");
        } catch (IllegalArgumentException e) {
            player.sendMessage("Invalid attribute. Valid attributes are: str, agi, dex, intel, vit, luk.");
        }

        return true;
    }
}