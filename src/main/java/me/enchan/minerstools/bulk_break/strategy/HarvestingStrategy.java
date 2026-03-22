package me.enchan.minerstools.bulk_break.strategy;

import java.util.Set;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HarvestingStrategy implements BulkBreakStrategy {

    @Override
    public boolean matches(BlockState state, PlayerEntity player, ItemStack tool) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'matches'");
    }

    @Override
    public Set<BlockPos> collectTargets(World world, BlockPos origin, BlockState originState) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'collectTargets'");
    }

}
