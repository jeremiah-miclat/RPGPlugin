package github.eremiyuh.rPGPlugin.commands;

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
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class SelectAbyssTrait implements CommandExecutor, Listener {

    private final PlayerProfileManager profileManager;
    private final NamespacedKey traitKey;
    private final Map<UUID, Long> traitCooldowns = new HashMap<>();
//    private static final long COOLDOWN_MILLIS = 10 * 60 * 1000; // 10 minutes
    private static final long COOLDOWN_MILLIS = 60 * 1000;

    public SelectAbyssTrait(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
        this.traitKey = new NamespacedKey("rpgplugin", "abyss_trait");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        openTraitGUI(player);
        return true;
    }

    private void openTraitGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "§8Select Abyss Trait");

        gui.setItem(10, createTraitItem(Material.REDSTONE, "§cBloodlust", Arrays.asList(
                "§7🩸 Lifesteal +5%",
                "§7💔 Max HP -10%",
                "§8A taste of power, at a cost."
        ), "Bloodlust"));

        gui.setItem(12, createTraitItem(Material.BLAZE_POWDER, "§6Frenzy", Arrays.asList(
                "§7⚔️ Damage +100%",
                "§7🛡️ Damage Taken +100%",
                "§8Go all-in, or fall faster."
        ), "Frenzy"));

        gui.setItem(14, createTraitItem(Material.IRON_CHESTPLATE, "§bFortress", Arrays.asList(
                "§7❤️ Max HP +100%",
                "§7⚔️ Damage -20%",
                "§8Endure, but strike soft."
        ), "Fortress"));

        gui.setItem(16, createTraitItem(Material.NETHER_STAR, "§dGamble", Arrays.asList(
                "§7❌ Crit Rate -25%",
                "§7💥 Crit Damage +50%",
                "§8Strike less often, but harder."
        ), "Gamble"));

        ItemStack closeButton = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeButton.getItemMeta();
        if (closeMeta != null) {
            closeMeta.setDisplayName("§cClose");
            closeButton.setItemMeta(closeMeta);
        }
        gui.setItem(22, closeButton);

        player.openInventory(gui);
    }

    private ItemStack createTraitItem(Material material, String name, List<String> lore, String traitId) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            meta.getPersistentDataContainer().set(traitKey, PersistentDataType.STRING, traitId);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        if (!event.getView().getTitle().equals("§8Select Abyss Trait")) return;

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        ItemMeta meta = clicked.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        String traitId = container.get(traitKey, PersistentDataType.STRING);

        if (traitId == null) {
            if (clicked.getType() == Material.BARRIER) {
                player.closeInventory();
            }
            return;
        }

        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();

        if (traitCooldowns.containsKey(uuid)) {
            long lastUsed = traitCooldowns.get(uuid);
            long remaining = lastUsed + COOLDOWN_MILLIS - now;

            if (remaining > 0) {
                long seconds = remaining / 1000;
                long minutes = seconds / 60;
                seconds %= 60;
                player.sendMessage("§cYou can select a new Abyss Trait in " + minutes + "m " + seconds + "s.");
                return;
            }
        }

        // Apply trait
        UserProfile profile = profileManager.getProfile(player.getName());
        profile.setAbyssTrait(traitId);
        profileManager.saveProfile(player.getName());

        switch (traitId) {
            case "Bloodlust" -> player.sendMessage("§cSelected Abyss Trait: §lBloodlust");
            case "Frenzy"    -> player.sendMessage("§6Selected Abyss Trait: §lFrenzy");
            case "Fortress"  -> player.sendMessage("§bSelected Abyss Trait: §lFortress");
            case "Gamble"    -> player.sendMessage("§dSelected Abyss Trait: §lGamble");
            default          -> player.sendMessage("§cInvalid trait selection.");
        }

        traitCooldowns.put(uuid, now);
        player.closeInventory();
    }
}
