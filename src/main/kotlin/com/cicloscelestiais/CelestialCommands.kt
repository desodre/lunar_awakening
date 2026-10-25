package com.cicloscelestiais

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.commands.Commands
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.server.MinecraftServer
import net.minecraft.world.clock.ClockTimeMarkers
import net.minecraft.world.clock.WorldClocks

object CelestialCommands {
    fun register() {
        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            dispatcher.register(
                Commands.literal("celestialcycles")
                    .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                    .then(Commands.literal("eclipse").executes { context ->
                        set(context.source.server, CelestialEvent.SOLAR_ECLIPSE)
                        context.source.sendSuccess({ Component.translatable("command.ciclos_celestiais.eclipse") }, false)
                        1
                    })
                    .then(Commands.literal("bloodmoon").executes { context ->
                        set(context.source.server, CelestialEvent.BLOOD_MOON)
                        context.source.sendSuccess({ Component.translatable("command.ciclos_celestiais.blood_moon") }, false)
                        1
                    })
                    .then(Commands.literal("off").executes { context ->
                        set(context.source.server, CelestialEvent.NONE)
                        context.source.sendSuccess({ Component.translatable("command.ciclos_celestiais.off") }, false)
                        1
                    })
                    .then(Commands.literal("auto").executes { context ->
                        CycleEventManager.force(context.source.server.overworld(), null)
                        context.source.sendSuccess({ Component.translatable("command.ciclos_celestiais.auto") }, false)
                        1
                    })
                    .then(Commands.literal("status").executes { context ->
                        val event = CycleEventManager.current(context.source.server.overworld())
                        context.source.sendSuccess({
                            Component.translatable(
                                "command.ciclos_celestiais.status",
                                Component.translatable("event.ciclos_celestiais.${event.name.lowercase()}")
                            )
                        }, false)
                        1
                    })
            )
        }
    }

    private fun set(server: MinecraftServer, event: CelestialEvent) {
        val timeMarker = when (event) {
            CelestialEvent.SOLAR_ECLIPSE -> ClockTimeMarkers.DAY
            CelestialEvent.BLOOD_MOON -> ClockTimeMarkers.NIGHT
            CelestialEvent.NONE -> null
        }
        if (timeMarker != null) {
            val overworldClock = server.registryAccess()
                .lookupOrThrow(Registries.WORLD_CLOCK)
                .getOrThrow(WorldClocks.OVERWORLD)
            server.clockManager().moveToTimeMarker(overworldClock, timeMarker)
        }
        CycleEventManager.force(server.overworld(), event)
    }
}
