package me.enchan.minerstools.bulk_break.strategy;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

/** 伐採ストラテジ */
public class FellingStrategy implements BulkBreakStrategy {

    private enum FellingMode {
        TREE,
        MUSHROOM,
        WARP_TREE,
        CRIMSON_TREE
    }

    @Override
    public boolean matches(
            BlockState state,
            PlayerEntity player,
            ItemStack tool) {
        var mode = classifyFellingMode(state);
        return isNode(mode, state);
    }

    @Override
    public Set<BlockPos> collectTargets(
            ServerWorld world,
            BlockPos origin,
            BlockState originState) {
        var marked = new HashSet<BlockPos>();
        var chainQueue = new ArrayDeque<BlockPos>();

        var mode = classifyFellingMode(originState);

        marked.add(origin);
        chainQueue.add(origin);

        while (!chainQueue.isEmpty()) {
            var center = chainQueue.poll();

            var centerState = center.equals(origin) ? originState : world.getBlockState(center);

            var unmarkedSurroundings = BlockPos
                    .stream(center.add(-1, -1, -1), center.add(1, 1, 1))
                    .map(BlockPos::toImmutable)
                    .filter(p -> !marked.contains(p))
                    .filter(p -> {
                        // 起点からXZ方向の距離4ブロック以内
                        var flatOrigin = origin.withY(0);
                        var flatPos = p.withY(0);

                        return flatPos.isWithinDistance(flatOrigin, 4);
                    });

            unmarkedSurroundings.forEach(p -> {
                var pState = world.getBlockState(p);
                if (pState.isAir()) {
                    return;
                }

                // 起点と同じブロックなら継続
                if (pState.getBlock().equals(centerState.getBlock())) {
                    marked.add(p);
                    chainQueue.add(p);
                    return;
                }

                // 起点がノードで相手がリーフなら継続
                if (isNode(mode, centerState) && isLeaf(mode, pState)) {
                    marked.add(p);
                    chainQueue.add(p);
                    return;
                }
            });
        }

        // このメソッドが呼び出された時点で起点のブロックは破壊されているので、候補から除外
        marked.remove(origin);
        return marked;
    }

    private FellingMode classifyFellingMode(BlockState state) {
        // 歪んだ幹
        if (state.isIn(BlockTags.WARPED_STEMS)) {
            return FellingMode.WARP_TREE;
        }

        // 真紅の幹
        if (state.isIn(BlockTags.CRIMSON_STEMS)) {
            return FellingMode.CRIMSON_TREE;
        }

        // キノコ
        if (state.isOf(Blocks.MUSHROOM_STEM)) {
            return FellingMode.MUSHROOM;
        }

        return FellingMode.TREE;
    }

    private boolean isNode(FellingMode mode, BlockState state) {
        if (mode == FellingMode.TREE && state.isIn(BlockTags.LOGS)) {
            return true;
        }

        // 歪んだ幹
        if (mode == FellingMode.WARP_TREE && state.isIn(BlockTags.WARPED_STEMS)) {
            return true;
        }

        // 真紅の幹
        if (mode == FellingMode.CRIMSON_TREE && state.isIn(BlockTags.CRIMSON_STEMS)) {
            return true;
        }

        // キノコ
        if (mode == FellingMode.MUSHROOM && state.isOf(Blocks.MUSHROOM_STEM)) {
            return true;
        }

        return false;
    }

    private boolean isLeaf(FellingMode mode, BlockState state) {
        if (mode == FellingMode.TREE && state.isIn(BlockTags.LEAVES)) {
            return true;
        }

        // ネザーウォートブロック・シュルームライト
        if (mode == FellingMode.CRIMSON_TREE
                && (state.isOf(Blocks.NETHER_WART_BLOCK) || state.isOf(Blocks.SHROOMLIGHT))) {
            return true;
        }

        // キノコ
        if (mode == FellingMode.MUSHROOM
                && (state.isOf(Blocks.BROWN_MUSHROOM_BLOCK) || state.isOf(Blocks.RED_MUSHROOM_BLOCK))) {
            return true;
        }

        return false;
    }
}
