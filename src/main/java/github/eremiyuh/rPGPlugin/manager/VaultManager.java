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
import java.util.*;
import java.util.concurrent.CompletableFuture;
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

    public void setMaxVaults(int maxVaults) {
        this.maxVaults = maxVaults;
    }

    public int getMaxVaults() {
        return maxVaults;
    }

    // Open a vault for a player
    public void openVault(Player player, int vaultNumber) {
        if (vaultNumber < 1 || vaultNumber > maxVaults) {
            player.sendMessage("Vault number must be between 1 and " + maxVaults);
            return;
        }

        String playerName = player.getName();
        Map<Integer, Inventory> vaults = playerVaults.computeIfAbsent(playerName, k -> new HashMap<>());

        // Check if the vault is already loaded
        if (!vaults.containsKey(vaultNumber)) {
            // Asynchronously load the vault to avoid blocking the server
            loadVaultAsync(playerName, vaultNumber).thenAccept(vault -> {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    vaults.put(vaultNumber, vault);
                    player.openInventory(vault);
                });
            });
        } else {
            player.openInventory(vaults.get(vaultNumber));
        }
    }

    // Asynchronous vault loading
    private CompletableFuture<Inventory> loadVaultAsync(String playerName, int vaultNumber) {
        return CompletableFuture.supplyAsync(() -> {
            Inventory vault = Bukkit.createInventory(null, 27, "Vault " + vaultNumber); // Default 27 slots
            File playerVaultFile = new File(pluginDataFolder, playerName + "_vaults.yml");
            if (playerVaultFile.exists()) {
                FileConfiguration vaultData = YamlConfiguration.loadConfiguration(playerVaultFile);
                String path = "vault" + vaultNumber;

                if (vaultData.contains(path)) {
                    List<ItemStack> items = vaultData.getList(path, Collections.emptyList()).stream()
                            .filter(item -> item instanceof ItemStack)
                            .map(item -> (ItemStack) item)
                            .collect(Collectors.toList());
                    vault.setContents(items.toArray(new ItemStack[0]));
                }
            }
            return vault;
        });
    }

    // Save vault when player disconnects
    public void saveAllVaultsForPlayer(Player player) {
        String playerName = player.getName();
        Map<Integer, Inventory> vaults = playerVaults.get(playerName);

        if (vaults != null) {
            File playerVaultFile = new File(pluginDataFolder, playerName + "_vaults.yml");
            FileConfiguration vaultData = YamlConfiguration.loadConfiguration(playerVaultFile);

            for (int vaultNumber : vaults.keySet()) {
                vaultData.set("vault" + vaultNumber, serializeVaultItems(vaults.get(vaultNumber)));
            }

            saveFile(playerVaultFile, vaultData);
            playerVaults.remove(playerName); // Free up memory after saving
        }
    }

    // Save all vaults on server shutdown
    public void saveAllVaults() {
        for (String playerName : playerVaults.keySet()) {
            saveAllVaultsForPlayer(Bukkit.getPlayer(playerName));
        }
    }

    // Serialize vault contents
    private List<ItemStack> serializeVaultItems(Inventory vault) {
        return Arrays.stream(vault.getContents())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // Save file to disk
    private void saveFile(File file, FileConfiguration vaultData) {
        try {
            vaultData.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveVault(Player player, int vaultNumber) {
        String playerName = player.getName();
        Map<Integer, Inventory> vaults = playerVaults.get(playerName);

        if (vaults == null || !vaults.containsKey(vaultNumber)) {
            Bukkit.getLogger().warning("Vault " + vaultNumber + " for player " + playerName + " does not exist in memory.");
            return;
        }

        Inventory vault = vaults.get(vaultNumber);
        File playerVaultFile = new File(pluginDataFolder, playerName + "_vaults.yml");
        FileConfiguration vaultData = YamlConfiguration.loadConfiguration(playerVaultFile);

        // Serialize and save the vault items
        vaultData.set("vault" + vaultNumber, serializeVaultItems(vault));

        // Save the file to disk
        saveFile(playerVaultFile, vaultData);

        Bukkit.getLogger().info("Vault " + vaultNumber + " for player " + playerName + " has been saved.");
    }

}
