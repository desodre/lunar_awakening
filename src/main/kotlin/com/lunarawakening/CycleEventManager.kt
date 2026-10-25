package com.lunarawakening

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import java.util.SplittableRandom
import java.util.WeakHashMap

object CycleEventManager {
    private const val DAY_TICKS = 24_000L
    private const val NIGHT_START = 12_000L
    private const val ECLIPSE_DURATION = 2_400L
    private const val ECLIPSE_CHANCE = 0.05
    private const val BLOOD_MOON_CHANCE = 0.10

    private val lastAnnounced = WeakHashMap<ServerLevel, CelestialEvent>()
    private val forced = WeakHashMap<ServerLevel, CelestialEvent>()
    private val decisions = WeakHashMap<ServerLevel, Decision>()

    private data class Decision(
        val day: Long,
        val eclipseStart: Long?,
        val bloodMoon: Boolean
    )

    @JvmStatic
    fun current(level: ServerLevel): CelestialEvent {
        if (level.dimension() != Level.OVERWORLD) return CelestialEvent.NONE
        forced[level]?.let { return it }

        val clock = level.getOverworldClockTime()
        val day = Math.floorDiv(clock, DAY_TICKS)
        val phase = Math.floorMod(clock, DAY_TICKS)
        val decision = decisions[level]?.takeIf { it.day == day } ?: run {
            val eclipseRandom = SplittableRandom(level.seed xor (day * -7046029254386353131L) xor 0x15E1C5EL)
            val eclipseStart = if (eclipseRandom.nextDouble() < ECLIPSE_CHANCE) {
                eclipseRandom.nextLong(NIGHT_START - ECLIPSE_DURATION + 1)
            } else {
                null
            }
            val bloodMoonRandom = SplittableRandom(level.seed xor (day * -7046029254386353131L) xor 0xB100D00DL)
            Decision(day, eclipseStart, bloodMoonRandom.nextDouble() < BLOOD_MOON_CHANCE)
                .also { decisions[level] = it }
        }

        return if (phase < NIGHT_START) {
            if (decision.eclipseStart != null &&
                phase >= decision.eclipseStart && phase < decision.eclipseStart + ECLIPSE_DURATION
            ) CelestialEvent.SOLAR_ECLIPSE else CelestialEvent.NONE
        } else {
            if (decision.bloodMoon) CelestialEvent.BLOOD_MOON else CelestialEvent.NONE
        }
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
            CelestialEvent.SOLAR_ECLIPSE -> Component.translatable("message.lunar_awakening.eclipse_start")
                .withStyle(ChatFormatting.GOLD)
            CelestialEvent.BLOOD_MOON -> Component.translatable("message.lunar_awakening.blood_moon_start")
                .withStyle(ChatFormatting.DARK_RED)
            CelestialEvent.NONE -> Component.translatable("message.lunar_awakening.end")
                .withStyle(ChatFormatting.GRAY)
        }
        level.server.playerList.broadcastSystemMessage(message, false)
    }
}
