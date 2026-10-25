package com.cicloscelestiais;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record EventSyncPayload(byte eventId) implements CustomPacketPayload {
    public static final Type<EventSyncPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath("ciclos_celestiais", "event_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EventSyncPayload> CODEC =
            StreamCodec.of(
                    (buffer, payload) -> buffer.writeByte(payload.eventId()),
                    buffer -> new EventSyncPayload(buffer.readByte())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
