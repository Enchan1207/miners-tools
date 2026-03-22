package me.enchan.minerstools.bulk_break.strategy;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class MiningStrategy implements BulkBreakStrategy {

    @Override
    public boolean matches(BlockState state, PlayerEntity player, ItemStack tool) {
        List<TagKey<Block>> oreTags = List.of(
                BlockTags.COAL_ORES,
                BlockTags.GOLD_ORES,
                BlockTags.IRON_ORES,
                BlockTags.LAPIS_ORES,
                BlockTags.COPPER_ORES,
                BlockTags.DIAMOND_ORES,
                BlockTags.EMERALD_ORES,
                BlockTags.REDSTONE_ORES);
        if (oreTags.stream().anyMatch(t -> state.isIn(t))) {
            return true;
        }

        List<Block> oreBlocks = List.of(
                Blocks.NETHER_QUARTZ_ORE,
                Blocks.GLOWSTONE,
                Blocks.OBSIDIAN);
        if (oreBlocks.stream().anyMatch(b -> state.isOf(b))) {
            return true;
        }

        return false;
    }

    @Override
    public Set<BlockPos> collectTargets(ServerWorld world, BlockPos origin, BlockState originState) {
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
                    .filter(p -> p.isWithinDistance(origin, 6))
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
