package dev.ironsunnerf;

import io.redspace.ironsspellbooks.api.config.ModifyDefaultConfigValuesEvent;
import io.redspace.ironsspellbooks.api.config.SpellConfigHolder;
import io.redspace.ironsspellbooks.api.config.SpellConfigManager;
import io.redspace.ironsspellbooks.api.config.SpellConfigParameter;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public final class SpellPatches {
    private static final String NS = "irons_spellbooks";

    private SpellPatches() {}

    /** 本体の元の値(最初にパッチする前に読み取って保存する) */
    private static final Map<String, Integer> ORIGINALS = new HashMap<>();

    /**
     * 魔法本体の数値フィールド(マナ・詠唱時間・基礎スペルパワー)を、設定に合わせて旧値または元の値にする。
     * configの読み込み・再読み込み・解除のたびに呼ばれる。無効な項目は元の値に戻る。
     */
    public static void refreshFields() {
        Map<String, Integer> desired = new LinkedHashMap<>();
        Set<String> keys = new LinkedHashSet<>();
        for (Patches.Toggle t : Patches.TOGGLES) {
            boolean enabled = UnnerfConfig.on(t.path());
            for (Patches.Change c : t.changes()) {
                if (c instanceof Patches.IntField f) {
                    String key = t.spell() + "/" + f.field();
                    keys.add(key);
                    if (enabled) desired.put(key, f.value());
                }
            }
        }
        for (String key : keys) {
            String[] parts = key.split("/");
            AbstractSpell spell = findSpell(parts[0]);
            if (spell == null) continue;
            try {
                Field field = AbstractSpell.class.getDeclaredField(parts[1]);
                field.setAccessible(true);
                ORIGINALS.computeIfAbsent(key, k -> {
                    try {
                        return field.getInt(spell);
                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException(e);
                    }
                });
                field.setInt(spell, desired.getOrDefault(key, ORIGINALS.get(key)));
            } catch (ReflectiveOperationException | IllegalStateException e) {
                IronsUnnerf.LOGGER.error("failed to patch {}", key, e);
            }
        }
    }

    private static AbstractSpell findSpell(String spellPath) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(NS, spellPath);
        AbstractSpell spell = SpellRegistry.getSpell(id);
        if (spell == null || !id.equals(spell.getSpellResource())) {
            IronsUnnerf.LOGGER.warn("spell not found, skipped: {}", id);
            return null;
        }
        return spell;
    }

    /** 最大Lv・クールダウン・最低レア度は、スペル設定の既定値として差し替える(ユーザーが設定ファイルで明示した値が優先される)。 */
    public static void applyConfigDefaults(ModifyDefaultConfigValuesEvent event) {
        ResourceLocation id = event.getSpell().getSpellResource();
        if (!NS.equals(id.getNamespace())) return;
        SpellConfigHolder holder = null;
        for (Patches.Toggle t : Patches.TOGGLES) {
            if (!t.spell().equals(id.getPath()) || !UnnerfConfig.on(t.path())) continue;
            if (holder == null) holder = holderOf(event.getSpell());
            for (Patches.Change c : t.changes()) {
                if (c instanceof Patches.MaxLevel m) {
                    applyConfig(event, holder, SpellConfigParameter.MAX_LEVEL, m.value());
                } else if (c instanceof Patches.Cooldown cd) {
                    applyConfig(event, holder, SpellConfigParameter.COOLDOWN_IN_SECONDS, cd.seconds());
                } else if (c instanceof Patches.MinRarity r) {
                    applyConfig(event, holder, SpellConfigParameter.MIN_RARITY, r.value());
                }
            }
        }
    }

    /**
     * 既定値として設定したうえで、ユーザーが明示していない項目は「上書き」としても設定する。
     * 本体がクライアントへ同期するのは上書き値だけなので、これでMODなしのクライアントにも
     * 最大Lv・最低レア度・クールダウンが反映される。ユーザーが設定ファイルで明示した値は変更しない。
     */
    private static <T> void applyConfig(ModifyDefaultConfigValuesEvent event, SpellConfigHolder holder,
                                        SpellConfigParameter<T> param, T value) {
        event.setDefaultValue(param, value);
        if (holder != null && holder.isDefault(param)) {
            holder.set(param, value);
        }
    }

    private static Field configField;
    private static boolean reflectionFailed;

    /** 本体の SpellConfigManager が保持する、魔法ごとの設定ホルダーを取得する(private フィールドをリフレクションで参照) */
    @SuppressWarnings("unchecked")
    private static SpellConfigHolder holderOf(AbstractSpell spell) {
        if (reflectionFailed) return null;
        try {
            if (configField == null) {
                configField = SpellConfigManager.class.getDeclaredField("config");
                configField.setAccessible(true);
            }
            Map<AbstractSpell, SpellConfigHolder> map =
                    (Map<AbstractSpell, SpellConfigHolder>) configField.get(SpellConfigManager.INSTANCE);
            return map.get(spell);
        } catch (ReflectiveOperationException | ClassCastException e) {
            reflectionFailed = true;
            IronsUnnerf.LOGGER.warn("could not access spell config holders; client sync of max level/rarity/cooldown is limited", e);
            return null;
        }
    }
}
