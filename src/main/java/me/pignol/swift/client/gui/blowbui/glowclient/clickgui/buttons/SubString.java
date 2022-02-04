package me.pignol.swift.client.gui.blowbui.glowclient.clickgui.buttons;

import me.pignol.swift.api.util.text.IdleUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.gui.blowbui.glowclient.clickgui.BaseButton;
import me.pignol.swift.client.gui.blowbui.glowclient.clickgui.ClickGUI;
import me.pignol.swift.client.gui.blowbui.glowclient.clickgui.utils.ColorUtils;
import me.pignol.swift.client.gui.blowbui.glowclient.utils.render.Colors;
import me.pignol.swift.client.gui.blowbui.glowclient.utils.render.SurfaceBuilder;
import me.pignol.swift.client.gui.blowbui.glowclient.utils.render.SurfaceHelper;
import me.pignol.swift.client.modules.other.ClickGuiModule;
import net.minecraft.util.ChatAllowedCharacters;

public class SubString extends BaseButton {

    private final Value<String> option;

    private final Button parent;

    private CurrentString currentString = new CurrentString("");
    private boolean isListening;

    public SubString(Button parent, Value<String> option) {
        super(parent.getWindow().getX() + 4, parent.getY() + 4, parent.getWindow().getWidth() - 8, 14);
        this.parent = parent;
        this.window = parent.getWindow();
        this.option = option;
    }

    public void processMouseClick(int mouseX, int mouseY, int button) {
        updateIsMouseHovered(mouseX, mouseY);
        if (isMouseHovered() && button == 0) {
            isListening = !isListening;
        }
    }

    public void draw(int mouseX, int mouseY) {
        int red = ClickGuiModule.INSTANCE.red.getValue();
        int green = ClickGuiModule.INSTANCE.green.getValue();
        int blue = ClickGuiModule.INSTANCE.blue.getValue();
        int alpha = ClickGuiModule.INSTANCE.alpha.getValue();

        y = window.getRenderYButton();
        x = window.getX() + 4;
        updateIsMouseHovered(mouseX, mouseY);
        SurfaceHelper.drawRect(getX(), getY(), getWidth(), height - 0.5F, getColor());
        SurfaceHelper.drawRect(getX(), getY(), -1, height - 0.5F, Colors.toRGBA(red, green, blue, ClickGuiModule.INSTANCE.lineAlpha.getValue()));
        SurfaceBuilder builder = new SurfaceBuilder();
        String text = isListening ? currentString.getString() + IdleUtil.getDots() : option.getName() + ": " + option.getValue();
        if (ClickGuiModule.INSTANCE.customFont.getValue() && ClickGUI.fontRenderer != null) {
            builder.reset();
            builder.task(SurfaceBuilder::enableBlend);
            builder.task(SurfaceBuilder::enableFontRendering);
            builder.fontRenderer(ClickGUI.fontRenderer);
            builder.color(Colors.WHITE);
            builder.text(text, getX() + 2 + 1, getY() + 2 + 1, true);
            builder.color(Colors.WHITE);
            builder.text(text, getX() + 2, getY() + 2);
        } else {
            builder.reset();
            builder.task(SurfaceBuilder::enableBlend);
            builder.task(SurfaceBuilder::enableFontRendering);
            builder.fontRenderer(ClickGUI.fontRenderer);
            builder.color(Colors.WHITE);
            builder.text(text, (double) (getX() + 2), (double) (getY() + 2), true);
        }
    }

    @Override
    public void processKeyPress(char typedChar, int keyCode) {
        if(isListening) {
            switch (keyCode) {
                case 1:
                    break;
                case 28:
                    enterString();
                    break;
                case 14:
                    setString(removeLastChar(currentString.getString()));
                    break;
                default:
                    if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                        setString(currentString.getString() + typedChar);
                    }
            }
        }
    }

    public boolean getState() {
        return !isListening;
    }

    private void enterString() {
        if (currentString.getString().isEmpty()) {
            option.setValue(option.getDefaultValue());
        } else {
            option.setValue(currentString.getString());
        }
        setString("");
        isListening = !isListening;
    }

    public void setString(String newString) {
        this.currentString = new CurrentString(newString);
    }

    public static String removeLastChar(String str) {
        String output = "";
        if (str != null && str.length() > 0) {
            output = str.substring(0, str.length() - 1);
        }
        return output;
    }

    //TODO: WTF IS THIS
    public static class CurrentString {
        private final String string;

        public CurrentString(String string) {
            this.string = string;
        }

        public String getString() {
            return this.string;
        }
    }

    public int getColor() {
        return ColorUtils.getColorForGuiEntry(3, isMouseHovered(), false);
    }

    public boolean shouldRender() {
        return parent.isOpen() && parent.shouldRender() && option.isVisible();
    }

    public String getName() {
        return option.getName();
    }

    public Button getParent() {
        return parent;
    }

}
