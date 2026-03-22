package me.enchan.minerstools.bulk_break.strategy;

import java.util.Set;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** 範囲破壊ストラテジ */
public interface BulkBreakStrategy {

    /** このブロックがこの戦略の対象かを判定する */
    boolean matches(BlockState state, PlayerEntity player, ItemStack tool);

    /** 起点ブロックから破壊すべき対象座標を収集する */
    Set<BlockPos> collectTargets(World world, BlockPos origin, BlockState originState);

    /** ブロックを破壊する */
    default void harvest(World world, BlockPos pos, PlayerEntity player) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.interactionManager.tryBreakBlock(pos);
        } else {
            // フォールバック
            world.breakBlock(pos, true, player);
        }
    }
}
