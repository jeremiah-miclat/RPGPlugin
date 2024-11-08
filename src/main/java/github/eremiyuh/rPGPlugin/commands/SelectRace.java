package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.buffs.VampireBuffs;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SelectRace implements CommandExecutor {

    private final PlayerProfileManager profileManager;

    public SelectRace(PlayerProfileManager profileManager) {
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

        if (args.length != 1) {
            player.sendMessage("Usage: /selectrace <human|elf|orc|vampire|dwarf|demon|angel|darkelf>");
            return true;
        }

        String race = args[0].toLowerCase();

        if (!race.isEmpty()) {
            sender.sendMessage("Still considering to add on game or not");
            return true;
        }

        // Check if the player can select a new element
        if (!profile.canSelectNewRace()) {
            player.sendMessage("You cannot switch races for another 10 minutes.");
            return true;
        }

        // Validate the chosen element and set it
        switch (race) {
            case "human":
            case "elf":
            case "orc":
            case "vampire":
                profile.setSelectedRace(race);
                player.sendMessage("You joined the " + race + " race!");

                // Check if the selected race is vampire and apply speed boost
                // Pass false for other races
                VampireBuffs.applyVampireSpeedBoost(player, race.equals("vampire")); // Pass true for vampire
            case "dwarf":
            case "angel":
            case "demon":
            case "darkelf":
                profile.setSelectedRace(race);
                player.sendMessage("You joined the " + race + " race!");
                break;
            default:
                player.sendMessage("Invalid race. human,elf,orc,vampire,dwarf,demon,angel,darkelf.");
                return true;
        }

        profileManager.saveProfile(player.getName());  // Save after changing race
        return true;
    }
}
