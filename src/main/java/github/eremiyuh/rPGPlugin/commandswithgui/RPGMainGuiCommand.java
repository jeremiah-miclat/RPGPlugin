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

        gui.setItem(22, menuItem(Material.ENCHANTING_TABLE, "§eAttributes Update", "ATTR_UPDATE"));

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
                    "+2HP per BP Level",
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

        if (action.startsWith("BUILD|")) {
            applyBuild(player, profile, action);
            profileManager.saveProfile(player.getName());
            openSafe(player, () -> openAttributesUpdateGui(player)); // stay here
            return;
        }


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

            case "ATTR_UPDATE" ->
                    openSafe(player, () -> openAttributesUpdateGui(player));

            case "ATTR_BACK" ->
                    openSafe(player, () -> openMainMenu(player));

            case "ATTR_RESET" -> {
                int allocatedTotal = profile.getTotalAllocatedPoints();
                profile.setCurrentAttributePoints(profile.getCurrentAttributePoints() + allocatedTotal);

                profile.getArcherClassInfo().resetAttributes();
                profile.getSwordsmanClassInfo().resetAttributes();
                profile.getAlchemistClassInfo().resetAttributes();

                profileManager.saveProfile(player.getName());
                player.sendMessage("§aAll class attributes reset, points refunded.");

                openSafe(player, () -> openAttributesUpdateGui(player));
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
                Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("RPGPlugin")),
                openAction
        );
    }
    /* ================= ATTRIBUTES UPDATE GUI ================= */

    private void openAttributesUpdateGui(Player player) {
        UserProfile profile = profileManager.getProfile(player.getName());
        if (profile == null) return;

        Inventory gui = Bukkit.createInventory(null, 54, "§8Attributes Update");

        // Back button (ONLY way to go back)
        gui.setItem(45, attrBackButton());

        // Reset button
        gui.setItem(49, attrResetButton());

        // ---------- PURE STAT BUILDS (show to all classes) ----------
        gui.setItem(10, buildButton("Pure STR",
                "Only recommended if you know game mechanics and this is your preference",
                "100",
                "STR",
                "BUILD|pure_str|str:100"));

        gui.setItem(11, buildButton("Pure AGI",
                "Only recommended if you know game mechanics and this is your preference",
                "100",
                "AGI",
                "BUILD|pure_agi|agi:100"));

        gui.setItem(12, buildButton("Pure DEX",
                "Only recommended if you know game mechanics and this is your preference",
                "100",
                "DEX",
                "BUILD|pure_dex|dex:100"));

        gui.setItem(13, buildButton("Pure INT",
                "Only recommended if you know game mechanics and this is your preference",
                "100",
                "INT",
                "BUILD|pure_int|intel:100"));

        gui.setItem(14, buildButton("Pure VIT",
                "Only recommended if you know game mechanics and this is your preference",
                "100",
                "VIT",
                "BUILD|pure_vit|vit:100"));

        gui.setItem(15, buildButton("Pure LUK",
                "Only recommended if you know game mechanics and this is your preference",
                "100",
                "LUK",
                "BUILD|pure_luk|luk:100"));

        // ---------- CLASS BUILDS (ONLY show builds for the chosen class) ----------
        String clazz = (profile.getChosenClass() == null) ? "default" : profile.getChosenClass().toLowerCase();

        int[] slots = {
                28, 29, 30, 31, 32, 33,
                37, 38, 39, 40, 41, 42
        };
        int idx = 0;

        switch (clazz) {

            case "swordsman" -> {
                idx = addBuild(gui, slots, idx,
                        "STR/DEX",
                        "recommended if you want to spam skills",
                        "60 / 40",
                        "STR + DEX",
                        "BUILD|sw_str_dex_60_40|str:60,dex:40");

                idx = addBuild(gui, slots, idx,
                        "STR/AGI",
                        "increase hp and damage, will get +50% atkspd and evasion",
                        "50 / 50",
                        "STR + AGI",
                        "BUILD|sw_str_agi_50_50|str:50,agi:50");

                idx = addBuild(gui, slots, idx,
                        "STR/AGI/DEX",
                        "increase hp and damage, +25% atkspd, slight evasion and max cooldown reduction",
                        "35 / 25 / 40",
                        "STR + AGI + DEX",
                        "BUILD|sw_str_agi_dex_35_25_40|str:35,agi:25,dex:40");

                idx = addBuild(gui, slots, idx,
                        "STR/AGI/LUK",
                        "increase hp and damage, +25% atkspd and evasion, max crit",
                        "25 / 25 / 50",
                        "STR + AGI + LUK",
                        "BUILD|sw_str_agi_luk_25_25_50|str:25,agi:25,luk:50");

                idx = addBuild(gui, slots, idx,
                        "STR/AGI/LUK",
                        "increase hp and damage, +50% atkspd and evasion, slight crit",
                        "25 / 50 / 25",
                        "STR + AGI + LUK",
                        "BUILD|sw_str_agi_luk_25_50_25|str:25,agi:50,luk:25");

                idx = addBuild(gui, slots, idx,
                        "AGI/LUK",
                        "crit build with +50% atkspd and evasion",
                        "50 / 50",
                        "AGI + LUK",
                        "BUILD|sw_agi_luk_50_50|agi:50,luk:50");

                idx = addBuild(gui, slots, idx,
                        "STR/LUK",
                        "crit build with atk dmg + hp",
                        "50 / 50",
                        "STR + LUK",
                        "BUILD|sw_str_luk_50_50|str:50,luk:50");

                idx = addBuild(gui, slots, idx,
                        "STR/VIT",
                        "tank with damage",
                        "50 / 50",
                        "STR + VIT",
                        "BUILD|sw_str_vit_50_50|str:50,vit:50");

                idx = addBuild(gui, slots, idx,
                        "STR/VIT/DEX",
                        "semi tank with max cooldown reduction",
                        "30 / 30 / 40",
                        "STR + VIT + DEX",
                        "BUILD|sw_str_vit_dex_30_30_40|str:30,vit:30,dex:40");
            }

            case "archer" -> {
                idx = addBuild(gui, slots, idx,
                        "VIT/DEX",
                        "you can spam skills while being tanky",
                        "60 / 40",
                        "VIT + DEX",
                        "BUILD|ar_vit_dex_60_40|vit:60,dex:40");

                idx = addBuild(gui, slots, idx,
                        "AGI/DEX",
                        "you spam skills while being evasive + damage",
                        "50 / 50",
                        "AGI + DEX",
                        "BUILD|ar_agi_dex_50_50|agi:50,dex:50");

                idx = addBuild(gui, slots, idx,
                        "LUK/DEX",
                        "crit build, max cooldown reduction",
                        "50 / 50",
                        "LUK + DEX",
                        "BUILD|ar_luk_dex_50_50|luk:50,dex:50");

                idx = addBuild(gui, slots, idx,
                        "DEX/AGI/LUK",
                        "slight dmg and cooldown, bonus dmg and evasion, max crit",
                        "25 / 25 / 50",
                        "DEX + AGI + LUK",
                        "BUILD|ar_dex_agi_luk_25_25_50|dex:25,agi:25,luk:50");

                idx = addBuild(gui, slots, idx,
                        "DEX/AGI/LUK",
                        "slight dmg and cooldown, agi bonus dmg and evasion, slight crit",
                        "25 / 50 / 25",
                        "DEX + AGI + LUK",
                        "BUILD|ar_dex_agi_luk_25_50_25|dex:25,agi:50,luk:25");
            }

            case "alchemist" -> {
                idx = addBuild(gui, slots, idx,
                        "INT/DEX",
                        "recommended if you want to spam skills, good for supporting/healing",
                        "60 / 40",
                        "INT + DEX",
                        "BUILD|al_int_dex_60_40|intel:60,dex:40");

                idx = addBuild(gui, slots, idx,
                        "INT/AGI",
                        "increase potion damage, agi bonus potion dmg, melee atkspd and evasion",
                        "50 / 50",
                        "INT + AGI",
                        "BUILD|al_int_agi_50_50|intel:50,agi:50");

                idx = addBuild(gui, slots, idx,
                        "INT/AGI/DEX",
                        "increase potion damage, slight melee atkspd, slight evasion and max cooldown reduction",
                        "35 / 25 / 40",
                        "INT + AGI + DEX",
                        "BUILD|al_int_agi_dex_35_25_40|intel:35,agi:25,dex:40");

                idx = addBuild(gui, slots, idx,
                        "INT/AGI/LUK",
                        "increase potion damage, bonus potion dmg and evasion, max crit",
                        "25 / 25 / 50",
                        "INT + AGI + LUK",
                        "BUILD|al_int_agi_luk_25_25_50|intel:25,agi:25,luk:50");

                idx = addBuild(gui, slots, idx,
                        "INT/AGI/LUK",
                        "increase potion damage, agi bonus potion dmg and evasion, slight crit",
                        "25 / 50 / 25",
                        "INT + AGI + LUK",
                        "BUILD|al_int_agi_luk_25_50_25|intel:25,agi:50,luk:25");

                idx = addBuild(gui, slots, idx,
                        "INT/LUK",
                        "crit build with potion damage",
                        "50 / 50",
                        "INT + LUK",
                        "BUILD|al_int_luk_50_50|intel:50,luk:50");

                idx = addBuild(gui, slots, idx,
                        "INT/LUK/DEX",
                        "potion damage with max cooldown reduction + slight crit chance and damage",
                        "35 / 25 / 40",
                        "INT + LUK + DEX",
                        "BUILD|al_int_luk_dex_35_25_40|intel:35,luk:25,dex:40");

                idx = addBuild(gui, slots, idx,
                        "VIT/DEX/INT",
                        "tanky support, best support build",
                        "30 / 40 / 30",
                        "VIT + DEX + INT",
                        "BUILD|al_vit_dex_int_30_40_30|vit:30,dex:40,intel:30");
            }

            default -> {
                gui.setItem(31, selectableItem(Material.BARRIER,
                        "§cChoose a class first",
                        List.of("§7You cannot apply builds while in default class."),
                        "NOOP"));
            }
        }

        player.openInventory(gui);
    }


    private int addBuild(Inventory gui, int[] slots, int idx,
                         String title, String descTop, String ratioText, String buildText, String action) {
        if (idx >= slots.length) return idx;

        gui.setItem(slots[idx], buildButton(title, descTop, ratioText, buildText, action));
        return idx + 1;
    }


    private ItemStack attrBackButton() {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§c⬅ Back");
        meta.setLore(List.of("§7Return to RPG Menu"));
        meta.getPersistentDataContainer().set(actionKey, PersistentDataType.STRING, "ATTR_BACK");
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack attrResetButton() {
        ItemStack item = new ItemStack(Material.MILK_BUCKET);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§cReset Attributes");
        meta.setLore(List.of(
                "§7Resets ALL class attributes",
                "§7Refunds all allocated points",
                "§eClick to reset"
        ));
        meta.getPersistentDataContainer().set(actionKey, PersistentDataType.STRING, "ATTR_RESET");
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack buildButton(String title, String descTop, String ratioText, String buildText, String action) {
        ItemStack item = new ItemStack(Material.LIME_DYE);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName("§a" + title);

        List<String> lore = new ArrayList<>();

        // TOP: very distinguishable description
        if (descTop != null && !descTop.isBlank()) {
            lore.add("§6§l✦ " + descTop);
            lore.add("§8────────────────");  // separator line
        }

        // Keep ratio, but less prominent than top description
        if (ratioText != null && !ratioText.isBlank()) {
            lore.add("§7Ratio: §f" + ratioText); // example: 60 / 40
        }

        // Build summary (nice & readable)
        if (buildText != null && !buildText.isBlank()) {
            lore.add("§7Build: §f" + buildText); // example: STR + DEX
        }

        lore.add(" ");
        lore.add("§aClick to apply");
        lore.add("§8Allocates in chunks of 100");
        lore.add("§8Remainder stays as points");

        meta.setLore(lore);
        meta.getPersistentDataContainer().set(actionKey, PersistentDataType.STRING, action);

        item.setItemMeta(meta);
        return item;
    }


    private void applyBuild(Player player, UserProfile profile, String action) {
        String clazz = (profile.getChosenClass() == null) ? "default" : profile.getChosenClass().toLowerCase();
        if (clazz.equals("default")) {
            player.sendMessage("§cYou must choose a class first.");
            return;
        }

        String[] parts = action.split("\\|");
        if (parts.length < 3) return;

        String ratioPart = parts[2].trim();
        Map<String, Integer> ratios = new LinkedHashMap<>();

        for (String kv : ratioPart.split(",")) {
            String[] kvp = kv.split(":");
            if (kvp.length != 2) continue;
            String stat = kvp[0].trim().toLowerCase();
            int r;
            try { r = Integer.parseInt(kvp[1].trim()); }
            catch (NumberFormatException ex) { continue; }
            ratios.put(stat, r);
        }

        if (ratios.isEmpty()) return;
        int sum = ratios.values().stream().mapToInt(Integer::intValue).sum();
        if (sum != 100) {
            player.sendMessage("§cInvalid build setup: ratios must total 100 (got " + sum + ").");
            player.sendMessage("This is a bug. Contact Dev");
            return;
        }

        int allocatedForClass = switch (clazz) {
            case "archer" -> profile.getTotalArcherAllocatedPoints();
            case "swordsman" -> profile.getTotalSwordsmanAllocatedPoints();
            case "alchemist" -> profile.getTotalAlchemistAllocatedPoints();
            default -> 0;
        };

        int totalForClass = profile.getCurrentAttributePoints() + allocatedForClass;

        int allocatable = Math.min(totalForClass, 20000);
        int overflow = Math.max(0, totalForClass - allocatable);

        int chunks = allocatable / 100;
        int remainder = (allocatable % 100) + overflow;

        UserProfile.ClassAttributes attrs = switch (clazz) {
            case "archer" -> profile.getArcherClassInfo();
            case "swordsman" -> profile.getSwordsmanClassInfo();
            case "alchemist" -> profile.getAlchemistClassInfo();
            default -> null;
        };
        if (attrs == null) return;

        attrs.resetAttributes();

        // Track how much each stat received (so we can message it)
        Map<String, Integer> allocatedStats = new LinkedHashMap<>();

        for (Map.Entry<String, Integer> e : ratios.entrySet()) {
            String stat = e.getKey();
            int ratio = e.getValue();     // e.g. 50
            int add = chunks * ratio;     // e.g. chunks=2 => 100

            if (add <= 0) continue;

            profile.addPointsToClass(attrs, stat, add);
            allocatedStats.put(stat, add);
        }

        profile.setCurrentAttributePoints(remainder);

        int allocatedNow = chunks * 100;

        // Build readable message: "AGI: 100  LUK: 100"
        String statBreakdown = allocatedStats.entrySet().stream()
                .map(en -> en.getKey().toUpperCase() + ": " + en.getValue())
                .reduce((a, b) -> a + "  §7| §f" + b)
                .orElse("None");

        player.sendMessage("§aBuild applied! §7Allocated §f" + allocatedNow + "§7. Remainder: §f" + remainder);
        player.sendMessage("§eStats: §f" + statBreakdown);
    }



}
