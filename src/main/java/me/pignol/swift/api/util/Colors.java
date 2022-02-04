package me.pignol.swift.api.util;

public class Colors {
   public static final int WHITE = toRGBA(255, 255, 255, 255);
   public static final int BLACK = toRGBA(0, 0, 0, 255);
   public static final int RED = toRGBA(255, 0, 0, 255);
   public static final int GREEN = toRGBA(0, 255, 0, 255);
   public static final int BLUE = toRGBA(0, 104, 255, 255);
   public static final int ORANGE = toRGBA(255, 128, 0, 255);
   public static final int PURPLE = toRGBA(163, 73, 163, 255);
   public static final int GRAY = toRGBA(127, 127, 127, 255);
   public static final int YELLOW = toRGBA(255, 255, 0, 255);
   public static final int LIGHT_BLUE = toRGBA(0, 180, 255, 255);
   public static final int AQUA = toRGBA(0, 255, 225, 255);
   public static final int SHADOW = toRGBA(20, 20, 20, 230);
   public static final int HUDGRAY = toRGBA(255, 255, 255, 255);

   public static int toRGBA(int r, int g, int b, int a) {
      return (r << 16) + (g << 8) + (b) + (a << 24);
   }

   public static int toRGBA(int r, int g, int b) {
      return toRGBA(r, g, b, 255);
   }

   public static int toRGBA(float r, float g, float b, float a) {
      return toRGBA((int)(r * 255.0F), (int)(g * 255.0F), (int)(b * 255.0F), (int)(a * 255.0F));
   }

   public static int toRGBA(float[] colors) {
      if (colors.length != 4) {
         throw new IllegalArgumentException("colors[] must have a length of 4!");
      } else {
         return toRGBA(colors[0], colors[1], colors[2], colors[3]);
      }
   }

   public static int toRGBA(double[] colors) {
      if (colors.length != 4) {
         throw new IllegalArgumentException("colors[] must have a length of 4!");
      } else {
         return toRGBA((float)colors[0], (float)colors[1], (float)colors[2], (float)colors[3]);
      }
   }

   public static int[] toRGBAArray(int colorBuffer) {
      return new int[]{colorBuffer >> 16 & 255, colorBuffer >> 8 & 255, colorBuffer & 255, colorBuffer >> 24 & 255};
   }

   public class Color {
      private final int color;

      public int getAsBuffer() {
         return this.color;
      }

      private Color(int color) {
         this.color = color;
      }

      public int getRed() {
         return this.color >> 16 & 255;
      }

      public int getGreen() {
         return this.color >> 8 & 255;
      }

      public int getBlue() {
         return this.color & 255;
      }

      public int getAlpha() {
         return this.color >> 24 & 255;
      }

      public boolean equals(Object obj) {
         return this == obj || obj instanceof Colors.Color && this.color == ((Colors.Color)obj).color;
      }

      public int hashCode() {
         return Integer.hashCode(this.color);
      }
   }

}
