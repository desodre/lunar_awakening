package com.lunarawakening

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.commands.Commands
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.server.MinecraftServer
import net.minecraft.world.clock.ClockTimeMarkers
import net.minecraft.world.clock.WorldClocks

object LunarCommands {
    fun register() {
        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            dispatcher.register(
                Commands.literal("lunar_awakening")
                    .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                    .then(Commands.literal("eclipse").executes { context ->
                        set(context.source.server, CelestialEvent.SOLAR_ECLIPSE)
                        context.source.sendSuccess({ Component.translatable("command.lunar_awakening.eclipse") }, false)
                        1
                    })
                    .then(Commands.literal("bloodmoon").executes { context ->
                        set(context.source.server, CelestialEvent.BLOOD_MOON)
                        context.source.sendSuccess({ Component.translatable("command.lunar_awakening.blood_moon") }, false)
                        1
                    })
                    .then(Commands.literal("off").executes { context ->
                        set(context.source.server, CelestialEvent.NONE)
                        context.source.sendSuccess({ Component.translatable("command.lunar_awakening.off") }, false)
                        1
                    })
                    .then(Commands.literal("auto").executes { context ->
                        CycleEventManager.force(context.source.server.overworld(), null)
                        context.source.sendSuccess({ Component.translatable("command.lunar_awakening.auto") }, false)
                        1
                    })
                    .then(Commands.literal("status").executes { context ->
                        val event = CycleEventManager.current(context.source.server.overworld())
                        context.source.sendSuccess({
                            Component.translatable(
                                "command.lunar_awakening.status",
                                Component.translatable("event.lunar_awakening.${event.name.lowercase()}")
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
