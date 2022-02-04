package me.pignol.swift.client.gui.blowbui.glowclient.clickgui.utils;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.modules.Module;

public class GuiUtils {

   public static void toggleMod(Module module) {
      module.toggle();
   }

   public static void toggleSetting(Value<Boolean> setting) {
      setting.setValue(!setting.getValue());
   }

   public static double roundSliderForConfig(double val) {
      return Double.parseDouble(String.format("%.2f", val));
   }

   public static String roundSlider(float f) {
      return String.format("%.2f", f);
   }

   public static float roundSliderStep(float input, float step) {
      return (float)Math.round(input / step) * step;
   }

   public static float reCheckSliderRange(float value, float min, float max) {
      return Math.min(Math.max(value, min), max);
   }
}
