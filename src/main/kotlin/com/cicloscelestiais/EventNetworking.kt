package com.cicloscelestiais

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer

object EventNetworking {
    fun sync(player: ServerPlayer) {
        val event = CycleEventManager.current(player.level())
        if (ServerPlayNetworking.canSend(player, EventSyncPayload.TYPE)) {
            ServerPlayNetworking.send(player, EventSyncPayload(event.ordinal.toByte()))
        }
    }

    fun syncLevel(level: ServerLevel, event: CelestialEvent) {
        for (player in level.players()) {
            if (ServerPlayNetworking.canSend(player, EventSyncPayload.TYPE)) {
                ServerPlayNetworking.send(player, EventSyncPayload(event.ordinal.toByte()))
            }
        }
    }
}
