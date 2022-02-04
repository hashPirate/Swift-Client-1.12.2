package me.pignol.swift.client.gui.blowbui.glowclient.clickgui.buttons;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.gui.blowbui.glowclient.clickgui.BaseButton;
import me.pignol.swift.client.gui.blowbui.glowclient.clickgui.ClickGUI;
import me.pignol.swift.client.gui.blowbui.glowclient.clickgui.utils.ColorUtils;
import me.pignol.swift.client.gui.blowbui.glowclient.clickgui.utils.GuiUtils;
import me.pignol.swift.client.gui.blowbui.glowclient.utils.render.Colors;
import me.pignol.swift.client.gui.blowbui.glowclient.utils.render.SurfaceBuilder;
import me.pignol.swift.client.gui.blowbui.glowclient.utils.render.SurfaceHelper;
import me.pignol.swift.client.modules.other.ClickGuiModule;

public class SubButton extends BaseButton {

   private final Value<Boolean> option;

   private final Button parent;

   public SubButton(Button parent, Value<Boolean> option) {
      super(parent.getWindow().getX() + 4, parent.getY() + 4, parent.getWindow().getWidth() - 8, 14);
      this.parent = parent;
      this.window = parent.getWindow();
      this.option = option;
   }

   public void processMouseClick(int mouseX, int mouseY, int button) {
      updateIsMouseHovered(mouseX, mouseY);
      if (isMouseHovered() && button == 0) {
         GuiUtils.toggleSetting(option);
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
      if (ClickGuiModule.INSTANCE.customFont.getValue() && ClickGUI.fontRenderer != null) {
         builder.reset();
         builder.task(SurfaceBuilder::enableBlend);
         builder.task(SurfaceBuilder::enableFontRendering);
         builder.fontRenderer(ClickGUI.fontRenderer);
         builder.color(Colors.WHITE);
         builder.text(option.getName(), getX() + 2 + 1, getY() + 2 + 1, true);
         builder.color(Colors.WHITE);
         builder.text(option.getName(), getX() + 2, getY() + 2);
      } else {
         builder.reset();
         builder.task(SurfaceBuilder::enableBlend);
         builder.task(SurfaceBuilder::enableFontRendering);
         builder.fontRenderer(ClickGUI.fontRenderer);
         builder.color(Colors.WHITE);
         builder.text(option.getName(), (double) (getX() + 2), (double) (getY() + 2), true);
      }
   }

   public int getColor() {
      return ColorUtils.getColorForGuiEntry(3, isMouseHovered(), option.getValue());
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
