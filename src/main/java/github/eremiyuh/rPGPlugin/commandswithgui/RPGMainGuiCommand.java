package github.eremiyuh.rPGPlugin.commandswithgui;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class RPGMainGuiCommand implements CommandExecutor, Listener {

    private final PlayerProfileManager profileManager;
    private final NamespacedKey actionKey;

    public RPGMainGuiCommand(RPGPlugin plugin, PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
        this.actionKey = new NamespacedKey(plugin, "rpg_action");
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /* ================= COMMAND ================= */

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;
        openMainMenu(player);
        return true;
    }

    /* ================= MAIN MENU ================= */

    private void openMainMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "§8RPG Menu");

        gui.setItem(10, menuItem(Material.DIAMOND_SWORD, "§aSelect Class", "CLASS"));
        gui.setItem(12, menuItem(Material.EMERALD, "§bSelect Skill", "SKILL"));
        gui.setItem(14, menuItem(Material.FIRE_CHARGE, "§cSelect Element", "ELEMENT"));
        gui.setItem(16, menuItem(Material.NETHER_STAR, "§dSelect Abyss Trait", "TRAIT"));

        player.openInventory(gui);
    }

    /* ================= CLASS GUI ================= */

    private void openClassGui(Player player) {
        Inventory gui = Bukkit.createInventory(null, 9, "§8Select Class");

        gui.setItem(2, classItem(Material.DIAMOND_SWORD,
                "Swordsman",
                "A fierce warrior skilled in melee combat.",
                "swordsman"));

        gui.setItem(4, classItem(Material.BOW,
                "Archer",
                "A master of ranged combat and precision.",
                "archer"));

        gui.setItem(6, classItem(Material.POTION,
                "Alchemist",
                "A wise potion maker, that applies buff/debuff.",
                "alchemist"));

        gui.setItem(8, backButton());

        player.openInventory(gui);
    }

    /* ================= SKILL GUI ================= */

    private void openSkillGui(Player player) {
        UserProfile profile = profileManager.getProfile(player.getName());
        if (profile == null) return;

        Inventory gui = Bukkit.createInventory(null, 9, "§8Select Skill");
        String clazz = profile.getChosenClass().toLowerCase();

        if (clazz.equals("swordsman")) {
            gui.setItem(1, skillItem("Skill 1",
                    "Int-based damage, normal attack applies element effect",
                    Arrays.asList(
                            "§7Right-click with sword (main hand).",
                            "§7Damages all living entities in 5 block radius",
                            "§7Melee damage x1.5",
                            "§7Multipliers apply, cannot crit.",
                            "§7Fire: Burn | Water: Weakness | Ice: Slowness",
                            "§730s cooldown, 5s fixed cooldown"
                    )));

            gui.setItem(3, skillItem("Skill 2",
                    "10% Lifesteal",
                    Arrays.asList(
                            "§7Right-click with sword in both hands.",
                            "§73 lines, 20 blocks forward.",
                            "§7Melee damage x3",
                            "§7Multipliers apply, cannot crit.",
                            "§730s cooldown"
                    )));

            gui.setItem(5, skillItem("Skill 3",
                    "Counter Helix",
                    Arrays.asList(
                            "§7Right-click with sword (main hand).",
                            "§710 block radius taunt.",
                            "§7Gain Strength & Resistance",
                            "§7Buff amplifier = mobs hit",
                            "§7Lasts 10s",
                            "§730s cooldown + 20s fixed"
                    )));
        }

        if (clazz.equals("archer")) {
            gui.setItem(1, skillItem("Skill 1",
                    "Int-based damage, applies element",
                    Arrays.asList(
                            "§7Right-click with bow/crossbow.",
                            "§7Damages mobs in 5 block radius",
                            "§7Ranged damage x1.5",
                            "§7Targets switch randomly",
                            "§730s cooldown, 5s fixed"
                    )));

            gui.setItem(3, skillItem("Skill 2",
                    "Arrow Shower",
                    Arrays.asList(
                            "§730x30 square area",
                            "§7Arrows rain from y+50",
                            "§730s cooldown, 10s fixed"
                    )));

            gui.setItem(5, skillItem("Skill 3",
                    "Rapid Fire",
                    Arrays.asList(
                            "§7Gain Speed II for 5s",
                            "§7Auto fires arrows",
                            "§710 arrows total",
                            "§730s cooldown, 10s fixed"
                    )));
        }

        if (clazz.equals("alchemist")) {
            gui.setItem(1, skillItem("Skill 1",
                    "Instant Damage Potion",
                    Arrays.asList(
                            "§7Shower damage potions for 20s",
                            "§7Position fixed",
                            "§71 potion every 0.5s",
                            "§730s cooldown + 10s fixed"
                    )));

            gui.setItem(3, skillItem("Skill 2",
                    "Random Buff/Debuff",
                    Arrays.asList(
                            "§7Strength/Resistance potions",
                            "§7Debuffs non-teammates",
                            "§71 potion every 0.5s",
                            "§730s cooldown + 10s fixed"
                    )));

            gui.setItem(5, skillItem("Skill 3",
                    "Healing Potion",
                    Arrays.asList(
                            "§7Instant Heal potions",
                            "§71 potion every 0.5s",
                            "§720s duration",
                            "§730s cooldown + 10s fixed"
                    )));
        }

        gui.setItem(8, backButton());
        player.openInventory(gui);
    }

    /* ================= ELEMENT GUI ================= */

    private void openElementGui(Player player) {
        Inventory gui = Bukkit.createInventory(null, 9, "§7Select Your Element");

        gui.setItem(2, elementItem(Material.FIRE_CHARGE, "fire", Arrays.asList(
                "§7🔥 Sets enemies ablaze",
                "§7+10% damage",
                "§75s base + scaling"
        )));

        gui.setItem(4, elementItem(Material.WATER_BUCKET, "water", Arrays.asList(
                "§7💧 Weakness effect",
                "§7+10% HP",
                "§7Scaling duration"
        )));

        gui.setItem(6, elementItem(Material.PACKED_ICE, "ice", Arrays.asList(
                "§7❄ Slowness",
                "§7+10% crit chance",
                "§7Scaling slow"
        )));

        gui.setItem(8, backButton());
        player.openInventory(gui);
    }

    /* ================= TRAIT GUI ================= */

    private void openTraitGui(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "§8Select Abyss Trait");

        gui.setItem(10, traitItem(Material.REDSTONE, "Bloodlust", Arrays.asList(
                "§7🩸 Lifesteal +5%",
                "§7💔 Max HP -10%"
        )));

        gui.setItem(12, traitItem(Material.BLAZE_POWDER, "Frenzy", Arrays.asList(
                "§7⚔ Damage +100%",
                "§7🛡 Damage Taken +100%"
        )));

        gui.setItem(14, traitItem(Material.IRON_CHESTPLATE, "Fortress", Arrays.asList(
                "§7❤️ Max HP +100%",
                "§7⚔ Damage -20%"
        )));

        gui.setItem(16, traitItem(Material.NETHER_STAR, "Gamble", Arrays.asList(
                "§7❌ Crit Rate -25%",
                "§7💥 Crit Damage Bonus +50%"
        )));

        gui.setItem(26, backButton());
        player.openInventory(gui);
    }

    /* ================= CLICK HANDLER ================= */

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        // Only handle YOUR GUIs
        if (!e.getView().getTitle().startsWith("§8") &&
                !e.getView().getTitle().startsWith("§7")) {
            return;
        }


        ItemStack item = e.getCurrentItem();
        if (item == null || !item.hasItemMeta()) return;

        e.setCancelled(true);

        ItemMeta meta = item.getItemMeta();
        String action = meta.getPersistentDataContainer()
                .get(actionKey, PersistentDataType.STRING);
        if (action == null) return;

        UserProfile profile = profileManager.getProfile(player.getName());
        if (profile == null) return;

        switch (action) {

            case "BACK" ->
                    openSafe(player, () -> openMainMenu(player));

            case "CLASS" ->
                    openSafe(player, () -> openClassGui(player));

            case "SKILL" ->
                    openSafe(player, () -> openSkillGui(player));

            case "ELEMENT" ->
                    openSafe(player, () -> openElementGui(player));

            case "TRAIT" ->
                    openSafe(player, () -> openTraitGui(player));

            case "swordsman", "archer", "alchemist" -> {
                profile.setChosenClass(action);
                profileManager.saveProfile(player.getName());
                player.sendMessage("§aClass set to " + action);
                openSafe(player, () -> openMainMenu(player));
            }

            case "Skill 1", "Skill 2", "Skill 3" -> {
                profile.setSelectedSkill(action);
                profileManager.saveProfile(player.getName());
                player.sendMessage("§bSelected " + action);
                openSafe(player, () -> openMainMenu(player));
            }

            case "fire", "water", "ice" -> {
                profile.setSelectedElement(action);
                profileManager.saveProfile(player.getName());
                player.sendMessage("§cElement set to " + action);
                openSafe(player, () -> openMainMenu(player));
            }

            default -> {
                profile.setAbyssTrait(action);
                profileManager.saveProfile(player.getName());
                player.sendMessage("§dAbyss Trait: " + action);
                openSafe(player, () -> openMainMenu(player));
            }
        }
    }


    /* ================= ITEM HELPERS ================= */

    private ItemStack backButton() {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§c⬅ Back");
        meta.setLore(List.of("§7Return to RPG Menu"));
        meta.getPersistentDataContainer().set(actionKey, PersistentDataType.STRING, "BACK");
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack menuItem(Material mat, String name, String action) {
        return selectableItem(mat, name, List.of("§7Click to open"), action);
    }

    private ItemStack classItem(Material mat, String name, String desc, String id) {
        return selectableItem(mat, name, List.of(desc), id);
    }

    private ItemStack skillItem(String skill, String desc, List<String> details) {
        List<String> lore = new ArrayList<>();
        lore.add("§7Description:");
        lore.add("§f" + desc);
        lore.addAll(details);
        return selectableItem(Material.EMERALD, "§a" + skill, lore, skill);
    }

    private ItemStack elementItem(Material mat, String id, List<String> lore) {
        return selectableItem(mat, "§e" + id.toUpperCase(), lore, id);
    }

    private ItemStack traitItem(Material mat, String name, List<String> lore) {
        return selectableItem(mat, "§d" + name, lore, name);
    }

    private ItemStack selectableItem(Material mat, String name, List<String> lore, String action) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        meta.getPersistentDataContainer().set(actionKey, PersistentDataType.STRING, action);
        item.setItemMeta(meta);
        return item;
    }

    private void openSafe(Player player, Runnable openAction) {
        Bukkit.getScheduler().runTask(
                Bukkit.getPluginManager().getPlugin("RPGPlugin"),
                openAction
        );
    }

}
