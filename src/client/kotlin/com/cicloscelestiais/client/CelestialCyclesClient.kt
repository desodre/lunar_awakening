package com.cicloscelestiais.client

import com.cicloscelestiais.CelestialEvent
import com.cicloscelestiais.EventStateBridge
import com.cicloscelestiais.EventSyncPayload
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking

class CelestialCyclesClient : ClientModInitializer {
    override fun onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(EventSyncPayload.TYPE) { payload, context ->
            context.client().execute {
                EventStateBridge.clientEvent = CelestialEvent.entries.getOrElse(payload.eventId().toInt()) {
                    CelestialEvent.NONE
                }
                context.client().level?.updateSkyBrightness()
            }
        }
        ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
            EventStateBridge.clientEvent = CelestialEvent.NONE
        }
    }
}
