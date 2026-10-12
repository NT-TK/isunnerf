package dev.ironsunnerf.mixin;

import dev.ironsunnerf.UnnerfConfig;
import io.redspace.ironsspellbooks.effect.EchoingStrikesData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EchoingStrikesData.class, remap = false)
public abstract class EchoingStrikesDataMixin {
    /** 命中回数を減らさない = 残り回数が0にならず、効果が切れるまでエコーが出続ける */
    @Inject(method = "decrementHit", at = @At("HEAD"), cancellable = true)
    private void unnerf$noDecrement(CallbackInfo ci) {
        if (UnnerfConfig.on("echoing_strikes.hit_limit")) {
            ci.cancel();
        }
    }
}
