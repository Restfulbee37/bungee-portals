package com.portalmod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class PortalCommand {
    public static final Map<BlockPos, BlockPos> portals = new HashMap<>();
    public static final Map<String, BlockPos> namedPortals = new HashMap<>();

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

                // Debug message to confirm portal setting
                ctx.getSource().sendFeedback(() -> Text.literal("Setting portal '" + portalName + "' from (" + x1 + ", " + y1 + ", " + z1 + ") to (" + x2 + ", " + y2 + ", " + z2 + ") -> " + destination), false);

                // Store every block in the portal range
                for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
                    for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
                        for (int z = Math.min(z1, z2); z <= Math.max(z1, z2); z++) {
                            BlockPos portalBlock = new BlockPos(x, y, z);
                            portals.put(portalBlock, destination);  // Store each portal block in the map
                            namedPortals.put(portalName, portalBlock);
                        }
                    }
                }

                ctx.getSource().sendFeedback(() -> Text.literal("Portal '" + portalName + "' successfully set!"), false);
                return 1;
            }))))))))))));

        dispatcher.register(CommandManager.literal("delportal")
            .then(CommandManager.argument("name", StringArgumentType.string())
            .executes(ctx -> {
                String portalName = StringArgumentType.getString(ctx, "name");
                BlockPos portalPos = namedPortals.remove(portalName);
                
                if (portalPos != null) {
                    portals.values().removeIf(name -> name.equals(portalName));
                    ctx.getSource().sendFeedback(() -> Text.literal("Portal '" + portalName + "' removed."), false);
                } else {
                    ctx.getSource().sendFeedback(() -> Text.literal("No portal found with name: " + portalName), false);
                }
                return 1;
            })));
    }
}
