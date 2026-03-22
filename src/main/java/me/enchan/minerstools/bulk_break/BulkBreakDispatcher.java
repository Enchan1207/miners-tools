package me.enchan.minerstools.bulk_break;

import java.util.List;

import me.enchan.minerstools.bulk_break.strategy.BulkBreakStrategy;
import me.enchan.minerstools.bulk_break.strategy.FellingStrategy;
import me.enchan.minerstools.bulk_break.strategy.MiningStrategy;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BulkBreakDispatcher {
    private static final List<BulkBreakStrategy> Strategies = List.of(
            new FellingStrategy(),
            new MiningStrategy());

    public static void onBreakBlock(
            World world,
            BlockPos origin,
            PlayerEntity player,
            BlockState originState) {
        var tool = player.getMainHandStack();

        Strategies.stream()
                .filter(s -> s.matches(originState, player, tool))
                .findFirst()
                .ifPresent(s -> {
                    var targets = s.collectTargets(world, origin, originState);

                    for (BlockPos pos : targets) {
                        s.harvest(world, pos, player);
                    }
                });
    }
}
