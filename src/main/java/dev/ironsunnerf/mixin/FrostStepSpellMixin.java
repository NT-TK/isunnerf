package dev.ironsunnerf.mixin;

import dev.ironsunnerf.UnnerfConfig;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.spells.ice.FrostStepSpell;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FrostStepSpell.class, remap = false)
public abstract class FrostStepSpellMixin {
    /** 旧式: 9 + ソフトキャップ(パワー倍率) × Lv × 1.5 */
    @Inject(method = "getDistance", at = @At("HEAD"), cancellable = true)
    private void unnerf$distance(int spellLevel, LivingEntity sourceEntity, CallbackInfoReturnable<Float> cir) {
        if (UnnerfConfig.on("frost_step.range")) {
            AbstractSpell self = (AbstractSpell) (Object) this;
            cir.setReturnValue(9 + (float) (Utils.softCapFormula(self.getEntityPowerMultiplier(sourceEntity)) * spellLevel * 1.5));
        }
    }
}
