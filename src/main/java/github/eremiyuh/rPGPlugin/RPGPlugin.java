package github.eremiyuh.rPGPlugin;

import github.eremiyuh.rPGPlugin.commands.CheckClassCommand;
import github.eremiyuh.rPGPlugin.commands.SelectClassCommand;
import github.eremiyuh.rPGPlugin.listeners.PlayerClassListener;
import github.eremiyuh.rPGPlugin.manager.PlayerClassManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class RPGPlugin extends JavaPlugin {

    private SelectClassCommand selectClassCommand;
    private CheckClassCommand checkClassCommand;
    private PlayerClassManager playerClassManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        playerClassManager = new PlayerClassManager(this); // Initialize PlayerClassManager

        // Register commands
        selectClassCommand = new SelectClassCommand(playerClassManager);
        checkClassCommand = new CheckClassCommand(playerClassManager);
        getCommand("selectclass").setExecutor(selectClassCommand);
        getCommand("checkclass").setExecutor(checkClassCommand);

        // Register listener
        PlayerClassListener playerClassListener = new PlayerClassListener(playerClassManager);
        getServer().getPluginManager().registerEvents(playerClassListener, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
