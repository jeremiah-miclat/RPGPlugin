package github.eremiyuh.rPGPlugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.plugin.java.JavaPlugin;

public class VillagerSetProfessionCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    public VillagerSetProfessionCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return false;
        }

        if (!sender.getName().equals("Eremiyuh")) {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }

        Player player = (Player) sender;

        // Check for correct number of arguments
        if (args.length < 2) {
            player.sendMessage("Usage: /villagerSetProf <villager customName> <profession>");
            return false;
        }

        String villagerName = args[0];
        String professionName = args[1].toUpperCase();

        // Attempt to parse the profession
        Villager.Profession profession;
        try {
            profession = Villager.Profession.valueOf(professionName);
        } catch (IllegalArgumentException e) {
            player.sendMessage("Invalid profession: " + professionName);
            return false;
        }

        // Find the villager with the specified custom name in the player's world
        boolean villagerFound = false;
        for (Entity entity : player.getWorld().getEntities()) {
            if (entity.getType() == EntityType.VILLAGER && entity.getCustomName() != null) {
                Villager villager = (Villager) entity;
                if (villager.getCustomName().equalsIgnoreCase(villagerName)) {
                    villager.setProfession(profession);
                    villagerFound = true;

                    break;
                }
            }
        }

        if (!villagerFound) {
            player.sendMessage("No villager found with the name: " + villagerName);
        }

        return true;
    }
}
