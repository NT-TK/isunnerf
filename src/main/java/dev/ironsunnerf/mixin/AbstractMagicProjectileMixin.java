package dev.ironsunnerf.mixin;

import dev.ironsunnerf.UnnerfConfig;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = AbstractMagicProjectile.class, remap = false)
public abstract class AbstractMagicProjectileMixin {
    /** カーソル誘導の最大射程 48 → 64(旧値) */
    @ModifyConstant(method = "handleCursorHoming", constant = @Constant(floatValue = 48.0f))
    private float unnerf$homingRange(float original) {
        return UnnerfConfig.on("flaming_barrage.homing_range") ? 64.0f : original;
    }
}
