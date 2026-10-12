# Iron's Spells Unnerf (v2.0)

Iron's Spells 'n Spellbooks **1.21.1-3.16.3**（NeoForge 1.21.1）のナーフを、項目ごとに取り消すアドオンです。
本体のコード・アセットは含まず、本体を依存関係にして動きます。配布せず個人利用してください（本体のライセンス）。

## ビルド
1. `libs/` に Iron's Spells 3.16.3 のjarと、コンパイルに必要な依存jar（Iron's Lib / GeckoLib / Curios / PlayerAnimator など）を入れる
2. `build.gradle` のプラグイン版を、お手持ちの他のNeoForge 1.21.1 MODと同じ版に合わせる
3. `gradlew build` → `build/libs/irons_unnerf-1.0.0.jar` を mods に入れる（サーバー・クライアント両方）

## コンフィグ(サーバー基準)
SERVERタイプのconfigです。**サーバーの設定が基準**で、接続時にクライアントへ自動で同期されます。
- ファイル: ワールドフォルダ内の `serverconfig/irons_unnerf-server.toml`(シングルプレイでは `saves/<ワールド>/serverconfig/`、専用サーバーでは `<ワールド>/serverconfig/`)。
- 初期値を全ワールド共通にしたい場合は、`defaultconfigs/irons_unnerf-server.toml` に置くと新規ワールドへ複製されます。
- 旧版の `config/irons_unnerf-common.toml` は使われません。
- ファイルの変更はNeoForgeが検知して反映します。ただし最大Lv・最低レア度・クールダウンは、Iron's Spells側の設定の再構築(`/reload`)後に反映されます。
- MODを入れたクライアントがMODなしのサーバーに接続した場合は、全項目が無効(現行のまま)になります。切断時も元に戻ります。

項目一覧(下記は旧ファイルの説明と同じ内容)。各項目は boolean（既定 true = ナーフを取り消す / false = 現行のまま）。
マルチプレイでは、サーバーとクライアントで内容を揃えてください。

| グループ | 項目 | 戻す内容（現行 → 旧） |
|---|---|---|
| charge | movement_speed / attack_damage / spell_power / duration | 速度 5→20%/Lv、攻撃力 5→10%/Lv、スペルパワー 雷のみ→全属性、持続 30秒固定→スペルパワー秒 |
| echoing_strikes | hit_limit | 命中回数の制限(Lv+2回)を解除。持続2分・ダメージ約80%の強化は維持 |
| sculk_tentacles | level_scaling / hitbox | ダメージのレベル依存、判定の膨らみ 0.5→1.2 |
| heal | min_rarity / max_level / cooldown / mana_cost_per_level / base_healing | UNCOMMON→COMMON、Lv8→10、30→25秒、マナ 15→10/Lv、回復 5→6 |
| heartstop | min_rarity / max_level / duration / base_mana_cost | RARE→COMMON、Lv5→10、持続 200→300、マナ 100→50 |
| heat_surge | max_level / mana_cost_per_level | Lv6→8、マナ 10→8/Lv |
| fortify | cooldown / mana_cost_per_level / cast_time / base_mana_cost | 180→35秒(2025-12に60→180へ再ナーフ)、10→5/Lv、3→2秒、80→40 |
| acid_orb | max_level / mana_cost_per_level / base_mana_cost | Lv8→10、10→3/Lv、40→30 |
| blight | max_level / cooldown / mana_cost_per_level / base_mana_cost | Lv8→10、90→35秒、20→5/Lv、60→10 |
| angel_wing | min_rarity / duration / base_mana_cost | LEGENDARY→EPIC、持続 基礎10/Lv10→基礎30/Lv30、マナ 80→60 |
| teleport | mana_cost_per_level / range / cooldown | マナ 5→2/Lv、射程 基礎8+2/Lv → 基礎10+10/Lv(Lv5で50)、CD 6→3秒 |
| blood_step | cooldown / range | CD 12→5秒、射程 基礎8+1/Lv → 基礎12+4/Lv(Lv5で28) |
| frost_step | cooldown / range | CD 12→10秒、射程の式を旧式(9+パワー補正×Lv×1.5)に |
| recall | combat_restriction / cast_time_scaling | 戦闘中の使用不可を解除、詠唱短縮が効くように |
| pocket_dimension | recast_instant | 再詠唱(帰還)の詠唱時間0 |
| haste / slow | cooldown / max_level / min_rarity | CD 80→45秒、最大Lv 4→6、EPIC→UNCOMMON |
| haste(追加) | effect_magnitude / mana_regen / multi_target | 効果量を約20%固定→Lv×10%(Lv4で+40%、スペルパワーの増加は維持)、マナ回復上昇の復活、対象を周囲半径3の最大5体に |
| oakskin | cooldown / base_mana_cost / mana_cost_per_level / level_scaling / movement_penalty | CD 90→35秒、マナ 基礎25→15・Lvごと10→5、軽減率をLv依存(10%+5%×Lv)に、移動速度-25%を解除 |
| frost_step(追加) | damage / mana_cost_per_level | ダメージ 基礎4+1/Lv→基礎14+3/Lv、Lvごとのマナ 5→3 |
| pocket_dimension(追加) | cooldown | CD 60→15秒 |
| arcane_shackle / root / flaming_strike / arrow_volley / evasion | cooldown / cast_time / power_per_level / power_per_level / radius | CD 45→12秒、詠唱 50→40tick、威力/Lv 2→3、威力/Lv 0→1、半径 12→18 |
| snowball / gravity_fissure / portal / sacrifice / sonic_boom / stomp | max_level | 最大Lv 5→8 / 5→6 / 3→5 / 5→10 / 3→5 / 5→10 |
| flaming_barrage | homing_range | カーソル誘導の最大射程 48→64 |
| oakskin_elixir | spell_power | 飲んだ人のスペルパワー補正が軽減率に効く |
| ball_lightning | damage | ダメージ [5,9.5]→[6,15]（寿命5秒への延長は維持） |

## v1に含まれないもの（Mixinや専用処理が必要）
Slowの効果量・単体化(Hasteは対応済み)、Fortifyの効果範囲(8→16。定数のインライン展開でMixinが不安定なため保留)、Earthquakeの鈍足上限、Flaming Barrageの旧ロックオン方式(仕様が別物のため復元しない)、
Fireballのダメージ式、Fortifyの射程、ソフトキャップ計算式。

## 注意
- 表示（ツールチップ）の数値は、本体の定数から計算される項目では旧値に変わらないことがあります（効果自体は戻ります）。
- 設定ファイル（config/irons_spellbooks_spell_config/）に同じ値を明示している場合は、そちらが優先されます。
- このコードは取得したソースと突き合わせて書きましたが、この環境ではビルドできていません。

## サーバー側のみで使う
このMODはパケットもレジストリも追加しないので、**専用サーバーだけに入れ、MODなしのクライアントが接続する**使い方ができる想定です(接続できるかは実機で確認してください)。

- 設定ファイルはサーバーの `serverconfig/irons_unnerf-server.toml` だけで有効です。
- サーバー側で効くもの: ダメージ・持続時間・当たり判定・Echoing Strikesの回数・Oakskinの軽減率・Recall/Pocket Dimensionの発動条件と詠唱時間・クールダウン・最大Lv・レア度(本体がサーバー設定をクライアントに同期します)・テレポート系の射程・マナの消費・状態効果の補正(移動速度・攻撃力など。プレイヤーの属性はサーバーから同期されます)。
- クライアントにMODがない場合の差: ツールチップ・スペル選択ホイール・詠唱バーの数値は旧(ナーフ後)のまま表示されます。また、右クリックで撃つ魔法は、クライアント側が「ナーフ後のマナ消費」で足りるかを先に判定するので、旧消費より多く必要に見える場面があります。
- 
