package dev.ironsunnerf.mixin;

import dev.ironsunnerf.UnnerfConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.spells.holy.HasteSpell;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = HasteSpell.class, remap = false)
public abstract class HasteSpellMixin {
    private static final int MAX_TARGETS = 5;
    private static final double RADIUS = 3.0;

    /**
     * 旧: 効果の段階 = 魔法Lv-1 (1段階 = +10%)。現行は約20%固定。
     * Lv × スペルパワー倍率 - 1 にして、旧式を基準に現行の「スペルパワーで増加」も維持する。
     */
    @Inject(method = "getAmplifier", at = @At("HEAD"), cancellable = true)
    private void unnerf$amplifier(int spellLevel, LivingEntity caster, CallbackInfoReturnable<Integer> cir) {
        if (UnnerfConfig.on("haste.effect_magnitude")) {
            AbstractSpell self = (AbstractSpell) (Object) this;
            cir.setReturnValue(Math.max(0, (int) (spellLevel * self.getEntityPowerMultiplier(caster)) - 1));
        }
    }

    /** 対象の周囲(半径3)の味方にも付与する。対象本人を含めて最大5体 */
    @Inject(method = "onCast", at = @At("TAIL"))
    private void unnerf$multiTarget(Level world, int spellLevel, LivingEntity entity, CastSource castSource,
                                    MagicData playerMagicData, CallbackInfo ci) {
        if (!UnnerfConfig.on("haste.multi_target")) return;
        if (!(world instanceof ServerLevel serverLevel)) return;
        if (!(playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData data)) return;
        LivingEntity center = data.getTarget(serverLevel);
        if (center == null) return;

        HasteSpell self = (HasteSpell) (Object) this;
        int duration = self.getDuration(spellLevel, entity);
        int amplifier = self.getAmplifier(spellLevel, entity);
        int applied = 1; // 対象本人には元の処理で付与済み
        for (LivingEntity victim : serverLevel.getEntitiesOfClass(LivingEntity.class, center.getBoundingBox().inflate(RADIUS))) {
            if (applied >= MAX_TARGETS) break;
            if (victim == center) continue;
            if (victim.distanceToSqr(center) < RADIUS * RADIUS && Utils.shouldHealEntity((Entity) entity, (Entity) victim)) {
                victim.addEffect(new MobEffectInstance(MobEffectRegistry.HASTENED, duration, amplifier, false, false, true));
                applied++;
            }
        }
    }
}
