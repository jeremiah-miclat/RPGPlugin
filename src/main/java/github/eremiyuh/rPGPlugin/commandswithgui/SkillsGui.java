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
import java.util.List;
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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can run this command.");
            return true;
        }

        Player player = (Player) sender;
        UserProfile profile = profileManager.getProfile(player.getName());

        if (profile == null) {
            player.sendMessage("You do not have a profile.");
            return true;
        }

        openSkillsGui(player, profile);
        return true;
    }

    public void openSkillsGui(Player player, UserProfile profile) {
        Inventory gui = Bukkit.createInventory(null, 9, "Select Skill");

        switch (profile.getChosenClass().toLowerCase()) {
            case "swordsman":
                addSkillsToGui(gui, "Swordsman",
                        "Int-based damage",
                        "10% Lifesteal",
                        "Counter Helix: Return Damage of 30% of your damage from strength when using thorns",
                        Arrays.asList(
                                "§7Right-click with sword (main hand).",
                                "§7Damages all living entities in 5 block radius by your §cmelee damage * 2§7.",
                                "§7Crit and other multipliers applies.",
                                "§7Fire: Burn 10s | Water: Weakness IX 10s | Ice: Slowness II 10s.",
                                "§730s cooldown."
                        ),
                        Arrays.asList(
                                "§7Right-click with sword in both hands.",
                                "§73 lines, 20 blocks forward, §cmelee damage§7.",
                                "§7Crit and other multipliers applies.",
                                "§73 Can hit a single target up to 5 times.",
                                "§730s cooldown + 4s fixed cooldown"
                        ),
                        Arrays.asList(
                                "§7Right-click with sword (main hand).",
                                "§710 block radius taunt – all mobs target you.",
                                "§7Gain Strength & Resistance based on number of mobs hit.",
                                "§7Effect lasts 10s. 30s cooldown."
                        )
                );
                break;
            case "archer":
                addSkillsToGui(gui, "Archer",
                        "High Int-based damage.",
                        "Arrow Shower. Dex for Chance & Int for Damage.",
                        "Bonus Crit Chance. Crossbow heals 5% of damage dealt (5% lifesteal effect)",
                        Arrays.asList(
                                "§7Right-click with bow/crossbow.",
                                "§7Damages mobs in 5 block radius by your §cranged damage§7.",
                                "§7Crit and other multipliers applies.",
                                "§7Targets switch to random mob (Boss priority).",
                                "§7Fire: Burn 10s | Water: Weakness IX 10s | Ice: Slowness II 10s.",
                                "§730s cooldown. Item on Slot 9 is the cd identifier"
                        ),
                        Arrays.asList(
                                "§7Right-click with bow/crossbow.",
                                "§730x30 square centered on player – arrow rain from y+50.",
                                "§730s cooldown. Item on Slot 9 is the cd identifier"
                        ),
                        Arrays.asList(
                                "§7Right-click with bow/crossbow.",
                                "§7Gain Speed II for 5 seconds.",
                                "§7Automatically fires 1 arrow every 0.5s in the",
                                "§7direction you're facing (10 arrows total).",
                                "§7No damage dealt by the skill itself.",
                                "§730s cooldown. Item in Slot 9 indicates cooldown."
                        )
                );
                break;
            case "alchemist":
                addSkillsToGui(gui, "Alchemist",
                        "Instant Damage Potion",
                        "Random Buff/Debuff",
                        "Healing Potion",
                        Arrays.asList(
                                "§7Right-click with main hand.",
                                "§7Shower location with Instant Damage splash potions for 20s.",
                                "§7Position fixed at cast start.",
                                "§7Potions fall fast from y+3.",
                                "§730s cooldown + 9s fixed cooldown."
                        ),
                        Arrays.asList(
                                "§7Right-click with main hand.",
                                "§7Shower location with Strength splash potions for 20s.",
                                "§71 potion every 0.5s.",
                                "§7Potions fall fast from y+3.",
                                "§730s cooldown + 9s fixed cooldown."
                        ),
                        Arrays.asList(
                                "§7Right-click with main hand.",
                                "§7Shower location with Instant Heal splash potions for 20s.",
                                "§71 potion every 0.5s.",
                                "§7Potions fall fast from y+3.",
                                "§730s cooldown + 9s fixed cooldown."
                        )
                );
                break;
            default:
                player.sendMessage("Your class does not have any skills.");
                return;
        }

        player.openInventory(gui);
    }

    private void addSkillsToGui(Inventory gui, String className, String skill1, String skill2, String skill3,
                                List<String> detail1, List<String> detail2, List<String> detail3) {
        gui.setItem(1, createSkillIcon(Material.EMERALD, "§a" + className + " Skill 1", skill1, detail1));
        gui.setItem(3, createSkillIcon(Material.EMERALD, "§a" + className + " Skill 2", skill2, detail2));
        gui.setItem(5, createSkillIcon(Material.EMERALD, "§a" + className + " Skill 3", skill3, detail3));
    }

    private ItemStack createSkillIcon(Material material, String displayName, String description, List<String> details) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(displayName);
            List<String> lore = Arrays.asList("§7Description:", "§f" + description, "");
            lore = new java.util.ArrayList<>(lore);
            lore.addAll(details);
            lore.add("§eUniqueSkillIcon");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Select Skill")) {
            event.setCancelled(true);

            if (event.getWhoClicked() instanceof Player) {
                Player player = (Player) event.getWhoClicked();
                UserProfile profile = profileManager.getProfile(player.getName());

                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem != null && clickedItem.hasItemMeta() && Objects.requireNonNull(clickedItem.getItemMeta()).hasLore()) {
                    ItemMeta itemMeta = clickedItem.getItemMeta();
                    if (itemMeta.getLore() != null && itemMeta.getLore().contains("§eUniqueSkillIcon")) {
                        String displayName = itemMeta.getDisplayName();
                        String currentSkill = profile.getSelectedSkill();

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

                        profile.setSelectedSkill(selectedSkill);
                        profileManager.saveProfile(player.getName());
                        player.sendMessage("You have selected " + selectedSkill + "!");
                        player.closeInventory();
                    }
                }
            }
        }
    }
}
