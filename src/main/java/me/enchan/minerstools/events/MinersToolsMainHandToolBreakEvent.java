package me.enchan.minerstools.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

/** プレイヤーのメインハンドに持っている道具が壊れた */
public interface MinersToolsMainHandToolBreakEvent {

    Event<MinersToolsMainHandToolBreakEvent> EVENT = EventFactory.createArrayBacked(
            MinersToolsMainHandToolBreakEvent.class,
            (listeners) -> (player) -> {
                for (MinersToolsMainHandToolBreakEvent l : listeners) {
                    l.onBreak(player);
                }
            });

    void onBreak(ServerPlayerEntity player);

}
