package com.portalmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PortalMod implements ModInitializer {
    public static final Map<BlockPos, BlockPos> portals = new HashMap<>();

    @Override
    public void onInitialize() {
        // Load portals from storage when the mod starts
        portals.putAll(PortalStorage.load());

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            PortalCommand.register(dispatcher);
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                Vec3d playerPos = player.getPos();
                BlockPos blockPos = new BlockPos((int) Math.floor(playerPos.x), (int) Math.floor(playerPos.y), (int) Math.floor(playerPos.z));

                if (portals.containsKey(blockPos)) {
                    BlockPos destination = portals.get(blockPos);
                    if (destination != null) {
                        ServerWorld world = (ServerWorld) player.getWorld();
                        player.teleport(world, destination.getX() + 0.5, destination.getY() + 0.1, destination.getZ() + 0.5, Collections.emptySet(), player.getYaw(), player.getPitch(), false);
                        player.sendMessage(Text.literal("Teleported successfully!"), false);
                    }
                }
            }
        });
    }
}
