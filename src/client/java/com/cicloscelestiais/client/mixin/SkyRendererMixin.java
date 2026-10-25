package com.cicloscelestiais.client.mixin;

import com.cicloscelestiais.CelestialEvent;
import com.cicloscelestiais.EventStateBridge;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.state.level.SkyRenderState;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkyRenderer.class)
public abstract class SkyRendererMixin {
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void celestial$colorSky(ClientLevel level, float partialTick, Camera camera,
                                     SkyRenderState state, CallbackInfo ci) {
        if (level.dimension() != Level.OVERWORLD) return;
        if (EventStateBridge.clientEvent == CelestialEvent.BLOOD_MOON) {
            state.skyColor = new Vector3f(0.45f, 0.05f, 0.05f);
        } else if (EventStateBridge.clientEvent == CelestialEvent.SOLAR_ECLIPSE) {
            state.skyColor = new Vector3f(0.05f, 0.05f, 0.10f);
        }
    }
}
