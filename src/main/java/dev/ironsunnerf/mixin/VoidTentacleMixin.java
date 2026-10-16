package dev.ironsunnerf.mixin;

import dev.ironsunnerf.UnnerfConfig;
import io.redspace.ironsspellbooks.entity.spells.void_tentacle.VoidTentacle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = VoidTentacle.class, remap = false)
public abstract class VoidTentacleMixin {
    /**
     * tick()(SRG名 m_8119_)内の最初の 0.5 が getBoundingBox().inflate(0.5) の引数。旧値の 1.2 に戻す。
     * 本体のjarのバイトコードで、この0.5が最初に現れる定数であることを確認済み。
     */
    @ModifyConstant(method = {"m_8119_", "tick"}, constant = @Constant(doubleValue = 0.5, ordinal = 0))
    private double unnerf$hitbox(double original) {
        return UnnerfConfig.on("sculk_tentacles.hitbox") ? 1.2 : original;
    }
}
