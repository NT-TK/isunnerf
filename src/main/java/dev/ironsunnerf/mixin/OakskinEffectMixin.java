package dev.ironsunnerf.mixin;

import dev.ironsunnerf.UnnerfConfig;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.effect.OakskinData;
import io.redspace.ironsspellbooks.effect.OakskinEffect;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = OakskinEffect.class, remap = false)
public abstract class OakskinEffectMixin {
    /**
     * 軽減率の式を、設定に応じて旧版寄りにする。
     * - oakskin.level_scaling: 旧式は レベル = amplifier+1 で 10%+5%×レベル (現行は amplifier のまま)
     * - oakskin_elixir.spell_power: エリクサー由来でもスペルパワー倍率を掛ける(現行は1に固定)
     * スペルパワー倍率そのもの(現行で追加された強化)と上限75%は維持する。
     */
    @Inject(method = "getReductionAmount", at = @At("HEAD"), cancellable = true)
    private static void unnerf$reduction(int amplifier, @Nullable LivingEntity livingEntity, CallbackInfoReturnable<Float> cir) {
        boolean levelScaling = UnnerfConfig.on("oakskin.level_scaling");
        boolean elixirPower = UnnerfConfig.on("oakskin_elixir.spell_power");
        if (!levelScaling && !elixirPower) return;

        float multiplier = SpellRegistry.OAKSKIN_SPELL.get().getEntityPowerMultiplier(livingEntity);
        if (!elixirPower && livingEntity != null && OakskinData.hasFromElixir(livingEntity)) {
            multiplier = 1;
        }
        int level = levelScaling ? amplifier + 1 : amplifier;
        cir.setReturnValue(Math.min(0.75f, (OakskinEffect.BASE_REDUCTION + OakskinEffect.REDUCTION_PER_LEVEL * level) * multiplier));
    }
}
