package github.eremiyuh.rPGPlugin.buffs;

import org.bukkit.entity.Player;

public class VampireBuffs {

    // Method to apply speed boost to vampire players
    public static void applyVampireSpeedBoost(Player player, boolean isVampire) {
        if (isVampire) {
            player.setWalkSpeed(0.3f); // Increase walk speed (0.3 is 50% faster than normal)
        } else {
            player.setWalkSpeed(0.2f); // Reset to normal walk speed for other players
        }
    }
}
