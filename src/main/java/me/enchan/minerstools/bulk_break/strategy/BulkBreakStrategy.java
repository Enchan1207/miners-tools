package me.enchan.minerstools.bulk_break.strategy;

import java.util.Set;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** 範囲破壊ストラテジ */
public interface BulkBreakStrategy {

    /** このブロックがこの戦略の対象かを判定する */
    boolean matches(BlockState state, PlayerEntity player, ItemStack tool);

    /** 起点ブロックから破壊すべき対象座標を収集する */
    Set<BlockPos> collectTargets(World world, BlockPos origin, BlockState originState);

    /** 1ブロック分の処理（農耕など再植えが必要な場合にオーバーライド） */
    default void harvest(World world, BlockPos pos, PlayerEntity player) {
        world.breakBlock(pos, true, player);
    }
}
