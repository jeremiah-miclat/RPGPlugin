package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.buffs.PlayerStatBuff;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class AddAttributeCommand implements CommandExecutor {

    private final PlayerProfileManager profileManager;
    private final PlayerStatBuff playerStatBuff;
    public AddAttributeCommand(PlayerProfileManager profileManager, PlayerStatBuff playerStatBuff) {
        this.profileManager = profileManager;
        this.playerStatBuff = playerStatBuff;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length < 1 || args.length > 2) {
            player.sendMessage("Usage: /addstat <attributeName> [amount](leave amount as <blank> to add all remaining points)");
            player.sendMessage("Valid attributes are: str, agi, dex, intel, vit, luk.");
            player.sendMessage("Example to add all remaining points to Strength: /addstat str");
            player.sendMessage("Example to add 100 points to Strength: /addstat str 100");
            return true;
        }

        UserProfile userProfile = profileManager.getProfile(player.getName());

        String attribute = args[0].toLowerCase();
        int availablePoints = userProfile.getCurrentAttributePoints();
        int points;

        if (args.length == 2) {
            try {
                points = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage("The amount must be a number.");
                return true;
            }
        } else {
            points = availablePoints; // Allocate all points if amount not given
        }

        if (points <= 0) {
            player.sendMessage("You must add a positive number of points.");
            return true;
        }

        if (availablePoints < points) {
            player.sendMessage("You don't have enough attribute points to allocate.");
            return true;
        }

        String currentClass = userProfile.getChosenClass();
        if (Objects.equals(currentClass, "default")) {
            player.sendMessage("You cannot allocate points while in the default class.");
            return true;
        }

        double allocatedPoints = switch (currentClass.toLowerCase()) {
            case "archer" -> userProfile.getTotalArcherAllocatedPoints();
            case "alchemist" -> userProfile.getTotalAlchemistAllocatedPoints();
            case "swordsman" -> userProfile.getTotalSwordsmanAllocatedPoints();
            default -> 0;
        };

        if (points + allocatedPoints > 20000) {
            player.sendMessage("Max attribute allocation per class reached.");
            return true;
        }

        try {
            switch (currentClass.toLowerCase()) {
                case "archer" -> userProfile.addPointsToClass(userProfile.getArcherClassInfo(), attribute, points);
                case "swordsman" -> userProfile.addPointsToClass(userProfile.getSwordsmanClassInfo(), attribute, points);
                case "alchemist" -> userProfile.addPointsToClass(userProfile.getAlchemistClassInfo(), attribute, points);
                default -> userProfile.addPointsToClass(userProfile.getDefaultClassInfo(), attribute, points);
            }

            userProfile.setCurrentAttributePoints(availablePoints - points);

            player.sendMessage("Successfully added " + points + " points to " + attribute + " for the " + currentClass + " class.");
            String worldName = player.getWorld().getName();
            if (!worldName.contains("rpg") && !worldName.contains("labyrinth")) {
                playerStatBuff.updatePlayerStatsToNormal(player);
            }
            if (worldName.contains("rpg") || worldName.contains("labyrinth")) {
                playerStatBuff.updatePlayerStatsToRPG(player);
            }


        } catch (IllegalArgumentException e) {
            player.sendMessage("Invalid attribute. Valid attributes are: str, agi, dex, intel, vit, luk.");
        }

        return true;
    }

}