package me.pignol.swift.client.gui.blowbui.glowclient.clickgui.buttons;

import me.pignol.swift.api.util.EnumHelper;
import me.pignol.swift.api.util.render.RenderUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.gui.blowbui.glowclient.clickgui.BaseButton;
import me.pignol.swift.client.gui.blowbui.glowclient.clickgui.ClickGUI;
import me.pignol.swift.client.gui.blowbui.glowclient.clickgui.Window;
import me.pignol.swift.client.gui.blowbui.glowclient.clickgui.utils.ColorUtils;
import me.pignol.swift.client.gui.blowbui.glowclient.utils.render.Colors;
import me.pignol.swift.client.gui.blowbui.glowclient.utils.render.SurfaceBuilder;
import me.pignol.swift.client.modules.other.ClickGuiModule;

public class SubMode extends BaseButton {
   private final Button parent;
   private Window window;
   private Value<Enum> option;
   private boolean reset = false;

   public SubMode(Button parent, Value<Enum> option) {
      super(parent.getWindow().getX() + 4, parent.getY() + 4, parent.getWindow().getWidth() - 8, 14);
      this.parent = parent;
      this.window = parent.getWindow();
      this.option = option;
   }

   public void processMouseClick(int mouseX, int mouseY, int button) {
      updateIsMouseHovered(mouseX, mouseY);
      if (isMouseHovered() && parent.isOpen()) {
         if (button == 0) {
            option.setValue(EnumHelper.next(option.getValue()));
         } else if (button == 1) {
            option.setValue(EnumHelper.previous(option.getValue()));
         }
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

      RenderUtil.drawRect(getX(), getY(), getX() + getWidth(), getY() + getHeight() - 0.5F, getColor());
      RenderUtil.drawRect(getX(), getY(), getX() - 1, getY() + getHeight() - 0.5F, Colors.toRGBA(red, green, blue, ClickGuiModule.INSTANCE.lineAlpha.getValue()));
      SurfaceBuilder builder = new SurfaceBuilder();
      if (ClickGuiModule.INSTANCE.customFont.getValue() && ClickGUI.fontRenderer != null) {
         builder.reset()
                 .task(SurfaceBuilder::enableBlend)
                 .task(SurfaceBuilder::enableFontRendering)
                 .fontRenderer(ClickGUI.fontRenderer)
                 .color(Colors.WHITE)
                 .text(option.getName() + ": " + option.getValue(), getX() + 2 + 1, getY() + 2 + 1, true)
                 .color(Colors.WHITE)
                 .text(option.getName() + ": " + option.getValue(), getX() + 2, getY() + 2);
      } else {
         builder
                 .reset()
                 .task(SurfaceBuilder::enableBlend)
                 .task(SurfaceBuilder::enableFontRendering)
                 .fontRenderer(ClickGUI.fontRenderer)
                 .color(Colors.WHITE)
                 .text(option.getName() + ": " + option.getValue(), getX() + 2, getY() + 2, true);
      }
   }

   public int getColor() {
      return ColorUtils.getColorForGuiEntry(2, isMouseHovered(), false);
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
