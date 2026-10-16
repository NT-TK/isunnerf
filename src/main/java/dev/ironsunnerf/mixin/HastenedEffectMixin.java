package dev.ironsunnerf.mixin;

import dev.ironsunnerf.UnnerfConfig;
import io.redspace.ironsspellbooks.effect.HastenedEffect;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = HastenedEffect.class, remap = false)
public abstract class HastenedEffectMixin {
    /** ツールチップの表示用。1段階あたり .025 → .10(旧値) */
    @Inject(method = "getPercentForAmplifier", at = @At("HEAD"), cancellable = true)
    private static void unnerf$percent(int amplifier, LivingEntity livingEntity, CallbackInfoReturnable<Float> cir) {
        if (UnnerfConfig.on("haste.effect_magnitude")) {
            cir.setReturnValue((1 + amplifier) * 0.10f);
        }
    }
}
