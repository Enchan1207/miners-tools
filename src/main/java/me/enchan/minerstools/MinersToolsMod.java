package me.enchan.minerstools;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.enchan.minerstools.payloads.MinersToolsModeTogglePayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

public class MinersToolsMod implements ModInitializer {
    public static final String ModId = "miners-tools";

    public static final Logger Logger = LoggerFactory.getLogger("miners-tools");

    private static final Map<UUID, Boolean> toolStatusByPlayer = new HashMap<>();

    @Override
    public void onInitialize() {
        Logger.info("miners-tools");

        PayloadTypeRegistry.playC2S().register(
                MinersToolsModeTogglePayload.ID,
                MinersToolsModeTogglePayload.CODEC);

        PlayerBlockBreakEvents.AFTER.register((world, player, origin, state, blockEntity) -> {
            Logger.info("broken: %s".formatted(state.getBlock().toString()));

            // 有効でなければ何もしない
            if (!toolStatusByPlayer.getOrDefault(player.getUuid(), false)) {
                return;
            }

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

        ServerPlayNetworking.registerGlobalReceiver(MinersToolsModeTogglePayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();

            var currentMode = toolStatusByPlayer.getOrDefault(player.getUuid(), false);
            var isEnabled = !currentMode;
            toolStatusByPlayer.put(player.getUuid(), isEnabled);

            Text message = Text.empty()
                    .append(Text.literal("Cut All: "))
                    .append(isEnabled
                            ? Text.literal("ON").formatted(Formatting.GREEN)
                            : Text.literal("OFF").formatted(Formatting.RED));
            player.sendMessage(message, true);
        });
    }
}
