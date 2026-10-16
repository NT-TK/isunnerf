package dev.ironsunnerf;

import com.mojang.logging.LogUtils;
import io.redspace.ironsspellbooks.api.config.ModifyDefaultConfigValuesEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(IronsUnnerf.MODID)
public class IronsUnnerf {
    public static final String MODID = "irons_unnerf";
    public static final Logger LOGGER = LogUtils.getLogger();

    // Forge 47.4.0 では ModLoadingContext.get() 等が「削除予定」表示になるが、Iron's 本体の1.20.1版も同じ書き方(動作に影響なし)
    @SuppressWarnings("removal")
    public IronsUnnerf() {
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        // SERVERタイプ: ワールドの serverconfig/ に作られ、サーバーの内容がクライアントへ同期される
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, UnnerfConfig.SPEC);
        modBus.addListener(this::onConfigLoading);
        modBus.addListener(this::onConfigReloading);
        modBus.addListener(this::onConfigUnloading);
        MinecraftForge.EVENT_BUS.addListener(this::onModifyDefaults);
    }

    private void onConfigLoading(ModConfigEvent.Loading event) {
        refresh(event.getConfig(), true);
    }

    private void onConfigReloading(ModConfigEvent.Reloading event) {
        refresh(event.getConfig(), true);
    }

    /** サーバーから切断・ワールド終了時: 全項目を現行(ナーフ後)の値に戻す */
    private void onConfigUnloading(ModConfigEvent.Unloading event) {
        refresh(event.getConfig(), false);
    }

    private void refresh(ModConfig config, boolean active) {
        if (!MODID.equals(config.getModId())) return;
        UnnerfConfig.setActive(active);
        SpellPatches.refreshFields();
        EffectPatches.refresh();
    }

    private void onModifyDefaults(ModifyDefaultConfigValuesEvent event) {
        SpellPatches.applyConfigDefaults(event);
    }
}
