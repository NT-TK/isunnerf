package dev.ironsunnerf.mixin;

import dev.ironsunnerf.UnnerfConfig;
import io.redspace.ironsspellbooks.effect.EvasionEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = EvasionEffect.class, remap = false)
public abstract class EvasionEffectMixin {
    /** doEffect() 内の maxRadius = 12 を、旧値の 18 に戻す */
    @ModifyConstant(method = "doEffect", constant = @Constant(floatValue = 12.0f))
    private static float unnerf$radius(float original) {
        return UnnerfConfig.on("evasion.radius") ? 18.0f : original;
    }
}
