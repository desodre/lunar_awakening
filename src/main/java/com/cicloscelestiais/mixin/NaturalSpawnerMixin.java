package com.cicloscelestiais.mixin;

import com.cicloscelestiais.spawn.SpawnStateExt;

import com.cicloscelestiais.CelestialEvent;
import com.cicloscelestiais.CycleEventManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LocalMobCapCalculator;
import net.minecraft.world.level.NaturalSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NaturalSpawner.class)
public abstract class NaturalSpawnerMixin {
    @Inject(method = "createState", at = @At("RETURN"))
    private static void celestial$markEvent(
            int chunkCount,
            ServerLevel level,
            NaturalSpawner.ChunkGetter getter,
            LocalMobCapCalculator localCap,
            CallbackInfoReturnable<NaturalSpawner.SpawnState> cir
    ) {
        ((SpawnStateExt) cir.getReturnValue()).celestial$setActive(
                CycleEventManager.current(level) != CelestialEvent.NONE
        );
    }
}
