package me.pignol.swift.client.gui.blowbui.glowclient.clickgui.buttons;

import me.pignol.swift.client.gui.blowbui.glowclient.clickgui.BaseButton;
import me.pignol.swift.client.gui.blowbui.glowclient.clickgui.ClickGUI;
import me.pignol.swift.client.gui.blowbui.glowclient.clickgui.Window;
import me.pignol.swift.client.gui.blowbui.glowclient.clickgui.utils.ColorUtils;
import me.pignol.swift.client.gui.blowbui.glowclient.utils.render.Colors;
import me.pignol.swift.client.gui.blowbui.glowclient.utils.render.SurfaceBuilder;
import me.pignol.swift.client.gui.blowbui.glowclient.utils.render.SurfaceHelper;
import me.pignol.swift.client.modules.other.ClickGuiModule;
import org.lwjgl.input.Keyboard;
public class SubBind extends BaseButton {
   private final Button parent;
   private Window window;
   private boolean accepting = false;

   public SubBind(Button parent) {
      super(parent.getWindow().getX() + 4, parent.getY() + 4, parent.getWindow().getWidth() - 8, 14);
      this.parent = parent;
      this.window = parent.getWindow();
   }

   public void processMouseClick(int mouseX, int mouseY, int button) {
      this.updateIsMouseHovered(mouseX, mouseY);
      if (this.isMouseHovered() && button == 0) {
         this.accepting = true;
      }
   }

   public void processKeyPress(char character, int key) {
      if (this.accepting) {
         if (key != 211 && key != 14 && key != 1) {
            this.parent.getModule().setKey(key);
            this.accepting = false;
         } else {
            this.parent.getModule().setKey(Keyboard.getKeyIndex("NONE"));
            this.accepting = false;
         }
      }
   }

   public void draw(int mouseX, int mouseY) {
      int red = ClickGuiModule.INSTANCE.red.getValue();
      int green = ClickGuiModule.INSTANCE.green.getValue();
      int blue = ClickGuiModule.INSTANCE.blue.getValue();
      int alpha = ClickGuiModule.INSTANCE.alpha.getValue();

      String keyname;
      if (!this.accepting) {
         keyname = "Bind: " + Keyboard.getKeyName(this.parent.getModule().getKey());
      } else {
         keyname = "Press a key...";
      }

      this.y = this.window.getRenderYButton();
      this.x = this.window.getX() + 4;
      this.updateIsMouseHovered(mouseX, mouseY);
      SurfaceHelper.drawRect(this.getX(), this.getY(), this.getWidth(), this.height - 0.5F, this.getColor());
      SurfaceHelper.drawRect(this.getX(), this.getY(), -1, this.height - 0.5F, Colors.toRGBA(red, green, blue, ClickGuiModule.INSTANCE.lineAlpha.getValue()));
      SurfaceBuilder builder = new SurfaceBuilder();
      if (ClickGuiModule.INSTANCE.customFont.getValue() && ClickGUI.fontRenderer != null) {
         builder.reset();
         builder.task(SurfaceBuilder::enableBlend);
         builder.task(SurfaceBuilder::enableFontRendering);
         builder.fontRenderer(ClickGUI.fontRenderer);
         builder.color(Colors.WHITE);
         builder.text(keyname, (double) (this.getX() + 2 + 1), (double) (this.getY() + 2 + 1), true);
         builder.color(Colors.WHITE);
         builder.text(keyname, (double) (this.getX() + 2), (double) (this.getY() + 2));
      } else {
         builder.reset();
         builder.task(SurfaceBuilder::enableBlend);
         builder.task(SurfaceBuilder::enableFontRendering);
         builder.fontRenderer(ClickGUI.fontRenderer);
         builder.color(Colors.WHITE);
         builder.text(keyname, (double) (this.getX() + 2), (double) (this.getY() + 2), true);
      }
   }

   public int getColor() {
      return ColorUtils.getColorForGuiEntry(3, isMouseHovered(), true);
   }

   public boolean shouldRender() {
      return this.parent.isOpen() && this.parent.shouldRender();
   }

   public String getName() {
      return "Bind: " + this.parent.getModule().getKey();
   }

   public Button getParent() {
      return this.parent;
   }
}
