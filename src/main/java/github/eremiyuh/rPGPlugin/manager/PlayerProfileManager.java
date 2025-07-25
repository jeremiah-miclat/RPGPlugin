package github.eremiyuh.rPGPlugin.manager;

import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        config.set("level", profile.getLevel());
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


        // Save skill choice
        config.set("selectedSkill",profile.getSelectedSkill());

        // save team
        config.set("team", profile.getTeam());
        config.set("teammembers", profile.getTeamMembers());

        //vaults
        config.set("vault1Players", profile.getVault1Players());
        config.set("vault2Players", profile.getVault2Players());
        config.set("vault3Players", profile.getVault3Players());
        config.set("vault4Players", profile.getVault4Players());
        config.set("vault5Players", profile.getVault5Players());

        //pvp
        config.set("pvpEnabled",profile.isPvpEnabled());


        //currencies
        config.set("diamond",profile.getDiamond());
        config.set("emerald", profile.getCurrency("emerald"));
        config.set("iron", profile.getCurrency("iron"));
        config.set("lapis", profile.getCurrency("lapis"));
        config.set("gold", profile.getCurrency("gold"));
        config.set("copper", profile.getCurrency("copper"));
        config.set("netherite", profile.getCurrency("netherite"));


        // TO ADD
        config.set("enderpearl", profile.getCurrency("enderpearl"));
        config.set("activitypoints", profile.getCurrency("activitypoints"));
        config.set("stamina", profile.getStamina());
        config.set("durability", profile.getDurability());
        config.set("abysspoints",profile.getAbysspoints());
        config.set("junkpoints",profile.getJunkPoints());

        //claimPoints
        config.set("claimPoints",profile.getClaimPoints());

        //potion
        config.set("potion",profile.getPotion());

        //login
        config.set("password",profile.getPassword());
        config.set("loggedin",profile.isLoggedIn());

        // passives
        config.set("builder",profile.getBuilder());
        config.set("destroyer",profile.getDestroyer());
        config.set("hunter",profile.getHunter());
        config.set("fisherman",profile.getFisherman());
        config.set("crafter",profile.getCrafter());
        config.set("trader",profile.getTrader());

        try {
            config.save(profileFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save profile for player: " + playerName, e);
        }

        //home
        config.set("maxHomes", profile.getMaxHomes());

        // Clear existing homes
        config.set("homes", null);

        config.set("isPublic",profile.getIsPublic());

        // team vault
        config.set("shareVault",profile.isShareVault());

        Map<String, Location> homes = profile.getHomes();
        for (Map.Entry<String, Location> homeEntry : homes.entrySet()) {
            String homeName = homeEntry.getKey();
            Location loc = homeEntry.getValue();
            config.set("homes." + homeName + ".world", loc.getWorld().getName());
            config.set("homes." + homeName + ".x", loc.getX());
            config.set("homes." + homeName + ".y", loc.getY());
            config.set("homes." + homeName + ".z", loc.getZ());
            config.set("homes." + homeName + ".yaw", loc.getYaw());
            config.set("homes." + homeName + ".pitch", loc.getPitch());
        }

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

    public boolean profileExist(String playerName) {
        File profileFile = new File(profilesFolder, playerName + ".yml");
        return profileFile.exists();
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
        int currentAttributePoints = config.getInt("currentAttributePoints", 100);

        // Initialize the user profile
        UserProfile profile = new UserProfile(playerName);
        profile.setChosenClass(chosenClass);
        profile.setCurrentAttributePoints(currentAttributePoints);

        // Load class-specific allocated points
        loadClassAttributes(config, "defaultClass", profile.getDefaultClassInfo());
        loadClassAttributes(config, "archerClass", profile.getArcherClassInfo());
        loadClassAttributes(config, "swordsmanClass", profile.getSwordsmanClassInfo());
        loadClassAttributes(config, "alchemistClass", profile.getAlchemistClassInfo());

        // Load elemental choice
        profile.setSelectedElement(config.getString("selectedElement", "none"));

        // Load skill choice
        profile.setSelectedSkill(config.getString("selectedSkill", "default"));

        //pvp
        profile.setPvpEnabled(config.getBoolean("pvpEnabled",false));

        //currencies
        profile.setCurrency("diamond", config.getDouble("diamond", 0));
        profile.setCurrency("emerald", config.getDouble("emerald", 0));
        profile.setCurrency("iron", config.getDouble("iron", 0));
        profile.setCurrency("lapis", config.getDouble("lapis", 0));
        profile.setCurrency("gold", config.getDouble("gold", 0));
        profile.setCurrency("copper", config.getDouble("copper", 0));
        profile.setCurrency("netherite", config.getDouble("netherite", 0));
        profile.setCurrency("activitypoints", config.getDouble("activitypoints", 0));
        profile.setJunkPoints(config.getInt("junkpoints",0));

        profile.setCurrency("enderpearl", config.getInt("enderpearl", 50));
        profile.setStamina(config.getInt("stamina", 100000));
        profile.setDurability(config.getInt("durability", 1000));
        profile.setAbysspoints(config.getDouble("abysspoints",0));

        //claim points
        profile.setClaimPoints(config.getDouble("claimPoints",10));
        profile.setLevel(config.getInt("level",0));
        //potion
        profile.setPotion(config.getDouble("potion",100));

        // load team
        profile.setTeam(config.getString("team","none"));
        List<String> teamMembers = config.getStringList("teammembers");
        if (teamMembers.isEmpty()) {
            teamMembers = new ArrayList<>(); // Default to an empty list if none exists
        }
        profile.setTeamMembers(teamMembers);

        //vaults
        profile.setVault1Players(safeGetList(config, "vault1Players"));
        profile.setVault2Players(safeGetList(config, "vault2Players"));
        profile.setVault3Players(safeGetList(config, "vault3Players"));
        profile.setVault4Players(safeGetList(config, "vault4Players"));
        profile.setVault5Players(safeGetList(config, "vault5Players"));

        //home
        int maxHomes = config.getInt("maxHomes", 5); // Default to 5 if not present
        profile.setMaxHomes(maxHomes);

        ConfigurationSection homesSection = config.getConfigurationSection("homes");
        if (homesSection != null) {
            for (String homeName : homesSection.getKeys(false)) {
                String worldName = homesSection.getString(homeName + ".world");
                World world = plugin.getServer().getWorld(worldName);
                if (world == null) continue; // Skip if the world doesn't exist

                double x = homesSection.getDouble(homeName + ".x");
                double y = homesSection.getDouble(homeName + ".y");
                double z = homesSection.getDouble(homeName + ".z");
                float yaw = (float) homesSection.getDouble(homeName + ".yaw");
                float pitch = (float) homesSection.getDouble(homeName + ".pitch");

                Location homeLocation = new Location(world, x, y, z, yaw, pitch);
                profile.addHome(homeName, homeLocation);
            }
        }

        // login
        profile.setPassword(config.getString("password",""));
        profile.setLoggedIn(config.getBoolean("loggedin",false));

        profile.setIsPublic(config.getBoolean("isPublic",false));

        //passives
        profile.setDestroyer(config.getInt("destroyer",0));
        profile.setBuilder(config.getInt("builder",0));
        profile.setFisherman(config.getInt("fisherman",0));
        profile.setHunter(config.getInt("hunter",0));
        profile.setCrafter(config.getInt("crafter",0));
        profile.setTrader(config.getInt("trader",0));

        profile.setShareVault(config.getBoolean("shareVault",false));

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

    public void resetLoginStatus() {
        if (!profilesFolder.exists()) {
            return; // No profiles to reset
        }

        File[] profileFiles = profilesFolder.listFiles();
        if (profileFiles == null) return;

        for (File profileFile : profileFiles) {
            if (!profileFile.isFile() || !profileFile.getName().endsWith(".yml")) continue;

            FileConfiguration config = YamlConfiguration.loadConfiguration(profileFile);

            // Set the "loggedin" field to false
            config.set("loggedin", false);

            try {
                config.save(profileFile);
                plugin.getLogger().info("Reset login status for: " + profileFile.getName());
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not reset login status for profile: " + profileFile.getName(), e);
            }
        }
    }

    public void removeProfileOnLogout(String playerName) {
        // Save profile data before removing it
        saveProfile(playerName);

        // Remove from memory (HashMap)
        playerProfiles.remove(playerName);
    }

    public void removeProfileOnMemory(String playerName) {
        playerProfiles.remove(playerName);
    }

    private List<String> safeGetList(ConfigurationSection config, String key) {
        List<String> list = config.getStringList(key);
        return list.isEmpty() ? new ArrayList<>() : list;
    }

}