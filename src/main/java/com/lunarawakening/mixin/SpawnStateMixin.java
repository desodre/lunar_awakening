package com.lunarawakening.mixin;

import com.lunarawakening.spawn.SpawnStateExt;
import com.lunarawakening.spawn.SpawnCapContext;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NaturalSpawner.SpawnState.class)
public abstract class SpawnStateMixin implements SpawnStateExt {
    @Unique private boolean celestial$active;

    @Override
    public void celestial$setActive(boolean active) {
        celestial$active = active;
    }

    @Inject(method = "canSpawnForCategoryGlobal", at = @At("HEAD"))
    private void celestial$enterGlobal(MobCategory category, CallbackInfoReturnable<Boolean> cir) {
        SpawnCapContext.push(celestial$active);
    }

    @Inject(method = "canSpawnForCategoryGlobal", at = @At("RETURN"))
    private void celestial$exitGlobal(MobCategory category, CallbackInfoReturnable<Boolean> cir) {
        SpawnCapContext.pop();
    }

    @Inject(method = "canSpawnForCategoryLocal", at = @At("HEAD"))
    private void celestial$enterLocal(MobCategory category, ChunkPos pos, CallbackInfoReturnable<Boolean> cir) {
        SpawnCapContext.push(celestial$active);
    }

    @Inject(method = "canSpawnForCategoryLocal", at = @At("RETURN"))
    private void celestial$exitLocal(MobCategory category, ChunkPos pos, CallbackInfoReturnable<Boolean> cir) {
        SpawnCapContext.pop();
    }
}
