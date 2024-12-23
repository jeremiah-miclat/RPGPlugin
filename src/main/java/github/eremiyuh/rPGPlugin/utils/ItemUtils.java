package github.eremiyuh.rPGPlugin.utils;

import io.papermc.paper.datacomponent.item.Equippable;
import io.papermc.paper.registry.set.RegistryKeySet;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.EquippableComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
                    Component.text("Can be traded for equipment"),
                    Component.text("Go to trading hall and look for Yabmat."),
                    Component.text("Buy at /abyssstore for 100,000 abyss points.")
            ));
            meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            meta.setEnchantmentGlintOverride(true);

            abyssIngot.setItemMeta(meta);
        }



        return abyssIngot;
    }

    public static ItemStack getAbyssPotion() {
        ItemStack resetItem = new ItemStack(Material.POTION); // Choose any material you prefer
        ItemMeta meta = resetItem.getItemMeta();

        if (meta != null) {
            meta.displayName(Component.text("Abyss Heal Potion").color(TextColor.color(255,0,0)));
            meta.lore(Arrays.asList(
                    Component.text("Cost: 1,000 abyss points."),
                    Component.text("Right-click to use"),
                    Component.text("Health will be restored.")
            ));

            meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            meta.setEnchantmentGlintOverride(true);
            resetItem.setItemMeta(meta);
        }

        return resetItem;
    }

    public static ItemStack getAbyssOre() {
        ItemStack pointItem = new ItemStack(Material.FLINT); // Choose any material you prefer
        ItemMeta meta = pointItem.getItemMeta();

        if (meta != null) {
            meta.displayName(Component.text("Abyss Ore").color(TextColor.color(255,0,0)));
            meta.lore(Arrays.asList(
                    Component.text("Cost: 1,000 abyss points."),
                    Component.text("Right-click to gain 1,000 abyss points")
            ));

            meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            meta.setEnchantmentGlintOverride(true);
            pointItem.setItemMeta(meta);
        }

        return pointItem;
    }

    public static ItemStack getBlackSaber() {
        ItemStack pointItem = new ItemStack(Material.NETHERITE_SWORD); // Choose any material you prefer
        ItemMeta meta = pointItem.getItemMeta();

        if (meta != null) {
            meta.displayName(Component.text("Dark Blade").color(TextColor.color(255,0,0)));
            meta.lore(Arrays.asList(
                    Component.text("Cosmetic")
            ));

            meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            meta.setEnchantmentGlintOverride(false);


            meta.setItemModel(NamespacedKey.minecraft("dark_blade"));

            EquippableComponent equippableComponent = meta.getEquippable();

            equippableComponent.setSwappable(false);

            meta.setEquippable(equippableComponent);

            pointItem.setItemMeta(meta);
        }

        return pointItem;
    }

    public static ItemStack getKatana() {
        ItemStack pointItem = new ItemStack(Material.NETHERITE_SWORD); // Choose any material you prefer
        ItemMeta meta = pointItem.getItemMeta();

        if (meta != null) {
            meta.displayName(Component.text("Broken Katana").color(TextColor.color(255,0,0)));
            meta.lore(Arrays.asList(
                    Component.text("Cosmetic")
            ));

            meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            meta.setEnchantmentGlintOverride(false);


            meta.setItemModel(NamespacedKey.minecraft("katana"));

            EquippableComponent equippableComponent = meta.getEquippable();

            equippableComponent.setSwappable(false);

            meta.setEquippable(equippableComponent);

            pointItem.setItemMeta(meta);
        }

        return pointItem;
    }

    public static ItemStack getDragonSlayer() {
        ItemStack pointItem = new ItemStack(Material.NETHERITE_SWORD); // Choose any material you prefer
        ItemMeta meta = pointItem.getItemMeta();

        if (meta != null) {
            meta.displayName(Component.text("Dragon Slaying Blade").color(TextColor.color(255,0,0)));
            meta.lore(Arrays.asList(
                    Component.text("Cosmetic")
            ));

            meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            meta.setEnchantmentGlintOverride(false);


            meta.setItemModel(NamespacedKey.minecraft("dragonslayingblade"));

            EquippableComponent equippableComponent = meta.getEquippable();

            equippableComponent.setSwappable(false);

            meta.setEquippable(equippableComponent);

            pointItem.setItemMeta(meta);
        }

        return pointItem;
    }

    public static ItemStack getCompositeBow() {
        ItemStack pointItem = new ItemStack(Material.BOW); // Choose any material you prefer
        ItemMeta meta = pointItem.getItemMeta();

        if (meta != null) {
            meta.displayName(Component.text("Composite Bow").color(TextColor.color(255,0,0)));
            meta.lore(Arrays.asList(
                    Component.text("Cosmetic")
            ));

            meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            meta.setEnchantmentGlintOverride(false);
            meta.setItemModel(NamespacedKey.minecraft("composite_bow"));
            pointItem.setItemMeta(meta);
        }

        return pointItem;
    }

    public static ItemStack getHaloChestPlate() {
        return createCustomArmor(
                Material.NETHERITE_CHESTPLATE,
                "Halo Chestplate",
                Arrays.asList(Component.text("Cosmetic")),
                "halo",
                "halo_chestplate",
                EquipmentSlot.CHEST
        );
    }

    public static ItemStack getHaloHelmet() {
        return createCustomArmor(
                Material.NETHERITE_HELMET,
                "Halo Helmet",
                Arrays.asList(Component.text("Cosmetic")),
                "halo",
                "halo_helmet",
                EquipmentSlot.HEAD
        );
    }

    public static ItemStack getHaloLeggings() {
        return createCustomArmor(
                Material.NETHERITE_LEGGINGS,
                "Halo Leggings",
                Arrays.asList(Component.text("Cosmetic")),
                "halo",
                "halo_leggings",
                EquipmentSlot.LEGS
        );
    }

    public static ItemStack getHaloBoots() {
        return createCustomArmor(
                Material.NETHERITE_BOOTS,
                "Halo Boots",
                Arrays.asList(Component.text("Cosmetic")),
                "halo",
                "halo_boots",
                EquipmentSlot.FEET
        );
    }




    public static ItemStack createCustomArmor(
            Material material,
            String displayName,
            List<Component> lore,
            String modelKey,
            String itemModelKey,
            EquipmentSlot slot
    ) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.displayName(Component.text(displayName).color(TextColor.color(255, 0, 0))); // Customize color as needed
            meta.lore(lore);

            meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            meta.setEnchantmentGlintOverride(false);

            EquippableComponent equippableComponent = meta.getEquippable();

            equippableComponent.setModel(NamespacedKey.minecraft(modelKey));
            equippableComponent.setSlot(slot);
            meta.setEquippable(equippableComponent);
            meta.setItemModel(NamespacedKey.minecraft(itemModelKey));


            item.setItemMeta(meta);
        }
        return item;
    }





    //Agility
    public static ItemStack getAgilityHelmet() {
        return createEquipment(Material.IRON_HELMET, "Agility", 1);
    }

    public static ItemStack getAgilityChestplate() {
        return createEquipment(Material.IRON_CHESTPLATE, "Agility", 1);
    }

    public static ItemStack getAgilityLeggings() {
        return createEquipment(Material.IRON_LEGGINGS, "Agility", 1);
    }

    public static ItemStack getAgilityBoots() {
        return createEquipment(Material.IRON_BOOTS, "Agility", 1);
    }

    public static ItemStack getAgilityBook() {
        return createEquipment(Material.BOOK, "Agility", 1);
    }

    public static ItemStack getAgilityBow() {
        return createEquipment(Material.BOW, "Agility", 1);
    }

    public static ItemStack getAgilityCrossbow() {
        return createEquipment(Material.CROSSBOW, "Agility", 1);
    }

    public static ItemStack getAgilitySword() {
        return createEquipment(Material.IRON_SWORD, "Agility", 1);
    }

    // Vitality
    public static ItemStack getVitalityHelmet() {
        return createEquipment(Material.IRON_HELMET, "Vitality", 1);
    }

    public static ItemStack getVitalityChestplate() {
        return createEquipment(Material.IRON_CHESTPLATE, "Vitality", 1);
    }

    public static ItemStack getVitalityLeggings() {
        return createEquipment(Material.IRON_LEGGINGS, "Vitality", 1);
    }

    public static ItemStack getVitalityBoots() {
        return createEquipment(Material.IRON_BOOTS, "Vitality", 1);
    }

    public static ItemStack getVitalityBook() {
        return createEquipment(Material.BOOK, "Vitality", 1);
    }

    public static ItemStack getVitalityBow() {
        return createEquipment(Material.BOW, "Vitality", 1);
    }

    public static ItemStack getVitalityCrossbow() {
        return createEquipment(Material.CROSSBOW, "Vitality", 1);
    }

    public static ItemStack getVitalitySword() {
        return createEquipment(Material.IRON_SWORD, "Vitality", 1);
    }

    //Strength

    public static ItemStack getStrengthHelmet() {
        return createEquipment(Material.IRON_HELMET, "Strength", 1);
    }

    public static ItemStack getStrengthChestplate() {
        return createEquipment(Material.IRON_CHESTPLATE, "Strength", 1);
    }

    public static ItemStack getStrengthLeggings() {
        return createEquipment(Material.IRON_LEGGINGS, "Strength", 1);
    }

    public static ItemStack getStrengthBoots() {
        return createEquipment(Material.IRON_BOOTS, "Strength", 1);
    }

    public static ItemStack getStrengthBook() {
        return createEquipment(Material.BOOK, "Strength", 1);
    }

    public static ItemStack getStrengthBow() {
        return createEquipment(Material.BOW, "Strength", 1);
    }

    public static ItemStack getStrengthCrossbow() {
        return createEquipment(Material.CROSSBOW, "Strength", 1);
    }

    public static ItemStack getStrengthSword() {
        return createEquipment(Material.IRON_SWORD, "Strength", 1);
    }

    // Dexterity
    public static ItemStack getDexterityHelmet() {
        return createEquipment(Material.IRON_HELMET, "Dexterity", 1);
    }

    public static ItemStack getDexterityChestplate() {
        return createEquipment(Material.IRON_CHESTPLATE, "Dexterity", 1);
    }

    public static ItemStack getDexterityLeggings() {
        return createEquipment(Material.IRON_LEGGINGS, "Dexterity", 1);
    }

    public static ItemStack getDexterityBoots() {
        return createEquipment(Material.IRON_BOOTS, "Dexterity", 1);
    }

    public static ItemStack getDexterityBook() {
        return createEquipment(Material.BOOK, "Dexterity", 1);
    }

    public static ItemStack getDexterityBow() {
        return createEquipment(Material.BOW, "Dexterity", 1);
    }

    public static ItemStack getDexterityCrossbow() {
        return createEquipment(Material.CROSSBOW, "Dexterity", 1);
    }

    public static ItemStack getDexteritySword() {
        return createEquipment(Material.IRON_SWORD, "Dexterity", 1);
    }

    // Luck
    public static ItemStack getLuckHelmet() {
        return createEquipment(Material.IRON_HELMET, "Luck", 1);
    }

    public static ItemStack getLuckChestplate() {
        return createEquipment(Material.IRON_CHESTPLATE, "Luck", 1);
    }

    public static ItemStack getLuckLeggings() {
        return createEquipment(Material.IRON_LEGGINGS, "Luck", 1);
    }

    public static ItemStack getLuckBoots() {
        return createEquipment(Material.IRON_BOOTS, "Luck", 1);
    }

    public static ItemStack getLuckBook() {
        return createEquipment(Material.BOOK, "Luck", 1);
    }

    public static ItemStack getLuckBow() {
        return createEquipment(Material.BOW, "Luck", 1);
    }

    public static ItemStack getLuckCrossbow() {
        return createEquipment(Material.CROSSBOW, "Luck", 1);
    }

    public static ItemStack getLuckSword() {
        return createEquipment(Material.IRON_SWORD, "Luck", 1);
    }

    // Intelligence
    public static ItemStack getIntelligenceHelmet() {
        return createEquipment(Material.IRON_HELMET, "Intelligence", 1);
    }

    public static ItemStack getIntelligenceChestplate() {
        return createEquipment(Material.IRON_CHESTPLATE, "Intelligence", 1);
    }

    public static ItemStack getIntelligenceLeggings() {
        return createEquipment(Material.IRON_LEGGINGS, "Intelligence", 1);
    }

    public static ItemStack getIntelligenceBoots() {
        return createEquipment(Material.IRON_BOOTS, "Intelligence", 1);
    }

    public static ItemStack getIntelligenceBook() {
        return createEquipment(Material.BOOK, "Intelligence", 1);
    }

    public static ItemStack getIntelligenceBow() {
        return createEquipment(Material.BOW, "Intelligence", 1);
    }

    public static ItemStack getIntelligenceCrossbow() {
        return createEquipment(Material.CROSSBOW, "Intelligence", 1);
    }

    public static ItemStack getIntelligenceSword() {
        return createEquipment(Material.IRON_SWORD, "Intelligence", 1);
    }

    // Vitality
    public static ItemStack getVitalityHelmet5() {
        return createEquipment(Material.NETHERITE_HELMET, "Vitality", 5);
    }

    public static ItemStack getVitalityChestplate5() {
        return createEquipment(Material.NETHERITE_CHESTPLATE, "Vitality", 5);
    }

    public static ItemStack getVitalityLeggings5() {
        return createEquipment(Material.NETHERITE_LEGGINGS, "Vitality", 5);
    }

    public static ItemStack getVitalityBoots5() {
        return createEquipment(Material.NETHERITE_BOOTS, "Vitality", 5);
    }

    public static ItemStack getVitalityBook5() {
        return createEquipment(Material.BOOK, "Vitality", 5);
    }

    public static ItemStack getVitalityBow5() {
        return createEquipment(Material.BOW, "Vitality", 5);
    }

    public static ItemStack getVitalityCrossbow5() {
        return createEquipment(Material.CROSSBOW, "Vitality", 5);
    }

    public static ItemStack getVitalitySword5() {
        return createEquipment(Material.NETHERITE_SWORD, "Vitality", 5);
    }

    //Strength

    public static ItemStack getStrengthHelmet5() {
        return createEquipment(Material.NETHERITE_HELMET, "Strength", 5);
    }

    public static ItemStack getStrengthChestplate5() {
        return createEquipment(Material.NETHERITE_CHESTPLATE, "Strength", 5);
    }

    public static ItemStack getStrengthLeggings5() {
        return createEquipment(Material.NETHERITE_LEGGINGS, "Strength", 5);
    }

    public static ItemStack getStrengthBoots5() {
        return createEquipment(Material.NETHERITE_BOOTS, "Strength", 5);
    }

    public static ItemStack getStrengthBook5() {
        return createEquipment(Material.BOOK, "Strength", 5);
    }

    public static ItemStack getStrengthBow5() {
        return createEquipment(Material.BOW, "Strength", 5);
    }

    public static ItemStack getStrengthCrossbow5() {
        return createEquipment(Material.CROSSBOW, "Strength", 5);
    }

    public static ItemStack getStrengthSword5() {
        return createEquipment(Material.NETHERITE_SWORD, "Strength", 5);
    }

    // Dexterity
    public static ItemStack getDexterityHelmet5() {
        return createEquipment(Material.NETHERITE_HELMET, "Dexterity", 5);
    }

    public static ItemStack getDexterityChestplate5() {
        return createEquipment(Material.NETHERITE_CHESTPLATE, "Dexterity", 5);
    }

    public static ItemStack getDexterityLeggings5() {
        return createEquipment(Material.NETHERITE_LEGGINGS, "Dexterity", 5);
    }

    public static ItemStack getDexterityBoots5() {
        return createEquipment(Material.NETHERITE_BOOTS, "Dexterity", 5);
    }

    public static ItemStack getDexterityBook5() {
        return createEquipment(Material.BOOK, "Dexterity", 5);
    }

    public static ItemStack getDexterityBow5() {
        return createEquipment(Material.BOW, "Dexterity", 5);
    }

    public static ItemStack getDexterityCrossbow5() {
        return createEquipment(Material.CROSSBOW, "Dexterity", 5);
    }

    public static ItemStack getDexteritySword5() {
        return createEquipment(Material.NETHERITE_SWORD, "Dexterity", 5);
    }

    // Luck
    public static ItemStack getLuckHelmet5() {
        return createEquipment(Material.NETHERITE_HELMET, "Luck", 5);
    }

    public static ItemStack getLuckChestplate5() {
        return createEquipment(Material.NETHERITE_CHESTPLATE, "Luck", 5);
    }

    public static ItemStack getLuckLeggings5() {
        return createEquipment(Material.NETHERITE_LEGGINGS, "Luck", 5);
    }

    public static ItemStack getLuckBoots5() {
        return createEquipment(Material.NETHERITE_BOOTS, "Luck", 5);
    }

    public static ItemStack getLuckBook5() {
        return createEquipment(Material.BOOK, "Luck", 5);
    }

    public static ItemStack getLuckBow5() {
        return createEquipment(Material.BOW, "Luck", 5);
    }

    public static ItemStack getLuckCrossbow5() {
        return createEquipment(Material.CROSSBOW, "Luck", 5);
    }

    public static ItemStack getLuckSword5() {
        return createEquipment(Material.NETHERITE_SWORD, "Luck", 5);
    }

    // Intelligence
    public static ItemStack getIntelligenceHelmet5() {
        return createEquipment(Material.NETHERITE_HELMET, "Intelligence", 5);
    }

    public static ItemStack getIntelligenceChestplate5() {
        return createEquipment(Material.NETHERITE_CHESTPLATE, "Intelligence", 5);
    }

    public static ItemStack getIntelligenceLeggings5() {
        return createEquipment(Material.NETHERITE_LEGGINGS, "Intelligence", 5);
    }

    public static ItemStack getIntelligenceBoots5() {
        return createEquipment(Material.NETHERITE_BOOTS, "Intelligence", 5);
    }

    public static ItemStack getIntelligenceBook5() {
        return createEquipment(Material.BOOK, "Intelligence", 5);
    }

    public static ItemStack getIntelligenceBow5() {
        return createEquipment(Material.BOW, "Intelligence", 5);
    }

    public static ItemStack getIntelligenceCrossbow5() {
        return createEquipment(Material.CROSSBOW, "Intelligence", 5);
    }

    public static ItemStack getIntelligenceSword5() {
        return createEquipment(Material.NETHERITE_SWORD, "Intelligence", 5);
    }

    public static ItemStack getAgilityHelmet5() {
        return createEquipment(Material.NETHERITE_HELMET, "Agility", 5);
    }

    public static ItemStack getAgilityChestplate5() {
        return createEquipment(Material.NETHERITE_CHESTPLATE, "Agility", 5);
    }

    public static ItemStack getAgilityLeggings5() {
        return createEquipment(Material.NETHERITE_LEGGINGS, "Agility", 5);
    }

    public static ItemStack getAgilityBoots5() {
        return createEquipment(Material.NETHERITE_BOOTS, "Agility", 5);
    }

    public static ItemStack getAgilityBook5() {
        return createEquipment(Material.BOOK, "Agility", 5);
    }

    public static ItemStack getAgilityBow5() {
        return createEquipment(Material.BOW, "Agility", 5);
    }

    public static ItemStack getAgilityCrossbow5() {
        return createEquipment(Material.CROSSBOW, "Agility", 5);
    }

    public static ItemStack getAgilitySword5() {
        return createEquipment(Material.NETHERITE_SWORD, "Agility", 5);
    }






    private static ItemStack createEquipment(Material material, String attribute, int value) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.displayName(Component.text(material.name().replace("_", " ") + " (" + attribute + ")").color(TextColor.color(200, 200, 255)));
            meta.lore(Arrays.asList(
                    Component.text(attribute + ": " + value)
            ));
            item.setItemMeta(meta);
        }

        return item;
    }

}

