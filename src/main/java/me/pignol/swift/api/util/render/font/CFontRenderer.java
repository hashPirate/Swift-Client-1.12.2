package me.pignol.swift.api.util.render.font;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class CFontRenderer extends CFont  {

    protected CFont.CharData[] boldChars = new CFont.CharData[256];
    protected CFont.CharData[] italicChars = new CFont.CharData[256];
    protected CFont.CharData[] boldItalicChars = new CFont.CharData[256];

    private final String colorcodeIdentifiers = "0123456789abcdefklmnor";
    private final int[] colorCode = new int[32];

    public CFontRenderer(Font font, boolean antiAlias, boolean fractionalMetrics) {
        super(font, antiAlias, fractionalMetrics);
        setupMinecraftColorcodes();
        setupBoldItalicIDs();
    }

    public float drawStringWithShadow(String text, float x, float y, int color) {
        float shadowWidth = drawString(text, x + 1.0F, y + 1.0F, color, true);
        return Math.max(shadowWidth, drawString(text, x, y, color, false));
    }

    public float drawString(String text, float x, float y, int color) {
        return drawString(text, x, y, color, false);
    }

    public float drawCenteredString(String text, float x, float y, int color) {
        return drawString(text, x - getStringWidth(text) / 2F, y, color);
    }

    public float drawCenteredStringWithShadow(String text, float x, float y, int color) {
        float shadowWidth =
                drawString(text, x - getStringWidth(text) / 2F + 1.0F, y + 1.0F, color, true);
        return drawString(text, x - getStringWidth(text) / 2F, y, color);
    }

    public float drawString(String text, float x, float y, int color, boolean shadow) {
        x -= 1;

        if (color == 553648127) {
            color = 16777215;
        }

        if ((color & 0xFC000000) == 0) {
            color |= -16777216;
        }

        if (shadow) {
            color = (color & 0xFCFCFC) >> 2 | color & 0xFF000000;
        }

        CFont.CharData[] currentData = this.charData;
        boolean bold = false;
        boolean italic = false;
        x *= 2.0F;
        y = (y - 3.0F) * 2.0F;

        final float alpha = (color >> 24 & 0xFF) / 255.0f;

        GlStateManager.color((color >> 16 & 0xFF) / 255.0F, (color >> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, alpha);
        GL11.glPushMatrix();
        GlStateManager.scale(0.5F, 0.5F, 0.5F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableTexture2D();
        GlStateManager.bindTexture(tex.getGlTextureId());
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex.getGlTextureId());
        GL11.glBegin(GL11.GL_TRIANGLES);
        final int size = text.length();
        for (int i = 0; i < size; i++) {
            char character = text.charAt(i);

            if (character == '\u00a7') {
                int colorIndex = colorcodeIdentifiers.indexOf(text.charAt(i + 1));

                if (colorIndex < 16) {
                    bold = false;
                    italic = false;
                    GlStateManager.bindTexture(tex.getGlTextureId());
                    // GL11.glBindTexture(GL11.GL_TEXTURE_2D,
                    // tex.getGlTextureId());
                    currentData = this.charData;

                    if (colorIndex < 0) {
                        colorIndex = 15;
                    }

                    if (shadow) {
                        colorIndex += 16;
                    }

                    int colorcode = this.colorCode[colorIndex];
                    GlStateManager.color(
                            (colorcode >> 16 & 0xFF) / 255.0F,
                            (colorcode >> 8 & 0xFF) / 255.0F,
                            (colorcode & 0xFF) / 255.0F,
                            alpha
                    );
                } else if (colorIndex == 17) {
                    bold = true;

                    if (italic) {
                        GlStateManager.bindTexture(texItalicBold.getGlTextureId());
                        // GL11.glBindTexture(GL11.GL_TEXTURE_2D,
                        // texItalicBold.getGlTextureId());
                        currentData = this.boldItalicChars;
                    } else {
                        GlStateManager.bindTexture(texBold.getGlTextureId());
                        // GL11.glBindTexture(GL11.GL_TEXTURE_2D,
                        // texBold.getGlTextureId());
                        currentData = this.boldChars;
                    }
                } else if (colorIndex == 20) {
                    italic = true;

                    if (bold) {
                        GlStateManager.bindTexture(texItalicBold.getGlTextureId());
                        // GL11.glBindTexture(GL11.GL_TEXTURE_2D,
                        // texItalicBold.getGlTextureId());
                        currentData = this.boldItalicChars;
                    } else {
                        GlStateManager.bindTexture(texItalic.getGlTextureId());
                        // GL11.glBindTexture(GL11.GL_TEXTURE_2D,
                        // texItalic.getGlTextureId());
                        currentData = this.italicChars;
                    }
                } else if (colorIndex == 21) {
                    bold = false;
                    italic = false;
                    GlStateManager.color(
                            (color >> 16 & 0xFF) / 255.0F,
                            (color >> 8 & 0xFF) / 255.0F,
                            (color & 0xFF) / 255.0F,
                            alpha
                    );
                    GlStateManager.bindTexture(tex.getGlTextureId());
                    currentData = this.charData;
                }

                i++;
            } else if (character < currentData.length) {
                drawChar(currentData, character, x, y);
                x += currentData[character].width - 8;
            }
        }
        GL11.glEnd();

        GL11.glPopMatrix();

        return x / 2.0F;
    }

    @Override
    public int getStringWidth(String text) {
        if (text == null) {
            return 0;
        }

        int width = 0;

        final int size = text.length();
        for (int i = 0; i < size; ++i) {
            final char character = text.charAt(i);

            if (character == '\u00a7') {
                i++;
            } else if (character < charData.length) {
                width += charData[character].width - 8;
            }
        }

        return width / 2;
    }

    public void setFont(Font font) {
        super.setFont(font);
        setupBoldItalicIDs();
    }

    public void setAntiAlias(boolean antiAlias) {
        super.setAntiAlias(antiAlias);
        setupBoldItalicIDs();
    }

    public void setFractionalMetrics(boolean fractionalMetrics) {
        super.setFractionalMetrics(fractionalMetrics);
        setupBoldItalicIDs();
    }

    protected DynamicTexture texBold;
    protected DynamicTexture texItalic;
    protected DynamicTexture texItalicBold;

    private void setupBoldItalicIDs() {
        texBold = setupTexture(this.font.deriveFont(1), this.antiAlias, this.fractionalMetrics, this.boldChars);
        texItalic = setupTexture(this.font.deriveFont(2), this.antiAlias, this.fractionalMetrics, this.italicChars);
        texItalicBold = setupTexture(this.font.deriveFont(3), this.antiAlias, this.fractionalMetrics, this.boldItalicChars);
    }

    private void setupMinecraftColorcodes() {
        for (int index = 0; index < 32; index++) {
            int noClue = (index >> 3 & 0x1) * 85;
            int red = (index >> 2 & 0x1) * 170 + noClue;
            int green = (index >> 1 & 0x1) * 170 + noClue;
            int blue = (index >> 0 & 0x1) * 170 + noClue;

            if (index == 6) {
                red += 85;
            }

            if (index >= 16) {
                red /= 4;
                green /= 4;
                blue /= 4;
            }

            this.colorCode[index] = ((red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF);
        }
    }

}
