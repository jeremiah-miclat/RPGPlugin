package github.eremiyuh.rPGPlugin;

import github.eremiyuh.rPGPlugin.buffs.PlayerStatBuff;
import github.eremiyuh.rPGPlugin.commandswithgui.AbyssStoreCommand;
import github.eremiyuh.rPGPlugin.manager.VaultManager;
import github.eremiyuh.rPGPlugin.utils.HologramUtil;
import github.eremiyuh.rPGPlugin.utils.TradeOffer;
import github.eremiyuh.rPGPlugin.commands.*;
import github.eremiyuh.rPGPlugin.commandswithgui.CheckClassCommand;
import github.eremiyuh.rPGPlugin.commandswithgui.SelectClassCommand;
import github.eremiyuh.rPGPlugin.commandswithgui.SkillsGui;
import github.eremiyuh.rPGPlugin.listeners.*;
import github.eremiyuh.rPGPlugin.manager.ChunkManager;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.methods.ChunkBorderBlueVisualizer;
import github.eremiyuh.rPGPlugin.methods.ChunkBorderRedVisualizer;
import github.eremiyuh.rPGPlugin.methods.DamageAbilityManager;
import github.eremiyuh.rPGPlugin.methods.EffectsAbilityManager;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class RPGPlugin extends JavaPlugin {

    private PlayerProfileManager profileManager;
    private ChunkManager chunkManager;
    private ChunkBorderBlueVisualizer chunkBorderBlueVisualizer;
    private ChunkBorderRedVisualizer chunkBorderRedVisualizer;
    private PlayerStatBuff playerStatBuff;
    private final HashMap<String, String> teleportRequests = new HashMap<>();
    private final HashMap<UUID, TradeOffer> activeTrades = new HashMap<>();
    private boolean serverLoaded = false;
    private VaultManager vaultManager;


    @Override
    public void onEnable() {


        loadResources();

        // Add a delay (e.g., 5 seconds) before allowing logins
        getServer().getScheduler().runTaskLater(this, () -> serverLoaded = true, 60); // 100 ticks = 5 seconds

    }

    // Method to access the listener
//    public MonsterStrengthScalingListener getMonsterStrengthScalingListener() {
//        return monsterStrengthScalingListener;
//    }

    @Override
    public void onDisable() {

        for (World map : Bukkit.getWorlds()) {
            for (Entity entity : map.getEntities()) {
                if (entity instanceof ArmorStand armorStand) {
                    if (armorStand.isInvisible()) {
                        armorStand.remove();
                    }
                }

            }
        }


        // Log that the shutdown process has started
        getLogger().info("RPGPlugin is shutting down...");

        // Ensure all player profiles are saved before other operations
        try {
            profileManager.saveAllProfiles();
            getLogger().info("All player profiles have been saved on server shutdown.");
        } catch (Exception e) {
            getLogger().severe("Error saving player profiles: " + e.getMessage());
            e.printStackTrace();
        }

        // Log that we are saving world data
        getLogger().info("Saving world data...");

        // Save worlds asynchronously if needed to avoid blocking the main thread
        World world = getServer().getWorld("world_rpg");
        if (world != null) {
            try {
                world.save();
                getLogger().info("World 'world_rpg' has been saved.");
            } catch (Exception e) {
                getLogger().severe("Error saving world 'world_rpg': " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            getLogger().warning("World 'world_rpg' was not found.");
        }

        // Save labyrinth world
        World labyrinthWorld = getServer().getWorld("world_labyrinth");
        if (labyrinthWorld != null) {
            try {

                labyrinthWorld.save();
                getLogger().info("World 'world_labyrinth' has been saved.");
            } catch (Exception e) {
                getLogger().severe("Error saving world 'world_labyrinth': " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            getLogger().warning("World 'world_labyrinth' was not found.");
        }

        // Save labyrinth world
        World labyrinthWorld2 = getServer().getWorld("world_labyrinth2");
        if (labyrinthWorld2 != null) {
            try {

                labyrinthWorld2.save();
                getLogger().info("World 'world_labyrinth' has been saved.");
            } catch (Exception e) {
                getLogger().severe("Error saving world 'world_labyrinth2': " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            getLogger().warning("World 'world_labyrinth2' was not found.");
        }


        // Save chunk data
        try {
            chunkManager.saveChunkData();
            getLogger().info("Chunk data has been saved.");
        } catch (Exception e) {
            getLogger().severe("Error saving chunk data: " + e.getMessage());
            e.printStackTrace();
        }

        // Handle vault data saving
        try {
            vaultManager.saveAllVaults();
            getLogger().info("All vault data has been saved.");
        } catch (Exception e) {
            getLogger().severe("Error saving vault data: " + e.getMessage());
            e.printStackTrace();
        }




        // Log to console that the plugin has been disabled successfully
        getLogger().info("RPGPlugin has been successfully disabled.");
    }


    private void loadWorld(String worldName, int sx, int sy, int sz, int syaw,  int spitch ,int bcx, int bcz, int bs, long time, GameRule<Boolean> rule, boolean grValue, World.Environment env, Biome biome) {
        // Check if the world is already loaded
        World world = getServer().getWorld(worldName);
        if (world == null) {
            world = getServer().createWorld(new WorldCreator(worldName).environment(env));
            assert world != null;

            // Set the biome for a region (example: 100x100 area at coordinates (0, 64, 0))
            for (int x = 0; x < 100; x++) {
                for (int z = 0; z < 100; z++) {
                    // Assuming y is set at 64 (you might want to adjust this depending on your needs)
                    world.setBiome(x, 64, z, biome);
                }
            }

            world.setGameRule(GameRule.SPAWN_RADIUS, 0);
            if (world == null) {
                getLogger().info("world STILL NULL");
                return;
            }

        } else {
            world.setGameRule(GameRule.SPAWN_RADIUS, 0);
            getLogger().info("World " + worldName + " is already loaded.");
        }

        if (sx != -1) {
            Location spawnLocation = new Location(
                    Bukkit.getWorld(worldName), // Specify the world
                    sx, sy, sz,             // Coordinates
                    syaw, spitch                     // Yaw (90° East), Pitch (0° level)
            );
            world.setSpawnLocation(spawnLocation);
        }

        if (bcx != -1) {
            WorldBorder worldBorder = world.getWorldBorder();
            worldBorder.setCenter(bcx, bcz);
            worldBorder.setSize(bs);
        }

        if (rule != null) {
            world.setGameRule(rule, grValue);
            world.setTime(time);
        }
    }



    private void worldConfig() {
        World world = Bukkit.getWorld("world");
        if (world != null) {
            world.setSpawnLocation(-14,72,-36);
            world.setGameRule(GameRule.SPAWN_RADIUS, 0);
            world.setGameRule(GameRule.PLAYERS_SLEEPING_PERCENTAGE, 0);
        }

        Location loc1 = new Location(world, -14, 75, -39);

        // Define the text for each stack
        String[] stack1 = {"&aEnjoy your stay!","&7Have fun!","&6Welcome to the server!" };

        // Create arrays for the locations and stacks
        Location[] locations = {loc1};
        String[][] stacks = {stack1};

        HologramUtil.createMultipleStacks(world, locations, stacks);
    }

    public boolean isServerLoaded() {
        return serverLoaded;
    }

    private void loadResources() {
        // Load your data, initialize managers, etc.
        // Initialize the profile manager
        profileManager = new PlayerProfileManager(this);

        profileManager.resetLoginStatus();
        vaultManager = new VaultManager(this, this.getDataFolder());
        Objects.requireNonNull(this.getCommand("vault")).setExecutor(new VaultCommand(vaultManager));

        this.chunkManager = new ChunkManager(getDataFolder());
        EffectsAbilityManager effectsAbilityManager = new EffectsAbilityManager(this);
        DamageAbilityManager damageAbilityManager = new DamageAbilityManager(this);
        MonsterStrengthScalingListener monsterStrScaler = new MonsterStrengthScalingListener();
        // Register events
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(profileManager), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(profileManager,vaultManager), this);
        Objects.requireNonNull(getCommand("selectclass")).setExecutor(new SelectClassCommand(this, profileManager));
        Objects.requireNonNull(getCommand("abyssstore")).setExecutor(new AbyssStoreCommand(this, profileManager));
        getServer().getPluginManager().registerEvents(new OptimizedVampireSunlightListener(profileManager, this),this);
        DamageListener damageListenerListener = new DamageListener(profileManager, effectsAbilityManager, damageAbilityManager,this);
        getServer().getPluginManager().registerEvents(damageListenerListener,this);
        getServer().getPluginManager().registerEvents(new PotionGiveListener(this,profileManager),this);
        new OverworldBlastProtectionListener(this);
        new ArrowHitListener((this));

        chunkBorderBlueVisualizer = new ChunkBorderBlueVisualizer(this);
        chunkBorderRedVisualizer = new ChunkBorderRedVisualizer(this);
        getServer().getPluginManager().registerEvents(new AlchemistThrowPotion(profileManager, this),this);
        getServer().getPluginManager().registerEvents(new DeadMobListener(profileManager),this);
        getServer().getPluginManager().registerEvents(new MonsterInitializer(this),this);
        getServer().getPluginManager().registerEvents(new MonsterInitializerLabyrinth(this),this);
        getServer().getPluginManager().registerEvents(new ChunkProtectionListener(chunkManager,chunkBorderBlueVisualizer,chunkBorderRedVisualizer),this);
        getServer().getPluginManager().registerEvents(new AreaProtectionListener(this), this);
        getServer().getPluginManager().registerEvents(new WorldProtectionListener(this), this);
        playerStatBuff = new PlayerStatBuff(profileManager);
        FlyCommand flyCommand = new FlyCommand(profileManager, this);
        getServer().getPluginManager().registerEvents(new PlayerTeleportListener(playerStatBuff,flyCommand,this ), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(profileManager), this);
        getServer().getPluginManager().registerEvents(new ArmorChangePlugin(profileManager,this), this);
        Bukkit.getPluginManager().registerEvents(new CheckClassCommand(profileManager), this);
        getServer().getPluginManager().registerEvents(new LoginListener(this), this);
        getServer().getPluginManager().registerEvents(new ItemAscensionListener(profileManager), this);
        getServer().getPluginManager().registerEvents(new ResetItemListener(profileManager), this);
        getServer().getPluginManager().registerEvents(new SummonVillagerListener(), this);


        // Register the command executor
        Objects.requireNonNull(this.getCommand("checkstatus")).setExecutor(new CheckClassCommand(profileManager));
        Objects.requireNonNull(getCommand("convertabysspoints")).setExecutor(new ConvertLevelsCommand(profileManager));
        Objects.requireNonNull(getCommand("selectelement")).setExecutor(new SelectElement(profileManager));
        Objects.requireNonNull(getCommand("selectrace")).setExecutor(new SelectRace(profileManager));
        Objects.requireNonNull(getCommand("selectskill")).setExecutor(new SkillsGui(this,profileManager));
        Objects.requireNonNull(getCommand("teamcreate")).setExecutor(new CreateTeamCommand(profileManager));
        Objects.requireNonNull(getCommand("teaminvite")).setExecutor(new TeamInviteCommand(profileManager));
        Objects.requireNonNull(getCommand("teamleave")).setExecutor(new TeamLeaveCommand(profileManager));
        Objects.requireNonNull(getCommand("teaminviteaccept")).setExecutor(new TeamInviteAcceptCommand(profileManager));
        Objects.requireNonNull(getCommand("teamremove")).setExecutor(new TeamRemoveCommand(profileManager));
        Objects.requireNonNull(getCommand("pvpstatus")).setExecutor(new PVPStatusCommand(profileManager));
        Objects.requireNonNull(getCommand("cc")).setExecutor(new ChunkCommand(chunkManager,profileManager, chunkBorderBlueVisualizer, chunkBorderRedVisualizer));
        Objects.requireNonNull(getCommand("trust")).setExecutor(new TrustCommand(chunkManager));
        Objects.requireNonNull(getCommand("trustall")).setExecutor(new TrustAllCommand(chunkManager));
        Objects.requireNonNull(getCommand("untrust")).setExecutor(new UntrustCommand(chunkManager));
        Objects.requireNonNull(getCommand("untrustall")).setExecutor(new UntrustAllCommand(chunkManager));
        Objects.requireNonNull(getCommand("buyclaim")).setExecutor(new BuyClaim(profileManager));
        Objects.requireNonNull(getCommand("convertmaterial")).setExecutor(new ConvertToEDiamond(profileManager));
        Objects.requireNonNull(getCommand("convertcurrency")).setExecutor(new CurrencyConverter(profileManager));
        Objects.requireNonNull(getCommand("warp")).setExecutor(new WorldSwitchCommand(this,playerStatBuff));
        Objects.requireNonNull(getCommand("giveap")).setExecutor(new AttributePointsCommand(profileManager));
        Objects.requireNonNull(getCommand("giveabysspoints")).setExecutor(new GiveAbyssPoints(profileManager));
        Objects.requireNonNull(getCommand("fly")).setExecutor(new FlyCommand(profileManager, this));
        Objects.requireNonNull(getCommand("buypotion")).setExecutor(new LapisToPotion(profileManager));
        Objects.requireNonNull(this.getCommand("givesword")).setExecutor(new SwordCommand(this));
        Objects.requireNonNull(this.getCommand("rtp")).setExecutor(new RTPCommand(this));
        Objects.requireNonNull(this.getCommand("spawn")).setExecutor(new SpawnCommand(profileManager));
        Objects.requireNonNull(this.getCommand("sethome")).setExecutor(new SetHomeCommand(profileManager));
        Objects.requireNonNull(this.getCommand("home")).setExecutor(new HomeCommand(profileManager));
        Objects.requireNonNull(this.getCommand("homedelete")).setExecutor(new DeleteHomeCommand(profileManager));
        Objects.requireNonNull(this.getCommand("convertfood")).setExecutor(new ConvertFoodCommand(profileManager));
        Objects.requireNonNull(this.getCommand("payblacksmith")).setExecutor(new PayBlackSmithCommand(profileManager));
        Objects.requireNonNull(getCommand("healthscale")).setExecutor(new HealthScale(this));
        Objects.requireNonNull(this.getCommand("sv")).setExecutor(new SummonVillagerCommand(this));
        Objects.requireNonNull(getCommand("rest")).setExecutor(new ResetRestCommand(profileManager));
        Objects.requireNonNull(getCommand("ascendtoggle")).setExecutor(new ToggleAscendingCommand(profileManager));
        Objects.requireNonNull(getCommand("tphb")).setExecutor(new TeleportToHighestBlock(profileManager));
        Objects.requireNonNull(getCommand("tpt")).setExecutor(new TptCommand(this,teleportRequests,profileManager));
        Objects.requireNonNull(getCommand("tpa")).setExecutor(new TpaCommand(this,teleportRequests,profileManager));
        Objects.requireNonNull(this.getCommand("sdw")).setExecutor(new ToggleBossIndicatorCommand(profileManager));
        Objects.requireNonNull(this.getCommand("villagerSetProf")).setExecutor(new VillagerSetProfessionCommand(this));
        Objects.requireNonNull(this.getCommand("changepassword")).setExecutor(new ChangePassCommand(profileManager));
        TradeCommand tradeCommand = new TradeCommand(activeTrades,this);
        TradeAcceptCommand tradeAcceptCommand = new TradeAcceptCommand(tradeCommand);
        Objects.requireNonNull(this.getCommand("tm")).setExecutor(tradeCommand);
        Objects.requireNonNull(this.getCommand("ta")).setExecutor(tradeAcceptCommand);
        Objects.requireNonNull(this.getCommand("iteminfo")).setExecutor(new ItemInfoCommand());
        Objects.requireNonNull(this.getCommand("grt")).setExecutor(new GiveResetTokenCommand());
        Objects.requireNonNull(this.getCommand("enderchest")).setExecutor(new EnderChestCommand());
        Objects.requireNonNull(this.getCommand("addstat")).setExecutor(new AddAttributeCommand(profileManager));

        vaultManager.loadVaults();
        //auth
        getCommand("register").setExecutor(new RegisterCommand(this,profileManager));
        getCommand("login").setExecutor(new LoginCommand(this,profileManager));
        getServer().getPluginManager().registerEvents(new PlayerMovementListener(this,profileManager), this);







        worldConfig();
        loadWorld("world_rpg",-15,72,-35, 270,0,-1,-1,-1,18000,GameRule.DO_DAYLIGHT_CYCLE,false, World.Environment.NORMAL, Biome.PLAINS);
        loadWorld("world_labyrinth",-23,312,-35, 270,0,0,0,100,18000,null,false, World.Environment.NORMAL,Biome.NETHER_WASTES);
        loadWorld("world_labyrinth2",-19,251,-36,270,0,0,0,100,18000,null,false, World.Environment.NETHER,Biome.NETHER_WASTES);
        new TabListCustomizer(this, profileManager);

    }
    public void clearHostileMobs(World world) {
        world.setDifficulty(Difficulty.PEACEFUL);
        getLogger().info("Difficulty set to PEACEFUL to despawn hostile mobs.");

        new BukkitRunnable() {
            @Override
            public void run() {
                world.setDifficulty(Difficulty.HARD);
                world.save();
                getLogger().info("Difficulty set back to HARD and world saved.");
            }
        }.runTaskLater(this, 20L); // Delay of 20 ticks (1 second)
    }




}
