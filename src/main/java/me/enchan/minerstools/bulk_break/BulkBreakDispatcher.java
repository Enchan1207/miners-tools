package me.enchan.minerstools.bulk_break;

import java.util.List;

import me.enchan.minerstools.bulk_break.strategy.BulkBreakStrategy;
import me.enchan.minerstools.bulk_break.strategy.FellingStrategy;
import me.enchan.minerstools.bulk_break.strategy.HarvestingStrategy;
import me.enchan.minerstools.bulk_break.strategy.MiningStrategy;
import me.enchan.minerstools.bulk_break.strategy.OneShotStrategy;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class BulkBreakDispatcher {
    private static final List<BulkBreakStrategy> DefaultStrategies = List.of(
            new FellingStrategy(),
            new MiningStrategy(),
            new HarvestingStrategy());

    /** ワンショットモード (一度だけ大規模破壊) フラグ */
    private boolean isOneshotModeEnabled;

    public BulkBreakDispatcher() {
        this.isOneshotModeEnabled = false;
    }

    public void dispatchStrategy(
            ServerWorld world,
            BlockPos origin,
            PlayerEntity player,
            BlockState originState) {
        var tool = player.getMainHandStack();

        var strategies = DefaultStrategies;
        if (isOneshotModeEnabled) {
            strategies = List.of(new OneShotStrategy());
        }

        strategies.stream()
                .filter(s -> s.matches(originState, player, tool))
                .findFirst()
                .ifPresent(s -> {
                    var targets = s.collectTargets(world, origin, originState);

                    for (BlockPos pos : targets) {
                        s.harvest(world, pos, player);
                    }
                });

        isOneshotModeEnabled = false;
    }

    public void enableOneshotMode() {
        this.isOneshotModeEnabled = true;
    }

    public void disableOneshotMode() {
        this.isOneshotModeEnabled = false;
    }
}
