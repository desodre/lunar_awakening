package com.cicloscelestiais.mixin;

import com.cicloscelestiais.CelestialEvent;
import com.cicloscelestiais.CycleEventManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobMixin {
    @Inject(method = "isSunBurnTick", at = @At("HEAD"), cancellable = true)
    private void celestial$preventEclipseBurn(CallbackInfoReturnable<Boolean> cir) {
        Mob mob = (Mob) (Object) this;
        if (mob.level() instanceof ServerLevel level
                && CycleEventManager.current(level) == CelestialEvent.SOLAR_ECLIPSE) {
            cir.setReturnValue(false);
        }
    }
}
