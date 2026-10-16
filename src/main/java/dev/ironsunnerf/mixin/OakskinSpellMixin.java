package dev.ironsunnerf.mixin;

import dev.ironsunnerf.UnnerfConfig;
import io.redspace.ironsspellbooks.spells.nature.OakskinSpell;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = OakskinSpell.class, remap = false)
public abstract class OakskinSpellMixin {
    /** 旧: 効果の amplifier = 魔法Lv-1(Lvに応じて軽減率が上がる)。現行: 固定 */
    @Inject(method = "getAmplifier", at = @At("HEAD"), cancellable = true)
    private void unnerf$amplifier(int spellLevel, LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        if (UnnerfConfig.on("oakskin.level_scaling")) {
            cir.setReturnValue(spellLevel - 1);
        }
    }
}
