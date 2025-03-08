package com.portalmod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

public class PortalCommand {
    public static final Map<String, BlockPos> namedPortals = new HashMap<>();
    public static final Map<BlockPos, String> portalNames = new HashMap<>(); // Now correctly defined!

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("setportal")
            .then(CommandManager.argument("x1", IntegerArgumentType.integer())
            .then(CommandManager.argument("y1", IntegerArgumentType.integer())
            .then(CommandManager.argument("z1", IntegerArgumentType.integer())
            .then(CommandManager.argument("x2", IntegerArgumentType.integer())
            .then(CommandManager.argument("y2", IntegerArgumentType.integer())
            .then(CommandManager.argument("z2", IntegerArgumentType.integer())
            .then(CommandManager.argument("destX", IntegerArgumentType.integer())
            .then(CommandManager.argument("destY", IntegerArgumentType.integer())
            .then(CommandManager.argument("destZ", IntegerArgumentType.integer())
            .then(CommandManager.argument("name", StringArgumentType.string())
            .executes(ctx -> {
                int x1 = IntegerArgumentType.getInteger(ctx, "x1");
                int y1 = IntegerArgumentType.getInteger(ctx, "y1");
                int z1 = IntegerArgumentType.getInteger(ctx, "z1");
                int x2 = IntegerArgumentType.getInteger(ctx, "x2");
                int y2 = IntegerArgumentType.getInteger(ctx, "y2");
                int z2 = IntegerArgumentType.getInteger(ctx, "z2");
                String portalName = StringArgumentType.getString(ctx, "name");

                BlockPos destination = new BlockPos(
                    IntegerArgumentType.getInteger(ctx, "destX"),
                    IntegerArgumentType.getInteger(ctx, "destY"),
                    IntegerArgumentType.getInteger(ctx, "destZ")
                );

                // Store all portal blocks & link them to the name
                for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
                    for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
                        for (int z = Math.min(z1, z2); z <= Math.max(z1, z2); z++) {
                            BlockPos portalBlock = new BlockPos(x, y, z);
                            PortalMod.portals.put(portalBlock, destination);
                            portalNames.put(portalBlock, portalName); // Store name for deletion
                        }
                    }
                }

                namedPortals.put(portalName, new BlockPos(x1, y1, z1)); // Store one reference for fast lookup

                // Save portals to file
                PortalStorage.save(PortalMod.portals);

                ctx.getSource().sendFeedback(() -> Text.literal("Portal '" + portalName + "' successfully set!"), false);
                return 1;
            }))))))))))));

        dispatcher.register(CommandManager.literal("delportal")
            .then(CommandManager.argument("name", StringArgumentType.string())
            .executes(ctx -> {
                String portalName = StringArgumentType.getString(ctx, "name");

                // Find all blocks linked to this portal name
                boolean found = false;
                for (Map.Entry<BlockPos, String> entry : portalNames.entrySet()) {
                    if (entry.getValue().equals(portalName)) {
                        PortalMod.portals.remove(entry.getKey());
                        found = true;
                    }
                }

                if (found) {
                    // Remove the portal from name lookup
                    namedPortals.remove(portalName);
                    portalNames.values().removeIf(name -> name.equals(portalName));

                    // Save updated portals
                    PortalStorage.save(PortalMod.portals);

                    ctx.getSource().sendFeedback(() -> Text.literal("Portal '" + portalName + "' removed."), false);
                } else {
                    ctx.getSource().sendFeedback(() -> Text.literal("No portal found with name: " + portalName), false);
                }
                return 1;
            })));

        dispatcher.register(CommandManager.literal("tpme")
            .then(CommandManager.argument("x", IntegerArgumentType.integer())
            .then(CommandManager.argument("y", IntegerArgumentType.integer())
            .then(CommandManager.argument("z", IntegerArgumentType.integer())
            .executes(ctx -> {
                int x = IntegerArgumentType.getInteger(ctx, "x");
                int y = IntegerArgumentType.getInteger(ctx, "y");
                int z = IntegerArgumentType.getInteger(ctx, "z");
                
                ServerPlayerEntity player = ctx.getSource().getPlayer();
                ServerWorld world = (ServerWorld) player.getWorld();

                player.teleport(world, x + 0.5, y, z + 0.5, Collections.emptySet(), player.getYaw(), player.getPitch(), false);
                player.sendMessage(Text.literal("Teleported to " + x + ", " + y + ", " + z), false);
                return 1;
            })))));
    }
}
