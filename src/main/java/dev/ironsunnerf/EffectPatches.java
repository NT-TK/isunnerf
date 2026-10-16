package dev.ironsunnerf;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.backwards_compat.IBackwardsAttributeCompatMobEffect;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class EffectPatches {
    private static final AttributeModifier.Operation OP = AttributeModifier.Operation.MULTIPLY_TOTAL;

    private EffectPatches() {}

    /** 同じ属性に addAttributeModifier し直すと、その属性の補正が上書きされる(Iron's 自身も同じ方法で登録している) */
    private static void set(MobEffect effect, Attribute attribute, ResourceLocation id, double amount) {
        ((IBackwardsAttributeCompatMobEffect) effect).addAttributeModifier(attribute, id, amount, OP);
    }

    /**
     * 状態効果の補正を、設定に合わせて旧値または現行値にする。
     * configの読み込み・再読み込み・解除のたびに呼ばれる(無効時は現行=ナーフ後の値に戻る)。
     * ※ Oakskin の移動速度ペナルティは、本体が補正値を返すメソッドを上書きしているため、Mixin 側(OakskinEffectMixin)で扱う。
     */
    public static void refresh() {
        // ---- Charged ----
        MobEffect charged = MobEffectRegistry.CHARGED.get();
        ResourceLocation chargedId = IronsSpellbooks.id("mobeffect_charged");
        set(charged, Attributes.MOVEMENT_SPEED, chargedId, UnnerfConfig.on("charge.movement_speed") ? 0.20 : 0.05);
        set(charged, Attributes.ATTACK_DAMAGE, chargedId, UnnerfConfig.on("charge.attack_damage") ? 0.10 : 0.05);
        boolean chargeSpellPower = UnnerfConfig.on("charge.spell_power");
        // 旧: 全属性共通の SPELL_POWER に +5%/Lv。現行: 雷属性のみ。切り替え時に二重掛けしないよう片方は0にする
        set(charged, AttributeRegistry.SPELL_POWER.get(), chargedId, chargeSpellPower ? 0.05 : 0.0);
        set(charged, AttributeRegistry.LIGHTNING_SPELL_POWER.get(), chargedId, chargeSpellPower ? 0.0 : 0.05);

        // ---- Hastened: 1段階あたり 旧.10 / 現行.025、マナ回復は旧のみ ----
        MobEffect hastened = MobEffectRegistry.HASTENED.get();
        ResourceLocation hasteId = IronsSpellbooks.id("mobeffect_haste");
        double perAmp = UnnerfConfig.on("haste.effect_magnitude") ? 0.10 : 0.025;
        set(hastened, Attributes.MOVEMENT_SPEED, hasteId, perAmp);
        set(hastened, Attributes.ATTACK_SPEED, hasteId, perAmp);
        set(hastened, AttributeRegistry.CAST_TIME_REDUCTION.get(), hasteId, perAmp);
        set(hastened, AttributeRegistry.MANA_REGEN.get(), hasteId, UnnerfConfig.on("haste.mana_regen") ? perAmp : 0.0);
    }
}
