package dev.ironsunnerf;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.HashMap;
import java.util.Map;

/**
 * 項目ごとのboolean。true = ナーフを取り消す(旧値に戻す)。既定は全てtrue。
 * SERVERタイプのconfigなので、サーバーのファイルが基準になり、接続時にクライアントへ同期される。
 */
public final class UnnerfConfig {
    public static final ModConfigSpec SPEC;
    private static final Map<String, ModConfigSpec.BooleanValue> FLAGS = new HashMap<>();

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();
        b.comment("true = その項目のナーフを取り消して旧値に戻す / false = 現行(ナーフ後)のまま。",
                "マルチプレイでは、サーバーとクライアントでこのファイルの内容を揃えてください。");
        String current = null;
        for (Patches.Toggle t : Patches.TOGGLES) {
            if (!t.spell().equals(current)) {
                if (current != null) b.pop();
                b.push(t.spell());
                current = t.spell();
            }
            FLAGS.put(t.path(), b.comment(t.comment()).define(t.key(), true));
        }
        if (current != null) b.pop();
        SPEC = b.build();
    }

    private UnnerfConfig() {}

    /** configが読み込まれている間だけ true(サーバー未接続・切断後は false で、全項目が現行のまま動く) */
    private static volatile boolean active;

    public static void setActive(boolean value) {
        active = value;
    }

    /** config未ロード・無効時は false(何もしない) */
    public static boolean on(String path) {
        if (!active) return false;
        ModConfigSpec.BooleanValue v = FLAGS.get(path);
        if (v == null) return false;
        try {
            return v.get();
        } catch (IllegalStateException notLoaded) {
            return false;
        }
    }
}
