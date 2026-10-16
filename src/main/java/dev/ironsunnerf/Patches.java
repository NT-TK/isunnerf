package dev.ironsunnerf;

import io.redspace.ironsspellbooks.api.spells.SpellRarity;

import java.util.List;

/**
 * ナーフ取り消し項目の一覧。コンフィグの項目名と適用内容をここ1か所で定義する。
 * 旧値は Iron's Spells のナーフ時コミット差分から取得したもの。
 * changes が空の項目は、EffectPatches / Mixin 側で個別に処理する。
 */
public final class Patches {
    private Patches() {}

    public sealed interface Change permits IntField, MaxLevel, Cooldown, MinRarity {}

    /** AbstractSpell の protected int フィールドを書き換える */
    public record IntField(String field, int value) implements Change {}

    /** スペル設定の既定値 max_level */
    public record MaxLevel(int value) implements Change {}

    /** スペル設定の既定値 cooldown_in_seconds */
    public record Cooldown(double seconds) implements Change {}

    /** スペル設定の既定値 min_rarity */
    public record MinRarity(SpellRarity value) implements Change {}

    public record Toggle(String spell, String key, String comment, List<Change> changes) {
        public String path() {
            return spell + "." + key;
        }
    }

    private static Toggle t(String spell, String key, String comment, Change... changes) {
        return new Toggle(spell, key, comment, List.of(changes));
    }

    public static final List<Toggle> TOGGLES = List.of(
            // ---- 帯電 (Charge) 3.16.2 ----
            t("charge", "movement_speed", "帯電: 移動速度 +5%/Lv → +20%/Lv"),
            t("charge", "attack_damage", "帯電: 攻撃力 +5%/Lv → +10%/Lv"),
            t("charge", "spell_power", "帯電: スペルパワー(雷のみ+5%/Lv)を全属性+5%/Lvに戻す"),
            t("charge", "duration", "帯電: 持続時間を30秒固定から、スペルパワー秒(Lvで伸びる)に戻す"),

            // ---- Echoing Strikes 3.16.2 ----
            // 現行の作り直し版は「命中回数制(Lv+2回)」。旧版は回数無制限だった。
            // 持続2分固定・ダメージ約80%固定は旧版より強いので、強化として維持する
            t("echoing_strikes", "hit_limit", "Echoing Strikes: 命中回数の制限(Lv+2回)をなくし、効果が切れるまでエコーが出続けるようにする"),

            // ---- Sculk Tentacles 3.14.2 ----
            t("sculk_tentacles", "level_scaling", "Sculk Tentacles: ダメージのレベル依存を戻す"),
            t("sculk_tentacles", "hitbox", "Sculk Tentacles: 触手の判定の大きさ(膨らみ0.5 → 1.2)を戻す"),

            // ---- Heal 3.6.0 ----
            t("heal", "min_rarity", "Heal: 最低レア度 UNCOMMON → COMMON", new MinRarity(SpellRarity.COMMON)),
            t("heal", "max_level", "Heal: 最大Lv 8 → 10", new MaxLevel(10)),
            t("heal", "cooldown", "Heal: クールダウン 30秒 → 25秒", new Cooldown(25)),
            t("heal", "mana_cost_per_level", "Heal: Lvごとのマナ 15 → 10", new IntField("manaCostPerLevel", 10)),
            t("heal", "base_healing", "Heal: 基礎回復量 5 → 6", new IntField("baseSpellPower", 6)),

            // ---- Heartstop 3.14.2 ----
            t("heartstop", "min_rarity", "Heartstop: 最低レア度 RARE → COMMON", new MinRarity(SpellRarity.COMMON)),
            t("heartstop", "max_level", "Heartstop: 最大Lv 5 → 10", new MaxLevel(10)),
            t("heartstop", "duration", "Heartstop: 基礎持続(基礎スペルパワー) 200 → 300", new IntField("baseSpellPower", 300)),
            t("heartstop", "base_mana_cost", "Heartstop: 基礎マナ 100 → 50", new IntField("baseManaCost", 50)),

            // ---- Heat Surge 3.14.2 ----
            t("heat_surge", "max_level", "Heat Surge: 最大Lv 6 → 8", new MaxLevel(8)),
            t("heat_surge", "mana_cost_per_level", "Heat Surge: Lvごとのマナ 10 → 8", new IntField("manaCostPerLevel", 8)),

            // ---- Fortify 3.14.2 ----
            t("fortify", "cooldown", "Fortify: クールダウン 60秒 → 35秒", new Cooldown(35)),
            t("fortify", "mana_cost_per_level", "Fortify: Lvごとのマナ 10 → 5", new IntField("manaCostPerLevel", 5)),
            t("fortify", "cast_time", "Fortify: 詠唱時間 60tick(3秒) → 40tick(2秒)", new IntField("castTime", 40)),
            t("fortify", "base_mana_cost", "Fortify: 基礎マナ 80 → 40", new IntField("baseManaCost", 40)),
            // 3.6.0 で 16 → 8。定数として本体に埋め込まれているため、Mixinで各使用箇所を書き換える
            t("fortify", "radius", "Fortify: 効果範囲 8 → 16ブロック(3.6.0の弱体化)"),

            // ---- Acid Orb (Acid Spit) 3.14.2 ----
            t("acid_orb", "max_level", "Acid Orb: 最大Lv 8 → 10", new MaxLevel(10)),
            t("acid_orb", "mana_cost_per_level", "Acid Orb: Lvごとのマナ 10 → 3", new IntField("manaCostPerLevel", 3)),
            t("acid_orb", "base_mana_cost", "Acid Orb: 基礎マナ 40 → 30", new IntField("baseManaCost", 30)),

            // ---- Blight 3.14.2 (最大Lvは1.1.1で10→8) ----
            t("blight", "max_level", "Blight: 最大Lv 8 → 10", new MaxLevel(10)),
            t("blight", "cooldown", "Blight: クールダウン 90秒 → 35秒", new Cooldown(35)),
            t("blight", "mana_cost_per_level", "Blight: Lvごとのマナ 20 → 5", new IntField("manaCostPerLevel", 5)),
            t("blight", "base_mana_cost", "Blight: 基礎マナ 60 → 10", new IntField("baseManaCost", 10)),

            // ---- Angel Wings 3.14.2 (spell id は angel_wing) ----
            t("angel_wing", "min_rarity", "Angel Wings: 最低レア度 LEGENDARY → EPIC", new MinRarity(SpellRarity.EPIC)),
            t("angel_wing", "duration", "Angel Wings: 持続 基礎10/Lvごと10 → 基礎30/Lvごと30(秒)",
                    new IntField("baseSpellPower", 30), new IntField("spellPowerPerLevel", 30)),
            t("angel_wing", "base_mana_cost", "Angel Wings: 基礎マナ 80 → 60", new IntField("baseManaCost", 60)),

            // ---- テレポート系 3.14.2 ----
            t("teleport", "mana_cost_per_level", "Teleport: Lvごとのマナ 5 → 2", new IntField("manaCostPerLevel", 2)),
            // 3.16.2: 射程 基礎10+Lvごと10(最大50) → 基礎8+Lvごと2、クールダウン 3秒 → 6秒
            t("teleport", "range", "Teleport: 射程 基礎8/Lvごと2 → 基礎10/Lvごと10(Lv5で50ブロック)",
                    new IntField("baseSpellPower", 10), new IntField("spellPowerPerLevel", 10)),
            t("teleport", "cooldown", "Teleport: クールダウン 6秒 → 3秒", new Cooldown(3)),
            t("blood_step", "cooldown", "Blood Step: クールダウン 12秒 → 5秒", new Cooldown(5)),
            // 3.16.2: 射程 基礎12+Lvごと4(最大28) → 基礎8+Lvごと1
            t("blood_step", "range", "Blood Step: 射程 基礎8/Lvごと1 → 基礎12/Lvごと4(Lv5で28ブロック)",
                    new IntField("baseSpellPower", 12), new IntField("spellPowerPerLevel", 4)),
            t("frost_step", "cooldown", "Frost Step: クールダウン 12秒 → 10秒", new Cooldown(10)),
            // 3.16.2: 射程の式 9 + ソフトキャップ(パワー)×Lv×1.5 → (8+(Lv-1)×0.5)×パワー  (Mixinで戻す)
            t("frost_step", "range", "Frost Step: 射程の計算式を旧式(9 + パワー補正×Lv×1.5)に戻す"),
            // 2025-03 の作り直しで 基礎14/Lvごと3 → 基礎4/Lvごと1。ダメージはスペルパワーそのもの
            t("frost_step", "damage", "Frost Step: ダメージ 基礎4/Lvごと1 → 基礎14/Lvごと3",
                    new IntField("baseSpellPower", 14), new IntField("spellPowerPerLevel", 3)),
            t("frost_step", "mana_cost_per_level", "Frost Step: Lvごとのマナ 5 → 3", new IntField("manaCostPerLevel", 3)),

            // ---- Recall / Pocket Dimension 3.14.2 ----
            t("recall", "combat_restriction", "Recall: 戦闘中は使用不可の制限をなくす"),
            t("recall", "cast_time_scaling", "Recall: 詠唱短縮(cast_time_reduction)が効かない仕様をやめ、効くように戻す"),
            t("pocket_dimension", "recast_instant", "Pocket Dimension: 再詠唱(帰還)の詠唱時間0を戻す"),
            t("pocket_dimension", "cooldown", "Pocket Dimension: クールダウン 60秒 → 15秒", new Cooldown(15)),

            // ---- Flaming Barrage 2025-10 ----
            // 旧版のロックオン複数ターゲット方式は仕様が別物のため戻さない。誘導の最大射程だけ 48 → 64
            t("flaming_barrage", "homing_range", "Flaming Barrage: カーソル誘導の最大射程 48 → 64ブロック"),

            // ---- Oakskin Elixir 2026-06 ----
            t("oakskin_elixir", "spell_power", "Oakskin Elixir: 飲んだ人のスペルパワー補正が軽減率に効くように戻す"),

            // ---- Ball Lightning 3.14.2 ----
            // 旧: ダメージ=スペルパワー(基礎6,Lv+1ごと+1) / 新: スペルパワー*0.5(基礎10)
            // 新の式の0.5倍を打ち消すため、基礎と成長を2倍にして旧ダメージ[6,15]に一致させる
            t("ball_lightning", "damage", "Ball Lightning: ダメージ [5,9.5] → [6,15] に戻す(寿命の延長は維持)",
                    new IntField("baseSpellPower", 12), new IntField("spellPowerPerLevel", 2)),

            // ---- Haste / Slow 2024-12 (3.16.2の単体化などの仕様変更は戻さない) ----
            t("haste", "cooldown", "Haste: クールダウン 80秒 → 45秒", new Cooldown(45)),
            t("haste", "max_level", "Haste: 最大Lv 4 → 6", new MaxLevel(6)),
            t("haste", "min_rarity", "Haste: 最低レア度 EPIC → UNCOMMON", new MinRarity(SpellRarity.UNCOMMON)),
            // 2026-06-25 の作り直し(効果量・マナ回復・対象数)を戻す。スペルパワーによる効果量増加と採掘速度は維持
            t("haste", "effect_magnitude", "Haste: 効果量を 約20%固定 → 旧式(Lv×10%、Lv4で+40%)に戻す。スペルパワーの増加は維持"),
            t("haste", "mana_regen", "Haste: マナ回復上昇を復活(移動速度などと同じ割合)"),
            t("haste", "multi_target", "Haste: 対象を単体 → 対象の周囲(半径3)の味方、最大5体に戻す"),
            t("slow", "cooldown", "Slow: クールダウン 80秒 → 45秒", new Cooldown(45)),
            t("slow", "max_level", "Slow: 最大Lv 4 → 6", new MaxLevel(6)),
            t("slow", "min_rarity", "Slow: 最低レア度 EPIC → UNCOMMON", new MinRarity(SpellRarity.UNCOMMON)),

            // ---- Oakskin 2025-01 / 2026-06 ----
            // 全盛期: CD35秒、基礎マナ15+5/Lv、軽減率 10%+5%×Lv(Lv8で50%)、移動速度ペナルティなし
            t("oakskin", "cooldown", "Oakskin: クールダウン 90秒 → 35秒", new Cooldown(35)),
            t("oakskin", "base_mana_cost", "Oakskin: 基礎マナ 25 → 15", new IntField("baseManaCost", 15)),
            t("oakskin", "mana_cost_per_level", "Oakskin: Lvごとのマナ 10 → 5", new IntField("manaCostPerLevel", 5)),
            t("oakskin", "level_scaling", "Oakskin: 軽減率をLv依存に戻す(旧: 10%+5%×Lv。現行は約20%固定)。エリクサーの軽減率も+5%戻る"),
            t("oakskin", "movement_penalty", "Oakskin: 移動速度-25%のペナルティをなくす(全盛期はペナルティなし)"),

            // ---- その他の数値 ----
            t("arcane_shackle", "cooldown", "Arcane Shackle: クールダウン 45秒 → 12秒", new Cooldown(12)),
            t("root", "cast_time", "Root: 詠唱 50tick → 40tick", new IntField("castTime", 40)),
            t("flaming_strike", "power_per_level", "Flaming Strike: Lvごとの威力 2 → 3", new IntField("spellPowerPerLevel", 3)),
            t("arrow_volley", "power_per_level", "Arrow Volley: Lvごとの威力 0 → 1", new IntField("spellPowerPerLevel", 1)),
            t("evasion", "radius", "Evasion: 回避テレポートの最大半径 12 → 18"),

            // ---- 最大Lvを最高時に戻す(上記以外の魔法。Heal/Heartstop/Heat Surge/Acid Orb/Blight/Haste/Slowは各グループにあり) ----
            t("snowball", "max_level", "Snowball: 最大Lv 5 → 8", new MaxLevel(8)),
            t("gravity_fissure", "max_level", "Gravity Fissure: 最大Lv 5 → 6", new MaxLevel(6)),
            t("portal", "max_level", "Portal: 最大Lv 3 → 5", new MaxLevel(5)),
            t("sacrifice", "max_level", "Sacrifice: 最大Lv 5 → 10", new MaxLevel(10)),
            t("sonic_boom", "max_level", "Sonic Boom: 最大Lv 3 → 5", new MaxLevel(5)),
            t("stomp", "max_level", "Stomp: 最大Lv 5 → 10", new MaxLevel(10))
    );
}
