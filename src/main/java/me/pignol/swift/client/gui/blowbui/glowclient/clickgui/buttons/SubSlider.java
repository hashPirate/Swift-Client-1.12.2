package me.pignol.swift.client.gui.blowbui.glowclient.clickgui.buttons;

import me.pignol.swift.api.util.render.RenderUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.gui.blowbui.glowclient.clickgui.BaseButton;
import me.pignol.swift.client.gui.blowbui.glowclient.clickgui.ClickGUI;
import me.pignol.swift.client.gui.blowbui.glowclient.clickgui.utils.ColorUtils;
import me.pignol.swift.client.gui.blowbui.glowclient.clickgui.utils.GuiUtils;
import me.pignol.swift.client.gui.blowbui.glowclient.utils.render.Colors;
import me.pignol.swift.client.gui.blowbui.glowclient.utils.render.SurfaceBuilder;
import me.pignol.swift.client.modules.other.ClickGuiModule;

public class SubSlider extends BaseButton {
   private final Button parent;
   private Value<Number> option;
   private float value;
   private int currentWidth;
   private boolean dragging = false;

   public SubSlider(Button parent, Value<Number> option) {
      super(parent.getWindow().getX() + 4, parent.getY() + 4, parent.getWindow().getWidth() - 8, 14);
      this.parent = parent;
      this.window = parent.getWindow();
      if (option != null) {
         this.value = option.getValue().floatValue();
      }
      this.option = option;
   }

   public void processMouseClick(int mouseX, int mouseY, int button) {
      updateIsMouseHovered(mouseX, mouseY);
      if (isMouseHovered() && button == 0) {
         dragging = true;
      }
   }

   public void processMouseRelease(int mouseX, int mouseY, int button) {
      updateIsMouseHovered(mouseX, mouseY);
      if (dragging && button == 0) {
         dragging = false;
         getWidthFromValue();
      }
   }

   public void draw(int mouseX, int mouseY) {
      if (dragging) {
         currentWidth = mouseX - getX();
         if (currentWidth < 0) {
            currentWidth = 0;
         } else if (currentWidth > width) {
            currentWidth = width;
         }
         updateValueFromWidth();
      }
      getWidthFromValue();

      int red = ClickGuiModule.INSTANCE.red.getValue();
      int green = ClickGuiModule.INSTANCE.green.getValue();
      int blue = ClickGuiModule.INSTANCE.blue.getValue();
      int alpha = ClickGuiModule.INSTANCE.alpha.getValue();
      y = window.getRenderYButton();
      x = window.getX() + 4;
      updateIsMouseHovered(mouseX, mouseY);

      RenderUtil.drawRect(getX(), getY(), getX() - 1, getY() + getHeight() - 0.5F, Colors.toRGBA(red, green, blue, ClickGuiModule.INSTANCE.lineAlpha.getValue()));
      RenderUtil.drawRect(getX(), getY(), getX() + getWidth(), getY() + getHeight() - 0.5F, Colors.toRGBA(0, 0, 0, 0));
      RenderUtil.drawRect(getX(), getY(), getX() + currentWidth, getY() + getHeight() - 0.5F, getColor());
      SurfaceBuilder builder = new SurfaceBuilder();
      if (ClickGuiModule.INSTANCE.customFont.getValue() && ClickGUI.fontRenderer != null) {
         builder.reset();
         builder.task(SurfaceBuilder::enableBlend);
         builder.task(SurfaceBuilder::enableFontRendering);
         builder.fontRenderer(ClickGUI.fontRenderer);
         builder.color(Colors.WHITE);
         builder.text(option.getName() + ": " + GuiUtils.roundSlider(value), getX() + 2 + 1, getY() + 2 + 1, true);
         builder.color(Colors.WHITE);
         builder.text(option.getName() + ": " + GuiUtils.roundSlider(value), getX() + 2, getY() + 2);
      } else {
         builder.reset();
         builder.task(SurfaceBuilder::enableBlend);
         builder.task(SurfaceBuilder::enableFontRendering);
         builder.fontRenderer(ClickGUI.fontRenderer);
         builder.color(Colors.WHITE);
         builder.text(option.getName() + ": " + GuiUtils.roundSlider(value), getX() + 2, getY() + 2, true);
      }
   }

   public int getColor() {
      return ColorUtils.getColorForGuiEntry(2, isMouseHovered(), false);
   }

   public boolean shouldRender() {
      return parent.isOpen() && parent.shouldRender() && option.isVisible();
   }

   public void openGui() {
      if (option != null) {
         value = option.getValue().floatValue();
      }

      getWidthFromValue();
   }

   public String getName() {
      return option.getName();
   }

   protected void getWidthFromValue() {
      float val = value;
      val -= getMin();
      val /= getMax() - getMin();
      val *= width;
      currentWidth = (int) GuiUtils.reCheckSliderRange(val, 0.0F, width);
   }

   protected void updateValueFromWidth() {
      float val = (float)currentWidth / width;
      val *= getMax() - getMin();
      val += getMin();
      val = GuiUtils.roundSliderStep(val, getStep());
      val = GuiUtils.reCheckSliderRange(val, getMin(), getMax());
      value = val;
      Double roundedValue = GuiUtils.roundSliderForConfig(val);
      if (option.getValue() instanceof Long) {
         option.setValue(roundedValue.longValue());
      } else if (option.getValue() instanceof Integer) {
         option.setValue(roundedValue.intValue());
      } else if (option.getValue() instanceof Float) {
         option.setValue(roundedValue.floatValue());
      } else if (option.getValue() instanceof Double) {
         option.setValue(roundedValue);
      }
   }

   public void setDragging(boolean dragging) {
      this.dragging = dragging;
   }

   public Button getParent() {
      return parent;
   }

   public float getMax() {
      Number inc = option.getMax();
      return inc == null ? 100.0F : inc.floatValue();
   }

   public float getMin() {
      Number inc = option.getMin();
      return inc == null ? 0.0F : inc.floatValue();
   }

   public float getStep() {
      Number inc = 0.01F;
      if (option.getValue() instanceof Integer) {
         inc = null;
      }
      if (option.getInc() != null) {
         inc = option.getInc();
      }
      return inc == null ? 1.0F : inc.floatValue();
   }

}
