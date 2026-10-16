package dev.ironsunnerf.mixin;

import dev.ironsunnerf.UnnerfConfig;
import io.redspace.ironsspellbooks.spells.holy.FortifySpell;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Fortify の効果範囲(radius = 8)は定数として各メソッドに埋め込まれている。
 * 実物のバイトコードで確認した使用箇所: getUniqueInfo(double)、onCast(double×6)、onServerPreCast(float)、lambda$onCast$0(float)
 */
@Mixin(value = FortifySpell.class, remap = false)
public abstract class FortifySpellMixin {
    @ModifyConstant(method = {"getUniqueInfo", "onCast"}, constant = @Constant(doubleValue = 8.0))
    private double unnerf$radiusD(double original) {
        return UnnerfConfig.on("fortify.radius") ? 16.0 : original;
    }

    @ModifyConstant(method = {"onServerPreCast", "lambda$onCast$0"}, constant = @Constant(floatValue = 8.0f))
    private float unnerf$radiusF(float original) {
        return UnnerfConfig.on("fortify.radius") ? 16.0f : original;
    }
}
