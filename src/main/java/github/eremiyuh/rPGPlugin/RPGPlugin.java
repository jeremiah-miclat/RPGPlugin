package github.eremiyuh.rPGPlugin;

import github.eremiyuh.rPGPlugin.commands.*;
import github.eremiyuh.rPGPlugin.commandswithgui.CheckClassCommand;
import github.eremiyuh.rPGPlugin.commandswithgui.SelectClassCommand;
import github.eremiyuh.rPGPlugin.commandswithgui.SkillsGui;
import github.eremiyuh.rPGPlugin.listeners.*;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.methods.AbilityManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class RPGPlugin extends JavaPlugin {

    private PlayerProfileManager profileManager;
//    private MonsterStrengthScalingListener monsterStrengthScalingListener;

    @Override
    public void onEnable() {
        // Initialize the profile manager
        profileManager = new PlayerProfileManager(this);
        AbilityManager abilityManager = new AbilityManager(this);
        MonsterStrengthScalingListener monsterStrScaler = new MonsterStrengthScalingListener();
        // Register events
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(profileManager), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(profileManager), this);
        Objects.requireNonNull(getCommand("selectclass")).setExecutor(new SelectClassCommand(this, profileManager));
        getServer().getPluginManager().registerEvents(new OptimizedVampireSunlightListener(profileManager, this),this);
        PlayerDamageOthers playerDamageOthersListener = new PlayerDamageOthers(profileManager, abilityManager, this);
        getServer().getPluginManager().registerEvents(playerDamageOthersListener,this);
        getServer().getPluginManager().registerEvents(new AlchemistThrowPotion(profileManager), this);



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
        Objects.requireNonNull(getCommand("setrpg")).setExecutor(new SetRPG(profileManager));
        // Register the listener
        Bukkit.getPluginManager().registerEvents(new CheckClassCommand(profileManager), this);


        // Log to console that the plugin has been enabled
        getLogger().info("RPGPlugin has been enabled.");
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
    }

}
