package com.lunarawakening.mixin;

import com.lunarawakening.spawn.SpawnCapContext;

import net.minecraft.world.entity.MobCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobCategory.class)
public abstract class MobCategoryMixin {
    @Inject(method = "getMaxInstancesPerChunk", at = @At("RETURN"), cancellable = true)
    private void celestial$raiseMonsterCap(CallbackInfoReturnable<Integer> cir) {
        if ((Object) this == MobCategory.MONSTER && SpawnCapContext.active()) {
            cir.setReturnValue(Math.round(cir.getReturnValue() * 1.25f));
        }
    }
}
