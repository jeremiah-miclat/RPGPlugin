package github.eremiyuh.rPGPlugin.profile;

import java.util.UUID;

public class UserProfile {
    private UUID playerID;
    private String playerName;
    private String chosenClass;
    private long lastClassSelection;
    private int currentAttributePoints;

    private ClassAttributes defaultClassInfo;
    private ClassAttributes archerClassInfo;
    private ClassAttributes swordsmanClassInfo;
    private ClassAttributes alchemistClassInfo;

    // New fields for elemental choice
    private String selectedElement = "none";  // Default to no element
    private long lastElementSelection = 0;    // Timestamp of the last element selection

    // New fields for race choice
    private String selectedRace = "default";  // Default to no race
    private long lastRaceSelection = 0;    // Timestamp of the last element selection

    public UserProfile(String playerName) {
        this.playerID = UUID.randomUUID(); // Generate a unique ID for the player
        this.playerName = playerName;
        this.chosenClass = "default"; // Default class upon joining
        this.lastClassSelection = 0; // Time of last class selection (could be used as timestamp)
        this.currentAttributePoints = 0; // Starts with 0 points

        // Initialize class-specific attributes
        this.defaultClassInfo = new ClassAttributes();
        this.archerClassInfo = new ClassAttributes();
        this.swordsmanClassInfo = new ClassAttributes();
        this.alchemistClassInfo = new ClassAttributes();


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

    public long getLastClassSelection() {
        return lastClassSelection;
    }

    public void setLastClassSelection(long lastClassSelection) {
        this.lastClassSelection = lastClassSelection;
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
        this.lastElementSelection = System.currentTimeMillis();  // Set timestamp
    }

    // Method to check if 10-minute cooldown has passed
    public boolean canSelectNewElement() {
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastElementSelection) >= 600000;  // 600,000 ms = 10 minutes
    }

    public long getLastElementSelection() {
        return lastElementSelection;
    }

    public void setLastElementSelection(long lastElementSelection) {
        this.lastElementSelection = lastElementSelection;
    }

    // Getter and setter for selectedRace
    public String getSelectedRace() {
        return selectedRace;
    }

    public void setSelectedRace(String race) {
        this.selectedRace = race;
        this.lastRaceSelection = System.currentTimeMillis();  // Set timestamp
    }

    // Method to check if 10-minute cooldown has passed
    public boolean canSelectNewRace() {
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastRaceSelection) >= 600000;  // 600,000 ms = 10 minutes
    }

    public long getLastRaceSelection() {
        return lastRaceSelection;
    }


    public void setLastRaceSelection(long lastRaceSelection) {
        this.lastRaceSelection = lastRaceSelection;
    }


    // Inner class to hold class-specific attributes
    public static class ClassAttributes {
        private int str;
        private int agi;
        private int dex;
        private int intel; // Renamed from "int" to avoid conflicts with reserved keyword
        private int vit;
        private int luk;

        public ClassAttributes() {
            this.str = 0;
            this.agi = 0;
            this.dex = 0;
            this.intel = 0;
            this.vit = 0;
            this.luk = 0;
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
        this.lastClassSelection = System.currentTimeMillis(); // Update the last selection timestamp
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

    // Helper method to add points to a class's attributes
    private void addPointsToClass(ClassAttributes classAttributes, String attribute, int points) {
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
}
