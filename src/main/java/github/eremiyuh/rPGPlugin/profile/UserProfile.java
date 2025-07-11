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

    //constructor
    public UserProfile(String playerName) {
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
        this.stamina = 1000;
        this.durability = 1000;
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

        // Get the old amount before setting the new one
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

        // Notify the player of the change in currency
        if (player != null) {
            double difference = amount - oldAmount;
            if (difference > 0) {
                // Player gained currency
                player.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "You gained " + (int) difference + " " + currencyName + ".");

            } else if (difference < 0) {
                // Player spent currency
                player.sendMessage(ChatColor.BLUE + "" + ChatColor.ITALIC + "You spent " + (int) Math.abs(difference) + " " + currencyName + ".");
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

}