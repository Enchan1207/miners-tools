package me.enchan.minerstools.bulk_break.strategy;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class OneShotStrategy implements BulkBreakStrategy {

    @Override
    public boolean matches(BlockState state, PlayerEntity player, ItemStack tool) {
        // ちょっと危ないかも?
        return true;
    }

    @Override
    public Set<BlockPos> collectTargets(ServerWorld world, BlockPos origin, BlockState originState) {
        var visited = new HashSet<BlockPos>();
        var queue = new ArrayDeque<BlockPos>();

        var maxChainDistance = 6;

        visited.add(origin);
        queue.add(origin);

        while (!queue.isEmpty()) {
            var pos = queue.poll();

            BlockPos
                    .stream(pos.add(-1, -1, -1), pos.add(1, 1, 1))
                    .map(BlockPos::toImmutable)
                    .filter(p -> !visited.contains(p))
                    .filter(p -> p.isWithinDistance(origin, maxChainDistance))
                    .filter(p -> {
                        var state = world.getBlockState(p);
                        return state.getBlock().equals(originState.getBlock());
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

}
