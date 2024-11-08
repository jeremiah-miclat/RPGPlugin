package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.entity.Player;
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
        UserProfile profile = profileManager.getProfile(player.getName());
        if (!player.getLocation().getWorld().getName().equalsIgnoreCase("world_rpg")) return;
        int durability = profile.getDurability();
        int newDurability = (int) Math.floor(durability * 0.80);
        profile.setDurability(newDurability < 1 ? 0 : newDurability);

        int stamina = profile.getStamina();
        int newStamina = (int) Math.floor(stamina * 0.80);
        profile.setStamina(newStamina < 1 ? 0 : newStamina);


        int abyssPoints = (int) profile.getAbyssPoints();
        int newAbyssPoints = (int) Math.floor(abyssPoints * 0.80);
        profile.setAbyssPoints(newAbyssPoints < 1 ? 0 : newAbyssPoints);


    }
}
