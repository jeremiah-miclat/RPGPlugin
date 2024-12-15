package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.TpAllowManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpAllowCommand implements CommandExecutor {
    private final TpAllowManager tpAllowManager;

    public TpAllowCommand(TpAllowManager tpAllowManager) {
        this.tpAllowManager = tpAllowManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length != 1) {
                player.sendMessage("Usage: /tpallow <playername>");
                return false;
            }

            String targetPlayerName = args[0];

            // Add the player to the tp allow list
            tpAllowManager.allowTeleport(player, targetPlayerName);

            return true;
        } else {
            sender.sendMessage("This command can only be used by a player.");
            return false;
        }
    }
}
