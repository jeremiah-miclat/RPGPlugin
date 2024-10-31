package github.eremiyuh.rPGPlugin.profile;

import org.bukkit.Chunk;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class OwnedChunk {
    private final String owner;
    private final Chunk chunk;
    private final Set<String> trustedPlayers;

    public OwnedChunk(String owner, Chunk chunk) {
        this.owner = owner;
        this.chunk = chunk;
        this.trustedPlayers = new HashSet<>();
    }

    public String getOwner() {
        return owner;
    }

    public Set<String> getTrustedPlayers() {
        return trustedPlayers;
    }

    public void addTrustedPlayer(String playerName) {
        trustedPlayers.add(playerName);
    }

    public void removeTrustedPlayer(String playerName) {
        trustedPlayers.remove(playerName);
    }
}

