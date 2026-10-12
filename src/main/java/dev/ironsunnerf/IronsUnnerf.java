package dev.ironsunnerf;

import com.mojang.logging.LogUtils;
import io.redspace.ironsspellbooks.api.config.ModifyDefaultConfigValuesEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(IronsUnnerf.MODID)
public class IronsUnnerf {
    public static final String MODID = "irons_unnerf";
    public static final Logger LOGGER = LogUtils.getLogger();

    public IronsUnnerf(IEventBus modBus, ModContainer container) {
        // SERVERタイプ: ワールドの serverconfig/ に作られ、サーバーの内容がクライアントへ同期される
        container.registerConfig(ModConfig.Type.SERVER, UnnerfConfig.SPEC);
        modBus.addListener(this::onConfigLoading);
        modBus.addListener(this::onConfigReloading);
        modBus.addListener(this::onConfigUnloading);
        NeoForge.EVENT_BUS.addListener(this::onModifyDefaults);
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
