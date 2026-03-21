package me.enchan.minerstools.payloads;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record MinersToolsModeTogglePayload() implements CustomPayload {
    public static final Id<MinersToolsModeTogglePayload> ID = new Id<>(Identifier.of("miners-tools", "toggle"));

    public static final MinersToolsModeTogglePayload INSTANCE = new MinersToolsModeTogglePayload();

    public static final PacketCodec<PacketByteBuf, MinersToolsModeTogglePayload> CODEC = PacketCodec.unit(INSTANCE);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
