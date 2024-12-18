package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SkillPaperListener implements Listener {
    private final PlayerProfileManager profileManager;

    public SkillPaperListener(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @EventHandler
    public void onPlayerRightClickPaper(PlayerInteractEvent event) {
        // Check if the action is a right-click
        if (!event.getAction().name().contains("RIGHT_CLICK")) {
            return;
        }

        // Check if the player is holding an item
        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.PAPER) {
            return;
        }

        // Check if the paper has a custom name
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return;
        }

        // Get the custom name of the paper
        String skillName = meta.getDisplayName();

        // Check if the name matches a valid skill
        if (!isValidSkill(skillName)) {
            return;
        }

        // Set the skill for the player
        Player player = event.getPlayer();
        UserProfile profile = profileManager.getProfile(player.getName());

        if (profile != null) {
            if (profile.getSelectedSkill().equalsIgnoreCase(skillName)) return;
            profile.setSelectedSkill(skillName);
            player.sendMessage(ChatColor.GREEN +"Your skill has been set to " + skillName + "!");
        }
    }

    private boolean isValidSkill(String skillName) {
        return skillName.equalsIgnoreCase("Skill 1") ||
                skillName.equalsIgnoreCase("Skill 2") ||
                skillName.equalsIgnoreCase("Skill 3");
    }
}
