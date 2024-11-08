package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ResetRestCommand implements CommandExecutor {

    private final PlayerProfileManager profileManager;

    public ResetRestCommand(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        UserProfile profile = profileManager.getProfile(player.getName());
        if (profile.getLapiz() < 10) {
            player.sendMessage("You need 10 lapis lazuli to buy the magic potion!");
            return true;
        }

        // Deduct lapis cost and reset rest time
        profile.setLapiz(profile.getLapiz() - 10);
        player.setStatistic(Statistic.TIME_SINCE_REST, 0);
        player.sendMessage("A mystical potion has been used, resetting your rest time!");

        // Apply the "magic potion" effect with visuals
        // Play potion sound effect
        player.playSound(player.getLocation(), Sound.ENTITY_WITCH_DRINK, 1.0F, 1.5F);

        // Display a spiral of particles around the player
        for (int i = 0; i < 20; i++) {
            double angle = i * (Math.PI / 10);
            double x = Math.cos(angle) * 1.5;
            double z = Math.sin(angle) * 1.5;
            player.getWorld().spawnParticle(Particle.WITCH, player.getLocation().add(x, 1, z), 0, 0, 0.1, 0, 0.1);
        }

        // Apply a sparkle effect above the player's head
        player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation().add(0, 2, 0), 50, 0.5, 0.5, 0.5, 0.05);

        // Apply a temporary "regeneration" potion effect to simulate a refreshing effect
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));

        // Final message for added immersion
        player.sendMessage("§6You feel a refreshing wave of magic envelop you. Your spirit is renewed!");

        return true;
    }
}
