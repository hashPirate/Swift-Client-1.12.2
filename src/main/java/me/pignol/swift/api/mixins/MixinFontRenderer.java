package me.pignol.swift.api.mixins;

import me.pignol.swift.client.modules.misc.NameProtectModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FontRenderer.class)
public abstract class MixinFontRenderer {

    @ModifyVariable(method = "renderString", at = @At("HEAD"), require = 1, ordinal = 0)
    private String renderString(final String string) {
        if (string == null)
            return null;

        if (NameProtectModule.INSTANCE.isEnabled()) {
            return StringUtils.replace(string, Minecraft.getMinecraft().getSession().getUsername(), NameProtectModule.INSTANCE.getFakeName());
        }
        return string;
    }

    @ModifyVariable(method = "getStringWidth", at = @At("HEAD"), require = 1, ordinal = 0)
    private String getStringWidth(final String string) {
        if (string == null)
            return null;
        if (NameProtectModule.INSTANCE.isEnabled()) {
            return StringUtils.replace(string, Minecraft.getMinecraft().getSession().getUsername(), NameProtectModule.INSTANCE.getFakeName());
        }
        return string;
    }

}
