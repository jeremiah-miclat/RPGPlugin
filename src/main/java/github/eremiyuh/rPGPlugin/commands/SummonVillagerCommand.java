package github.eremiyuh.rPGPlugin.commands;

import net.kyori.adventure.text.Component;
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
            villager.setProfession(Villager.Profession.ARMORER);
        villager.setVillagerLevel(5);
            villager.customName(Component.text("Armorer"));
            List<MerchantRecipe> trades = new ArrayList<>();

            ItemStack emerald = new ItemStack(Material.EMERALD, 1);

            // Trade 1: Emerald for Iron Sword
            ItemStack coal = new ItemStack(Material.COAL, 10);
            ItemStack emeraldGiven = new ItemStack(Material.EMERALD, 1);
            MerchantRecipe trade1 = new MerchantRecipe(emeraldGiven, Integer.MAX_VALUE);
            trade1.setIngredients(Collections.singletonList(coal));
            trades.add(trade1);

            // Trade 2: Emerald for Diamond Pickaxe
        ItemStack emeraldForIH = new ItemStack(Material.EMERALD, 3);
        ItemStack ironHelm = new ItemStack(Material.IRON_HELMET, 1);
        MerchantRecipe trade2= new MerchantRecipe(ironHelm, Integer.MAX_VALUE);
        trade2.setIngredients(Collections.singletonList(emeraldForIH));
        trades.add(trade2);

            // Trade 3: Emerald for Enchanted Book (Sharpness V)
            ItemStack emerald4CP = new ItemStack(Material.EMERALD, 7);
        ItemStack ironCP = new ItemStack(Material.IRON_CHESTPLATE, 1);
            MerchantRecipe trade3 = new MerchantRecipe(ironCP, Integer.MAX_VALUE);
            trade3.setIngredients(Collections.singletonList(emerald4CP));
            trades.add(trade3);

            ItemStack emerald4IL = new ItemStack(Material.EMERALD, 5);
            ItemStack ironL = new ItemStack(Material.IRON_LEGGINGS,1);
            MerchantRecipe trade4 = new MerchantRecipe(ironL,Integer.MAX_VALUE);
        trade4.setIngredients(Collections.singletonList(emerald4IL));
        trades.add(trade4);

        ItemStack emerald4B = new ItemStack(Material.EMERALD, 4);
        ItemStack ironB = new ItemStack(Material.IRON_BOOTS,1);
        MerchantRecipe trade5 = new MerchantRecipe(ironB,Integer.MAX_VALUE);
        trade5.setIngredients(Collections.singletonList(emerald4B));
        trades.add(trade5);

        ItemStack emerald4I = new ItemStack(Material.EMERALD, 1);
        ItemStack ironI = new ItemStack(Material.IRON_INGOT,3);
        MerchantRecipe trade6 = new MerchantRecipe(emerald4I,Integer.MAX_VALUE);
        trade6.setIngredients(Collections.singletonList(ironI));
        trades.add(trade6);

        ItemStack bell = new ItemStack(Material.BELL, 1);
        ItemStack emerald4Bell = new ItemStack(Material.EMERALD,28);
        MerchantRecipe trade7 = new MerchantRecipe(bell,Integer.MAX_VALUE);
        trade7.setIngredients(Collections.singletonList(emerald4Bell));
        trades.add(trade7);

        ItemStack lavabucket = new ItemStack(Material.LAVA_BUCKET, 1);
        ItemStack emerald4LB = new ItemStack(Material.EMERALD,1);
        MerchantRecipe trade8 = new MerchantRecipe(lavabucket,Integer.MAX_VALUE);
        trade8.setIngredients(Collections.singletonList(emerald4LB));
        trades.add(trade8);

        ItemStack diamond = new ItemStack(Material.DIAMOND, 1);
        ItemStack emerald4D = new ItemStack(Material.EMERALD,1);
        MerchantRecipe trade9 = new MerchantRecipe(emerald4D,Integer.MAX_VALUE);
        trade9.setIngredients(Collections.singletonList(diamond));
        trades.add(trade9);

        ItemStack shield = new ItemStack(Material.SHIELD, 1);
        ItemStack e4s = new ItemStack(Material.EMERALD,3);
        MerchantRecipe trade10 = new MerchantRecipe(shield,Integer.MAX_VALUE);
        trade10.setIngredients(Collections.singletonList(emerald4D));
        trades.add(trade10);

        ItemStack dLeggings = new ItemStack(Material.DIAMOND_LEGGINGS, 1);
        ItemStack e4dL = new ItemStack(Material.EMERALD,22);
        MerchantRecipe trade11 = new MerchantRecipe(dLeggings,Integer.MAX_VALUE);
        trade11.setIngredients(Collections.singletonList(e4dL));
        trades.add(trade11);

        ItemStack dboots = new ItemStack(Material.DIAMOND_BOOTS, 1);
        ItemStack e4dB = new ItemStack(Material.EMERALD,16);
        MerchantRecipe trade12 = new MerchantRecipe(dboots,Integer.MAX_VALUE);
        trade12.setIngredients(Collections.singletonList(e4dB));
        trades.add(trade12);

        ItemStack dChestP = new ItemStack(Material.DIAMOND_CHESTPLATE, 1);
        ItemStack e4dCP = new ItemStack(Material.EMERALD,25);
        MerchantRecipe trade13 = new MerchantRecipe(dChestP,Integer.MAX_VALUE);
        trade13.setIngredients(Collections.singletonList(e4dCP));
        trades.add(trade13);

        ItemStack dBoots = new ItemStack(Material.DIAMOND_HELMET, 1);
        ItemStack e4Boots = new ItemStack(Material.EMERALD,15);
        MerchantRecipe trade14 = new MerchantRecipe(dBoots,Integer.MAX_VALUE);
        trade14.setIngredients(Collections.singletonList(e4Boots));
        trades.add(trade14);



            villager.setRecipes(trades);
            player.sendMessage("A Super Trader Villager has been summoned with custom trades and profession!");
            villager.setAI(true);  // Re-enable AI after setting properties



        return true;
    }
}
