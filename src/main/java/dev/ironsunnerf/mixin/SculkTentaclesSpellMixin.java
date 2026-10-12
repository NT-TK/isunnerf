package dev.ironsunnerf.mixin;

import dev.ironsunnerf.UnnerfConfig;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.spells.eldritch.SculkTentaclesSpell;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SculkTentaclesSpell.class, remap = false)
public abstract class SculkTentaclesSpellMixin {
    /** 旧: ダメージ = getSpellPower(レベル依存)。現行: baseSpellPower × パワー倍率(レベル非依存) */
    @Inject(method = "getDamage", at = @At("HEAD"), cancellable = true)
    private void unnerf$damage(int spellLevel, LivingEntity entity, CallbackInfoReturnable<Float> cir) {
        if (UnnerfConfig.on("sculk_tentacles.level_scaling")) {
            AbstractSpell self = (AbstractSpell) (Object) this;
            cir.setReturnValue(self.getSpellPower(spellLevel, entity));
        }
    }
}
