package github.eremiyuh.rPGPlugin.manager;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VaultManager {

    private final RPGPlugin plugin;
    private final Map<String, Map<Integer, Inventory>> playerVaults = new HashMap<>();
    private int maxVaults = 5;
    private final File pluginDataFolder;

    public VaultManager(RPGPlugin plugin, File pluginDataFolder) {
        this.plugin = plugin;
        this.pluginDataFolder = pluginDataFolder;
    }

    // Set max vaults to allow future expansion
    public void setMaxVaults(int maxVaults) {
        this.maxVaults = maxVaults;
    }

    // Open a vault for a player, initializing if necessary
    public void openVault(Player player, int vaultNumber) {
        if (vaultNumber < 1 || vaultNumber > maxVaults) {
            player.sendMessage("Vault number must be between 1 and " + maxVaults);
            return;
        }

        String playerName = player.getName();
        Map<Integer, Inventory> vaults = playerVaults.computeIfAbsent(playerName, k -> new HashMap<>());
        Inventory vault = vaults.computeIfAbsent(vaultNumber, v -> loadVault(playerName, vaultNumber));

        player.openInventory(vault);
    }

    // Load vault contents from the file or create a new one if not present
    // Load vault contents from the file or create a new one if not present
    private Inventory loadVault(String playerName, int vaultNumber) {
        Inventory vault = Bukkit.createInventory(null, 27, "Vault " + vaultNumber); // 27 slots
        File playerVaultFile = new File(pluginDataFolder, playerName + "_vaults.yml");
        FileConfiguration vaultData = YamlConfiguration.loadConfiguration(playerVaultFile);
        String path = "vault" + vaultNumber;

        // Check if the path exists in the configuration
        if (vaultData.contains(path)) {
            Object itemsObj = vaultData.get(path);

            // Check if the itemsObj is a list and contains ItemStack elements
            if (itemsObj instanceof List<?> itemsList) {
                // Validate that every item is an ItemStack
                List<ItemStack> validItems = itemsList.stream()
                        .filter(item -> item instanceof ItemStack)
                        .map(item -> (ItemStack) item)
                        .toList(); // Only collect valid ItemStacks

                if (!validItems.isEmpty()) {
                    vault.setContents(validItems.toArray(new ItemStack[0])); // Set valid items
                } else {
                    Bukkit.getLogger().warning("Vault data for " + playerName + " vault " + vaultNumber + " is invalid. Resetting vault.");
                    vault.clear();  // Clear the vault if invalid data
                }
            } else {
                Bukkit.getLogger().warning("Expected list of ItemStacks for " + path + " but found: " + itemsObj);
                vault.clear();  // Reset the vault if data is not as expected
            }
        }

        return vault;
    }


    // Save a player's vault contents to the file
    public void saveVault(Player player, int vaultNumber) {
        String playerName = player.getName();
        Map<Integer, Inventory> vaults = playerVaults.get(playerName);
        if (vaults != null && vaults.containsKey(vaultNumber)) {
            Inventory vault = vaults.get(vaultNumber);
            File playerVaultFile = new File(pluginDataFolder, playerName + "_vaults.yml");
            FileConfiguration vaultData = YamlConfiguration.loadConfiguration(playerVaultFile);
            vaultData.set("vault" + vaultNumber, serializeVaultItems(vault));

            // Save the file after each vault save
            saveFile(playerVaultFile, vaultData);
        }
    }

    // Save all vaults on server shutdown
    public void saveAllVaults() {
        for (String playerName : playerVaults.keySet()) {
            Map<Integer, Inventory> vaults = playerVaults.get(playerName);
            if (vaults != null) {
                File playerVaultFile = new File(pluginDataFolder, playerName + "_vaults.yml");
                FileConfiguration vaultData = YamlConfiguration.loadConfiguration(playerVaultFile);
                for (int vaultNumber : vaults.keySet()) {
                    vaultData.set("vault" + vaultNumber, serializeVaultItems(vaults.get(vaultNumber)));
                }
                // Save after all vaults are saved
                saveFile(playerVaultFile, vaultData);
            }
        }
    }

    // Serialize the vault contents (convert ItemStacks to a list for saving)
    private List<ItemStack> serializeVaultItems(Inventory vault) {
        ItemStack[] contents = vault.getContents();

        // Filter out null values before converting to a list
        return Arrays.stream(contents)
                .filter(item -> item != null)  // Remove any null items from the list
                .collect(Collectors.toList()); // Collect valid items into a list
    }


    // Save vault data to disk
    private void saveFile(File file, FileConfiguration vaultData) {
        try {
            vaultData.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load all player vaults from their respective files on startup
    public void loadVaults() {
        File[] files = pluginDataFolder.listFiles((dir, name) -> name.endsWith("_vaults.yml"));
        if (files != null) {
            for (File file : files) {
                String playerName = file.getName().replace("_vaults.yml", "");
                FileConfiguration vaultData = YamlConfiguration.loadConfiguration(file);
                Map<Integer, Inventory> vaults = new HashMap<>();

                for (int i = 1; i <= maxVaults; i++) {
                    if (vaultData.contains("vault" + i)) {
                        vaults.put(i, loadVault(playerName, i));
                    }
                }
                playerVaults.put(playerName, vaults);
            }
        }
    }

    public int getMaxVaults() {
        return maxVaults;
    }

    public Map<Integer, Inventory> getPlayerVaults(String playerName) {
        return playerVaults.get(playerName);
    }
}
