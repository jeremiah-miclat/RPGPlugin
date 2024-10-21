package github.eremiyuh.rPGPlugin.commandswithgui;

import github.eremiyuh.rPGPlugin.profile.UserProfile;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class SkillsGui implements Listener {

    private final PlayerProfileManager profileManager;

    public SkillsGui(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    public void openSkillsGui(Player player) {
        UserProfile profile = profileManager.getProfile(player.getName());

        if (profile == null) {
            player.sendMessage("You do not have a profile.");
            return;
        }

        Inventory gui = Bukkit.createInventory(null, 9, "Your Skills");

        // Set up skills based on the player's chosen class
        switch (profile.getChosenClass().toLowerCase()) {
            case "swordsman":
                addSkillsToGui(gui, "Swordsman", "Swordsman Skill 1", "Swordsman Skill 2", "Swordsman Skill 3", "Swordsman Skill 4");
                break;
            case "archer":
                addSkillsToGui(gui, "Archer", "Archer Skill 1", "Archer Skill 2", "Archer Skill 3", "Archer Skill 4");
                break;
            case "alchemist":
                addSkillsToGui(gui, "Alchemist", "Alchemist Skill 1", "Alchemist Skill 2", "Alchemist Skill 3", "Alchemist Skill 4");
                break;
            default:
                player.sendMessage("Your class does not have any skills.");
                return;
        }

        // Open the GUI for the player
        player.openInventory(gui);
    }

    private void addSkillsToGui(Inventory gui, String className, String skill1, String skill2, String skill3, String skill4) {
        // Add skill 1
        gui.setItem(1, createSkillIcon(Material.DIAMOND_SWORD, "§a" + className + " Skill 1", skill1));

        // Add skill 2
        gui.setItem(3, createSkillIcon(Material.BOW, "§a" + className + " Skill 2", skill2));

        // Add skill 3
        gui.setItem(5, createSkillIcon(Material.POTION, "§a" + className + " Skill 3", skill3));

        // Add skill 4
        gui.setItem(7, createSkillIcon(Material.BOOK, "§a" + className + " Skill 4", skill4));
    }

    private ItemStack createSkillIcon(Material material, String displayName, String description) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(displayName);
            meta.setLore(Arrays.asList("§7Description:", "§f" + description));
            item.setItemMeta(meta);
        }

        return item;
    }

    @org.bukkit.event.EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Your Skills")) {
            event.setCancelled(true);  // Prevent interaction with the inventory
        }
    }
}
