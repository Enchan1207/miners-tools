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
            var center = queue.poll();

            BlockPos
                    .stream(center.add(-1, -1, -1), center.add(1, 1, 1))
                    .map(BlockPos::toImmutable)
                    .filter(p -> !visited.contains(p))
                    .filter(p -> {
                        // 起点からXZ方向の距離4ブロック以内
                        var flatOrigin = origin.withY(0);
                        var flatPos = p.withY(0);

                        return flatPos.isWithinDistance(flatOrigin, 4);
                    })
                    .forEach(p -> {
                        var pState = world.getBlockState(p);
                        var centerState = world.getBlockState(center);

                        // 起点と同じブロックなら継続
                        if (pState.getBlock().equals(originState.getBlock())) {
                            visited.add(p);
                            queue.add(p);
                            return;
                        }

                        // 中央が原木で相手が葉なら継続
                        if (centerState.isIn(BlockTags.LOGS) && pState.isIn(BlockTags.LEAVES)) {
                            visited.add(p);
                            queue.add(p);
                            return;
                        }

                        // 中央が葉の場合、
                        if (centerState.isIn(BlockTags.LEAVES)) {
                            // 相手が葉なら継続
                            if (pState.isIn(BlockTags.LEAVES)) {
                                visited.add(p);
                                queue.add(p);
                                return;
                            }

                            // 相手が原木なら連鎖を中止
                            if (pState.isIn(BlockTags.LOGS)) {
                                visited.add(p);
                                return;
                            }
                        }
                    });
        }

        // このメソッドが呼び出された時点で起点のブロックは破壊されているので、候補から除外
        visited.remove(origin);
        return visited;
    }
}
