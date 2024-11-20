package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.RGBLike;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

public class PlayerDeathListener implements Listener {
    private final PlayerProfileManager profileManager;

    private final String resourceWorldName = "world_resource"; // Name of the world where item loss is allowed

    public PlayerDeathListener(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        event.getDrops().clear();
        // Check if the player is in a world other than "world_resource"
        if (!Objects.requireNonNull(player.getLocation().getWorld()).getName().equals(resourceWorldName)) {
            event.setKeepInventory(true);

            player.sendMessage("You will not lose your items in this world.");
        }
        if (player.getAllowFlight()) {
            player.setAllowFlight(false);
        }
        UserProfile profile = profileManager.getProfile(player.getName());
        if (!player.getLocation().getWorld().getName().equalsIgnoreCase("world_rpg")
            &&  !player.getLocation().getWorld().getName().contains("labyrinth")
        ) {player.teleport(Objects.requireNonNull(Bukkit.getWorld("world")).getSpawnLocation());} else {


        int durability = profile.getDurability();
        int newDurability = (int) Math.floor(durability * 0.90);
        profile.setDurability(newDurability < 1 ? 0 : newDurability);

        int stamina = profile.getStamina();
        int newStamina = (int) Math.floor(stamina * 0.90);
        profile.setStamina(newStamina < 1 ? 0 : newStamina);


        int abyssPoints = (int) profile.getAbyssPoints();
        int newAbyssPoints = (int) Math.floor(abyssPoints * 0.90);
        profile.setAbyssPoints(newAbyssPoints < 1 ? 0 : newAbyssPoints);
        String msgToPlayer = "You lost 10% of your durability, stamina, and abyss points";
        player.sendMessage(Component.text(msgToPlayer).color(TextColor.color(255,0,0)));

        if (player.getKiller() != null) {
            Player killer = player.getKiller();
            UserProfile killerProfile = profileManager.getProfile(killer.getName());
            killerProfile.setAbyssPoints(killerProfile.getAbyssPoints()+(abyssPoints*.10));
            String messageToKiller = "You killed "+ player.getName()  + ". Received " + (int) abyssPoints*.10 + " abyss points.";
            killer.sendMessage(Component.text(messageToKiller).color(TextColor.color(124,252,0)));
        }

        player.teleport(Objects.requireNonNull(Bukkit.getWorld("world")).getSpawnLocation());
        }

    }


}
