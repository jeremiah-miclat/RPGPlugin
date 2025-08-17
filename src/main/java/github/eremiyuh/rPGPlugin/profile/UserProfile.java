package github.eremiyuh.rPGPlugin.profile;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class UserProfile {
    private UUID playerID;
    private String playerName;
    private String chosenClass;

    private int currentAttributePoints;

    private ClassAttributes defaultClassInfo;
    private ClassAttributes archerClassInfo;
    private ClassAttributes swordsmanClassInfo;
    private ClassAttributes alchemistClassInfo;

    // New fields for elemental choice
    private String selectedElement = "none";  // Default to no element


    // New fields for skill choice
    private  String selectedSkill = "none";


    // ascend
    private boolean isAscending;



    private boolean pvpEnabled = true;

    private double diamond;
    private double emerald;
    private double iron;
    private double lapiz;
    private double gold;
    private double copper;
    private double netherite;
    private double claimPoints;

    // TEAMS
    private String team = "none";
    private List<String> teamInvites;
    private List<String> teamMembers = new ArrayList<>();

    //vaults
    private List<String> vault1Players = new ArrayList<>();
    private List<String> vault2Players = new ArrayList<>();
    private List<String> vault3Players = new ArrayList<>();
    private List<String> vault4Players = new ArrayList<>();
    private List<String> vault5Players = new ArrayList<>();

    private double potion;


    //homes
    private Map<String, Location> homes = new HashMap<>();
    private int maxHomes = 5;  // Initial maximum homes


    // TO ADD
    // other currencies
    private int enderpearl;
    private int stamina;
    private int durability;
    private double abysspoints;


    //register and login
    private String password;
    private boolean loggedIn;

    // boss health indicator
    private boolean bossIndicator;

    private int junkPoints;

    private boolean isPublic;

    private double activitypoints;

    private int builder;
    private int fisherman;
    private int destroyer;
    private int hunter;
    private int crafter;
    private int trader;

    private int level;



    private int hpMultiplier;
    private int statDmgMultiplier;

    private int tempStr;
    private int tempAgi;
    private int tempVit;
    private int tempDex;
    private int tempIntel;
    private int tempLuk;

    private double crit;
    private double critDmg;
    private double ls;

    //team vault
    private boolean shareVault;

    // abyss traits
    private String abyssTrait = "none";

    private double meleeDmg = 0;
    private double longDmg = 0;
    private double splashDmg = 0;

    // abyss royale level
    private int arLvl = 0;

    private double cdr = 0;
    private double critResist =0;
    //constructor
    public UserProfile(String playerName) {
        this.crit = 0;
        this.critDmg = 1.5;
        this.ls = 0;
        this.playerID = UUID.randomUUID(); // Generate a unique ID for the player
        this.playerName = playerName;
        this.chosenClass = "default"; // Default class upon joining

        this.currentAttributePoints = 100;

        // Initialize class-specific attributes
        this.defaultClassInfo = new ClassAttributes();
        this.archerClassInfo = new ClassAttributes();
        this.swordsmanClassInfo = new ClassAttributes();
        this.alchemistClassInfo = new ClassAttributes();

        // Initialize the team invite list
        this.teamInvites = new ArrayList<>();

        // Initialize pvp toggle
        this.pvpEnabled =false;


        // money
        this.diamond = 0;
        this.emerald = 0;
        this.iron = 0;
        this.lapiz = 0;
        this.copper = 0;
        this.gold = 0;
        this.netherite=0;

        // other currencies
        this.enderpearl = 50;
        this.stamina = 100000;
        this.durability = 100000;
        this.abysspoints = 0;

        this.claimPoints =10;
        this.team = "none";
        this.teamMembers = getTeamMembers();

        this.potion=100;

        //home
        this.homes = new HashMap<>();
        this.maxHomes = 5;

        this.password="";
        this.loggedIn=isLoggedIn();

        this.bossIndicator = true;
        this.isAscending=false;

        this.junkPoints = 0;

        this.isPublic = true;
        this.activitypoints=0;
        this.builder = 0;
        this.destroyer = 0;
        this.fisherman = 0;
        this.hunter = 0;
        this.crafter = 0;
        this.trader = 0;
        this.level=0;

        this.hpMultiplier = 0;
        this.statDmgMultiplier = 0;
        this.tempStr = 0;
        this.tempAgi = 0;
        this.tempVit = 0;
        this.tempDex = 0;
        this.tempIntel = 0;
        this.tempLuk = 0;

        this.shareVault = false;
        this.vault1Players = getVault1Players();
        this.vault2Players =getVault2Players();
        this.vault3Players =getVault3Players();
        this.vault4Players =getVault4Players();
        this.vault5Players =getVault5Players();
    }

    // Getters and setters


    public UUID getPlayerID() {
        return playerID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getChosenClass() {
        return chosenClass;
    }

    public void setChosenClass(String chosenClass) {
        this.chosenClass = chosenClass;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }


    public int getHpMultiplier() {
        return hpMultiplier;
    }

    public void setHpMultiplier(int hpMultiplier) {
        this.hpMultiplier = hpMultiplier;
    }

    public int getStatDmgMultiplier() {
        return statDmgMultiplier;
    }

    public void setStatDmgMultiplier(int statDmgMultiplier) {
        this.statDmgMultiplier = statDmgMultiplier;
    }

    public int getTempStr() {
        return tempStr;
    }

    public void setTempStr(int tempStr) {
        this.tempStr = tempStr;
    }

    public int getTempAgi() {
        return tempAgi;
    }

    public void setTempAgi(int tempAgi) {
        this.tempAgi = tempAgi;
    }

    public int getTempVit() {
        return tempVit;
    }

    public void setTempVit(int tempVit) {
        this.tempVit = tempVit;
    }

    public int getTempDex() {
        return tempDex;
    }

    public void setTempDex(int tempDex) {
        this.tempDex = tempDex;
    }

    public int getTempIntel() {
        return tempIntel;
    }

    public void setTempIntel(int tempIntel) {
        this.tempIntel = tempIntel;
    }

    public int getTempLuk() {
        return tempLuk;
    }

    public void setTempLuk(int tempLuk) {
        this.tempLuk = tempLuk;
    }

    public void resetTemporaryStats() {
        this.tempStr = 0;
        this.tempAgi = 0;
        this.tempVit = 0;
        this.tempDex = 0;
        this.tempIntel = 0;
        this.tempLuk = 0;
        this.hpMultiplier = 1;
        this.statDmgMultiplier = 1;
    }

    public int getCurrentAttributePoints() {
        return currentAttributePoints;
    }

    public void setCurrentAttributePoints(int points) {
        this.currentAttributePoints = points;
    }

    // Calculate total points for a specific class
    private int calculateTotalPointsForClass(ClassAttributes classAttributes) {
        return classAttributes.getStr() + classAttributes.getAgi() + classAttributes.getDex() +
                classAttributes.getIntel() + classAttributes.getVit() + classAttributes.getLuk();
    }

    // Getters for total points based on class
    public int getTotalArcherAllocatedPoints() {
        return calculateTotalPointsForClass(archerClassInfo);
    }

    public int getTotalSwordsmanAllocatedPoints() {
        return calculateTotalPointsForClass(swordsmanClassInfo);
    }

    public int getTotalAlchemistAllocatedPoints() {
        return calculateTotalPointsForClass(alchemistClassInfo);
    }

    // Calculate and return total allocated points across all classes
    public int getTotalAllocatedPoints() {
        return getTotalArcherAllocatedPoints() + getTotalSwordsmanAllocatedPoints() + getTotalAlchemistAllocatedPoints();
    }

    public ClassAttributes getDefaultClassInfo() {
        return defaultClassInfo;
    }

    public ClassAttributes getArcherClassInfo() {
        return archerClassInfo;
    }

    public ClassAttributes getSwordsmanClassInfo() {
        return swordsmanClassInfo;
    }

    public ClassAttributes getAlchemistClassInfo() {
        return alchemistClassInfo;
    }

    // Getter and setter for selectedElement
    public String getSelectedElement() {
        return selectedElement;
    }

    public void setSelectedElement(String element) {
        this.selectedElement = element;

    }


    // Getter and setter for selectedSkill
    public String getSelectedSkill() {
        return selectedSkill;
    }

    public void setSelectedSkill(String skill) {



        this.selectedSkill = skill;


    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    // Getters and setters for team invites
    public List<String> getTeamInvites() {
        return teamInvites;
    }

    public void addTeamInvite(String playerName) {
        if (!teamInvites.contains(playerName)) {
            teamInvites.add(playerName);
        }
    }

    public void removeTeamInvite(String playerName) {
        teamInvites.remove(playerName);
    }

    public boolean isInvited(String playerName) {
        return teamInvites.contains(playerName);
    }

    public boolean isPvpEnabled() {
        return pvpEnabled;
    }

    public void setPvpEnabled(boolean pvpEnabled) {
        this.pvpEnabled = pvpEnabled;
    }


    public double getDiamond() {
        return diamond;
    }

    public void setDiamond(double diamond) {
        this.diamond = diamond;
    }



    public double getClaimPoints() {
        return claimPoints;
    }

    public void setClaimPoints(double claimPoints) {
        this.claimPoints = claimPoints;
    }

    public double getPotion() {
        return potion;
    }

    public void setPotion(double potion) {
        this.potion = potion;
    }

    public double getEmerald() {
        return emerald;
    }

    public void setEmerald(double emerald) {
        this.emerald = emerald;
    }

    public double getIron() {
        return iron;
    }

    public void setIron(double iron) {
        this.iron = iron;
    }

    public double getLapiz() {
        return lapiz;
    }

    public void setLapiz(double lapiz) {
        this.lapiz = lapiz;
    }

    public double getGold() {
        return gold;
    }

    public void setGold(double gold) {
        this.gold = gold;
    }

    public double getActivitypoints() {
        return activitypoints;
    }

    public void setActivitypoints(double activitypoints) {
        this.activitypoints = activitypoints;
    }

    // currencies
    public double getCurrency(String currencyName) {
        switch (currencyName.toLowerCase()) {
            case "diamond":
                return diamond;
            case "emerald":
                return emerald;
            case "iron":
                return iron;
            case "lapis":
                return lapiz;
            case "gold":
                return gold;
            case "enderpearl":
                return enderpearl;
            case "netherite":
                return netherite;
            case "copper":
                return copper;
            case "abysspoints":
                return abysspoints;
            case "activitypoints":
                return activitypoints;
            default:
                throw new IllegalArgumentException("Invalid currency name: " + currencyName);
        }
    }

    // TO ADD
    // other currencies
    public int getEnderPearl() {
        return enderpearl;
    }

    public void setEnderPearl(int enderpearl) {
        this.enderpearl = enderpearl;
        if (enderpearl < 10) {
            Player player = Bukkit.getPlayer(this.getPlayerName());
            player.sendMessage(ChatColor.YELLOW +"You only have " + enderpearl + " enderpearl/s left for teleportation. You can buy some on /tradinghall using "
                    + ChatColor.GREEN + "emeralds");
        }
    }

    public int getStamina() {
        return stamina;
    }

    public void setStamina(int stamina) {
        this.stamina = stamina;
    }


    public int getDurability() {
        return durability;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }


    public void setCurrency(String currencyName, double amount) {
        Player player = Bukkit.getPlayer(this.getPlayerName());
        double oldAmount = 0;

        // Store old amount & update currency
        switch (currencyName.toLowerCase()) {
            case "diamond":
                oldAmount = this.diamond;
                this.diamond = amount;
                break;
            case "emerald":
                oldAmount = this.emerald;
                this.emerald = amount;
                break;
            case "iron":
                oldAmount = this.iron;
                this.iron = amount;
                break;
            case "lapis":
                oldAmount = this.lapiz;
                this.lapiz = amount;
                break;
            case "gold":
                oldAmount = this.gold;
                this.gold = amount;
                break;
            case "enderpearl":
                oldAmount = this.enderpearl;
                this.enderpearl = (int) amount;
                break;
            case "netherite":
                oldAmount = this.netherite;
                this.netherite = amount;
                break;
            case "copper":
                oldAmount = this.copper;
                this.copper = amount;
                break;
            case "abysspoints":
                oldAmount = this.abysspoints;
                this.abysspoints = amount;
                break;
            case "activitypoints":
                oldAmount = this.activitypoints;
                this.activitypoints = amount;
                break;
            default:
                throw new IllegalArgumentException("Invalid currency name: " + currencyName);
        }

        // Notify player
        if (player != null) {
            double difference = amount - oldAmount;

            // Format difference — if less than 1 or not a whole number, keep 2 decimals
            String formattedDiff;
            if (Math.abs(difference) < 1 || difference % 1 != 0) {
                formattedDiff = String.format("%.2f", Math.abs(difference));
            } else {
                formattedDiff = String.valueOf((int) Math.abs(difference));
            }

            if (difference > 0) {
                player.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC +
                        "You gained " + formattedDiff + " " + currencyName + ".");
            } else if (difference < 0) {
                player.sendMessage(ChatColor.BLUE + "" + ChatColor.ITALIC +
                        "You spent " + formattedDiff + " " + currencyName + ".");
            }
        }
    }



    public double getAbysspoints() {
        return abysspoints;
    }

    public void setAbysspoints(double abysspoints) {
        this.abysspoints = abysspoints;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isBossIndicator() {
        return bossIndicator;
    }

    public void setBossIndicator(boolean bossIndicator) {
        this.bossIndicator = bossIndicator;
    }

    public boolean isAscending() {
        return isAscending;
    }

    public void setAscending(boolean ascending) {
        isAscending = ascending;
    }

    public double getNetherite() {
        return netherite;
    }

    public void setNetherite(double netherite) {
        this.netherite = netherite;
    }

    public double getCopper() {
        return copper;
    }

    public void setCopper(double copper) {
        this.copper = copper;
    }

    public List<String> getVault1Players() {
        return vault1Players;
    }

    public void setVault1Players(List<String> vault1Players) {
        this.vault1Players = vault1Players;
    }

    public List<String> getVault2Players() {
        return vault2Players;
    }

    public void setVault2Players(List<String> vault2Players) {
        this.vault2Players = vault2Players;
    }

    public List<String> getVault3Players() {
        return vault3Players;
    }

    public void setVault3Players(List<String> vault3Players) {
        this.vault3Players = vault3Players;
    }

    public List<String> getVault4Players() {
        return vault4Players;
    }

    public void setVault4Players(List<String> vault4Players) {
        this.vault4Players = vault4Players;
    }

    public List<String> getVault5Players() {
        return vault5Players;
    }

    public void setVault5Players(List<String> vault5Players) {
        this.vault5Players = vault5Players;
    }

    public String getAbyssTrait() {
        return abyssTrait;
    }

    public void setAbyssTrait(String abyssTrait) {
        this.abyssTrait = abyssTrait;
    }

    public double getMeleeDmg() {
        return meleeDmg;
    }

    public void setMeleeDmg(double meleeDmg) {
        this.meleeDmg = meleeDmg;
    }

    public double getLongDmg() {
        return longDmg;
    }

    public void setLongDmg(double longDmg) {
        this.longDmg = longDmg;
    }

    public double getSplashDmg() {
        return splashDmg;
    }

    public void setSplashDmg(double splashDmg) {
        this.splashDmg = splashDmg;
    }

    public int getArLvl() {
        return arLvl;
    }

    public void setArLvl(int arLvl) {
        this.arLvl = arLvl;
    }

    public double getCdr() {
        return cdr;
    }

    public void setCdr(double cdr) {
        this.cdr = cdr;
    }

    public double getCritResist() {
        return critResist;
    }

    public void setCritResist(double critResist) {
        this.critResist = critResist;
    }

    // Inner class to hold class-specific attributes
    public static class ClassAttributes {
        private int str;
        private int agi;
        private int dex;
        private int intel; // Renamed from "int" to avoid conflicts with reserved keyword
        private int vit;
        private int luk;
        private int totalPointsSpent;

        public ClassAttributes() {
            this.str = 0;
            this.agi = 0;
            this.dex = 0;
            this.intel = 0;
            this.vit = 0;
            this.luk = 0;
            this.totalPointsSpent = 0;
        }

        // Getters and setters for class attributes
        public int getStr() {
            return str;
        }

        public void setStr(int str) {
            this.str = str;
        }

        public int getAgi() {
            return agi;
        }

        public void setAgi(int agi) {
            this.agi = agi;
        }

        public int getDex() {
            return dex;
        }

        public void setDex(int dex) {
            this.dex = dex;
        }

        public int getIntel() {
            return intel;
        }

        public void setIntel(int intel) {
            this.intel = intel;
        }

        public int getVit() {
            return vit;
        }

        public void setVit(int vit) {
            this.vit = vit;
        }

        public int getLuk() {
            return luk;
        }

        public void setLuk(int luk) {
            this.luk = luk;
        }

        // Method to reset all attributes to zero
        public void resetAttributes() {
            this.str = 0;
            this.agi = 0;
            this.dex = 0;
            this.intel = 0;
            this.vit = 0;
            this.luk = 0;
        }

        // Method to display current attribute values
        public String displayAttributes() {
            return "Strength: " + str + ", Agility: " + agi + ", Dexterity: " + dex +
                    ", Intelligence: " + intel + ", Vitality: " + vit + ", Luck: " + luk;
        }
    }

    // Utility method to switch classes
    public void switchClass(String newClass) {
        this.chosenClass = newClass;
        resetCurrentClassAttributes(); // Reset attribute points if class is switched
    }

    // Reset attribute points for the current class
    public void resetCurrentClassAttributes() {
        if (chosenClass.equalsIgnoreCase("Archer")) {
            archerClassInfo.resetAttributes();
        } else if (chosenClass.equalsIgnoreCase("Swordsman")) {
            swordsmanClassInfo.resetAttributes();
        } else if (chosenClass.equalsIgnoreCase("Alchemist")) {
            alchemistClassInfo.resetAttributes();
        } else {
            defaultClassInfo.resetAttributes();
        }
    }

    // Display attributes of the current chosen class
    public String displayCurrentClassAttributes() {
        if (chosenClass.equalsIgnoreCase("Archer")) {
            return archerClassInfo.displayAttributes();
        } else if (chosenClass.equalsIgnoreCase("Swordsman")) {
            return swordsmanClassInfo.displayAttributes();
        } else if (chosenClass.equalsIgnoreCase("Alchemist")) {
            return alchemistClassInfo.displayAttributes();
        } else {
            return defaultClassInfo.displayAttributes();
        }
    }

    // Add attribute points to the current class
    public void addAttributePoints(String attribute, int points) {
        if (chosenClass.equalsIgnoreCase("Archer")) {
            addPointsToClass(archerClassInfo, attribute, points);
        } else if (chosenClass.equalsIgnoreCase("Swordsman")) {
            addPointsToClass(swordsmanClassInfo, attribute, points);
        } else if (chosenClass.equalsIgnoreCase("Alchemist")) {
            addPointsToClass(alchemistClassInfo, attribute, points);
        } else {
            addPointsToClass(defaultClassInfo, attribute, points);
        }
        currentAttributePoints -= points; // Decrease available attribute points
    }

    // Getter for team members
    public List<String> getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(List<String> teamMembers) {
        this.teamMembers = teamMembers;
    }

    // Method to add a team member (only if the list has fewer than 4 members)
    public boolean addTeamMember(String playerName) {
        if (teamMembers.size() < 10 && !teamMembers.contains(playerName)) {
            teamMembers.add(playerName);
            return true; // Successfully added
        }
        return false; // Failed to add (list full or player already in team)
    }

    // Method to remove a team member
    public boolean removeTeamMember(String playerName) {
        return teamMembers.remove(playerName); // Returns true if player was removed
    }

    // Method to check if a player is a team member
    public boolean isTeamMember(String playerName) {
        return teamMembers.contains(playerName);
    }

    // Helper method to add points to a class's attributes
    public void addPointsToClass(ClassAttributes classAttributes, String attribute, int points) {
        switch (attribute.toLowerCase()) {
            case "str":
                classAttributes.setStr(classAttributes.getStr() + points);
                break;
            case "agi":
                classAttributes.setAgi(classAttributes.getAgi() + points);
                break;
            case "dex":
                classAttributes.setDex(classAttributes.getDex() + points);
                break;
            case "intel":
                classAttributes.setIntel(classAttributes.getIntel() + points);
                break;
            case "vit":
                classAttributes.setVit(classAttributes.getVit() + points);
                break;
            case "luk":
                classAttributes.setLuk(classAttributes.getLuk() + points);
                break;
            default:
                throw new IllegalArgumentException("Invalid attribute: " + attribute);
        }
    }


    //home methods
    public Map<String, Location> getHomes() {
        return homes;
    }

    public boolean addHome(String name, Location location) {
        if (this.homes.size() < maxHomes) {
            this.homes.put(name, location);
            return true;
        }
        return false;
    }

    public Location getHome(String name) {
        return homes.get(name);
    }

    public boolean homeExists(String name) {
        return homes.containsKey(name);
    }

    // Getter for maxHomes
    public int getMaxHomes() {
        return maxHomes;
    }

    // Method to increase max homes
    public void increaseMaxHomes(int increment) {
        this.maxHomes += increment;
    }

    // Optional: Method to set max homes directly
    public void setMaxHomes(int maxHomes) {
        this.maxHomes = maxHomes;
    }

    public boolean removeHome(String homeName) {
        if (this.homes.containsKey(homeName)) {
            this.homes.remove(homeName);
            return true;
        }
        else {
            return false;
        }
    }

    public int getJunkPoints() {
        return junkPoints;
    }

    public void setJunkPoints(int junkPoints) {
        this.junkPoints = junkPoints;
    }

    public boolean getIsPublic() {
        return this.isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    // Getter and Setter for builder
    public int getBuilder() {
        return builder;
    }

    public void setBuilder(int builder) {
        this.builder = builder;
    }

    // Getter and Setter for fisherman
    public int getFisherman() {
        return fisherman;
    }

    public void setFisherman(int fisherman) {
        this.fisherman = fisherman;
    }

    // Getter and Setter for destroyer
    public int getDestroyer() {
        return destroyer;
    }

    public void setDestroyer(int destroyer) {
        this.destroyer = destroyer;
    }

    // Getter and Setter for hunter
    public int getHunter() {
        return hunter;
    }

    public void setHunter(int hunter) {
        this.hunter = hunter;
    }

    // Getter and Setter for crafter
    public int getCrafter() {
        return crafter;
    }

    public void setCrafter(int crafter) {
        this.crafter = crafter;
    }

    // Getter and Setter for trader
    public int getTrader() {
        return trader;
    }

    public void setTrader(int trader) {
        this.trader = trader;
    }

    public boolean isShareVault() {
        return shareVault;
    }

    public void setShareVault(boolean shareVault) {
        this.shareVault = shareVault;
    }

    public double getCrit() {
        return crit;
    }

    public void setCrit(double crit) {
        this.crit = crit;
    }

    public double getCritDmg() {
        return critDmg;
    }

    public void setCritDmg(double critDmg) {
        this.critDmg = critDmg;
    }

    public double getLs() {
        return ls;
    }

    public void setLs(double ls) {
        this.ls = ls;
    }


}