package me.enchan.minerstools;

import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.util.math.BlockPos;

public class MinersToolsMod implements ModInitializer {
    public static final String ModId = "miners-tools";

    public static final Logger Logger = LoggerFactory.getLogger("miners-tools");

    @Override
    public void onInitialize() {
        Logger.info("miners-tools");

        PlayerBlockBreakEvents.AFTER.register((world, player, origin, state, blockEntity) -> {
            Logger.info("broken: %s".formatted(state.getBlock().toString()));

            // 破壊時にメインハンドに持っているアイテムを取得しておく (耐久値考慮などへの利用を想定)
            var tool = player.getMainHandStack().getItem();
            MinersToolsMod.Logger.info(tool.toString());

            var queue = new LinkedList<BlockPos>();
            queue.add(origin);

            while (!queue.isEmpty()) {
                var pos = queue.poll();

                var candidates = BlockPos
                        // 三軸方向にクエリして原点だけ除外 原点からの距離4ブロックまで
                        .stream(pos.add(-1, -1, -1), pos.add(1, 1, 1))
                        .filter(p -> !p.equals(pos))
                        .filter(p -> p.isWithinDistance(origin, 4))
                        // streamで取得されるBlockPosは参照なので、ここで変換
                        .map(p -> p.toImmutable())
                        .filter(p -> world.getBlockState(p).getBlock().equals(state.getBlock()))
                        .toList();

                for (var candidate : candidates) {
                    world.breakBlock(candidate, true, player);
                }

                queue.addAll(candidates);
            }
        });
    }
}
