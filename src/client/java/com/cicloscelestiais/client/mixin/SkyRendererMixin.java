package com.cicloscelestiais.client.mixin;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.cicloscelestiais.CelestialEvent;
import com.cicloscelestiais.EventStateBridge;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.state.level.SkyRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkyRenderer.class)
public abstract class SkyRendererMixin {
    @Shadow @Final private TextureAtlas celestialsAtlas;

    @Shadow
    private static GpuBuffer buildCelestialQuad(String name, TextureAtlasSprite sprite) {
        throw new AssertionError();
    }

    @Unique private GpuBuffer celestial$eclipseSunBuffer;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void celestial$buildEclipseSun(TextureManager textures, AtlasManager atlases,
                                           RenderTarget target, CallbackInfo ci) {
        TextureAtlasSprite sprite = celestialsAtlas.getSprite(
            Identifier.fromNamespaceAndPath("ciclos_celestiais", "eclipse_sun")
        );
        celestial$eclipseSunBuffer = buildCelestialQuad("Eclipsed sun quad", sprite);
    }

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void celestial$colorSky(ClientLevel level, float partialTick, Camera camera,
                                     SkyRenderState state, CallbackInfo ci) {
        if (level.dimension() != Level.OVERWORLD) return;
        if (EventStateBridge.clientEvent == CelestialEvent.BLOOD_MOON) {
            state.skyColor = new Vector3f(0.34f, 0.035f, 0.04f);
            state.sunriseAndSunsetColor = new Vector4f(0.0f, 0.0f, 0.0f, 0.0f);
        } else if (EventStateBridge.clientEvent == CelestialEvent.SOLAR_ECLIPSE) {
            state.skyColor = new Vector3f(0.025f, 0.025f, 0.05f);
            state.sunriseAndSunsetColor = new Vector4f(0.0f, 0.0f, 0.0f, 0.0f);
        }
    }

    @ModifyArg(
        method = "renderSun",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;drawCelestialBody(Ljava/util/function/Supplier;Lcom/mojang/renderpearl/api/commands/RenderPass;Lcom/mojang/renderpearl/api/buffers/GpuBufferSlice;Lcom/mojang/renderpearl/api/buffers/GpuBuffer;Lcom/mojang/renderpearl/api/buffers/GpuBuffer;I)V"),
        index = 4
    )
    private GpuBuffer celestial$eclipseSun(GpuBuffer vanilla) {
        return EventStateBridge.clientEvent == CelestialEvent.SOLAR_ECLIPSE
            ? celestial$eclipseSunBuffer : vanilla;
    }

    @ModifyArg(
        method = "renderMoon",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/DynamicGpuData;writeTransform(Lorg/joml/Matrix4f;Lorg/joml/Vector4f;)Lcom/mojang/renderpearl/api/buffers/GpuBufferSlice;"),
        index = 1
    )
    private Vector4f celestial$redMoon(Vector4f vanilla) {
        return EventStateBridge.clientEvent == CelestialEvent.BLOOD_MOON
            ? new Vector4f(1.0f, 0.16f, 0.13f, vanilla.w)
            : vanilla;
    }

    @Inject(method = "close", at = @At("TAIL"))
    private void celestial$closeEclipseSun(CallbackInfo ci) {
        if (celestial$eclipseSunBuffer != null) {
            celestial$eclipseSunBuffer.close();
        }
    }
}
