package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TradeHall implements CommandExecutor {
    private final PlayerProfileManager playerProfileManager;

    public TradeHall(PlayerProfileManager playerProfileManager) {
        this.playerProfileManager = playerProfileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;

        player.sendMessage("Server needs to build a trading hall first");
        return  true;

//        World world = Bukkit.getWorld("world");
//        if (world == null) {
//            player.sendMessage("The 'world' map is not loaded.");
//            return true;
//        }
//
//        UserProfile profile = playerProfileManager.getProfile(player.getName());
//        int profileEnderPearl = profile.getEnderPearl();
//
//        if (profileEnderPearl <= 0) {
//            player.sendMessage("Ender Pearl currency required. Collect ender pearl and then enter /convertmaterial enderpearl");
//            return true;
//        }
//
//        Location location = new Location(world, -48, 74, 112);
//        if (player.teleport(location)) {
//            player.sendMessage(ChatColor.GREEN +"Welcome to the trading hall!");
//            profile.setEnderPearl(profileEnderPearl-1);
//        }
//        else {
//            player.sendMessage("Failed to teleport.");
//        }
//        return true;
    }
}
