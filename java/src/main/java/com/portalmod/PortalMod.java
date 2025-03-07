package com.portalmod;

import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class PortalMod implements ModInitializer {
    @Override
    public void onInitialize() {
        PortalStorage.load();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            PortalCommand.register(dispatcher);
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                BlockPos playerPos = player.getBlockPos();
                if (PortalCommand.portals.containsKey(playerPos)) {
                    BlockPos destination = PortalCommand.portals.get(playerPos);
                    player.teleport(destination.getX(), destination.getY(), destination.getZ());
                    player.sendMessage(Text.literal("Teleported to portal destination!"), false);
                }
            }
        });
    }
}
