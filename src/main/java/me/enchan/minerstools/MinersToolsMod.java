package me.enchan.minerstools;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.enchan.minerstools.bulk_break.BulkBreakDispatcher;
import me.enchan.minerstools.events.MinersToolsMainHandToolBreakEvent;
import me.enchan.minerstools.payloads.MinersToolsModeTogglePayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

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
            if (!toolStatusByPlayer.getOrDefault(player.getUuid(), false)) {
                return;
            }

            BulkBreakDispatcher.onBreakBlock((ServerWorld) world, origin, player, state);
        });

        MinersToolsMainHandToolBreakEvent.EVENT.register(player -> {
            if (!toolStatusByPlayer.getOrDefault(player.getUuid(), false)) {
                return;
            }

            // 壊れたアイテムを取得
            var brokenItemStack = player.getMainHandStack();

            var inventory = player.getInventory().main;
            var candidateIndex = -1;
            for (int i = 0; i < inventory.size(); i++) {
                var itemStack = inventory.get(i);
                if (itemStack.equals(brokenItemStack)) {
                    continue;
                }

                if (!itemStack.getItem().equals(brokenItemStack.getItem())) {
                    continue;
                }

                // TODO: より耐久の残っているものから利用していくなどの最適化もできるかも
                candidateIndex = i;
            }

            if (candidateIndex < 0) {
                player.sendMessage(Text.literal("No alternative tools!"), true);
                return;
            }
            var alternativeItem = inventory.get(candidateIndex);

            // 持ち替え
            var currentSlot = player.getInventory().selectedSlot;
            inventory.set(candidateIndex, inventory.get(currentSlot));
            inventory.set(currentSlot, alternativeItem);

            player.currentScreenHandler.sendContentUpdates();

            player.sendMessage(Text.literal("Switched to new one!"), true);
        });

        ServerPlayNetworking.registerGlobalReceiver(MinersToolsModeTogglePayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();

            var currentMode = toolStatusByPlayer.getOrDefault(player.getUuid(), false);
            var isEnabled = !currentMode;
            toolStatusByPlayer.put(player.getUuid(), isEnabled);

            Text message = Text.empty()
                    .append(Text.literal("Miners tools: "))
                    .append(isEnabled
                            ? Text.literal("ON").formatted(Formatting.GREEN)
                            : Text.literal("OFF").formatted(Formatting.RED));
            player.sendMessage(message, true);
        });

    }
}
