package github.eremiyuh.rPGPlugin;

import github.eremiyuh.rPGPlugin.commands.ConvertLevelsCommand;
import github.eremiyuh.rPGPlugin.commands.SelectElement;
import github.eremiyuh.rPGPlugin.commands.SelectRace;
import github.eremiyuh.rPGPlugin.commandswithgui.CheckClassCommand;
import github.eremiyuh.rPGPlugin.commandswithgui.SelectClassCommand;
import github.eremiyuh.rPGPlugin.listeners.OptimizedVampireSunlightListener;
import github.eremiyuh.rPGPlugin.listeners.PlayerJoinListener;
import github.eremiyuh.rPGPlugin.listeners.PlayerQuitListener;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class RPGPlugin extends JavaPlugin {

    private PlayerProfileManager profileManager;

    @Override
    public void onEnable() {
        // Initialize the profile manager
        profileManager = new PlayerProfileManager(this);

        // Register events
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(profileManager), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(profileManager), this);
        Objects.requireNonNull(getCommand("selectclass")).setExecutor(new SelectClassCommand(this, profileManager));
        getServer().getPluginManager().registerEvents(new OptimizedVampireSunlightListener(profileManager, this), this);

        // Register the command executor
        Objects.requireNonNull(this.getCommand("checkclass")).setExecutor(new CheckClassCommand(profileManager));
        Objects.requireNonNull(getCommand("convertlevels")).setExecutor(new ConvertLevelsCommand(profileManager));
        Objects.requireNonNull(getCommand("selectelement")).setExecutor(new SelectElement(profileManager));
        Objects.requireNonNull(getCommand("selectrace")).setExecutor(new SelectRace(profileManager));
        // Register the listener
        Bukkit.getPluginManager().registerEvents(new CheckClassCommand(profileManager), this);


        // Log to console that the plugin has been enabled
        getLogger().info("RPGPlugin has been enabled.");
    }

    @Override
    public void onDisable() {
        // Save all profiles when the server is shutting down
        profileManager.saveAllProfiles();
        getLogger().info("All player profiles have been saved on server shutdown.");

        // Log to console that the plugin has been disabled
        getLogger().info("RPGPlugin has been disabled.");
    }

}
