package me.pignol.swift.client.gui.blowbui.glowclient.clickgui;

public abstract class BaseButton {
   private static BaseButton INSTANCE;

   protected Window window;
   protected final int width;
   protected final int height;
   protected int x;
   protected int y;
   boolean isHoveredCached;

   public BaseButton(int x, int y, int width, int height) {
      INSTANCE = this;
      this.isHoveredCached = false;
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
   }

   protected boolean isMouseHovered() {
      return this.isHoveredCached;
   }

   protected void updateIsMouseHovered(int mouseX, int mouseY) {
      int x = this.getX();
      int y = this.getY();
      int maxX = x + this.width;
      int maxY = y + this.height;
      this.isHoveredCached = x <= mouseX && mouseX <= maxX && y <= mouseY && mouseY <= maxY;
   }

   public void processMouseClick(int mouseX, int mouseY, int button) {
      this.updateIsMouseHovered(mouseX, mouseY);
   }

   public void processMouseRelease(int mouseX, int mouseY, int button) {
      this.updateIsMouseHovered(mouseX, mouseY);
   }

   public void processKeyPress(char character, int key) {
   }

   public int getX() {
      return this.x;
   }

   public void setX(int x) {
      this.x = x;
   }

   public int getY() {
      return this.y;
   }

   public void setY(int y) {
      this.y = y;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public boolean shouldRender() {
      return this.window.isOpen;
   }

   public void openGui() {
   }

   public Window getWindow() {
      return this.window;
   }

   public boolean isOpen() {
      return false;
   }

   public void setOpen(boolean open) {
   }

   public abstract String getName();

   public abstract void draw(int mouseX, int mouseY);

   public abstract int getColor();

   public static BaseButton getInstance() {
      return INSTANCE;
   }
}
