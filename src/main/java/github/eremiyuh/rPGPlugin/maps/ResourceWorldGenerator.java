package github.eremiyuh.rPGPlugin.maps;

import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;

import javax.sql.rowset.spi.SyncFactoryException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getServer;

public class ResourceWorldGenerator {

    // List of worlds with configuration for each
    private static final List<WorldConfig> worldConfigs = new ArrayList<>();

    static {
        // Example configuration (you can load this dynamically from a config file, database, etc.)
        worldConfigs.add(new WorldConfig("world_resource_1", 1000, 1000, 100));
        worldConfigs.add(new WorldConfig("world_resource_2", 500, 500, 100));
        // Add more worlds here as needed
    }

    // Create all resource worlds based on the configuration list
    public static void createResourceWorlds() {
        for (WorldConfig config : worldConfigs) {
            createWorld(config);
        }
    }

    // Create a single world based on the configuration
    private static void createWorld(WorldConfig config) {
        WorldCreator creator = new WorldCreator(config.getName());
        creator.type(WorldType.NORMAL);
        creator.generator(new ResourceWorldChunkGenerator());
        creator.generateStructures(true); // Optional: can be false if you don't need structures
        World world = creator.createWorld();

        if (world != null) {
            world.getWorldBorder().setSize(config.getSize());
            world.setSpawnLocation(config.getX(), 65, config.getZ()); // Set spawn at (X, 65, Z)
            world.getWorldBorder().setCenter(config.getX(), config.getZ());

            // Ensure the world has biome assignments (important for terrain generation)
            assignBiomes(world);
        }
    }

    // Ensure biomes are assigned to all chunks in the world
    private static void assignBiomes(World world) {
        int radius = 60; // Adjust if you need more area
        Random random = new Random();

        for (int x = -radius; x < radius; x++) {
            for (int z = -radius; z < radius; z++) {
                // Randomly select a biome (Plains, Nether Wastes, or The End)
                Biome biome = getRandomBiome(random);

                // Set the biome for the entire chunk
                for (int cx = 0; cx < 16; cx++) { // Iterate over the chunk's X coordinate
                    for (int cz = 0; cz < 16; cz++) { // Iterate over the chunk's Z coordinate
                        world.setBiome(x * 16 + cx, z * 16 + cz, biome);
                    }
                }
            }
        }
    }

    // Randomly select a biome (Plains, Nether Wastes, or The End)
    private static Biome getRandomBiome(Random random) {
        int choice = random.nextInt(3);
        switch (choice) {
            case 0: return Biome.PLAINS;
            case 1: return Biome.NETHER_WASTES;
            case 2: return Biome.THE_END;
            default: return Biome.PLAINS;  // Default biome if something goes wrong
        }
    }

    // Delete all resource worlds based on the configuration list
    public static void deleteResourceWorlds() throws SyncFactoryException {
        for (WorldConfig config : worldConfigs) {
            deleteWorld(config);
        }
    }

    // Delete a single world based on its configuration
    private static void deleteWorld(WorldConfig config) throws SyncFactoryException {
        World world = Bukkit.getWorld(config.getName());

        if (world != null) {
            // Unload world without saving (false parameter means don't save)
            Bukkit.unloadWorld(world, false);
            getLogger().info("World " + config.getName() + " unloaded.");
        }

        // Delete the world folder entirely
        File worldFolder = new File(getServer().getWorldContainer(), config.getName());
        if (worldFolder.exists()) {
            try {
                deleteDirectory(worldFolder);
                getLogger().info("World directory " + config.getName() + " deleted.");
            } catch (IOException e) {
                getLogger().warning("Failed to delete world directory: " + e.getMessage());
            }
        }
    }

    // Delete directory and its contents recursively
    private static void deleteDirectory(File directory) throws IOException {
        if (directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                deleteDirectory(file);
            }
        }
        Files.delete(directory.toPath());
    }

    // WorldConfig class to store world-specific information
    private static class WorldConfig {
        private final String name;
        private final int x;
        private final int z;
        private final int size;

        public WorldConfig(String name, int x, int z, int size) {
            this.name = name;
            this.x = x;
            this.z = z;
            this.size = size;
        }

        public String getName() {
            return name;
        }

        public int getX() {
            return x;
        }

        public int getZ() {
            return z;
        }

        public int getSize() {
            return size;
        }
    }

    // Custom chunk generator for resource worlds
    private static class ResourceWorldChunkGenerator extends ChunkGenerator {

        @Override
        public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
            ChunkData chunkData = createChunkData(world);

            // Get the biome for this chunk
            Biome chunkBiome = world.getBiome(chunkX * 16, chunkZ * 16);

            // Generate terrain based on the biome
            switch (chunkBiome) {
                case PLAINS:
                    generatePlainsTerrain(chunkData, random);
                    break;
                case NETHER_WASTES:
                    generateNetherTerrain(chunkData, random);
                    break;
                case THE_END:
                    generateEndTerrain(chunkData, random);
                    break;
                default:
                    generatePlainsTerrain(chunkData, random); // Default to Plains if no match
                    break;
            }

            return chunkData;
        }

        // Generate terrain for Plains biome
        private void generatePlainsTerrain(ChunkData chunkData, Random random) {
            PerlinNoise noise = new PerlinNoise(random.nextInt(1000));

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    // Use Perlin noise for height calculation
                    int maxHeight = (int)(50 + 10 * noise.noise(x * 0.1, z * 0.1));
                    chunkData.setBlock(x, 0, z, Material.BEDROCK); // Bedrock at base

                    // Generate blocks above bedrock with smoother transitions
                    for (int y = 1; y < maxHeight; y++) {
                        Material material = generateRandomMaterialForPlains(random);
                        chunkData.setBlock(x, y, z, material);
                    }

                    // Top layer (grass block)
                    chunkData.setBlock(x, maxHeight, z, Material.GRASS_BLOCK);

                    // Dirt layers just below the grass
                    for (int y = maxHeight - 1; y > maxHeight - 4 && y > 0; y--) {
                        chunkData.setBlock(x, y, z, Material.DIRT);
                    }

                    // Generate ores at random heights
                    generateOres(chunkData, random, x, z, maxHeight);
                }
            }
        }

        // Generate terrain for Nether Wastes biome
        private void generateNetherTerrain(ChunkData chunkData, Random random) {
            PerlinNoise noise = new PerlinNoise(random.nextInt(1000));

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    // Use Perlin noise for jagged terrain
                    int maxHeight = (int)(100 + 10 * noise.noise(x * 0.2, z * 0.2));
                    chunkData.setBlock(x, 0, z, Material.BEDROCK); // Bedrock at base

                    // Generate more jagged, harsh terrain
                    for (int y = 1; y < maxHeight; y++) {
                        Material material = generateRandomMaterialForNether(random);
                        chunkData.setBlock(x, y, z, material);
                    }

                    // Top layer (Netherrack)
                    chunkData.setBlock(x, maxHeight, z, Material.NETHERRACK);

                    // Make the top look like layers of Netherrack
                    for (int y = maxHeight - 1; y > maxHeight - 4 && y > 0; y--) {
                        chunkData.setBlock(x, y, z, Material.NETHERRACK);
                    }

                    // Generate ores at random heights
                    generateOres(chunkData, random, x, z, maxHeight);
                }
            }
        }

        // Generate terrain for End biome
        private void generateEndTerrain(ChunkData chunkData, Random random) {
            PerlinNoise noise = new PerlinNoise(random.nextInt(1000));

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    // Use Perlin noise for smoother, more varied heights
                    int maxHeight = (int)(75 + 10 * noise.noise(x * 0.1, z * 0.1));
                    chunkData.setBlock(x, 0, z, Material.BEDROCK); // Bedrock at base

                    // Generate the End terrain above bedrock
                    for (int y = 1; y < maxHeight; y++) {
                        Material material = generateRandomMaterialForEnd(random);
                        chunkData.setBlock(x, y, z, material);
                    }

                    // End stone at the top layer
                    chunkData.setBlock(x, maxHeight, z, Material.END_STONE);

                    // More End stone beneath it to add depth
                    for (int y = maxHeight - 1; y > maxHeight - 4 && y > 0; y--) {
                        chunkData.setBlock(x, y, z, Material.END_STONE);
                    }

                    // Generate ores at random heights
                    generateOres(chunkData, random, x, z, maxHeight);
                }
            }
        }

        // Generate ores for a given chunk
        private void generateOres(ChunkData chunkData, Random random, int x, int z, int maxHeight) {
            for (int y = 5; y < maxHeight; y++) {
                // Randomly decide where ores will appear
                if (random.nextInt(100) < 5) { // 5% chance per block for ore generation
                    chunkData.setBlock(x, y, z, generateRandomOre(random));
                }
            }
        }

        // Random materials for Plains
        private Material generateRandomMaterialForPlains(Random random) {
            int chance = random.nextInt(100);
            if (chance < 45) return Material.STONE;
            else if (chance < 50) return Material.COBBLESTONE;
            else if (chance < 55) return Material.DIRT;
            else if (chance < 60) return Material.IRON_ORE;
            else if (chance < 65) return Material.LAPIS_ORE;
            else if (chance < 70) return Material.GOLD_ORE;
            else if (chance < 75) return Material.DIAMOND_ORE;
            else return Material.LAPIS_ORE;
        }

        // Random materials for Nether Wastes
        private Material generateRandomMaterialForNether(Random random) {
            int chance = random.nextInt(100);
            if (chance < 45) return Material.NETHERRACK;
            else if (chance < 50) return Material.SOUL_SAND;
            else if (chance < 55) return Material.CHISELED_NETHER_BRICKS;
            else if (chance < 60) return Material.IRON_ORE;
            else if (chance < 65) return Material.LAPIS_ORE;
            else if (chance < 70) return Material.NETHER_GOLD_ORE;
            else if (chance < 75) return Material.DIAMOND_ORE;
            else if (chance < 90) return Material.NETHER_QUARTZ_ORE;
            else return Material.LAPIS_ORE;
        }

        // Random materials for End
        private Material generateRandomMaterialForEnd(Random random) {
            int chance = random.nextInt(100);
            if (chance < 45) return Material.END_STONE_BRICKS;
            else if (chance < 50) return Material.PURPUR_BLOCK;
            else if (chance < 55) return Material.PURPUR_PILLAR;
            else if (chance < 60) return Material.IRON_ORE;
            else if (chance < 65) return Material.LAPIS_ORE;
            else if (chance < 70) return Material.GOLD_ORE;
            else if (chance < 75) return Material.DIAMOND_ORE;
            else if (chance < 99) return Material.DRAGON_EGG;
            else return Material.LAPIS_ORE;
        }

        // Random ore generation (applies to all biomes)
        private Material generateRandomOre(Random random) {
            int choice = random.nextInt(5);
            switch (choice) {
                case 0: return Material.IRON_ORE;
                case 1: return Material.GOLD_ORE;
                case 2: return Material.DIAMOND_ORE;
                case 3: return Material.LAPIS_ORE;
                case 4: return Material.REDSTONE_ORE;
                default: return Material.COAL_ORE;
            }
        }
    }
}
