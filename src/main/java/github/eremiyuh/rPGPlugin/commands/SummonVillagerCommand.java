package github.eremiyuh.rPGPlugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SummonVillagerCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    public SummonVillagerCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return false;
        }

        Player player = (Player) sender;
        Villager villager = (Villager) player.getWorld().spawnEntity(player.getLocation(), EntityType.VILLAGER);
        villager.setAI(false);
            villager.setProfession(Villager.Profession.CLERIC);
            villager.setVillagerExperience(1);
            villager.setCustomName("Super");
            List<MerchantRecipe> trades = new ArrayList<>();

            ItemStack emerald = new ItemStack(Material.EMERALD, 1);

            // Trade 1: Emerald for Iron Sword
            ItemStack ironSword = new ItemStack(Material.IRON_SWORD, 1);
            MerchantRecipe trade1 = new MerchantRecipe(ironSword, Integer.MAX_VALUE);
            trade1.setIngredients(Collections.singletonList(emerald));
            trades.add(trade1);

            // Trade 2: Emerald for Diamond Pickaxe
            ItemStack diamondPickaxe = new ItemStack(Material.DIAMOND_PICKAXE, 1);
            MerchantRecipe trade2 = new MerchantRecipe(diamondPickaxe, Integer.MAX_VALUE);
            trade2.setIngredients(Collections.singletonList(emerald));
            trades.add(trade2);

            // Trade 3: Emerald for Enchanted Book (Sharpness V)
            ItemStack enchantedBook = new ItemStack(Material.ENCHANTED_BOOK, 1);
            enchantedBook.addUnsafeEnchantment(Enchantment.SHARPNESS, 5);
            MerchantRecipe trade3 = new MerchantRecipe(enchantedBook, Integer.MAX_VALUE);
            trade3.setIngredients(Collections.singletonList(emerald));
            trades.add(trade3);

            villager.setRecipes(trades);

            player.sendMessage("A Super Trader Villager has been summoned with custom trades and profession!");
            villager.setAI(true);  // Re-enable AI after setting properties



        return true;
    }
}
