package com.cicloscelestiais

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player

class CelestialCycles : ModInitializer {
    override fun onInitialize() {
        PayloadTypeRegistry.clientboundPlay().register(EventSyncPayload.TYPE, EventSyncPayload.CODEC)
        ServerTickEvents.END_LEVEL_TICK.register(CycleEventManager::tick)
        ServerPlayConnectionEvents.JOIN.register { handler, _, _ ->
            EventNetworking.sync(handler.player)
        }
        ServerEntityLevelChangeEvents.AFTER_PLAYER_CHANGE_LEVEL.register { player, _, _ ->
            EventNetworking.sync(player)
        }
        CelestialCommands.register()

        EntitySleepEvents.ALLOW_SLEEPING.register { player, _ ->
            val level = player.level() as? ServerLevel
            if (level != null && CycleEventManager.current(level) == CelestialEvent.BLOOD_MOON) {
                Player.BedSleepingProblem(
                    Component.translatable("message.ciclos_celestiais.no_sleep")
                )
            } else {
                null
            }
        }
    }
}
