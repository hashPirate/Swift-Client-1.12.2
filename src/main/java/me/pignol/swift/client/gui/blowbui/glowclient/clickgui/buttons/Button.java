package me.pignol.swift.client.gui.blowbui.glowclient.clickgui.buttons;


import me.pignol.swift.client.gui.blowbui.glowclient.clickgui.BaseButton;
import me.pignol.swift.client.gui.blowbui.glowclient.clickgui.ClickGUI;
import me.pignol.swift.client.gui.blowbui.glowclient.clickgui.Window;
import me.pignol.swift.client.gui.blowbui.glowclient.clickgui.utils.ColorUtils;
import me.pignol.swift.client.gui.blowbui.glowclient.clickgui.utils.GuiUtils;
import me.pignol.swift.client.gui.blowbui.glowclient.utils.render.Colors;
import me.pignol.swift.client.gui.blowbui.glowclient.utils.render.SurfaceBuilder;
import me.pignol.swift.client.gui.blowbui.glowclient.utils.render.SurfaceHelper;
import me.pignol.swift.client.modules.Module;
import me.pignol.swift.client.modules.other.ClickGuiModule;

import java.util.ArrayList;
import java.util.List;

public class Button extends BaseButton {
    private final List<BaseButton> subEntries = new ArrayList<>();
    private final Module module;
    private boolean isOpen = false;

    public Button(Window window, Module module) {
        super(window.getX() + 2, window.getY() + 2, window.getWidth() - 6, 14);
        this.window = window;
        this.module = module;
    }

    public void processMouseClick(int mouseX, int mouseY, int button) {
        this.updateIsMouseHovered(mouseX, mouseY);
        if (this.isMouseHovered()) {
            if (button == 0) {
                GuiUtils.toggleMod(this.module);
            }

            if (button == 1) {
                this.isOpen = !this.isOpen;
            }
        }
    }

    public void draw(int mouseX, int mouseY) {
        int red = ClickGuiModule.INSTANCE.red.getValue();
        int green = ClickGuiModule.INSTANCE.green.getValue();
        int blue = ClickGuiModule.INSTANCE.blue.getValue();
        int alpha = ClickGuiModule.INSTANCE.alpha.getValue();

        SurfaceBuilder builder = new SurfaceBuilder();
        this.y = this.window.getRenderYButton();
        this.x = this.window.getX() + 2;
        this.updateIsMouseHovered(mouseX, mouseY);
        if (module.isEnabled()) {
            if (ClickGuiModule.INSTANCE.moduleButtons.getValue()) {
                SurfaceHelper.drawRect(getX(), getY(), getWidth(), height - 0.5F, Colors.toRGBA(red, green, blue, alpha));
            }
        } else {
            if (ClickGuiModule.INSTANCE.moduleDisabledButtons.getValue()) {
                SurfaceHelper.drawRect(getX(), getY(), getWidth(), height - 0.5F, Colors.toRGBA(ClickGuiModule.INSTANCE.disabledRed.getValue(),
                        ClickGuiModule.INSTANCE.disabledGreen.getValue(),
                        ClickGuiModule.INSTANCE.disabledBlue.getValue(),
                        ClickGuiModule.INSTANCE.disabledAlpha.getValue())
                );
            }
        }
        if (ClickGuiModule.INSTANCE.customFont.getValue() && ClickGUI.fontRenderer != null) {
           builder.reset();
           builder.task(SurfaceBuilder::enableBlend);
           builder.task(SurfaceBuilder::enableFontRendering);
           builder.fontRenderer(ClickGUI.fontRenderer);
           builder.color(this.getColor());
           builder.text(this.module.getName(), this.getX() + 2 + 1, this.getY() + 3 + 1, true);
           builder.color(this.getColor());
           builder.text(this.module.getName(), this.getX() + 2, this.getY() + 3);
        } else {
           builder.reset();
           builder.task(SurfaceBuilder::enableBlend);
           builder.task(SurfaceBuilder::enableFontRendering);
           builder.fontRenderer(ClickGUI.fontRenderer);
           builder.color(this.getColor());
           builder.text(this.module.getName(), (double) (this.getX() + 2), (double) (this.getY() + 3), true);
        }
    }

    public int getHeight() {
        int i = this.height;
        if (this.isOpen) {
            i += (height + 1) * this.subEntries.size();
        }

        return i;
    }

    public int getColor() {
        return ColorUtils.getColorForGuiEntry(0, this.isMouseHovered(), this.module.isEnabled());
    }

    public String getName() {
        return this.module.getName();
    }

    public boolean isOpen() {
        return this.isOpen;
    }

    public void setOpen(boolean val) {
        this.isOpen = val;
    }

    public Module getModule() {
        return this.module;
    }

    public List getSubEntries() {
        return this.subEntries;
    }
}
