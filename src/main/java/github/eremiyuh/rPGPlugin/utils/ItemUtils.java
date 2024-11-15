package github.eremiyuh.rPGPlugin.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

public class ItemUtils {
    public static ItemStack getResetItem() {
        ItemStack resetItem = new ItemStack(Material.POTION); // Choose any material you prefer
        ItemMeta meta = resetItem.getItemMeta();

        if (meta != null) {
            meta.displayName(Component.text("Attribute Reset Potion").color(TextColor.color(250,250,8)));
            meta.lore(Arrays.asList(
                    Component.text("Right-click to reset all attributes"),
                    Component.text("Points will be refunded.")
            ));

            meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            meta.setEnchantmentGlintOverride(true);
            resetItem.setItemMeta(meta);
        }

        return resetItem;
    }

    public static ItemStack getAbyssIngot() {
        ItemStack abyssIngot = new ItemStack(Material.NETHERITE_INGOT);
        ItemMeta meta = abyssIngot.getItemMeta();

        if (meta!=null) {
            meta.displayName(Component.text("Abyss Mana Stone").color(TextColor.color(250,250,8)));
            meta.lore(Arrays.asList(
                    Component.text("Can be traded with equips"),
                    Component.text("Go to trading hall and look for Yabmat.")
            ));
            meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            meta.setEnchantmentGlintOverride(true);
            abyssIngot.setItemMeta(meta);
        }



        return abyssIngot;
    }
}

