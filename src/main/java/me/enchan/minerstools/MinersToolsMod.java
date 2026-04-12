package me.enchan.minerstools;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.enchan.minerstools.auto_tool.AutoToolSwitcher;
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

            AutoToolSwitcher.onBreakMainhandTool(player);
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
