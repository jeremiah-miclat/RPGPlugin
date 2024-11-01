package github.eremiyuh.rPGPlugin.manager;

import github.eremiyuh.rPGPlugin.profile.OwnedChunk;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ChunkManager {
    private final Map<String, OwnedChunk> ownedChunks = new HashMap<>();
    private final File dataFile;
    private final FileConfiguration config;

    public ChunkManager(File dataFolder) {
        this.dataFile = new File(dataFolder, "chunk_data.yml");
        this.config = YamlConfiguration.loadConfiguration(dataFile);
        loadChunkData();
    }

    public void claimChunk(Player player, Chunk chunk) {
        String key = generateChunkKey(chunk);
        if (!ownedChunks.containsKey(key)) {
            ownedChunks.put(key, new OwnedChunk(player.getName(), chunk));
            saveChunkData();
            player.sendMessage("Chunk claimed successfully!");
        } else {
            player.sendMessage("This chunk is already claimed.");
        }
    }

    public void unclaimChunk(Player player, Chunk chunk) {
        String key = generateChunkKey(chunk);
        OwnedChunk ownedChunk = ownedChunks.get(key);

        if (ownedChunk != null && ownedChunk.getOwner().equals(player.getName())) {
            ownedChunks.remove(key);
            saveChunkData();
            player.sendMessage("Chunk unclaimed successfully!");
        } else {
            player.sendMessage("You do not own this chunk or it is not claimed.");
        }
    }

    public boolean isOwner(String playerName, Chunk chunk) {
        OwnedChunk ownedChunk = ownedChunks.get(generateChunkKey(chunk));
        return ownedChunk != null && ownedChunk.getOwner().equals(playerName);
    }

    public OwnedChunk getOwnedChunk(Chunk chunk) {
        return ownedChunks.get(generateChunkKey(chunk));
    }

    public boolean isTrusted(String playerName, Chunk chunk) {
        OwnedChunk ownedChunk = ownedChunks.get(generateChunkKey(chunk));
        return ownedChunk != null && ownedChunk.getTrustedPlayers().contains(playerName);
    }

    public void trustPlayer(String ownerName, Chunk chunk, String trustedPlayer) {
        OwnedChunk ownedChunk = ownedChunks.get(generateChunkKey(chunk));
        if (ownedChunk != null && ownedChunk.getOwner().equals(ownerName)) {
            ownedChunk.addTrustedPlayer(trustedPlayer);
            saveChunkData();
        }
    }

    public List<OwnedChunk> getOwnedChunksByOwner(String ownerName) {
        List<OwnedChunk> ownedChunksList = new ArrayList<>();
        for (OwnedChunk ownedChunk : ownedChunks.values()) {
            if (ownedChunk.getOwner().equals(ownerName)) {
                ownedChunksList.add(ownedChunk);
            }
        }
        return ownedChunksList;
    }


    public void untrustPlayer(String ownerName, Chunk chunk, String trustedPlayer) {
        OwnedChunk ownedChunk = ownedChunks.get(generateChunkKey(chunk));
        if (ownedChunk != null && ownedChunk.getOwner().equals(ownerName)) {
            ownedChunk.removeTrustedPlayer(trustedPlayer);
            saveChunkData();
        }
    }

    public String getOwner(Chunk chunk) {
        OwnedChunk ownedChunk = ownedChunks.get(generateChunkKey(chunk));
        return ownedChunk != null ? ownedChunk.getOwner() : null;
    }

    private String generateChunkKey(Chunk chunk) {
        return chunk.getWorld().getName() + ":" + chunk.getX() + ":" + chunk.getZ();
    }

    public void saveChunkData() {
        for (Map.Entry<String, OwnedChunk> entry : ownedChunks.entrySet()) {
            String key = entry.getKey();
            OwnedChunk ownedChunk = entry.getValue();
            config.set(key + ".owner", ownedChunk.getOwner());
            List<String> trustedPlayers = new ArrayList<>(ownedChunk.getTrustedPlayers());
            config.set(key + ".trustedPlayers", trustedPlayers);
        }
        try {
            config.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadChunkData() {
        for (String key : config.getKeys(false)) {
            String ownerName = config.getString(key + ".owner");
            List<String> trustedPlayers = config.getStringList(key + ".trustedPlayers");
            Chunk chunk = parseChunkKey(key);
            OwnedChunk ownedChunk = new OwnedChunk(ownerName, chunk);
            for (String playerName : trustedPlayers) {
                ownedChunk.addTrustedPlayer(playerName);
            }
            ownedChunks.put(key, ownedChunk);
        }
    }

    private Chunk parseChunkKey(String key) {
        String[] parts = key.split(":");
        String worldName = parts[0];
        int x = Integer.parseInt(parts[1]);
        int z = Integer.parseInt(parts[2]);
        return Bukkit.getWorld(worldName).getChunkAt(x, z);
    }
}

