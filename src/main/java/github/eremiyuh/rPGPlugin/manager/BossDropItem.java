package github.eremiyuh.rPGPlugin.manager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class BossDropItem {
    private final ItemStack item;
    private final int quantity;
    private final double dropChance;
    private static final Random random = new Random();
    private final List<String> attributes = Arrays.asList("Agility", "Strength", "Luck", "Vitality", "Intelligence", "Dexterity");


    public BossDropItem(ItemStack item, int quantity, double dropChance) {
        this.item = item;
        this.quantity = quantity;
        this.dropChance = dropChance;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getDropChance() {
        return dropChance;
    }

    // Calculate a random boss drop item from a list
    public static BossDropItem getRandomBossDropItem(List<BossDropItem> dropList) {
        double totalDropChance = 0.0;
        for (BossDropItem dropItem : dropList) {
            totalDropChance += dropItem.getDropChance();
        }

        double randomValue = Math.random() * totalDropChance;

        for (BossDropItem dropItem : dropList) {
            randomValue -= dropItem.getDropChance();
            if (randomValue <= 0) {
                return dropItem; // This is the selected drop item
            }
        }

        return null; // Shouldn't reach here if drop chances are set correctly
    }

    public void addLoreWithBossLevel(ItemStack item, int bossLevel, int playerLevel, boolean isWorldBoss) {
        if (item == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        // --- Reward Multiplier Logic Based on Level Gap ---
        int levelDiff = bossLevel - playerLevel;
        double rewardMultiplier;
        if (Math.abs(levelDiff) > 30) {
            rewardMultiplier = 0.1;
        } else if (Math.abs(levelDiff) > 12) {
            rewardMultiplier = 0.5;
        } else if (Math.abs(levelDiff) > 10) {
            rewardMultiplier = 0.8;
        } else {
            rewardMultiplier = 1.0;
        }

        // --- Select a random attribute ---
        String selectedAttribute = attributes.get(random.nextInt(attributes.size()));

        int baseAttributeValue = Math.max(1, bossLevel / 10);
        if (isWorldBoss) baseAttributeValue *= 5;

        // --- Apply scaling based on reward multiplier ---
        int scaledValue = Math.max(1, (int) (baseAttributeValue * rewardMultiplier));

        int randomStatValue = 1 + random.nextInt(scaledValue); // Random from 1 to scaledValue

        // --- Set lore ---
        List<String> lore = new ArrayList<>();
        lore.add(selectedAttribute + ": " + randomStatValue);
        meta.setLore(lore);
        item.setItemMeta(meta);
    }


}


