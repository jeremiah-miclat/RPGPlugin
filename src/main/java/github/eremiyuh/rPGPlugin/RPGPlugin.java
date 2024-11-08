package github.eremiyuh.rPGPlugin;

import github.eremiyuh.rPGPlugin.buffs.PlayerStatBuff;
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
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Objects;

public class RPGPlugin extends JavaPlugin {

    private PlayerProfileManager profileManager;
    private ChunkManager chunkManager;
    private ChunkBorderBlueVisualizer chunkBorderBlueVisualizer;
    private ChunkBorderRedVisualizer chunkBorderRedVisualizer;
    private PlayerStatBuff playerStatBuff;
    private final HashMap<String, String> teleportRequests = new HashMap<>();
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

    private void loadWorld(String worldName) {
        // Check if the world is already loaded
        World world = getServer().getWorld(worldName);
        if (world == null) {
            world = getServer().createWorld(new WorldCreator(worldName).environment(World.Environment.NORMAL));
            if (world==null) {getLogger().info("world STILL NULL");}

        } else {
            getLogger().info("World " + worldName + " is already loaded.");
        }
    }

    private void setNightForever() {
        World resourceWorld = Bukkit.getWorld("world_resource");
        if (resourceWorld != null) {
            resourceWorld.setTime(18000); // Set to night initially
            resourceWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        }
    }

    private void oneManSleep() {
        World world = Bukkit.getWorld("world");
        if (world != null) {
            world.setGameRule(GameRule.PLAYERS_SLEEPING_PERCENTAGE, 0);
        }
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
        getServer().getPluginManager().registerEvents(new WorldProtectionListener(this), this);
        playerStatBuff = new PlayerStatBuff(profileManager);
        FlyCommand flyCommand = new FlyCommand(profileManager, this);
        getServer().getPluginManager().registerEvents(new PlayerTeleportListener(playerStatBuff,flyCommand,this ), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(profileManager), this);
        getServer().getPluginManager().registerEvents(new ArmorChangePlugin(profileManager,this), this);
        Bukkit.getPluginManager().registerEvents(new CheckClassCommand(profileManager), this);
        getServer().getPluginManager().registerEvents(new LoginListener(this), this);
        getServer().getPluginManager().registerEvents(new ItemAscensionListener(profileManager), this);
        // Register the command executor
        Objects.requireNonNull(this.getCommand("checkstatus")).setExecutor(new CheckClassCommand(profileManager));
        Objects.requireNonNull(getCommand("convertlevels")).setExecutor(new ConvertLevelsCommand(profileManager));
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
        Objects.requireNonNull(getCommand("convert")).setExecutor(new ConvertToEDiamond(profileManager));
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
        //auth
        getCommand("register").setExecutor(new RegisterCommand(this,profileManager));
        getCommand("login").setExecutor(new LoginCommand(this,profileManager));
        getServer().getPluginManager().registerEvents(new PlayerMovementListener(this,profileManager), this);


        oneManSleep();

        // Load the world
        loadWorld("world_rpg");


        // Log to console that the plugin has been enabled
        getLogger().info("RPGPlugin has been enabled.");


        // Step 1: Create a new world with the desired settings
        String worldName = "world_labyrinth";
        WorldCreator worldCreator = new WorldCreator(worldName);
        World world = Bukkit.createWorld(worldCreator);

        if (world != null) {
            // Step 2: Set the world border to a radius of 100 blocks
            WorldBorder border = world.getWorldBorder();
            border.setCenter(0, 0); // Set the center of the border, usually (0,0)
            border.setSize(200); // Border size is diameter, so 200 for a 100-block radius
        } else {
            getLogger().severe("Failed to create world " + worldName);
        }
    }

}
