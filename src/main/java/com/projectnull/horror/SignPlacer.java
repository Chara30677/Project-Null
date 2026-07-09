package com.projectnull.horror;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

public final class SignPlacer {
    private SignPlacer() {
    }

    public static void placeCreepySign(ServerLevel level, Player player, RandomSource random) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        BlockPos signPos = findSignPosition(level, serverPlayer, random);
        if (signPos == null) {
            return;
        }

        Direction facing = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        int rotation = directionToRotation(facing);
        BlockState signState = Blocks.OAK_SIGN.defaultBlockState()
                .setValue(StandingSignBlock.ROTATION, rotation);

        if (!level.setBlock(signPos, signState, 3)) {
            return;
        }

        if (!(level.getBlockEntity(signPos) instanceof SignBlockEntity sign)) {
            return;
        }

        PlayerDossier dossier = DossierResolver.resolve(serverPlayer);
        String[] lines = CreepyMessages.createSignLines(serverPlayer.getGameProfile().getName(), dossier, random);
        SignText text = new SignText(
                toComponents(lines),
                toComponents(lines),
                net.minecraft.world.item.DyeColor.BLACK,
                true
        );
        sign.setText(text, true);
        sign.setChanged();
        level.sendBlockUpdated(signPos, signState, signState, 3);
    }

    private static int directionToRotation(Direction direction) {
        return switch (direction) {
            case NORTH -> 0;
            case SOUTH -> 8;
            case WEST -> 4;
            case EAST -> 12;
            default -> 0;
        };
    }

    private static Component[] toComponents(String[] lines) {
        Component[] components = new Component[4];
        for (int i = 0; i < 4; i++) {
            components[i] = Component.literal(lines[i]);
        }
        return components;
    }

    private static BlockPos findSignPosition(ServerLevel level, Player player, RandomSource random) {
        for (int attempt = 0; attempt < 12; attempt++) {
            int distance = 8 + random.nextInt(14);
            double angle = random.nextDouble() * Math.PI * 2.0;
            int x = player.getBlockX() + (int) (Math.cos(angle) * distance);
            int z = player.getBlockZ() + (int) (Math.sin(angle) * distance);
            int y = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);

            BlockPos ground = new BlockPos(x, y, z);
            BlockPos signPos = ground.above();

            if (!level.getBlockState(ground).isAir()
                    && level.getBlockState(signPos).isAir()
                    && level.getBlockState(signPos.above()).isAir()) {
                return signPos;
            }
        }
        return null;
    }
}
