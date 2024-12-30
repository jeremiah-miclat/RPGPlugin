package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class OreBreakListener implements Listener {


    private final Random random = new Random();
    private final PlayerProfileManager profileManager;

    public OreBreakListener(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @EventHandler
    public void onOreBreak(BlockDropItemEvent event) {

        // Check if the block is an ore (ends with _ORE)
        if (!(event.getBlockState().getType().name().endsWith("_ORE"))) return;
        UserProfile profile = profileManager.getProfile(event.getPlayer().getName());

        if (profile!=null) {
            profile.setActivitypoints(profile.getActivitypoints()+5);
        }

        Player player = event.getPlayer();
        ItemStack itemOnHand = player.getInventory().getItemInMainHand();

        if (!itemOnHand.getType().name().endsWith("_PICKAXE")) return;
        ItemMeta onHandMeta = itemOnHand.getItemMeta();

        // If there's no item meta or lore, return
        if (onHandMeta == null || !onHandMeta.hasLore()) return;

        // Get the lore from the item
        List<String> lore = onHandMeta.getLore();
        String oreHunterLore = null;

        // Look through the lore to find the "Ores Hunter" line
        assert lore != null;
        for (String line : lore) {
            if (line.contains("OresHunter")) {
                oreHunterLore = line;
            }
        }

        // If no "Ores Hunter" lore is found, return
        if (oreHunterLore == null) {

            return;
        };

        // Extract the number from the lore, assuming format is "Ores Hunter X"
        double oreChance = 0;

        try {
            String[] parts = oreHunterLore.split(":");
            oreChance = Integer.parseInt(parts[1].trim()) * .01;
        } catch (Exception e) {
            return; // If parsing fails, return
        }
        int chanceMultiplier = (int) oreChance;
        int multiplier = chanceMultiplier + 2;
        if (oreChance > .5) {
            oreChance = .5;
        }



        // Check if the ore chance condition is met
        if (Math.random() < oreChance) {
            player.sendMessage(multiplier + " multiplier");
            // Modify the drop amounts based on the multiplier
            for (Item item : event.getItems()) {
                ItemStack itemStack = item.getItemStack();
                // Increase the dropped amount based on the multiplier
                int amount = itemStack.getAmount() * multiplier;
                if (amount >= 100) amount = 99;
                itemStack.setAmount(amount);
            }
        }
    }


}