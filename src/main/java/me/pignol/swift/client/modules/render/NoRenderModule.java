package me.pignol.swift.client.modules.render;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.ToastEvent;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoRenderModule extends Module
{

    public static NoRenderModule INSTANCE = new NoRenderModule();

    public final Value<Boolean> fog = new Value<>("Fog", true);
    public final Value<Boolean> vines = new Value<>("Vines", true);
    public final Value<Boolean> itemframes = new Value<>("Itemframes", true);
    public final Value<Boolean> bossbar = new Value<>("Bossbar", true);
    public final Value<Boolean> toast = new Value<>("Toast", true);
    public final Value<Boolean> chatBox = new Value<>("ChatBox", true);
    public final Value<Boolean> hurtcam = new Value<>("Hurtcam", true);
    public final Value<Boolean> entityFire = new Value<>("EntityFire", true);
    public final Value<Boolean> totemAnimation = new Value<>("TotemAnimation", true);
    public final Value<Boolean> suffocation = new Value<>("Suffocation", true);
    public final Value<Boolean> portalGui = new Value<>("PortalGui", true);
    public final Value<Boolean> portalEffect = new Value<>("PortalEffect", true);
    public final Value<Boolean> loadingScreen = new Value<>("LoadingScreen", true);
    public final Value<Boolean> pigs = new Value<>("Pigs", true);

    public NoRenderModule()
    {
        super("NoRender", Category.RENDER, false, true);
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event)
    {
        if (portalGui.getValue())
        {
            mc.player.inPortal = false;
        }

        if (portalEffect.getValue())
        {
            GuiIngameForge.renderPortal = false;
        }
        else
        {
            GuiIngameForge.renderPortal = true;
        }
    }

    @SubscribeEvent
    public void onFogDensity(EntityViewRenderEvent.FogDensity event)
    {
        if (fog.getValue())
        {
            event.setCanceled(true);
            event.setDensity(0);
        }
    }

    @SubscribeEvent
    public void onToastRender(ToastEvent event)
    {
        if (toast.getValue())
        {
            event.setCanceled(true);
        }
    }

}
