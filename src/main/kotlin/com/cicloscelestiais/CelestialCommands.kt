package com.cicloscelestiais

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component

object CelestialCommands {
    fun register() {
        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            dispatcher.register(
                Commands.literal("celestialcycles")
                    .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                    .then(Commands.literal("eclipse").executes { context ->
                        set(context.source.level, CelestialEvent.SOLAR_ECLIPSE)
                        context.source.sendSuccess({ Component.translatable("command.ciclos_celestiais.eclipse") }, false)
                        1
                    })
                    .then(Commands.literal("bloodmoon").executes { context ->
                        set(context.source.level, CelestialEvent.BLOOD_MOON)
                        context.source.sendSuccess({ Component.translatable("command.ciclos_celestiais.blood_moon") }, false)
                        1
                    })
                    .then(Commands.literal("off").executes { context ->
                        set(context.source.level, CelestialEvent.NONE)
                        context.source.sendSuccess({ Component.translatable("command.ciclos_celestiais.off") }, false)
                        1
                    })
                    .then(Commands.literal("auto").executes { context ->
                        CycleEventManager.force(context.source.level, null)
                        context.source.sendSuccess({ Component.translatable("command.ciclos_celestiais.auto") }, false)
                        1
                    })
                    .then(Commands.literal("status").executes { context ->
                        val event = CycleEventManager.current(context.source.level)
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

    private fun set(level: net.minecraft.server.level.ServerLevel, event: CelestialEvent) {
        CycleEventManager.force(level, event)
    }
}
