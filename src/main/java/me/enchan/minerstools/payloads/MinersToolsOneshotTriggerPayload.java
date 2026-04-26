package me.enchan.minerstools.payloads;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record MinersToolsOneshotTriggerPayload() implements CustomPayload {
    public static final Id<MinersToolsOneshotTriggerPayload> ID = new Id<>(
            Identifier.of("miners-tools", "oneshot-trigger"));

    public static final MinersToolsOneshotTriggerPayload INSTANCE = new MinersToolsOneshotTriggerPayload();

    public static final PacketCodec<PacketByteBuf, MinersToolsOneshotTriggerPayload> CODEC = PacketCodec.unit(INSTANCE);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
