package dev.ironsunnerf.mixin;

import dev.ironsunnerf.UnnerfConfig;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.spells.lightning.ChargeSpell;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ChargeSpell.class, remap = false)
public abstract class ChargeSpellMixin {
    /** 旧: 持続 = スペルパワー × 20tick(Lvで伸びる)。現行: 30秒固定 */
    @Inject(method = "getDurationTicks", at = @At("HEAD"), cancellable = true)
    private void unnerf$duration(int spellLevel, LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        if (UnnerfConfig.on("charge.duration")) {
            AbstractSpell self = (AbstractSpell) (Object) this;
            cir.setReturnValue((int) (self.getSpellPower(spellLevel, entity) * 20));
        }
    }

    /** ツールチップの表示: 移動速度 5% → 20%/Lv */
    @Inject(method = "getPercentSpeed", at = @At("HEAD"), cancellable = true)
    private void unnerf$tooltipSpeed(int spellLevel, LivingEntity entity, CallbackInfoReturnable<Float> cir) {
        if (UnnerfConfig.on("charge.movement_speed")) {
            cir.setReturnValue(spellLevel * 0.20f * 100);
        }
    }

    /** ツールチップの表示: 攻撃力 5% → 10%/Lv */
    @Inject(method = "getPercentAttackDamage", at = @At("HEAD"), cancellable = true)
    private void unnerf$tooltipAttack(int spellLevel, LivingEntity entity, CallbackInfoReturnable<Float> cir) {
        if (UnnerfConfig.on("charge.attack_damage")) {
            cir.setReturnValue(spellLevel * 0.10f * 100);
        }
    }
}
