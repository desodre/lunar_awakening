package com.cicloscelestiais

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import java.util.SplittableRandom
import java.util.WeakHashMap

object CycleEventManager {
    private const val DAY_TICKS = 24_000L
    private const val NIGHT_START = 12_000L
    private const val ECLIPSE_CHANCE = 0.05
    private const val BLOOD_MOON_CHANCE = 0.10

    private val lastAnnounced = WeakHashMap<ServerLevel, CelestialEvent>()
    private val forced = WeakHashMap<ServerLevel, CelestialEvent>()
    private val decisions = WeakHashMap<ServerLevel, Decision>()

    private data class Decision(
        val day: Long,
        val daytime: Boolean,
        val event: CelestialEvent
    )

    @JvmStatic
    fun current(level: ServerLevel): CelestialEvent {
        if (level.dimension() != Level.OVERWORLD) return CelestialEvent.NONE
        forced[level]?.let { return it }

        val clock = level.getOverworldClockTime()
        val day = Math.floorDiv(clock, DAY_TICKS)
        val phase = Math.floorMod(clock, DAY_TICKS)
        val daytime = phase < NIGHT_START
        decisions[level]?.let { cached ->
            if (cached.day == day && cached.daytime == daytime) return cached.event
        }

        val chance = if (daytime) ECLIPSE_CHANCE else BLOOD_MOON_CHANCE
        val salt = if (daytime) 0x15E1C5EL else 0xB100D00DL
        val random = SplittableRandom(level.seed xor (day * -7046029254386353131L) xor salt)
        val event = if (random.nextDouble() < chance) {
            if (daytime) CelestialEvent.SOLAR_ECLIPSE else CelestialEvent.BLOOD_MOON
        } else {
            CelestialEvent.NONE
        }
        decisions[level] = Decision(day, daytime, event)
        return event
    }

    fun force(level: ServerLevel, event: CelestialEvent?) {
        if (event == null) forced.remove(level) else forced[level] = event
        tick(level)
    }

    fun tick(level: ServerLevel) {
        if (level.dimension() != Level.OVERWORLD) return
        val event = current(level)
        val previous = lastAnnounced.put(level, event)
        if (event == previous) return
        EventNetworking.syncLevel(level, event)
        if (previous == null) return

        val message = when (event) {
            CelestialEvent.SOLAR_ECLIPSE -> Component.translatable("message.ciclos_celestiais.eclipse_start")
                .withStyle(ChatFormatting.GOLD)
            CelestialEvent.BLOOD_MOON -> Component.translatable("message.ciclos_celestiais.blood_moon_start")
                .withStyle(ChatFormatting.DARK_RED)
            CelestialEvent.NONE -> Component.translatable("message.ciclos_celestiais.end")
                .withStyle(ChatFormatting.GRAY)
        }
        level.server.playerList.broadcastSystemMessage(message, false)
    }
}
