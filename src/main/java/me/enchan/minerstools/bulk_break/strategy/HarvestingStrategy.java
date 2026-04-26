package me.enchan.minerstools.bulk_break.strategy;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class HarvestingStrategy implements BulkBreakStrategy {

    @Override
    public boolean matches(BlockState state, PlayerEntity player, ItemStack tool) {
        return isMature(state);
    }

    @Override
    public Set<BlockPos> collectTargets(ServerWorld world, BlockPos origin, BlockState originState) {
        var originBlock = originState.getBlock();

        var visited = new HashSet<BlockPos>();
        var queue = new ArrayDeque<BlockPos>();

        var harvestRadius = 18;

        visited.add(origin);
        queue.add(origin);

        while (!queue.isEmpty()) {
            var pos = queue.poll();

            BlockPos
                    .stream(pos.add(-1, 0, -1), pos.add(1, 0, 1))
                    .map(BlockPos::toImmutable)
                    .filter(p -> !visited.contains(p))
                    .filter(p -> p.isWithinDistance(origin, harvestRadius))
                    .filter(p -> {
                        var state = world.getBlockState(p);

                        if (!state.getBlock().equals(originBlock)) {
                            return false;
                        }

                        return isMature(state);
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
    public void harvest(ServerWorld world, BlockPos pos, PlayerEntity player) {
        var state = world.getBlockState(pos);

        BulkBreakStrategy.super.harvest(world, pos, player);

        var block = state.getBlock();

        // 作物の場合、種に対応するアイテムがあるなら植え直す
        var seedItem = block.getPickStack(world, pos, state).getItem();

        var hasSeed = player.getInventory().contains(stack -> stack.isOf(seedItem));
        if (!hasSeed) {
            return;
        }

        player.getInventory().remove(stack -> stack.isOf(seedItem), 1,
                player.playerScreenHandler.getCraftingInput());

        if (block instanceof CropBlock) {
            world.setBlockState(pos, state.with(CropBlock.AGE, 0));
        }

        if (block instanceof NetherWartBlock) {
            world.setBlockState(pos, state.with(NetherWartBlock.AGE, 0));
        }
    }

    private boolean isMature(BlockState state) {
        // TODO: #6 スイカ・カボチャには対応できていない (が、そこまで大規模にやるか……?)

        var block = state.getBlock();

        // 作物であり、収穫可能であること
        if (block instanceof CropBlock crop && crop.isMature(state)) {
            return true;
        }

        // ネザーウォートであり、age=3(最大)であること
        if (block instanceof NetherWartBlock) {
            var age = state.get(NetherWartBlock.AGE).intValue();
            return age == 3;
        }

        return false;
    }

}
