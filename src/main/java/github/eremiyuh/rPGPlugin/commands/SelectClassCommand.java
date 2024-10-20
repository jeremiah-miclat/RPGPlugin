package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerClassManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class SelectClassCommand implements CommandExecutor {

    private final PlayerClassManager playerClassManager;

    public SelectClassCommand(PlayerClassManager playerClassManager) {
        this.playerClassManager = playerClassManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can select a class!");
            return true;
        }

        Player player = (Player) sender;

        // Check if the player can select a class
        if (!playerClassManager.canSelectClass(player)) {
            long lastSelectionTime = playerClassManager.getLastSelectionTime(player);
            long timeUntilNextSelection = TimeUnit.HOURS.toMillis(24) - (System.currentTimeMillis() - lastSelectionTime);
            long secondsUntilNextSelection = TimeUnit.MILLISECONDS.toSeconds(timeUntilNextSelection);

            player.sendMessage("You can select a class again in " + secondsUntilNextSelection + " seconds.");
            return true;
        }

        openClassSelectionGUI(player);
        return true;
    }

    private void openClassSelectionGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 9, "Select Your Class");

        // Create items for each class
        gui.setItem(2, createClassItem(Material.IRON_SWORD, "Swordsman", "A brave warrior skilled with swords."));
        gui.setItem(4, createClassItem(Material.BOW, "Archer", "A nimble marksman with a bow."));
        gui.setItem(6, createClassItem(Material.POTION, "Alchemist", "A master of potions and elixirs."));

        // Open the GUI for the player
        player.openInventory(gui);
    }

    private ItemStack createClassItem(Material material, String className, String description) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§a" + className); // Green name
            meta.setLore(Arrays.asList(description));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }
        return item;
    }
}
