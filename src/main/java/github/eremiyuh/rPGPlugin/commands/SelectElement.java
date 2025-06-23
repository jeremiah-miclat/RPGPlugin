package github.eremiyuh.rPGPlugin.commands;

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

public class SelectElement implements CommandExecutor, Listener {

    private final PlayerProfileManager profileManager;

    public SelectElement(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        UserProfile profile = profileManager.getProfile(player.getName());


        if (args.length == 0) {
            openSelectionGUI(player);
            return true;
        }


        if (args.length != 1) {
            player.sendMessage("Usage: /selectelement < fire |water | ice >");
            return true;
        }

        String element = args[0].toLowerCase();

        if (profile.getSelectedElement().equalsIgnoreCase(element)) {
            player.sendMessage("Current element is " + element);
            return true;
        }


//        if (profile.getLapiz()<100 && !profile.getSelectedElement().equalsIgnoreCase("none")) {
//            player.sendMessage("Need 100 lapis to reselect element");
//            return true;
//        }
//
//        if (!profile.getSelectedElement().equalsIgnoreCase("none")) profile.setLapiz(profile.getLapiz()-100);

        // Validate the chosen element and set it
        switch (element) {
            case "fire":
            case "water":
            case "ice":

                profile.setSelectedElement(element);
                player.sendMessage("You have chosen the " + element + " element!");
                break;
            default:
                player.sendMessage("Invalid element. Choose either fire, water, or ice.");
                return true;
        }

        profileManager.saveProfile(player.getName());  // Save after changing element
        return true;
    }


    private void openSelectionGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 18, "§7Select Your Element");

        // Fire Element
        ItemStack fire = new ItemStack(Material.FIRE_CHARGE);  // Changed to FIRE_CHARGE for better visual representation
        ItemMeta fireMeta = fire.getItemMeta();
        if (fireMeta != null) {
            fireMeta.setDisplayName("§cFire Element");
//            fireMeta.setLore(Arrays.asList("§7Master the flames!", "§7Price: 100 Lapis"));
            fire.setItemMeta(fireMeta);
        }

        // Water Element
        ItemStack water = new ItemStack(Material.POTION);  // Changed to POTION for better visual representation
        ItemMeta waterMeta = water.getItemMeta();
        if (waterMeta != null) {
            waterMeta.setDisplayName("§9Water Element");
//            waterMeta.setLore(Arrays.asList("§7Control the oceans!", "§7Price: 100 Lapis"));
            water.setItemMeta(waterMeta);
        }

        // Ice Element
        ItemStack ice = new ItemStack(Material.PACKED_ICE);  // Changed to PACKED_ICE for better visual representation
        ItemMeta iceMeta = ice.getItemMeta();
        if (iceMeta != null) {
            iceMeta.setDisplayName("§bIce Element");
//            iceMeta.setLore(Arrays.asList("§7Harness the cold!", "§7Price: 100 Lapis"));
            ice.setItemMeta(iceMeta);
        }

        // Arrange items in the inventory with some spacing
        gui.setItem(2, fire);
        gui.setItem(4, water);
        gui.setItem(6, ice);

        // Optionally, add "back" or "close" button if needed (slot 8)
        ItemStack closeButton = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeButton.getItemMeta();
        if (closeMeta != null) {
            closeMeta.setDisplayName("§cClose");
            closeButton.setItemMeta(closeMeta);
        }
        gui.setItem(8, closeButton);

        player.openInventory(gui);
    }




    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();

        if (!("§7Select Your Element").equals(event.getView().getTitle())) return;

        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        // Handle element selection
        String element = null;
        switch (clickedItem.getType()) {
            case FIRE_CHARGE:
                element = "fire";
                break;
            case POTION:  // Adjust the type check if you use a different material for water
                element = "water";
                break;
            case PACKED_ICE:
                element = "ice";
                break;
            case BARRIER:  // Close the GUI if the "Close" button is clicked
                player.closeInventory();
                return;
        }

        if (element != null) {
            player.closeInventory();
            player.performCommand("selectelement " + element);
        }
    }

}
