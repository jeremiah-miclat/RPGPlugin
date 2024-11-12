package github.eremiyuh.rPGPlugin.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class ItemUtils {
    public static ItemStack getResetItem() {
        ItemStack resetItem = new ItemStack(Material.NETHER_STAR); // Choose any material you prefer
        ItemMeta meta = resetItem.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("Attribute Reset Token");
            meta.setLore(Arrays.asList("Right-click to reset all attributes", "Points will be refunded."));
            resetItem.setItemMeta(meta);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS); // Hides enchantment text
            resetItem.addUnsafeEnchantment(Enchantment.FLAME, 5); // Adds glint without visible enchant
            resetItem.setItemMeta(meta);
        }

        return resetItem;
    }
}

