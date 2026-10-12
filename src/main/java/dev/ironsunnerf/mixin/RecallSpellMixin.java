package dev.ironsunnerf.mixin;

import dev.ironsunnerf.UnnerfConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.spells.ender.RecallSpell;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = RecallSpell.class, remap = false)
public abstract class RecallSpellMixin {
    /** 戦闘中は使用不可の制限を外す(旧版は発動条件なし) */
    @Inject(method = "checkPreCastConditions", at = @At("HEAD"), cancellable = true)
    private void unnerf$allowInCombat(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData,
                                      CallbackInfoReturnable<Boolean> cir) {
        if (UnnerfConfig.on("recall.combat_restriction")) {
            cir.setReturnValue(true);
        }
    }

    /** 詠唱短縮が効く標準の計算(AbstractSpell と同じ式)に戻す */
    @Inject(method = "getEffectiveCastTime", at = @At("HEAD"), cancellable = true)
    private void unnerf$castTimeScaling(int spellLevel, @Nullable LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        if (!UnnerfConfig.on("recall.cast_time_scaling")) return;
        AbstractSpell self = (AbstractSpell) (Object) this;
        double modifier = 1;
        if (entity != null) {
            double reduction = entity.getAttributeValue(AttributeRegistry.CAST_TIME_REDUCTION);
            modifier = self.getCastType() != CastType.CONTINUOUS ? 2 - Utils.softCapFormula(reduction) : reduction;
        }
        cir.setReturnValue(Math.round(self.getCastTime(spellLevel) * (float) modifier));
    }
}
