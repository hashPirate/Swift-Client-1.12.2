package me.pignol.swift.client.gui.blowbui.glowclient.clickgui;

import me.pignol.swift.api.util.EnumHelper;
import me.pignol.swift.api.util.render.font.CFontRenderer;
import me.pignol.swift.client.gui.blowbui.glowclient.clickgui.buttons.SubSlider;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.other.ClickGuiModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClickGUI extends GuiScreen {

   public static CFontRenderer fontRenderer = new CFontRenderer(new Font("Verdana", Font.PLAIN, 18), true, true);

   private static ClickGUI INSTANCE;

   private final Minecraft mc;

   private List<Window> windows = new ArrayList<>();

   public ClickGUI() {
      INSTANCE = this;
      this.mc = Minecraft.getMinecraft();
   }

   @Override
   public boolean doesGuiPauseGame() {
      return false;
   }

   public void drawScreen(int x, int y, float ticks) {
      windows.forEach(window -> window.draw(x, y));
   }

   public void mouseClicked(int x, int y, int b) throws IOException {
      windows.forEach(window -> window.processMouseClick(x, y, b));
      super.mouseClicked(x, y, b);
   }

   public void mouseReleased(int x, int y, int state) {
      windows.forEach(window -> window.processMouseRelease(x, y, state));
      super.mouseReleased(x, y, state);
   }

   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      int dWheel = Mouse.getDWheel();
      if (dWheel < 0) {
         for (Window component : windows) {
            component.setY(component.getY() - 10);
            for (BaseButton button : component.buttons) {
               button.setY(button.y - 10);
            }
         }
      } else if (dWheel > 0) {
         for (Window component : windows) {
            component.setY(component.getY() + 10);
            for (BaseButton button : component.buttons) {
               button.setY(button.y - 10);
            }
         }
      }
      /*
      int dWheel = MathHelper.clamp(Mouse.getEventDWheel(), -1, 1);
      if (dWheel != 0) {
         dWheel *= -1;
         int x = Mouse.getEventX() * this.width / mc.displayWidth;
         int y = this.height - Mouse.getEventY() * this.height / mc.displayHeight - 1;
         for (Window window : windows) {
            window.handleScroll(dWheel, x, y);
         }
      }*/
   }

   protected void keyTyped(char eventChar, int eventKey) {
      windows.forEach(window -> window.processKeyPress(eventChar, eventKey));

      if (eventKey == 1) {
         mc.displayGuiScreen(null);
         if (mc.currentScreen == null) {
            mc.setIngameFocus();
         }
      }
   }

   @Override
   public void onGuiClosed() {
      ClickGuiModule.INSTANCE.setEnabled(false);
      for (Window button : windows) {
         for (BaseButton comp : button.buttons) {
            if (ClickGuiModule.INSTANCE.closeSettings.getValue()) {
               comp.setOpen(false);
            }
            if (comp instanceof SubSlider) {
               ((SubSlider) comp).setDragging(false);
            }
         }
      }
      super.onGuiClosed();
   }

   public void initWindows() {
      int xOffset = 2;
      for (Category category : Category.VALUES) {
         String name = EnumHelper.getCapitalizedName(category.name());
         Window window = new Window(xOffset, 2, name, category);
         windows.add(window);
         window.init(category);
         xOffset += 115;
      }
   }

   public List<Window> getWindows() {
      return windows;
   }

   public static ClickGUI getInstance() {
      return INSTANCE;
   }

}
