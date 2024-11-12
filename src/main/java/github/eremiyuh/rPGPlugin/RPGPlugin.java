package github.eremiyuh.rPGPlugin;

import github.eremiyuh.rPGPlugin.buffs.PlayerStatBuff;
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
import org.bukkit.plugin.java.JavaPlugin;

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
        // Save all profiles when the server is shutting down
        profileManager.saveAllProfiles();
        getLogger().info("All player profiles have been saved on server shutdown.");

        // Log to console that the plugin has been disabled
        getLogger().info("RPGPlugin has been disabled.");
        World world = getServer().getWorld("world_rpg");
        if (world != null) {
            world.save();
            getLogger().info("world rpg was SAVED.");
        }

        World labyrinthWorld = getServer().getWorld("world_labyrinth");
        if (labyrinthWorld != null) {
            labyrinthWorld.save();
            getLogger().info("world labyrinth was SAVED.");
        }
        chunkManager.saveChunkData();
        // Handle resource worlds separately

    }

    private void loadWorld(String worldName,int sx,int sy, int sz,int bcx,int bcz, int bs, long time, GameRule<Boolean> rule, boolean grValue, World.Environment env) {
        // Check if the world is already loaded
        World world = getServer().getWorld(worldName);
        if (world == null) {
            world = getServer().createWorld(new WorldCreator(worldName).environment(env));
            if (world==null) {getLogger().info("world STILL NULL"); return;}


        } else {
            getLogger().info("World " + worldName + " is already loaded.");
        }

        if (!(sx ==-1) ) {
            world.setSpawnLocation(sx,sy,sz);
        }

        if (!(bcx==-1)) {
            WorldBorder worldBorder= world.getWorldBorder();
            worldBorder.setCenter(bcx,bcz);
            worldBorder.setSize(bs);
        }

        if (rule!=null) {
            world.setGameRule(rule,grValue);
            world.setTime(time);
        }

    }

    private void oneManSleep() {
        World world = Bukkit.getWorld("world");
        if (world != null) {
            world.setGameRule(GameRule.PLAYERS_SLEEPING_PERCENTAGE, 0);
        }
        double x = 100.5, y = 70, z = 100.5;
        String text = "&6Hello, this is a hologram!"; // Supports color codes

        Location loc1 = new Location(world, -14, 75, -39);
        Location loc2 = new Location(world, -114, 75, -139);

        // Define the text for each stack
        String[] stack1 = {"&6Welcome to the server!", "&7Have fun!", "&aEnjoy your stay!"};
        String[] stack2 = {"&bServer Info", "&eIP: play.example.com"};

        // Create arrays for the locations and stacks
        Location[] locations = {loc1, loc2};
        String[][] stacks = {stack1, stack2};

        HologramUtil.createHologram(world, x, y, z, text);
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

        this.chunkManager = new ChunkManager(getDataFolder());
        EffectsAbilityManager effectsAbilityManager = new EffectsAbilityManager(this);
        DamageAbilityManager damageAbilityManager = new DamageAbilityManager(this);
        MonsterStrengthScalingListener monsterStrScaler = new MonsterStrengthScalingListener();
        // Register events
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(profileManager), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(profileManager), this);
        Objects.requireNonNull(getCommand("selectclass")).setExecutor(new SelectClassCommand(this, profileManager));
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
        Objects.requireNonNull(getCommand("switchworld")).setExecutor(new WorldSwitchCommand(this,playerStatBuff));
        Objects.requireNonNull(getCommand("giveap")).setExecutor(new AttributePointsCommand(profileManager));
        Objects.requireNonNull(getCommand("fly")).setExecutor(new FlyCommand(profileManager, this));
        Objects.requireNonNull(getCommand("buypotion")).setExecutor(new LapisToPotion(profileManager));
        Objects.requireNonNull(this.getCommand("givesword")).setExecutor(new SwordCommand());
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
        Objects.requireNonNull(this.getCommand("toggleBossIndicator")).setExecutor(new ToggleBossIndicatorCommand(profileManager));
        Objects.requireNonNull(this.getCommand("villagerSetProf")).setExecutor(new VillagerSetProfessionCommand(this));
        Objects.requireNonNull(this.getCommand("changepassword")).setExecutor(new ChangePassCommand(profileManager));
        TradeCommand tradeCommand = new TradeCommand(activeTrades,this);
        TradeAcceptCommand tradeAcceptCommand = new TradeAcceptCommand(tradeCommand);
        Objects.requireNonNull(this.getCommand("tm")).setExecutor(tradeCommand);
        Objects.requireNonNull(this.getCommand("ta")).setExecutor(tradeAcceptCommand);
        Objects.requireNonNull(this.getCommand("iteminfo")).setExecutor(new ItemInfoCommand());
        Objects.requireNonNull(this.getCommand("grt")).setExecutor(new GiveResetTokenCommand());

        //auth
        getCommand("register").setExecutor(new RegisterCommand(this,profileManager));
        getCommand("login").setExecutor(new LoginCommand(this,profileManager));
        getServer().getPluginManager().registerEvents(new PlayerMovementListener(this,profileManager), this);



        oneManSleep();
        loadWorld("world_rpg",-1,-1,-1,-1,-1,-1,18000,GameRule.DO_DAYLIGHT_CYCLE,false, World.Environment.NORMAL);
        loadWorld("world_labyrinth",0,64,0,0,0,100,18000,null,false, World.Environment.NORMAL);

    }




}
