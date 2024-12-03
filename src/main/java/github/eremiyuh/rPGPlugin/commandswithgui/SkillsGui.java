package github.eremiyuh.rPGPlugin.commandswithgui;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Objects;

public class SkillsGui implements CommandExecutor, Listener {
    private final RPGPlugin plugin;
    private final PlayerProfileManager profileManager;

    public SkillsGui(RPGPlugin plugin, PlayerProfileManager profileManager) {
        this.plugin = plugin;
        this.profileManager = profileManager;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private final long COOLDOWN = 10000;

    // When a player enters /selectskill, the GUI will appear
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can run this command.");
            return true;
        }

        Player player = (Player) sender;
        UserProfile profile = profileManager.getProfile(player.getName());
        double playerLapis = profile.getLapiz();

        if (profile == null) {
            player.sendMessage("You do not have a profile.");
            return true;
        }

        openSkillsGui(player, profile);
        return true;
    }

    // Open the skills GUI for the player
    public void openSkillsGui(Player player, UserProfile profile) {
        Inventory gui = Bukkit.createInventory(null, 9, "Select Skill");

        // Set up skills based on the player's chosen class
        switch (profile.getChosenClass().toLowerCase()) {
            case "swordsman":
                addSkillsToGui(gui, "Swordsman", "Int-based damage", "10% Lifesteal", "50% Damage Reduction");
                break;
            case "archer":
                addSkillsToGui(gui, "Archer", "Int-based damage", "Balanced Dex & Int Build", "Bonus Crit Chance");
                break;
            case "alchemist":
                addSkillsToGui(gui, "Alchemist", "Instant Damage Potion", "Random Buff/Debuff", "Healing Potion");
                break;
            default:
                player.sendMessage("Your class does not have any skills.");
                return;
        }

        // Open the GUI for the player
        player.openInventory(gui);
    }

    // Add the skills for the player's chosen class to the GUI
    private void addSkillsToGui(Inventory gui, String className, String skill1, String skill2, String skill3) {
        // Skill 1
        gui.setItem(1, createSkillIcon(Material.EMERALD, "§a" + className + " Skill 1", skill1));

        // Skill 2
        gui.setItem(3, createSkillIcon(Material.EMERALD, "§a" + className + " Skill 2", skill2));

        // Skill 3
        gui.setItem(5, createSkillIcon(Material.EMERALD, "§a" + className + " Skill 3", skill3));
    }

    // Create an icon for each skill with a unique lore to identify it
    private ItemStack createSkillIcon(Material material, String displayName, String description) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(displayName);
            // Add a unique line of lore to identify the GUI item
            meta.setLore(Arrays.asList("§7Description:", "§f" + description, "§eUniqueSkillIcon"));
            item.setItemMeta(meta);
        }

        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Check if the inventory is the skills GUI by checking its title
        if (event.getView().getTitle().equals("Select Skill")) {
            event.setCancelled(true);  // Prevent interactions such as clicking or dragging

            // Ensure the player who clicked is a Player instance
            if (event.getWhoClicked() instanceof Player) {
                Player player = (Player) event.getWhoClicked();
                UserProfile profile = profileManager.getProfile(player.getName());

                // Get the clicked item
                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem != null && clickedItem.hasItemMeta() && Objects.requireNonNull(clickedItem.getItemMeta()).hasLore()) {
                    ItemMeta itemMeta = clickedItem.getItemMeta();
                    // Ensure the item has the unique lore for skill selection
                    if (itemMeta.getLore() != null && itemMeta.getLore().contains("§eUniqueSkillIcon")) {
                        String displayName = itemMeta.getDisplayName();
                        String currentSkill = profile.getSelectedSkill();

                        // Identify the selected skill
                        String selectedSkill;
                        if (displayName.contains("Skill 1")) {
                            selectedSkill = "Skill 1";
                        } else if (displayName.contains("Skill 2")) {
                            selectedSkill = "Skill 2";
                        } else if (displayName.contains("Skill 3")) {
                            selectedSkill = "Skill 3";
                        } else {
                            player.sendMessage("Invalid skill selection.");
                            return;
                        }

                        if (currentSkill.equalsIgnoreCase(selectedSkill)) {
                            player.sendMessage("Already using this skill");
                            return;
                        }

                        // Check if the player has selected a skill before
                        if (!profile.getSelectedSkill().equals("none")) {
                            // Check if the player has enough lapis
                            if (profile.getLapiz() < 100) {
                                player.sendMessage("You need at least 100 lapis to select a new skill.");
                                return;
                            }
                            // Deduct 100 lapis for skill reselection
                            profile.setLapiz(profile.getLapiz() - 100);
                        }

                        // Save the selected skill to the profile
                        profile.setSelectedSkill(selectedSkill);
                        profileManager.saveProfile(player.getName());
                        player.sendMessage("You have selected " + selectedSkill + "!");

                        // Close the inventory after the player selects a skill
                        player.closeInventory();
                    }
                }
            }
        }
    }
}
