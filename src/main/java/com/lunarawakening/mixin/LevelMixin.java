package com.lunarawakening.mixin;

import com.lunarawakening.CelestialEvent;
import com.lunarawakening.CycleEventManager;
import com.lunarawakening.EventStateBridge;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Level.class)
public abstract class LevelMixin {
    @Shadow private int skyDarken;

    @Inject(method = "updateSkyBrightness", at = @At("TAIL"))
    private void celestial$darkenEclipse(CallbackInfo ci) {
        Level level = (Level) (Object) this;
        if (level.dimension() != Level.OVERWORLD) return;

        boolean eclipse = level instanceof ServerLevel serverLevel
                ? CycleEventManager.current(serverLevel) == CelestialEvent.SOLAR_ECLIPSE
                : EventStateBridge.clientEvent == CelestialEvent.SOLAR_ECLIPSE;
        if (eclipse) skyDarken = 11;
    }
}
