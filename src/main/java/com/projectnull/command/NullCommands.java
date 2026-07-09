package com.projectnull.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.projectnull.entity.NullEntity;
import com.projectnull.event.NullJoinHandler;
import com.projectnull.horror.NullHorrorEffects;
import com.projectnull.horror.SignPlacer;
import com.projectnull.network.HorrorEffectPayload;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = com.projectnull.ProjectNull.MODID)
public final class NullCommands {
    private NullCommands() {
    }

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("null")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("forcejoin")
                        .executes(ctx -> {
                            NullJoinHandler.forceJoin(ctx.getSource().getServer());
                            ctx.getSource().sendSuccess(() -> Component.literal("Forced Null to join the server"), true);
                            return 1;
                        }))
                .then(Commands.literal("scare")
                        .executes(ctx -> scare(ctx, ctx.getSource().getPlayerOrException()))
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(ctx -> scare(ctx, EntityArgument.getPlayer(ctx, "target"))))
                        .then(Commands.literal("effect")
                                .then(Commands.argument("type", StringArgumentType.word())
                                        .executes(ctx -> scareEffect(
                                                ctx,
                                                ctx.getSource().getPlayerOrException(),
                                                StringArgumentType.getString(ctx, "type")
                                        ))))));
    }

    private static int scare(CommandContext<CommandSourceStack> ctx, ServerPlayer player) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerLevel level = player.serverLevel();
        BlockPos spawnPos = findSpawnPosition(level, player);
        if (spawnPos != null) {
            NullEntity nullEntity = NullEntity.createForPlayer(level, player, spawnPos);
            if (nullEntity != null) {
                level.addFreshEntity(nullEntity);
            }
        }

        SignPlacer.placeCreepySign(level, player, level.random);
        SignPlacer.placeCreepySign(level, player, level.random);
        NullHorrorEffects.triggerAllEffects(player);

        ctx.getSource().sendSuccess(() -> Component.literal("Triggered Null scare for " + player.getGameProfile().getName()), true);
        return 1;
    }

    private static int scareEffect(CommandContext<CommandSourceStack> ctx, ServerPlayer player, String typeName) {
        HorrorEffectPayload.HorrorEffectType type;
        try {
            type = HorrorEffectPayload.HorrorEffectType.valueOf(typeName.toUpperCase());
        } catch (IllegalArgumentException e) {
            ctx.getSource().sendFailure(Component.literal("Unknown effect. Use: GLITCH, DESKTOP, CREATE_FILE, WALLPAPER, FILE_EXPLORER, CHAT, SOUND, TASK_MANAGER, CMD_PROMPT, BSOD, NOTIFICATION, RUN_DIALOG, ERROR_DIALOG"));
            return 0;
        }

        NullHorrorEffects.triggerEffect(player, type);
        ctx.getSource().sendSuccess(() -> Component.literal("Triggered " + type + " for " + player.getGameProfile().getName()), true);
        return 1;
    }

    private static BlockPos findSpawnPosition(ServerLevel level, ServerPlayer player) {
        for (int attempt = 0; attempt < 8; attempt++) {
            int distance = 16 + level.random.nextInt(24);
            double angle = level.random.nextDouble() * Math.PI * 2.0;
            int x = player.getBlockX() + (int) (Math.cos(angle) * distance);
            int z = player.getBlockZ() + (int) (Math.sin(angle) * distance);
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            BlockPos pos = new BlockPos(x, y, z);
            if (!level.getBlockState(pos.below()).isAir() && level.getBlockState(pos).isAir()) {
                return pos;
            }
        }
        return player.blockPosition().offset(8, 0, 8);
    }
}
