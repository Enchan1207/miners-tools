package me.enchan.minerstools.bulk_break.strategy;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;

/** 範囲破壊ストラテジ */
public interface BulkBreakStrategy {

    /** このブロックがこの戦略の対象かを判定する */
    boolean matches(BlockState state, PlayerEntity player, ItemStack tool);

    /** 起点ブロックから破壊すべき対象座標を収集する */
    Set<BlockPos> collectTargets(ServerWorld world, BlockPos origin, BlockState originState);

    /** ブロックを破壊する */
    default void harvest(ServerWorld world, BlockPos pos, PlayerEntity player) {
        var tool = player.getMainHandStack();
        var state = world.getBlockState(pos);

        // アイテムがドロップできる場合
        var shouldDropItem = !player.isCreative() && (!state.isToolRequired() || tool.isSuitableFor(state));
        if (shouldDropItem) {
            var blockEntity = world.getBlockEntity(pos);
            Block
                    .getDroppedStacks(state, world, pos, blockEntity, player, tool)
                    .forEach(stack -> Block.dropStack(world, pos, stack));

            // シルクタッチなら経験値をドロップしない
            var isSilkTouch = EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, tool) > 0;
            state.onStacksDropped(world, pos, tool, !isSilkTouch);
        }

        tool.postMine(world, state, pos, player);

        player.incrementStat(Stats.MINED.getOrCreateStat(state.getBlock()));
        world.removeBlock(pos, false);

    }
}
