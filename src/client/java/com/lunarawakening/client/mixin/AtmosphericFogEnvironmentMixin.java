package com.lunarawakening.client.mixin;

import com.lunarawakening.CelestialEvent;
import com.lunarawakening.EventStateBridge;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.environment.AtmosphericFogEnvironment;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AtmosphericFogEnvironment.class)
public abstract class AtmosphericFogEnvironmentMixin {
    @Inject(method = "getBaseColor", at = @At("RETURN"), cancellable = true)
    private void celestial$darkenEclipseHorizon(ClientLevel level, Camera camera,
                                                int renderDistance, float partialTick,
                                                CallbackInfoReturnable<Vector3fc> cir) {
        if (level.dimension() != Level.OVERWORLD) return;
        if (EventStateBridge.clientEvent == CelestialEvent.SOLAR_ECLIPSE) {
            cir.setReturnValue(new Vector3f(0.025f, 0.025f, 0.05f));
        } else if (EventStateBridge.clientEvent == CelestialEvent.BLOOD_MOON) {
            cir.setReturnValue(new Vector3f(0.22f, 0.025f, 0.03f));
        }
    }
}
