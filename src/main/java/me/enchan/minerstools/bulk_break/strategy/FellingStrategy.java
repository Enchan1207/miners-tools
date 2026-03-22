package me.enchan.minerstools.bulk_break.strategy;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** 伐採ストラテジ */
public class FellingStrategy implements BulkBreakStrategy {
    @Override
    public boolean matches(
            BlockState state,
            PlayerEntity player,
            ItemStack tool) {
        // 葉から破壊することはない想定
        return state.isIn(BlockTags.LOGS);
    }

    @Override
    public Set<BlockPos> collectTargets(
            World world,
            BlockPos origin,
            BlockState originState) {
        var visited = new HashSet<BlockPos>();
        var queue = new ArrayDeque<BlockPos>();

        visited.add(origin);
        queue.add(origin);

        while (!queue.isEmpty()) {
            var pos = queue.poll();

            BlockPos
                    .stream(pos.add(-1, -1, -1), pos.add(1, 1, 1))
                    .map(BlockPos::toImmutable)
                    .filter(p -> !visited.contains(p))
                    .filter(p -> isInRange(origin, p))
                    .filter(p -> {
                        // FIXME #6: なんかここおかしい, 木 -> 葉 -> 木 のチェインが成立してしまっている
                        var state = world.getBlockState(p);
                        var posState = world.getBlockState(pos);

                        // 同じブロックか、木 -> 葉 のチェインは継続する。葉 -> 木のチェインは繋がない

                        if (state.getBlock().equals(originState.getBlock())) {
                            return true;
                        }

                        if (state.isIn(BlockTags.LEAVES) && posState.isIn(BlockTags.LOGS)) {
                            return true;
                        }

                        return false;
                    })
                    .forEach(p -> {
                        visited.add(p);
                        queue.add(p);
                    });
        }

        // このメソッドが呼び出された時点で起点のブロックは破壊されているので、候補から除外
        visited.remove(origin);
        return visited;
    }

    private Boolean isInRange(BlockPos origin, BlockPos pos) {
        var xzLimit = 4;
        var yLimit = 20;

        var dx = Math.abs(pos.getX() - origin.getX());
        var dz = Math.abs(pos.getZ() - origin.getZ());
        var dy = Math.abs(pos.getY() - origin.getY());

        return dx <= xzLimit && dz <= xzLimit && dy <= yLimit;
    }
}
