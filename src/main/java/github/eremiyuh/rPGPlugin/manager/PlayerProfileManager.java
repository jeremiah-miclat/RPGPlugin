package github.eremiyuh.rPGPlugin.manager;

import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class PlayerProfileManager {

    private final JavaPlugin plugin;
    private final Map<String, UserProfile> playerProfiles;
    private final File profilesFolder;

    public PlayerProfileManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.playerProfiles = new HashMap<>();
        this.profilesFolder = new File(plugin.getDataFolder(), "profiles");
        if (!profilesFolder.exists()) {
            profilesFolder.mkdirs(); // Create profiles folder if it doesn't exist
        }
    }

    public boolean hasProfile(String playerName) {
        return playerProfiles.containsKey(playerName);
    }

    public void createProfile(String playerName) {
        UserProfile profile = new UserProfile(playerName);
        playerProfiles.put(playerName, profile);
        saveProfile(playerName); // Save the newly created profile to a file
    }

    public UserProfile getProfile(String playerName) {
        if (!hasProfile(playerName)) {
            loadProfile(playerName); // Attempt to load from file if not already in memory
        }
        return playerProfiles.get(playerName);
    }

    public void saveAllProfiles() {
        for (String playerName : playerProfiles.keySet()) {
            saveProfile(playerName); // Save all profiles to their respective files
        }
    }

    public void saveProfile(String playerName) {
        UserProfile profile = playerProfiles.get(playerName);
        if (profile == null) return;

        File profileFile = new File(profilesFolder, playerName + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(profileFile);

        // Save profile data
        config.set("playerID", profile.getPlayerID().toString());
        config.set("playerName", profile.getPlayerName());
        config.set("chosenClass", profile.getChosenClass());
        config.set("lastClassSelection", profile.getLastClassSelection());
        config.set("currentAttributePoints", profile.getCurrentAttributePoints());
        config.set("totalAllocatedPoints", profile.getTotalAllocatedPoints());

        // Save class-specific allocated points
        config.set("totalArcherAllocatedPoints", profile.getTotalArcherAllocatedPoints());
        config.set("totalSwordsmanAllocatedPoints", profile.getTotalSwordsmanAllocatedPoints());
        config.set("totalAlchemistAllocatedPoints", profile.getTotalAlchemistAllocatedPoints());

        // Save class-specific attributes
        saveClassAttributes(config, "defaultClass", profile.getDefaultClassInfo());
        saveClassAttributes(config, "archerClass", profile.getArcherClassInfo());
        saveClassAttributes(config, "swordsmanClass", profile.getSwordsmanClassInfo());
        saveClassAttributes(config, "alchemistClass", profile.getAlchemistClassInfo());

        // Save elemental choice
        config.set("selectedElement", profile.getSelectedElement());
        config.set("lastElementSelection", profile.getLastElementSelection());

        // Save race choice
        config.set("selectedRace", profile.getSelectedRace());
        config.set("lastRaceSelection", profile.getLastRaceSelection());

        // Save skill choice
        config.set("selectedSkill",profile.getSelectedSkill());
        config.set("lastSkillSelection", profile.getLastSkillSelection());

        // save team
        config.set("team", profile.getTeam());

        //pvp
        config.set("pvpEnabled",profile.isPvpEnabled());

        //RPG
        config.set("rpgStatus",profile.getRPG());


        try {
            config.save(profileFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save profile for player: " + playerName, e);
        }
    }

    private void saveClassAttributes(FileConfiguration config, String path, UserProfile.ClassAttributes attributes) {
        config.set(path + ".str", attributes.getStr());
        config.set(path + ".agi", attributes.getAgi());
        config.set(path + ".dex", attributes.getDex());
        config.set(path + ".intel", attributes.getIntel());
        config.set(path + ".vit", attributes.getVit());
        config.set(path + ".luk", attributes.getLuk());
    }

    public void loadProfile(String playerName) {
        File profileFile = new File(profilesFolder, playerName + ".yml");
        if (!profileFile.exists()) {
            createProfile(playerName); // Create a new profile if none exists
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(profileFile);
        String playerID = config.getString("playerID");
        String chosenClass = config.getString("chosenClass", "default");
        long lastClassSelection = config.getLong("lastClassSelection", 0);
        int currentAttributePoints = config.getInt("currentAttributePoints", 0);

        // Initialize the user profile
        UserProfile profile = new UserProfile(playerName);
        profile.setChosenClass(chosenClass);
        profile.setLastClassSelection(lastClassSelection);
        profile.setCurrentAttributePoints(currentAttributePoints);

        // Load class-specific allocated points
        loadClassAttributes(config, "defaultClass", profile.getDefaultClassInfo());
        loadClassAttributes(config, "archerClass", profile.getArcherClassInfo());
        loadClassAttributes(config, "swordsmanClass", profile.getSwordsmanClassInfo());
        loadClassAttributes(config, "alchemistClass", profile.getAlchemistClassInfo());

        // Load elemental choice
        profile.setSelectedElement(config.getString("selectedElement", "none"));
        profile.setLastElementSelection(config.getLong("lastElementSelection", 0));

        // Load race choice
        profile.setSelectedRace(config.getString("selectedRace", "default"));
        profile.setLastRaceSelection(config.getLong("lastRaceSelection", 0));

        // Load skill choice
        profile.setSelectedSkill(config.getString("selectedSkill", "default"));
        profile.setLastSkillSelection(config.getLong("lastSkillSelection", 0));

        // load team
        profile.setTeam(config.getString("team","none"));

        //pvp
        profile.setPvpEnabled(config.getBoolean("pvpEnabled",false));

        //RPG
        profile.setRPG(config.getString("rpgStatus","off"));

        playerProfiles.put(playerName, profile);
    }

    private void loadClassAttributes(FileConfiguration config, String path, UserProfile.ClassAttributes attributes) {
        attributes.setStr(config.getInt(path + ".str", 0));
        attributes.setAgi(config.getInt(path + ".agi", 0));
        attributes.setDex(config.getInt(path + ".dex", 0));
        attributes.setIntel(config.getInt(path + ".intel", 0));
        attributes.setVit(config.getInt(path + ".vit", 0));
        attributes.setLuk(config.getInt(path + ".luk", 0));
    }
}
