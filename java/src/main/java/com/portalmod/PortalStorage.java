package com.portalmod;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.math.BlockPos;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class PortalStorage {
    private static final File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), "portals.json");
    private static final Gson gson = new Gson();

    public static void save(Map<BlockPos, BlockPos> portals) {
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(portals, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<BlockPos, BlockPos> load() {
        if (!file.exists()) return new HashMap<>();
        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<BlockPos, BlockPos>>() {}.getType();
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }
}
