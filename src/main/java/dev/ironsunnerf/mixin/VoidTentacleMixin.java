package dev.ironsunnerf.mixin;

import dev.ironsunnerf.UnnerfConfig;
import io.redspace.ironsspellbooks.entity.spells.void_tentacle.VoidTentacle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = VoidTentacle.class, remap = false)
public abstract class VoidTentacleMixin {
    /** tick() 内の getBoundingBox().inflate(0.5) の 0.5 を、旧値の 1.2 に戻す */
    @ModifyArg(method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/AABB;inflate(D)Lnet/minecraft/world/phys/AABB;"),
            index = 0)
    private double unnerf$hitbox(double original) {
        return UnnerfConfig.on("sculk_tentacles.hitbox") ? 1.2 : original;
    }
}
