package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.utils.ItemUtils;
import io.papermc.paper.event.player.PlayerTradeEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class SummonVillagerListener implements Listener {

    @EventHandler
    public void onVillagerSpawn(CreatureSpawnEvent event) {
        if (event.getEntityType() == EntityType.VILLAGER) {
            Villager villager = (Villager) event.getEntity();
//            villager.setCustomNameVisible(true);
            // Check if the villager was spawned using a spawn egg with a custom name "Armorer"
            if (villager.customName() != null && Objects.equals(villager.customName(), Component.text("Armorer"))) {
                setupArmorerVillager(villager);
            }
            if (villager.customName() != null && Objects.equals(villager.customName(), Component.text("Farmer"))) {
                setupFarmerVillager(villager);
            }
            if (villager.customName() != null && Objects.equals(villager.customName(), Component.text("Butcher"))) {
                setupButcherVillager(villager);
            }
            if (villager.customName() != null && Objects.equals(villager.customName(), Component.text("Cartographer"))) {
                setupCartographerVillager(villager);
            }
            if (villager.customName() != null && Objects.equals(villager.customName(), Component.text("Cleric"))) {
                setupClericVillager(villager);
            }
            if (villager.customName() != null && Objects.equals(villager.customName(), Component.text("Fisherman"))) {
                setupFishermanVillager(villager);
            }
            if (villager.customName() != null && Objects.equals(villager.customName(), Component.text("Fletcher"))) {
                setupFletcherVillager(villager);
            }
            if (villager.customName() != null && Objects.equals(villager.customName(), Component.text("LeatherWorker"))) {
                setupLeatherWorkerVillager(villager);
            }
            if (villager.customName() != null && Objects.equals(villager.customName(), Component.text("Librarian"))) {
                setupLibrarianWorkerVillager(villager);
            }
            if (villager.customName() != null && Objects.equals(villager.customName(), Component.text("Mason"))) {
                setupMasonVillager(villager);
            }
            if (villager.customName() != null && Objects.equals(villager.customName(), Component.text("Shepherd"))) {
                setupShepherdVillager(villager);
            }
            if (villager.customName() != null && Objects.equals(villager.customName(), Component.text("Toolsmith"))) {
                setupToolSmithVillager(villager);
            }
            if (villager.customName() != null && Objects.equals(villager.customName(), Component.text("Weaponsmith"))) {
                setupWeaponSmithVillager(villager);
            }
            if (villager.customName() != null && Objects.equals(villager.customName(), Component.text("Abysstrader"))) {
                setupAbyssTraderVillager(villager);
            }
        }
    }

    @EventHandler
    public void onTradeSelect(PlayerTradeEvent event) {
        Player player = event.getPlayer();
        AbstractVillager villager = event.getVillager();

        // Check if villager has a custom name
        if (villager.getCustomName() != null) {
            MerchantRecipe recipe = event.getTrade();

            // Get the amount of items the player receives from the trade
            int receivedAmount = recipe.getResult().getAmount();

            // Give 5 XP per item received
            int xpToGive = 5 * receivedAmount;
            player.giveExp(xpToGive);

            // Optional: Send feedback message to player
//            player.sendMessage(ChatColor.GREEN + "You received " + xpToGive + " XP for trading with " + villager.getCustomName());
        }
    }

    private void setupArmorerVillager(Villager villager) {
        villager.setAI(false);
        villager.setProfession(Villager.Profession.ARMORER);
        villager.setVillagerExperience(1);
        villager.setVillagerLevel(5);
        villager.customName(Component.text("Armorer").color(TextColor.color(39,222,94)));

        List<MerchantRecipe> trades = new ArrayList<>();

        // Define trades
        trades.add(createTrade(Material.COAL, 10, Material.EMERALD, 1));
        trades.add(createTrade(Material.EMERALD, 2, Material.IRON_HELMET, 1));
        trades.add(createTrade(Material.EMERALD, 5, Material.IRON_CHESTPLATE, 1));
        trades.add(createTrade(Material.EMERALD, 3, Material.IRON_LEGGINGS, 1));
        trades.add(createTrade(Material.EMERALD, 2, Material.IRON_BOOTS, 1));
        trades.add(createTrade(Material.IRON_INGOT, 3, Material.EMERALD, 1));
        trades.add(createTrade(Material.EMERALD, 18, Material.BELL, 1));
        trades.add(createTrade(Material.EMERALD, 1, Material.LAVA_BUCKET, 1));
        trades.add(createTrade(Material.DIAMOND, 1, Material.EMERALD, 1));
        trades.add(createTrade(Material.EMERALD, 3, Material.SHIELD, 1));
        trades.add(createTrade(Material.EMERALD, 10, Material.DIAMOND_LEGGINGS, 1));
        trades.add(createTrade(Material.EMERALD, 8, Material.DIAMOND_BOOTS, 1));
        trades.add(createTrade(Material.EMERALD, 14, Material.DIAMOND_CHESTPLATE, 1));
        trades.add(createTrade(Material.EMERALD, 10, Material.DIAMOND_HELMET, 1));

        villager.setRecipes(trades);
        villager.setAI(true);
    }

    private void setupFarmerVillager(Villager villager) {
        villager.setAI(false);
        villager.setProfession(Villager.Profession.FARMER);
        villager.setVillagerExperience(1);
        villager.setVillagerLevel(5);
        villager.customName(Component.text("Farmer").color(TextColor.color(39,222,94)));

        List<MerchantRecipe> trades = new ArrayList<>();

        // Define trades
        trades.add(createTrade(Material.WHEAT, 12, Material.EMERALD, 1));
        trades.add(createTrade(Material.POTATO, 12, Material.EMERALD, 1));
        trades.add(createTrade(Material.CARROT, 12, Material.EMERALD, 1));
        trades.add(createTrade(Material.BEETROOT, 10, Material.EMERALD, 1));
        trades.add(createTrade(Material.EMERALD, 1, Material.BREAD, 8));
        trades.add(createTrade(Material.PUMPKIN, 4, Material.EMERALD, 1));
        trades.add(createTrade(Material.EMERALD, 1, Material.PUMPKIN_PIE, 6));
        trades.add(createTrade(Material.EMERALD, 1, Material.APPLE, 6));
        trades.add(createTrade(Material.MELON, 6, Material.EMERALD, 1));
        trades.add(createTrade(Material.EMERALD, 2, Material.COOKIE, 22));
        trades.add(createTrade(Material.EMERALD, 1, Material.CAKE, 2));
        trades.add(createTrade(Material.EMERALD, 3, Material.GOLDEN_CARROT, 5));
        trades.add(createTrade(Material.EMERALD, 3, Material.GLISTERING_MELON_SLICE, 5));

        villager.setRecipes(trades);
        villager.setAI(true);
    }

    private void setupButcherVillager(Villager villager) {
        villager.setAI(false);
        villager.setProfession(Villager.Profession.BUTCHER);
        villager.setVillagerExperience(1);
        villager.setVillagerLevel(5);
        villager.customName(Component.text("Butcher").color(TextColor.color(39,222,94)));

        List<MerchantRecipe> trades = new ArrayList<>();

        // Define trades
        trades.add(createTrade(Material.CHICKEN, 10, Material.EMERALD, 1));
        trades.add(createTrade(Material.RABBIT, 3, Material.EMERALD, 1));
        trades.add(createTrade(Material.PORKCHOP, 5, Material.EMERALD, 1));
        trades.add(createTrade(Material.EMERALD, 1, Material.RABBIT_STEW, 2));
        trades.add(createTrade(Material.COAL, 10, Material.EMERALD, 1));
        trades.add(createTrade(Material.EMERALD, 1, Material.COOKED_CHICKEN, 10));
        trades.add(createTrade(Material.EMERALD, 1, Material.COOKED_PORKCHOP, 8));
        trades.add(createTrade(Material.BEEF, 7, Material.EMERALD, 1));
        trades.add(createTrade(Material.MUTTON, 5, Material.EMERALD, 1));
        trades.add(createTrade(Material.DRIED_KELP_BLOCK, 6, Material.EMERALD, 1));
        trades.add(createTrade(Material.SWEET_BERRIES, 6, Material.EMERALD, 1));


        villager.setRecipes(trades);
        villager.setAI(true);
    }

    private void setupCartographerVillager(Villager villager) {
        villager.setAI(false);
        villager.setVillagerExperience(1);
        villager.setProfession(Villager.Profession.CARTOGRAPHER);
        villager.setVillagerExperience(1);
        villager.setVillagerLevel(5);
        villager.customName(Component.text("Cartographer").color(TextColor.color(39,222,94)));

        List<MerchantRecipe> trades = new ArrayList<>();

        // Define trades
        trades.add(createTrade(Material.PAPER, 16, Material.EMERALD, 1));
        trades.add(createTrade(Material.EMERALD, 5, Material.MAP, 1));
        trades.add(createTrade(Material.GLASS_PANE, 8, Material.EMERALD, 1));
        trades.add(createTrade(Material.COMPASS, 1, Material.EMERALD, 1));
        trades.add(createTrade(Material.EMERALD, 5, Material.ITEM_FRAME, 1));
        trades.add(createTrade(Material.EMERALD, 6, Material.GLOBE_BANNER_PATTERN, 1));
        trades.add(createTrade(Material.EMERALD, 2, Material.BLACK_BANNER, 1));
        trades.add(createTrade(Material.EMERALD, 2, Material.BLUE_BANNER, 1));
        trades.add(createTrade(Material.EMERALD, 2, Material.CYAN_BANNER, 1));
        trades.add(createTrade(Material.EMERALD, 2, Material.BROWN_BANNER, 1));
        trades.add(createTrade(Material.EMERALD, 2, Material.GRAY_BANNER, 1));
        trades.add(createTrade(Material.EMERALD, 2, Material.GREEN_BANNER, 1));
        trades.add(createTrade(Material.EMERALD, 2, Material.LIGHT_BLUE_BANNER, 1));
        trades.add(createTrade(Material.EMERALD, 2, Material.LIME_BANNER, 1));
        trades.add(createTrade(Material.EMERALD, 2, Material.MAGENTA_BANNER, 1));
        trades.add(createTrade(Material.EMERALD, 2, Material.ORANGE_BANNER, 1));
        trades.add(createTrade(Material.EMERALD, 2, Material.PURPLE_BANNER, 1));
        trades.add(createTrade(Material.EMERALD, 2, Material.PINK_BANNER, 1));
        trades.add(createTrade(Material.EMERALD, 2, Material.WHITE_BANNER, 1));
        trades.add(createTrade(Material.EMERALD, 2, Material.YELLOW_BANNER, 1));

        villager.setRecipes(trades);

        villager.setAI(true);
    }

    private void setupClericVillager(Villager villager) {
        villager.setAI(false);
        villager.setProfession(Villager.Profession.CLERIC);
        villager.setVillagerExperience(1);
        villager.setVillagerLevel(5);
        villager.customName(Component.text("Cleric").color(TextColor.color(39,222,94)));

        List<MerchantRecipe> trades = new ArrayList<>();

        // Define trades
        trades.add(createTrade(Material.ROTTEN_FLESH, 20, Material.EMERALD, 1));
        trades.add(createTrade(Material.EMERALD, 1, Material.REDSTONE, 3));
        trades.add(createTrade(Material.GOLD_INGOT, 2, Material.EMERALD, 1));
        trades.add(createTrade(Material.EMERALD, 1, Material.LAPIS_LAZULI, 2));
        trades.add(createTrade(Material.RABBIT_FOOT, 2, Material.EMERALD, 1));
        trades.add(createTrade(Material.EMERALD, 3, Material.GLOWSTONE, 1));
        trades.add(createTrade(Material.TURTLE_SCUTE, 3, Material.EMERALD, 1));
        trades.add(createTrade(Material.GLASS_BOTTLE, 7, Material.EMERALD, 1));
        trades.add(createTrade(Material.EMERALD, 4, Material.ENDER_PEARL, 1));
        trades.add(createTrade(Material.NETHER_WART, 16, Material.EMERALD, 1));
        trades.add(createTrade(Material.EMERALD, 2, Material.EXPERIENCE_BOTTLE, 1));


        villager.setRecipes(trades);
        villager.setAI(true);
    }

    private void setupFishermanVillager(Villager villager) {
        villager.setAI(false);
        villager.setProfession(Villager.Profession.FISHERMAN);
        villager.setVillagerExperience(1);
        villager.setVillagerLevel(5);
        villager.customName(Component.text("Fisherman").color(TextColor.color(39,222,94)));

        List<MerchantRecipe> trades = new ArrayList<>();

        // Define trades
        trades.add(createTrade(Material.STRING, 14, Material.EMERALD, 1));
        trades.add(createTrade(Material.COAL, 10, Material.EMERALD, 1));
        trades.add(createTrade(Material.EMERALD, 2, Material.COD_BUCKET, 1));
        trades.add(createTradeWithTwoItems(new ItemStack(Material.COD,10),new ItemStack(Material.EMERALD,1),new ItemStack(Material.COOKED_COD,10)));
        trades.add(createTrade(Material.COD, 11, Material.EMERALD, 1));
        trades.add(createTrade(Material.EMERALD, 2, Material.CAMPFIRE, 1));
        trades.add(createTradeWithTwoItems(new ItemStack(Material.SALMON,10),new ItemStack(Material.EMERALD,1),new ItemStack(Material.COOKED_SALMON,10)));
        trades.add(createTrade(Material.SALMON, 10, Material.EMERALD, 1));
        trades.add(createTrade(Material.TROPICAL_FISH, 4, Material.EMERALD, 1));
        trades.add(createTrade(Material.PUFFERFISH, 3, Material.EMERALD, 1));


        villager.setRecipes(trades);
        villager.setAI(true);
    }

    private void setupFletcherVillager(Villager villager) {
        villager.setAI(false);
        villager.setProfession(Villager.Profession.FLETCHER);
        villager.setVillagerExperience(1);
        villager.setVillagerLevel(5);
        villager.customName(Component.text("Fletcher").color(TextColor.color(39,222,94)));

        List<MerchantRecipe> trades = new ArrayList<>();

        // Define trades
        trades.add(createTrade(Material.STICK, 20, Material.EMERALD, 1));
        trades.add(createTrade(Material.EMERALD, 1, Material.ARROW, 24));
        trades.add(createTradeWithTwoItems(new ItemStack(Material.GRAVEL,6),new ItemStack(Material.EMERALD,1),new ItemStack(Material.FLINT,10)));
        trades.add(createTrade(Material.FLINT, 18, Material.EMERALD, 1));
        trades.add(createTrade(Material.EMERALD, 1, Material.BOW, 1));
        trades.add(createTrade(Material.STRING, 10, Material.EMERALD, 1));
        trades.add(createTrade(Material.EMERALD, 2, Material.CROSSBOW, 1));
        trades.add(createTrade(Material.FEATHER, 18, Material.EMERALD, 1));
        trades.add(createEnchantedTrade(Material.EMERALD, 20, Material.BOW, 1,Enchantment.INFINITY, 1));
        trades.add(createEnchantedTrade(Material.EMERALD, 30, Material.CROSSBOW, 1,Enchantment.INFINITY, 1));
        trades.add(createTippedArrowTrade(Material.EMERALD, 4, PotionEffectType.POISON, 100, 1, 10));
        trades.add(createTippedArrowTrade(Material.EMERALD, 4, PotionEffectType.SLOWNESS, 100, 1, 10));
        trades.add(createTippedArrowTrade(Material.EMERALD, 4, PotionEffectType.WEAKNESS, 100, 1, 10));
        trades.add(createTippedArrowTrade(Material.EMERALD, 4, PotionEffectType.NAUSEA, 100, 1, 10));
        trades.add(createTippedArrowTrade(Material.EMERALD, 4, PotionEffectType.BLINDNESS, 100, 1, 10));

        villager.setRecipes(trades);
        villager.setAI(true);
    }

    private void setupLeatherWorkerVillager(Villager villager) {
        villager.setAI(false);
        villager.setProfession(Villager.Profession.LEATHERWORKER);
        villager.setVillagerExperience(1);
        villager.setVillagerLevel(5);
        villager.customName(Component.text("LeatherWorker").color(TextColor.color(39,222,94)));

        List<MerchantRecipe> trades = new ArrayList<>();

        // Define trades
        trades.add(createTrade(Material.LEATHER, 4, Material.EMERALD, 1));
        trades.add(createTrade(Material.FLINT, 18, Material.EMERALD, 1));
        trades.add(createTrade(Material.RABBIT_HIDE, 6, Material.EMERALD, 1));
        trades.add(createTrade(Material.TURTLE_SCUTE, 3, Material.EMERALD, 1));
        trades.add(createTrade(Material.EMERALD, 4, Material.LEATHER_HORSE_ARMOR, 1));
        trades.add(createTrade(Material.EMERALD, 4, Material.SADDLE, 1));

        villager.setRecipes(trades);
        villager.setAI(true);
    }

    private void setupLibrarianWorkerVillager(Villager villager) {
        villager.setAI(false);
        villager.setProfession(Villager.Profession.LIBRARIAN);
        villager.setVillagerExperience(1);
        villager.setVillagerLevel(5);
        villager.customName(Component.text("Librarian").color(TextColor.color(39, 222, 94)));

        List<MerchantRecipe> trades = new ArrayList<>();

        // --- Basic Item Trades ---
        trades.add(createTrade(Material.PAPER, 16, Material.EMERALD, 1));
        trades.add(createTrade(Material.EMERALD, 6, Material.BOOKSHELF, 1));
        trades.add(createTrade(Material.BOOK, 3, Material.EMERALD, 1));
        trades.add(createTrade(Material.EMERALD, 1, Material.LANTERN, 1));
        trades.add(createTrade(Material.INK_SAC, 3, Material.EMERALD, 1));
        trades.add(createTrade(Material.EMERALD, 6, Material.GLASS, 6));
        trades.add(createTrade(Material.EMERALD, 3, Material.COMPASS, 1));
        trades.add(createTrade(Material.EMERALD, 4, Material.CLOCK, 1));
        trades.add(createTrade(Material.EMERALD, 12, Material.NAME_TAG, 1));

        // --- Weapon Enchantments ---
        trades.add(createEnchantedTradeWithBook(Material.EMERALD, 30, Material.ENCHANTED_BOOK, 1, Enchantment.SHARPNESS, 5));
        trades.add(createEnchantedTradeWithBook(Material.EMERALD, 30, Material.ENCHANTED_BOOK, 1, Enchantment.SMITE, 5));
        trades.add(createEnchantedTradeWithBook(Material.EMERALD, 30, Material.ENCHANTED_BOOK, 1, Enchantment.BANE_OF_ARTHROPODS, 5));
        trades.add(createEnchantedTradeWithBook(Material.EMERALD, 15, Material.ENCHANTED_BOOK, 1, Enchantment.FIRE_ASPECT, 2));
        trades.add(createEnchantedTradeWithBook(Material.EMERALD, 15, Material.ENCHANTED_BOOK, 1, Enchantment.KNOCKBACK, 2));
        trades.add(createEnchantedTradeWithBook(Material.EMERALD, 20, Material.ENCHANTED_BOOK, 1, Enchantment.LOOTING, 3));
        trades.add(createEnchantedTradeWithBook(Material.EMERALD, 15, Material.ENCHANTED_BOOK, 1, Enchantment.SWEEPING_EDGE, 3));
        trades.add(createEnchantedTradeWithBook(Material.EMERALD, 30, Material.ENCHANTED_BOOK, 1, Enchantment.POWER, 5));
        trades.add(createEnchantedTradeWithBook(Material.EMERALD, 15, Material.ENCHANTED_BOOK, 1, Enchantment.PUNCH, 2));
        trades.add(createEnchantedTradeWithBook(Material.EMERALD, 10, Material.ENCHANTED_BOOK, 1, Enchantment.FLAME, 1));
        trades.add(createEnchantedTradeWithBook(Material.EMERALD, 10, Material.ENCHANTED_BOOK, 1, Enchantment.INFINITY, 1));

        // --- Armor Enchantments ---
        trades.add(createEnchantedTradeWithBook(Material.EMERALD, 25, Material.ENCHANTED_BOOK, 1, Enchantment.PROTECTION, 4));
        trades.add(createEnchantedTradeWithBook(Material.EMERALD, 25, Material.ENCHANTED_BOOK, 1, Enchantment.FIRE_PROTECTION, 4));
        trades.add(createEnchantedTradeWithBook(Material.EMERALD, 30, Material.ENCHANTED_BOOK, 1, Enchantment.BLAST_PROTECTION, 4));
        trades.add(createEnchantedTradeWithBook(Material.EMERALD, 20, Material.ENCHANTED_BOOK, 1, Enchantment.PROJECTILE_PROTECTION, 4));
        trades.add(createEnchantedTradeWithBook(Material.EMERALD, 15, Material.ENCHANTED_BOOK, 1, Enchantment.THORNS, 3));
        trades.add(createEnchantedTradeWithBook(Material.EMERALD, 25, Material.ENCHANTED_BOOK, 1, Enchantment.FEATHER_FALLING, 4));
        trades.add(createEnchantedTradeWithBook(Material.EMERALD, 20, Material.ENCHANTED_BOOK, 1, Enchantment.DEPTH_STRIDER, 3));
        trades.add(createEnchantedTradeWithBook(Material.EMERALD, 20, Material.ENCHANTED_BOOK, 1, Enchantment.FROST_WALKER, 2));
        trades.add(createEnchantedTradeWithBook(Material.EMERALD, 15, Material.ENCHANTED_BOOK, 1, Enchantment.RESPIRATION, 3));
        trades.add(createEnchantedTradeWithBook(Material.EMERALD, 15, Material.ENCHANTED_BOOK, 1, Enchantment.AQUA_AFFINITY, 1));
        trades.add(createEnchantedTradeWithBook(Material.EMERALD, 15, Material.ENCHANTED_BOOK, 1, Enchantment.UNBREAKING, 3));
        trades.add(createEnchantedTradeWithBook(Material.EMERALD, 10, Material.ENCHANTED_BOOK, 1, Enchantment.BINDING_CURSE, 1));
        trades.add(createEnchantedTradeWithBook(Material.EMERALD, 10, Material.ENCHANTED_BOOK, 1, Enchantment.VANISHING_CURSE, 1));

        // --- Tool Enchantments ---
        trades.add(createEnchantedTradeWithBook(Material.EMERALD, 30, Material.ENCHANTED_BOOK, 1, Enchantment.EFFICIENCY, 5));
        trades.add(createEnchantedTradeWithBook(Material.EMERALD, 20, Material.ENCHANTED_BOOK, 1, Enchantment.FORTUNE, 3));
        trades.add(createEnchantedTradeWithBook(Material.EMERALD, 10, Material.ENCHANTED_BOOK, 1, Enchantment.SILK_TOUCH, 1));
        trades.add(createEnchantedTradeWithBook(Material.EMERALD, 5, Material.ENCHANTED_BOOK, 1, Enchantment.MENDING, 1));

        // --- Bow & Crossbow Enchantments ---
        trades.add(createEnchantedTradeWithBook(Material.EMERALD, 10, Material.ENCHANTED_BOOK, 1, Enchantment.MULTISHOT, 1));
        trades.add(createEnchantedTradeWithBook(Material.EMERALD, 15, Material.ENCHANTED_BOOK, 1, Enchantment.QUICK_CHARGE, 3));
        trades.add(createEnchantedTradeWithBook(Material.EMERALD, 30, Material.ENCHANTED_BOOK, 1, Enchantment.PIERCING, 4));

        // --- Trident Enchantments ---
        trades.add(createEnchantedTradeWithBook(Material.EMERALD, 20, Material.ENCHANTED_BOOK, 1, Enchantment.LOYALTY, 3));
        trades.add(createEnchantedTradeWithBook(Material.EMERALD, 15, Material.ENCHANTED_BOOK, 1, Enchantment.RIPTIDE, 3));
        trades.add(createEnchantedTradeWithBook(Material.EMERALD, 15, Material.ENCHANTED_BOOK, 1, Enchantment.CHANNELING, 1));
        trades.add(createEnchantedTradeWithBook(Material.EMERALD, 30, Material.ENCHANTED_BOOK, 1, Enchantment.IMPALING, 5));

        // --- Fishing Enchantments ---
        trades.add(createEnchantedTradeWithBook(Material.EMERALD, 15, Material.ENCHANTED_BOOK, 1, Enchantment.LURE, 3));
        trades.add(createEnchantedTradeWithBook(Material.EMERALD, 15, Material.ENCHANTED_BOOK, 1, Enchantment.LUCK_OF_THE_SEA, 3));

        // --- Custom Enchantments ---
        trades.add(createEnchantedTradeWithBook(Material.EMERALD, 30, Material.ENCHANTED_BOOK, 1, Enchantment.DENSITY, 5));
        trades.add(createEnchantedTradeWithBook(Material.EMERALD, 30, Material.ENCHANTED_BOOK, 1, Enchantment.BREACH, 4));
        trades.add(createEnchantedTradeWithBook(Material.EMERALD, 30, Material.ENCHANTED_BOOK, 1, Enchantment.WIND_BURST, 3));

        villager.setRecipes(trades);
        villager.setAI(true);
    }


    private void setupMasonVillager(Villager villager) {
        villager.setAI(false);
        villager.setProfession(Villager.Profession.MASON);
        villager.setVillagerExperience(1);
        villager.setVillagerLevel(5);
        villager.customName(Component.text("Mason").color(TextColor.color(39,222,94)));

        List<MerchantRecipe> trades = new ArrayList<>();

        // Define trades
        trades.add(createTrade(Material.CLAY_BALL, 6, Material.EMERALD, 1));
        trades.add(createTrade(Material.EMERALD, 1, Material.BRICK, 14));
        trades.add(createTrade(Material.STONE, 12, Material.EMERALD, 1));
        trades.add(createTrade(Material.EMERALD, 1, Material.CHISELED_STONE_BRICKS, 6));
        trades.add(createTrade(Material.GRANITE, 12, Material.EMERALD, 1));
        trades.add(createTrade(Material.ANDESITE, 12, Material.EMERALD, 1));
        trades.add(createTrade(Material.DIORITE, 12, Material.EMERALD, 1));
        trades.add(createTrade(Material.EMERALD, 1, Material.DRIPSTONE_BLOCK, 6));
        trades.add(createTrade(Material.EMERALD, 1, Material.POLISHED_ANDESITE, 6));
        trades.add(createTrade(Material.EMERALD, 1, Material.POLISHED_DIORITE, 6));
        trades.add(createTrade(Material.EMERALD, 1, Material.POLISHED_GRANITE, 6));
        trades.add(createTrade(Material.QUARTZ, 8, Material.EMERALD, 1));
        trades.add(createTrade(Material.EMERALD, 1, Material.QUARTZ_PILLAR, 1));
        trades.add(createTrade(Material.EMERALD, 1, Material.QUARTZ_BLOCK, 1));
        trades.addAll(createTerracottaTrades());

        villager.setRecipes(trades);
        villager.setAI(true);
    }

    private void setupShepherdVillager(Villager villager) {
        villager.setAI(false);
        villager.setProfession(Villager.Profession.SHEPHERD);
        villager.setVillagerExperience(1);
        villager.setVillagerLevel(5);
        villager.customName(Component.text("Shepherd").color(TextColor.color(39,222,94)));

        List<MerchantRecipe> trades = new ArrayList<>();

        // Define trades
        trades.add(createTrade(Material.EMERALD, 1, Material.SHEARS, 1));
        trades.add(createTrade(Material.EMERALD, 2, Material.PAINTING, 5));
        trades.addAll(createWoolTrades());
        trades.addAll(createDyeTrades());
        trades.addAll(createBuyWoolTrades());
        trades.addAll(createBedTrades());
        trades.addAll(createBannerTrades());


        villager.setRecipes(trades);
        villager.setAI(true);
    }

    private void setupToolSmithVillager(Villager villager) {
        villager.setAI(false);
        villager.setProfession(Villager.Profession.TOOLSMITH);
        villager.setVillagerExperience(1);
        villager.setVillagerLevel(5);
        villager.customName(Component.text("Toolsmith").color(TextColor.color(39,222,94)));

        List<MerchantRecipe> trades = new ArrayList<>();

        // Define trades
        trades.add(createTrade(Material.COAL, 10, Material.EMERALD, 1));
        trades.add(createTrade(Material.IRON_INGOT, 3, Material.EMERALD, 1));
        trades.add(createTrade(Material.EMERALD, 18, Material.BELL, 1));
        trades.add(createTrade(Material.FLINT, 18, Material.EMERALD, 1));
        trades.add(createTrade(Material.DIAMOND, 1, Material.EMERALD, 1));
        trades.add(createTrade(Material.EMERALD, 2, Material.IRON_AXE, 1));
        trades.add(createTrade(Material.EMERALD, 2, Material.IRON_SHOVEL, 1));
        trades.add(createTrade(Material.EMERALD, 2, Material.IRON_PICKAXE, 1));
        trades.add(createTrade(Material.EMERALD, 4, Material.DIAMOND_HOE, 1));
        trades.add(createTrade(Material.EMERALD, 8, Material.DIAMOND_AXE, 1));
        trades.add(createTrade(Material.EMERALD, 6, Material.DIAMOND_SHOVEL, 1));
        trades.add(createTrade(Material.EMERALD, 8, Material.DIAMOND_PICKAXE, 1));

        villager.setRecipes(trades);
        villager.setAI(true);
    }

    private void setupWeaponSmithVillager(Villager villager) {
        villager.setAI(false);
        villager.setProfession(Villager.Profession.WEAPONSMITH);
        villager.setVillagerExperience(1);
        villager.setVillagerLevel(5);
        villager.customName(Component.text("Weaponsmith").color(TextColor.color(39,222,94)));

        List<MerchantRecipe> trades = new ArrayList<>();

        // Define trades
        trades.add(createTrade(Material.COAL, 10, Material.EMERALD, 1));
        trades.add(createTrade(Material.IRON_INGOT, 3, Material.EMERALD, 1));
        trades.add(createTrade(Material.EMERALD, 18, Material.BELL, 1));
        trades.add(createTrade(Material.FLINT, 18, Material.EMERALD, 1));
        trades.add(createTrade(Material.DIAMOND, 1, Material.EMERALD, 1));
        trades.add(createTrade(Material.EMERALD, 2, Material.IRON_SWORD, 1));
        trades.add(createTrade(Material.EMERALD, 10, Material.DIAMOND_SWORD, 1));


        villager.setRecipes(trades);
        villager.setAI(true);
    }

    private void setupAbyssTraderVillager(Villager villager) {
        villager.setAI(false);
        villager.setProfession(Villager.Profession.NITWIT);
        villager.setVillagerExperience(1);
        villager.setVillagerLevel(5);
        villager.customName(Component.text("Yabmat - the Real Boss").color(TextColor.color(220,21,21)));

        List<MerchantRecipe> trades = new ArrayList<>();

        // Define trades
        trades.add(createCustomItemTrade(Material.EMERALD, 10, ItemUtils.getResetItem()));
        trades.addAll(createAbyssTrades());
        villager.setRecipes(trades);
        villager.setCustomNameVisible(true);
        villager.setAI(true);
    }

    private MerchantRecipe createTrade(Material ingredient, int ingredientCount, Material result, int resultCount) {
        ItemStack ingredientItem = new ItemStack(ingredient, ingredientCount);
        ItemStack resultItem = new ItemStack(result, resultCount);
        MerchantRecipe recipe = new MerchantRecipe(resultItem, Integer.MAX_VALUE);
        recipe.setIngredients(Collections.singletonList(ingredientItem));
        return recipe;
    }
    private MerchantRecipe createTradeWithTwoItems(ItemStack ingredient1, ItemStack ingredient2, ItemStack result) {
        List<ItemStack> ingredients = new ArrayList<>();
        ingredients.add(ingredient1);
        ingredients.add(ingredient2);

        MerchantRecipe recipe = new MerchantRecipe(result, Integer.MAX_VALUE);
        recipe.setIngredients(ingredients);
        return recipe;
    }


    private MerchantRecipe createEnchantedTrade(Material ingredient, int ingredientCount, Material result, int resultCount, Enchantment enchantment, int enchantmentLevel) {
        ItemStack ingredientItem = new ItemStack(ingredient, ingredientCount);
        ItemStack resultItem = new ItemStack(result, resultCount);

        // Apply the specified enchantment to the result item
        resultItem.addUnsafeEnchantment(enchantment, enchantmentLevel);

        MerchantRecipe recipe = new MerchantRecipe(resultItem, Integer.MAX_VALUE);
        recipe.setIngredients(Collections.singletonList(ingredientItem));
        return recipe;
    }

    private MerchantRecipe createEnchantedTradeWithBook(
            Material ingredient, int ingredientCount,
            Material result, int resultCount, // (kept for your signature; result is ignored)
            Enchantment enchantment, int enchantmentLevel
    ) {
        ItemStack enchantedBook = new ItemStack(Material.ENCHANTED_BOOK, Math.max(1, resultCount));

        ItemMeta rawMeta = enchantedBook.getItemMeta();
        if (rawMeta instanceof EnchantmentStorageMeta meta) {
            meta.addStoredEnchant(enchantment, enchantmentLevel, true);

            // ✅ Make Bedrock players see the enchant in the trade list
            String niceName = prettifyEnchantmentName(enchantment) + " " + toRoman(enchantmentLevel);

            meta.displayName(net.kyori.adventure.text.Component.text(niceName));

            meta.lore(java.util.List.of(
                    net.kyori.adventure.text.Component.text("Enchant: " + niceName),
                    net.kyori.adventure.text.Component.text("Use on an anvil")
            ));

            enchantedBook.setItemMeta(meta);
        }

        ItemStack ingredientItem = new ItemStack(ingredient, ingredientCount);
        ItemStack bookItem = new ItemStack(Material.BOOK, 1);

        MerchantRecipe recipe = new MerchantRecipe(enchantedBook, Integer.MAX_VALUE);
        recipe.setIngredients(java.util.Arrays.asList(ingredientItem, bookItem));
        return recipe;
    }

    private String prettifyEnchantmentName(Enchantment ench) {
        // e.g. minecraft:fire_aspect -> Fire Aspect
        String key = ench.getKey().getKey().replace('_', ' ');
        String[] parts = key.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (p.isEmpty()) continue;
            sb.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }

    private String toRoman(int num) {
        return switch (num) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";
            default -> String.valueOf(num);
        };
    }

    private MerchantRecipe createTippedArrowTrade(Material ingredient, int ingredientCount, PotionEffectType effectType, int durationTicks, int amplifier, int resultCount) {

        ItemStack ingredientItem = new ItemStack(ingredient, ingredientCount);

        ItemStack arrow = new ItemStack(Material.TIPPED_ARROW, resultCount);

        PotionMeta meta = (PotionMeta) arrow.getItemMeta();
        if (meta != null) {

            PotionEffect effect = new PotionEffect(effectType, durationTicks, amplifier);
            meta.addCustomEffect(effect, true);
            meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            // Create custom lore for the arrow
            List<Component> lore = new ArrayList<>();

            TextComponent line1 = Component.text("Tipped Arrow with effect")
                    .color(TextColor.color(0, 255, 255));  // Cyan color

            TextComponent line2 = Component.text("Effect: " + effectType.getName() + " (5 seconds)")
                    .color(TextColor.color(255, 165, 0));  // Orange color for duration

            lore.add(line1);
            lore.add(line2);

            // Set the lore to the arrow item
            meta.lore(lore);
            meta.itemName(Component.text("Fletcher's Crafted Arrow").color(TextColor.color(140,28,196)));
            // Set the modified meta back to the arrow
            arrow.setItemMeta(meta);

        }

        // Create the trade with the custom tipped arrow
        MerchantRecipe recipe = new MerchantRecipe(arrow, Integer.MAX_VALUE);
        recipe.setIngredients(Collections.singletonList(ingredientItem));

        return recipe;
    }
    public List<MerchantRecipe> createTerracottaTrades() {
        List<MerchantRecipe> trades = new ArrayList<>();

        // Loop through all colors of stained terracotta
        for (Material material : Material.values()) {
            if (material.name().endsWith("_TERRACOTTA") && !material.name().contains("LEGACY")) {
                MerchantRecipe trade = createTrade(Material.EMERALD, 1, material, 1);
                trades.add(trade);
            }
        }

        return trades;
    }

    public List<MerchantRecipe> createWoolTrades() {
        List<MerchantRecipe> trades = new ArrayList<>();

        // Loop through all colors of stained terracotta
        for (Material material : Material.values()) {
            if (material.name().endsWith("_WOOL") && !material.name().contains("LEGACY")) {
                MerchantRecipe trade = createTrade(material, 12, Material.EMERALD, 1);
                trades.add(trade);
            }
        }

        return trades;
    }

    public List<MerchantRecipe> createDyeTrades() {
        List<MerchantRecipe> trades = new ArrayList<>();

        // Loop through all colors of stained terracotta
        for (Material material : Material.values()) {
            if (material.name().endsWith("_DYE")) {
                MerchantRecipe trade = createTrade(material, 8, Material.EMERALD, 1);
                trades.add(trade);
            }
        }

        return trades;
    }

    public List<MerchantRecipe> createBuyWoolTrades() {
        List<MerchantRecipe> trades = new ArrayList<>();

        // Loop through all colors of stained terracotta
        for (Material material : Material.values()) {
            if (material.name().endsWith("_WOOL") && !material.name().contains("LEGACY")) {
                MerchantRecipe trade = createTrade(Material.EMERALD, 1, material, 1);
                trades.add(trade);
            }
        }

        return trades;
    }

    public List<MerchantRecipe> createBedTrades() {
        List<MerchantRecipe> trades = new ArrayList<>();

        // Loop through all colors of stained terracotta
        for (Material material : Material.values()) {
            if (material.name().endsWith("_BED") && !material.name().contains("LEGACY")) {
                MerchantRecipe trade = createTrade(Material.EMERALD, 2, material, 1);
                trades.add(trade);
            }
        }

        return trades;
    }

    public List<MerchantRecipe> createBannerTrades() {
        List<MerchantRecipe> trades = new ArrayList<>();

        // Loop through all colors of stained terracotta
        for (Material material : Material.values()) {
            if (material.name().endsWith("_BANNER") && !material.name().contains("LEGACY") && !material.name().contains("_WALL")) {
                MerchantRecipe trade = createTrade(Material.EMERALD, 2, material, 1);
                trades.add(trade);
            }
        }

        return trades;
    }

    private MerchantRecipe createCustomItemTrade(Material ingredient, int ingredientCount, ItemStack customResult) {
        ItemStack ingredientItem = new ItemStack(ingredient, ingredientCount);

        MerchantRecipe recipe = new MerchantRecipe(customResult, Integer.MAX_VALUE);
        recipe.setIngredients(Collections.singletonList(ingredientItem));
        return recipe;
    }

    private List<MerchantRecipe> createAbyssTrades() {
        List<MerchantRecipe> trades = new ArrayList<>();

        ItemStack abyssIngot = ItemUtils.getAbyssIngot();

//
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getAgilityHelmet()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getAgilityChestplate()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getAgilityLeggings()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getAgilityBoots()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getAgilityBook()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getAgilityBow()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getAgilityCrossbow()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getAgilitySword()));
//
//
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getVitalityHelmet()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getVitalityChestplate()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getVitalityLeggings()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getVitalityBoots()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getVitalityBook()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getVitalityBow()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getVitalityCrossbow()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getVitalitySword()));
//
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getStrengthHelmet()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getStrengthChestplate()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getStrengthLeggings()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getStrengthBoots()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getStrengthBook()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getStrengthBow()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getStrengthCrossbow()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getStrengthSword()));
//
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getDexterityHelmet()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getDexterityChestplate()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getDexterityLeggings()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getDexterityBoots()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getDexterityBook()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getDexterityBow()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getDexterityCrossbow()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getDexteritySword()));
//
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getLuckHelmet()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getLuckChestplate()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getLuckLeggings()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getLuckBoots()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getLuckBook()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getLuckBow()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getLuckCrossbow()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getLuckSword()));
//
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getIntelligenceHelmet()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getIntelligenceChestplate()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getIntelligenceLeggings()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getIntelligenceBoots()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getIntelligenceBook()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getIntelligenceBow()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getIntelligenceCrossbow()));
//        trades.add(createCustomEquipTrade(abyssIngot, ItemUtils.getIntelligenceSword()));

        // NETHERITES

        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getVitalityHelmet5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getVitalityChestplate5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getVitalityLeggings5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getVitalityBoots5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getVitalityBook5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getVitalityBow5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getVitalityCrossbow5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getVitalitySword5()));

        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getStrengthHelmet5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getStrengthChestplate5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getStrengthLeggings5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getStrengthBoots5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getStrengthBook5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getStrengthBow5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getStrengthCrossbow5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getStrengthSword5()));

        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getDexterityHelmet5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getDexterityChestplate5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getDexterityLeggings5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getDexterityBoots5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getDexterityBook5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getDexterityBow5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getDexterityCrossbow5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getDexteritySword5()));

        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getLuckHelmet5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getLuckChestplate5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getLuckLeggings5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getLuckBoots5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getLuckBook5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getLuckBow5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getLuckCrossbow5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getLuckSword5()));

        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getIntelligenceHelmet5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getIntelligenceChestplate5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getIntelligenceLeggings5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getIntelligenceBoots5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getIntelligenceBook5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getIntelligenceBow5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getIntelligenceCrossbow5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getIntelligenceSword5()));

        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getAgilityHelmet5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getAgilityChestplate5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getAgilityLeggings5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getAgilityBoots5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getAgilityBook5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getAgilityBow5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getAgilityCrossbow5()));
        trades.add(createCustomEquipTradeFor5(abyssIngot, ItemUtils.getAgilitySword5()));

        return trades;
    }

    // Helper method to create a single custom trade
    private MerchantRecipe createCustomEquipTrade(ItemStack payment, ItemStack result) {
        MerchantRecipe recipe = new MerchantRecipe(result, Integer.MAX_VALUE); // Unlimited uses
        recipe.addIngredient(payment);
        return recipe;
    }

    private MerchantRecipe createCustomEquipTradeFor5(ItemStack cost, ItemStack result) {
        ItemStack costStack = cost.clone(); // Clone the cost item to modify its amount
        costStack.setAmount(10); // Set the stack size to 5

        MerchantRecipe recipe = new MerchantRecipe(result, Integer.MAX_VALUE); // Infinite uses
        recipe.addIngredient(costStack); // Add the single stacked cost
        return recipe;
    }



}

