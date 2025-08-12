package github.eremiyuh.rPGPlugin.manager;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class BlockRewardManager {

    private final int xxx1 = -1928, zzz1 = -150;
    private final int xxx2 = -1837, zzz2 = -59;
    private final PlayerProfileManager profileManager;

    private final boolean testMode = false;
    private final int SECONDS_REQUIRED = testMode ? 5 : 60;

    private final Map<Material, String> firstStepMessages = Map.of(
            Material.EMERALD_BLOCK,  "§aYou stepped on an Emerald Block! Stay here for 1 minute to get Emerald rewards.",
            Material.IRON_BLOCK,     "§fYou stepped on an Iron Block! Stay here for 1 minute to get Iron rewards.",
            Material.NETHERITE_BLOCK,"§8You stepped on a Netherite Block! Stay here for 1 minute to get Netherite rewards.",
            Material.LAPIS_BLOCK,    "§9You stepped on a Lapis Block! Stay here for 1 minute to get Lapis rewards.",
            Material.GOLD_BLOCK,     "§6You stepped on a Gold Block! Stay here for 1 minute to get Gold rewards.",
            Material.COPPER_BLOCK,   "§cYou stepped on a Copper Block! Stay here for 1 minute to get Copper rewards.",
            Material.DIAMOND_BLOCK,  "§bYou stepped on a Diamond Block! Stay here for 1 minute to get Diamond rewards."
    );

    private final Map<Material, String> rewardMessages = Map.of(
            Material.EMERALD_BLOCK,  "§aYou earned the Emerald reward!",
            Material.IRON_BLOCK,     "§fYou earned the Iron reward!",
            Material.NETHERITE_BLOCK,"§8You earned the Netherite reward!",
            Material.LAPIS_BLOCK,    "§9You earned the Lapis reward!",
            Material.GOLD_BLOCK,     "§6You earned the Gold reward!",
            Material.COPPER_BLOCK,   "§cYou earned the Copper reward!",
            Material.DIAMOND_BLOCK,  "§bYou earned the Diamond reward!"
    );

    private final Map<Material, String> currencyMap = Map.of(
            Material.EMERALD_BLOCK,  "emerald",
            Material.IRON_BLOCK,     "iron",
            Material.NETHERITE_BLOCK,"netherite",
            Material.LAPIS_BLOCK,    "lapis",
            Material.GOLD_BLOCK,     "gold",
            Material.COPPER_BLOCK,   "copper",
            Material.DIAMOND_BLOCK,  "diamond"
    );

    private final Map<Material, Double> rewardPerMinute = Map.of(
            Material.EMERALD_BLOCK,  500.0 / 60.0,
            Material.IRON_BLOCK,     1000.0 / 60.0,
            Material.NETHERITE_BLOCK,6.0 / 60.0,
            Material.LAPIS_BLOCK,    300.0 / 60.0,
            Material.GOLD_BLOCK,     500.0 / 60.0,
            Material.COPPER_BLOCK,   5000.0 / 60.0,
            Material.DIAMOND_BLOCK,  20.0 / 60.0
    );

    private final Map<UUID, Integer> timeOnBlock = new HashMap<>();
    private final Map<UUID, Material> lastBlockType = new HashMap<>();

    private final RPGPlugin plugin;

    public BlockRewardManager(PlayerProfileManager profileManager, RPGPlugin plugin) {
        this.profileManager = profileManager;
        this.plugin = plugin;
    }

    public void start() {
        plugin.getLogger().info("Starting BlockRewardManager (testMode=" + testMode + ", secondsRequired=" + SECONDS_REQUIRED + ")");

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!player.getWorld().getName().equals("world")) {
                        continue; // skip players not in "world"
                    }
                    UUID uuid = player.getUniqueId();
                    Material blockType = player.getLocation().subtract(0, 1, 0).getBlock().getType();
                    UserProfile profile = profileManager.getProfile(player.getName());
                    int x = player.getLocation().getBlockX();
                    int z = player.getLocation().getBlockZ();

                    boolean insideRegion = testMode || (
                            x >= Math.min(xxx1, xxx2) && x <= Math.max(xxx1, xxx2) &&
                                    z >= Math.min(zzz1, zzz2) && z <= Math.max(zzz1, zzz2)
                    );

                    if (insideRegion && firstStepMessages.containsKey(blockType)) {

                        Material last = lastBlockType.get(uuid);
                        if (last == null || last != blockType) {
                            player.sendMessage(firstStepMessages.get(blockType));
                            lastBlockType.put(uuid, blockType);
                            timeOnBlock.put(uuid, 0);
                            plugin.getLogger().info("First-step: " + player.getName() + " on " + blockType + " at " + x + "," + z);
                        }

                        int secs = timeOnBlock.getOrDefault(uuid, 0) + 1;
                        timeOnBlock.put(uuid, secs);

                        if (secs >= SECONDS_REQUIRED) {
                            player.sendMessage(rewardMessages.get(blockType));

                            String currencyName = currencyMap.get(blockType);
                            double rewardAmount = rewardPerMinute.getOrDefault(blockType, 0.0);

                            if (currencyName != null && profile != null) {
                                double currentAmount = profile.getCurrency(currencyName);
                                profile.setCurrency(currencyName, currentAmount + rewardAmount);
                                plugin.getLogger().info("Rewarded " + player.getName() + " with " + rewardAmount + " " + currencyName);
                            } else {
                                plugin.getLogger().warning("Missing profile or currency mapping for " + player.getName());
                            }

                            timeOnBlock.put(uuid, 0); // reset timer
                        }

                    } else {
                        if (lastBlockType.containsKey(uuid) && !testMode) {
                            plugin.getLogger().info(player.getName() + " left reward block/region (x=" + x + ", z=" + z + ")");
                        }
                        timeOnBlock.remove(uuid);
                        lastBlockType.remove(uuid);
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 20L); // every second
    }
}
