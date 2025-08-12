package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.entity.Player;

import java.util.Objects;

public class PlayerDeathListener implements Listener {
    private final PlayerProfileManager profileManager;

    private final String keepInventoryWorld  = "world_rpg"; // Name of the world where item loss is allowed

    public PlayerDeathListener(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
//        event.getDrops().clear();
        // Check if the player is in a world other than "world_resource"
        if (Objects.requireNonNull(player.getLocation().getWorld()).getName().contains(keepInventoryWorld)) {
            event.setKeepInventory(true);
            event.getDrops().clear();

//            player.sendMessage("You will not lose your items in this world.");
        }
        if (player.getAllowFlight()) {
            player.setAllowFlight(false);
        }

        if (player.getWorld().getName().contains("resource")) {
            World world = Bukkit.getWorld("world");
            assert world != null;
            Location spawnLocation = world.getSpawnLocation();
            player.teleport(spawnLocation);
        }


        UserProfile profile = profileManager.getProfile(player.getName());
        if (player.getLocation().getWorld().getName().contains("world_rpg")
        ) {
            int durability = profile.getDurability();
            int newDurability = (int) Math.floor(durability * 0.50);
            profile.setDurability(newDurability < 1 ? 0 : newDurability);

            int stamina = profile.getStamina();
            int newStamina = (int) Math.floor(stamina * 0.50);
            profile.setStamina(newStamina < 1 ? 0 : newStamina);


//            int abyssPoints = (int) profile.getAbysspoints();
//            int newAbyssPoints = (int) Math.floor(abyssPoints * 0.90);
//            profile.setAbysspoints(newAbyssPoints < 1 ? 0 : newAbyssPoints);
//            String msgToPlayer = "You lost 50% of your durability and stamina, and 10% of your abyss points.";
//            player.sendMessage(Component.text(msgToPlayer).color(TextColor.color(255, 0, 0)));
//
//            if (player.getKiller() != null) {
//                Player killer = player.getKiller();
//                UserProfile killerProfile = profileManager.getProfile(killer.getName());
//                killerProfile.setAbysspoints(killerProfile.getAbysspoints() + (abyssPoints * .10));
//                String messageToKiller = "You killed " + player.getName() + ". Received " + (int) (abyssPoints * .10) + " abyss points.";
//                killer.sendMessage(Component.text(messageToKiller).color(TextColor.color(124, 252, 0)));
//            }

            player.teleport(Objects.requireNonNull(Bukkit.getWorld("world")).getSpawnLocation());


        }
    }

}
