package com.portalmod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class PortalCommand {
    public static final Map<BlockPos, BlockPos> portals = new HashMap<>();

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("setportal")
            .then(CommandManager.argument("x", IntegerArgumentType.integer())
            .then(CommandManager.argument("y", IntegerArgumentType.integer())
            .then(CommandManager.argument("z", IntegerArgumentType.integer())
            .then(CommandManager.argument("destX", IntegerArgumentType.integer())
            .then(CommandManager.argument("destY", IntegerArgumentType.integer())
            .then(CommandManager.argument("destZ", IntegerArgumentType.integer())
            .executes(ctx -> {
                BlockPos portalPos = new BlockPos(
                    IntegerArgumentType.getInteger(ctx, "x"),
                    IntegerArgumentType.getInteger(ctx, "y"),
                    IntegerArgumentType.getInteger(ctx, "z")
                );
                BlockPos destination = new BlockPos(
                    IntegerArgumentType.getInteger(ctx, "destX"),
                    IntegerArgumentType.getInteger(ctx, "destY"),
                    IntegerArgumentType.getInteger(ctx, "destZ")
                );
                portals.put(portalPos, destination);
                ctx.getSource().sendFeedback(Text.literal("Portal set at " + portalPos + " -> " + destination), false);
                return 1;
            }))))))));

        dispatcher.register(CommandManager.literal("delportal")
            .then(CommandManager.argument("x", IntegerArgumentType.integer())
            .then(CommandManager.argument("y", IntegerArgumentType.integer())
            .then(CommandManager.argument("z", IntegerArgumentType.integer())
            .executes(ctx -> {
                BlockPos portalPos = new BlockPos(
                    IntegerArgumentType.getInteger(ctx, "x"),
                    IntegerArgumentType.getInteger(ctx, "y"),
                    IntegerArgumentType.getInteger(ctx, "z")
                );
                if (portals.remove(portalPos) != null) {
                    ctx.getSource().sendFeedback(Text.literal("Portal removed at " + portalPos), false);
                } else {
                    ctx.getSource().sendFeedback(Text.literal("No portal found at this location!"), false);
                }
                return 1;
            })))));
    }
}
