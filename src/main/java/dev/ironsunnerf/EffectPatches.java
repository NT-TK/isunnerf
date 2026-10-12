package dev.ironsunnerf;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class EffectPatches {
    private EffectPatches() {}

    /**
     * 状態効果の補正を、設定に合わせて旧値または現行値にする。
     * 同じ属性・同じIDで addAttributeModifier し直すと上書きされるので、Mixinなしで切り替えられる。
     * configの読み込み・再読み込み・解除のたびに呼ばれる(無効時は現行=ナーフ後の値に戻る)。
     */
    public static void refresh() {
        var op = AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;

        // ---- Charged ----
        MobEffect charged = MobEffectRegistry.CHARGED.get();
        ResourceLocation chargedId = IronsSpellbooks.id("mobeffect_charged");
        charged.addAttributeModifier(Attributes.MOVEMENT_SPEED, chargedId,
                UnnerfConfig.on("charge.movement_speed") ? 0.20 : 0.05, op);
        charged.addAttributeModifier(Attributes.ATTACK_DAMAGE, chargedId,
                UnnerfConfig.on("charge.attack_damage") ? 0.10 : 0.05, op);
        boolean chargeSpellPower = UnnerfConfig.on("charge.spell_power");
        // 旧: 全属性共通の SPELL_POWER に +5%/Lv。現行: 雷属性のみ。切り替え時に二重掛けしないよう片方は0にする
        charged.addAttributeModifier(AttributeRegistry.SPELL_POWER, chargedId, chargeSpellPower ? 0.05 : 0.0, op);
        charged.addAttributeModifier(AttributeRegistry.LIGHTNING_SPELL_POWER, chargedId, chargeSpellPower ? 0.0 : 0.05, op);

        // ---- Oakskin の移動速度ペナルティ(全盛期はなし / 現行 -25%) ----
        boolean noPenalty = UnnerfConfig.on("oakskin.movement_penalty");
        MobEffectRegistry.OAKSKIN.get().addAttributeModifier(
                Attributes.MOVEMENT_SPEED, IronsSpellbooks.id("mobeffect_oakskin"), op, level -> noPenalty ? 0.0 : -0.25);

        // ---- Hastened: 1段階あたり 旧.10 / 現行.025、マナ回復は旧のみ ----
        MobEffect hastened = MobEffectRegistry.HASTENED.get();
        ResourceLocation hasteId = IronsSpellbooks.id("mobeffect_haste");
        double perAmp = UnnerfConfig.on("haste.effect_magnitude") ? 0.10 : 0.025;
        hastened.addAttributeModifier(Attributes.MOVEMENT_SPEED, hasteId, perAmp, op);
        hastened.addAttributeModifier(Attributes.ATTACK_SPEED, hasteId, perAmp, op);
        hastened.addAttributeModifier(AttributeRegistry.CAST_TIME_REDUCTION, hasteId, perAmp, op);
        hastened.addAttributeModifier(AttributeRegistry.MANA_REGEN, hasteId, UnnerfConfig.on("haste.mana_regen") ? perAmp : 0.0, op);
    }
}
