package com.portalmod;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.math.BlockPos;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class PortalStorage {
    private static final Path configDir = FabricLoader.getInstance().getConfigDir();
    private static final File file = configDir.resolve("portals.json").toFile();
    
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public static Map<BlockPos, BlockPos> load() {
        if (!file.exists()) {
            return new HashMap<>();
        }
        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<List<PortalEntry>>() {}.getType();
            List<PortalEntry> portalList = gson.fromJson(reader, type);

            Map<BlockPos, BlockPos> portals = new HashMap<>();
            for (PortalEntry entry : portalList) {
                portals.put(entry.portal, entry.destination);
            }
            return portals;
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        } catch (JsonSyntaxException e) {
            System.err.println("Error: Corrupted portal config file! Deleting...");
            file.delete(); // Delete corrupted file
            return new HashMap<>();
        }
    }

    public static void save(Map<BlockPos, BlockPos> portals) {
        List<PortalEntry> portalList = new ArrayList<>();
        for (Map.Entry<BlockPos, BlockPos> entry : portals.entrySet()) {
            portalList.add(new PortalEntry(entry.getKey(), entry.getValue()));
        }

        try (Writer writer = new FileWriter(file)) {
            gson.toJson(portalList, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class PortalEntry {
        BlockPos portal;
        BlockPos destination;

        PortalEntry(BlockPos portal, BlockPos destination) {
            this.portal = portal;
            this.destination = destination;
        }
    }
}
