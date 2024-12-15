package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.TpAllowManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpToggleCommand implements CommandExecutor {
    private final TpAllowManager tpAllowManager;

    public TpToggleCommand(TpAllowManager tpAllowManager) {
        this.tpAllowManager = tpAllowManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return false;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            // If no arguments are provided, show the help or list of allowed players.
            tpAllowManager.listAllowedPlayers(player);
            return true;
        }

        // Handle the /tptoggle list command
        if (args[0].equalsIgnoreCase("list")) {
            tpAllowManager.listAllowedPlayers(player);
            return true;
        }

        // Handle the /tptoggle remove <playername> command
        if (args[0].equalsIgnoreCase("remove") && args.length == 2) {
            String targetPlayerName = args[1];
            tpAllowManager.removeAllowedPlayer(player, targetPlayerName);
            return true;
        }

        // If command is not recognized, return false (show usage)
        return false;
    }
}
