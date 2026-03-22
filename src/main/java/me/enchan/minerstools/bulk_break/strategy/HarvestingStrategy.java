package me.enchan.minerstools.bulk_break.strategy;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HarvestingStrategy implements BulkBreakStrategy {

    @Override
    public boolean matches(BlockState state, PlayerEntity player, ItemStack tool) {
        // TODO: #6 スイカ・カボチャには対応できていない (が、そこまで大規模にやるか……?)

        // 作物かつ収穫可能であること
        if (state.getBlock() instanceof CropBlock crop && crop.isMature(state)) {
            return true;
        }

        return false;
    }

    @Override
    public Set<BlockPos> collectTargets(World world, BlockPos origin, BlockState originState) {
        var originBlock = originState.getBlock();

        var visited = new HashSet<BlockPos>();
        var queue = new ArrayDeque<BlockPos>();

        visited.add(origin);
        queue.add(origin);

        while (!queue.isEmpty()) {
            var pos = queue.poll();

            BlockPos
                    .stream(pos.add(-1, 0, -1), pos.add(1, 0, 1))
                    .map(BlockPos::toImmutable)
                    .filter(p -> !visited.contains(p))
                    .filter(p -> p.isWithinDistance(origin, 6))
                    .filter(p -> {
                        var state = world.getBlockState(p);
                        var block = state.getBlock();

                        if (!block.equals(originBlock)) {
                            return false;
                        }

                        if (!(block instanceof CropBlock)) {
                            return false;
                        }

                        return block instanceof CropBlock crop && crop.isMature(state);
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

    @Override
    public void harvest(World world, BlockPos pos, PlayerEntity player) {
        // TODO: #6 アイテムスタックを見て自動で植え直したい
        BulkBreakStrategy.super.harvest(world, pos, player);
    }

}
