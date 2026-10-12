package dev.ironsunnerf.mixin;

import dev.ironsunnerf.UnnerfConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.spells.eldritch.PocketDimensionSpell;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PocketDimensionSpell.class, remap = false)
public abstract class PocketDimensionSpellMixin {
    /** 旧: すでに再詠唱が待機中なら詠唱時間0(即時)。現行は常に固定の詠唱時間 */
    @Inject(method = "getEffectiveCastTime", at = @At("HEAD"), cancellable = true)
    private void unnerf$instantRecast(int spellLevel, @Nullable LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        if (!UnnerfConfig.on("pocket_dimension.recast_instant") || entity == null) return;
        AbstractSpell self = (AbstractSpell) (Object) this;
        if (MagicData.getPlayerMagicData(entity).getPlayerRecasts().hasRecastForSpell(self)) {
            cir.setReturnValue(0);
        }
    }
}
