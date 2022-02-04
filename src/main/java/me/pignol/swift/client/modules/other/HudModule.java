package me.pignol.swift.client.modules.other;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.pignol.swift.Swift;
import me.pignol.swift.api.util.*;
import me.pignol.swift.api.util.objects.ModuleList;
import me.pignol.swift.api.util.objects.StopWatch;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.Render2DEvent;
import me.pignol.swift.client.managers.*;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import me.pignol.swift.client.modules.misc.TotemPopCounterModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.Comparator;

public class HudModule extends Module {

    public static HudModule INSTANCE = new HudModule();

    public final Value<Integer> alpha = new Value<>("Alpha", 255, 0, 255);
    public final Value<Integer> ySpace = new Value<>("YSpace", 10, 0, 20);
    public final Value<Integer> sortDelay = new Value<>("SortDelay", 1000, 0, 10000);
    public final Value<Boolean> watermark = new Value<>("Watermark", true);
    public final Value<String> watermarkText = new Value<>("WatermarkText", Swift.VERSION);
    private final Value<Integer> watermarkX = new Value<>("WatermarkX", 2, 0, 100, v -> watermark.getValue());
    private final Value<Boolean> watermarkOffset = new Value<>("WatermarkOffset", false, v -> watermark.getValue());
    private final Value<Boolean> watermark2 = new Value<>("Watermark2", true);
    private final Value<Integer> watermark2y = new Value<>("Watermark2Y", 100, 0, 600, v -> watermark2.getValue());
    private final Value<Boolean> arraylist = new Value<>("Arraylist", true);
    private final Value<Boolean> retardMode = new Value<>("RetardMode", true, v -> arraylist.getValue());
    private final Value<Boolean> ping = new Value<>("Ping", true);
    private final Value<Boolean> coords = new Value<>("Coords", true);
    private final Value<Boolean> direction = new Value<>("Direction", true);
    private final Value<Boolean> netherCoords = new Value<>("NetherCoords", true);
    private final Value<Boolean> speed = new Value<>("Speed", true);
    private final Value<Boolean> armor = new Value<>("Armor", true);
    private final Value<Boolean> potionEffects = new Value<>("PotionEffects", true);
	private final Value<Boolean> potionEffectsFag = new Value<>("PotionEffectsWave", true);
    private final Value<Boolean> lag = new Value<>("Lag", true);
    private final Value<Boolean> tps = new Value<>("TPS", true);
    private final Value<Boolean> tpsAvg = new Value<>("TPS-Average", true);
    private final Value<Boolean> fps = new Value<>("FPS", true);
    public final Value<Boolean> hotbarKeys = new Value<>("HotbarKeys", true);
    private final Value<Rendering> rendering = new Value<>("Rendering", Rendering.UP);
    public final Value<Boolean> hideEffects = new Value<>("HideEffects", true);
    private final Value<Boolean> textRadar = new Value<>("TextRadar", true);
    private final Value<Integer> textRadarY = new Value<>("TextRadarY", 30, 0, 500);
    public final Value<Boolean> welcomer = new Value<>("Welcomer", true);
    public final Value<String> welcomerText = new Value<>("WelcomerText", "Hello %s :^)");
    public final Value<Boolean> totems = new Value<>("Totems", true);
    private final Value<Integer> totemX = new Value<>("TotemX", 2, -5, 5);
    public final Value<Boolean> capes = new Value<>("Capes", true);

    private final StopWatch sortTimer = new StopWatch();

    private static final ItemStack TOTEM = new ItemStack(Items.TOTEM_OF_UNDYING);

    private boolean needsSort;

    public ModuleList modules;

    public HudModule() {
        super("HUD", Category.OTHER, true);
        setDrawn(false);
    }

    public void setupModules() {
        modules = new ModuleList(ModuleManager.getInstance().getModules());
    }

    public void sortModules() {
        modules.sort(Comparator.comparing(mod -> -FontManager.getInstance().getStringWidth(mod.getDisplayName() + (mod.getSuffix().length() == 0 ? "" : (" \u00a77[\u00a7f" + mod.getSuffix() + "\u00a77]")))));
    }

    public static int changeAlpha(int origColor, final int userInputedAlpha) {
        origColor = origColor & 0x00FFFFFF;
        return (userInputedAlpha << 24) | origColor;
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent event) {
        final ScaledResolution resolution = BetterScaledResolution.getInstance();
        final int color = changeAlpha(ColorsModule.INSTANCE.getColor(), alpha.getValue());

        if (needsSort && sortTimer.passed(sortDelay.getValue())) {
            sortModules();
            sortTimer.reset();
        }
		
		int count = 10;
		int index = 0;

        if (watermark.getValue()) {
			index++;
            FontManager.getInstance().drawStringWithShadow(watermarkText.getValue(), watermarkX.getValue(), 2 + (watermarkOffset.getValue() ? 10 : 0), color(index, count));
        }

        if (watermark2.getValue()) {
            FontManager.getInstance().drawStringWithShadow("trollgod.cc", 2, watermark2y.getValue(), color);
        }

        if (welcomer.getValue()) {
            index++;
            String welcomerString = String.format(welcomerText.getValue(), mc.player.getName());
            FontManager.getInstance().drawStringWithShadow(welcomerString, (resolution.getScaledWidth() / 2F) - (FontManager.getInstance().getStringWidth(welcomerString) / 2F) + 2, 2, color(index, count));
        }

        if (lag.getValue()) {
            if (ServerManager.getInstance().isServerNotResponding()) {
                String lagString = "Server hasn't responded in " + String.format("%.2f", (ServerManager.getInstance().getTimer().getTime() / 1000f)) + "s";
                FontManager.getInstance().drawStringWithShadow(lagString, (resolution.getScaledWidth() / 2F) - (FontManager.getInstance().getStringWidth(lagString) / 2F) + 2, welcomer.getValue() ? 12 : 2, color(index, count));
            }
        }

        boolean renderingUp = rendering.getValue() == Rendering.UP;
        boolean chatOpened = mc.ingameGUI.getChatGUI().getChatOpen();
        if (arraylist.getValue()) {
            int offset = renderingUp ? 2 : resolution.getScaledHeight() - (chatOpened ? 24 : 10);
            for (int i = 0, modulesSize = modules.size; i < modulesSize; i++) {
                final Module module = modules.array[i];
                if ((module.isEnabled()) && module.isDrawn()) {
                    String suffix = (module.getSuffix().length() == 0 ? "" : (" \u00a77[\u00a7f" + module.getSuffix() + "\u00a77]"));
                    String nameAndLabel = module.getDisplayName() + suffix;
                    FontManager.getInstance().drawStringWithShadow(nameAndLabel, resolution.getScaledWidth() - FontManager.getInstance().getStringWidth(nameAndLabel) - 2, offset, color(i, modulesSize));
					offset += (renderingUp ? 10 : -10);
                }
            }
        }

        if (armor.getValue()) {
            final int width = resolution.getScaledWidth() >> 1;
            final int height = resolution.getScaledHeight() - 55 - (mc.player.isInWater() && mc.playerController.gameIsSurvivalOrAdventure() ? 10 : 0);
            GlStateManager.enableTexture2D();
            for (int i = 0; i < 4; ++i) {
                final ItemStack is = mc.player.inventory.armorInventory.get(i);
                if (is.isEmpty()) continue;
                final int x = width - 90 + (9 - i - 1) * 20 + 2;
                if (armor.getValue()) {
                    GlStateManager.enableDepth();
                    mc.getRenderItem().zLevel = 200.0f;
                    mc.getRenderItem().renderItemAndEffectIntoGUI(is, x, height);
                    mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, is, x, height, "");
                    mc.getRenderItem().zLevel = 0.0f;
                    GlStateManager.enableTexture2D();
                    GlStateManager.disableLighting();
                    GlStateManager.disableDepth();
                }
                final int dmg = (int) ItemUtil.getDamageInPercent(is);
                final String dmgString = String.valueOf(dmg);
                FontManager.getInstance().drawStringWithShadow(dmgString, x + 8 - (FontManager.getInstance().getStringWidth(dmgString) >> 1), height - 8, changeAlpha(ColorUtil.getDurabilityColor(is), alpha.getValue()));
            }
            GlStateManager.enableDepth();
            GlStateManager.disableLighting();
        }

        int offset = renderingUp ? (chatOpened ? 24 : 10) : 2;

        if (potionEffects.getValue()) {
            int size = mc.player.getActivePotionEffects().size();
            int i = 0;
            for (PotionEffect effect : mc.player.getActivePotionEffects()) {
                i++;
                int amplifier = effect.getAmplifier();
                String potionString = I18n.format(effect.getEffectName()) + (amplifier > 0 ? (" " + (amplifier + 1) + "") : "") + ": " + ChatFormatting.WHITE + Potion.getPotionDurationString(effect, 1);
				boolean fag = false;
				if (potionEffectsFag.getValue()) {
					index++;
					fag = true;
				}
				int potionColor = fag ? color(i, size, false) : effect.getPotion().getLiquidColor();
                FontManager.getInstance().drawStringWithShadow(potionString, resolution.getScaledWidth() - FontManager.getInstance().getStringWidth(potionString) - 2, renderingUp ? resolution.getScaledHeight() - offset : offset, changeAlpha(potionColor, alpha.getValue()));
                offset += ySpace.getValue();
            }
        }

        if (speed.getValue()) {
            index++;
            String speedString = "Speed: \u00a7f" + String.format("%.2f", SpeedManager.getInstance().getSpeedKpH()) + "km/h";
            FontManager.getInstance().drawStringWithShadow(speedString, resolution.getScaledWidth() - FontManager.getInstance().getStringWidth(speedString) - 2, renderingUp ? resolution.getScaledHeight() - offset : offset, color(index, count));
            offset += ySpace.getValue();
        }

        if (tps.getValue()) {
            index++;
            String tpsString = "TPS: \u00a7f" + String.format("%.2f", ServerManager.getInstance().getTPS()) + (tpsAvg.getValue() ? (" \u00a77(\u00a7f" + String.format("%.2f", ServerManager.getInstance().getAverageTPS()) + "\u00a77)") : "");
            FontManager.getInstance().drawStringWithShadow(tpsString, resolution.getScaledWidth() - FontManager.getInstance().getStringWidth(tpsString) - 2, renderingUp ? resolution.getScaledHeight() - offset : offset, color(index, count));
            offset += ySpace.getValue();
        }

        if (ping.getValue()) {
            index++;
            String pingString = "Ping: \u00a7f" + EntityUtil.getPing(mc.player) + "ms";
            FontManager.getInstance().drawStringWithShadow(pingString, resolution.getScaledWidth() - FontManager.getInstance().getStringWidth(pingString) - 2, renderingUp ? resolution.getScaledHeight() - offset : offset, color(index, count));
            offset += ySpace.getValue();
        }

        if (fps.getValue()) {
            index++;
            String pingString = "FPS: \u00a7f" + Minecraft.getDebugFPS();
            FontManager.getInstance().drawStringWithShadow(pingString, resolution.getScaledWidth() - FontManager.getInstance().getStringWidth(pingString) - 2, renderingUp ? resolution.getScaledHeight() - offset : offset, color(index, count));
        }

        if (totems.getValue()) {
            renderTotemHUD();
        }

        if (coords.getValue()) {
			index++;
            String coordsString = "XYZ: \u00a7f" + getRoundedDouble(mc.player.posX) + "\u00a77,\u00a7f " + getRoundedDouble(mc.player.posY) + "\u00a77,\u00a7f " + getRoundedDouble(mc.player.posZ);
            if (netherCoords.getValue() && mc.player.dimension != 1) {
                coordsString += " \u00a77(\u00a7f" + getRoundedDouble(getDimensionCoord(mc.player.posX)) + "\u00a77,\u00a7f " + getRoundedDouble(getDimensionCoord(mc.player.posZ)) + "\u00a77)";
            }
            if (direction.getValue()) {
                coordsString += " \u00a77(\u00a7f" + DirectionUtil.convertToCoords(mc.player.getHorizontalFacing()) + "\u00a77)";
            }
            FontManager.getInstance().drawStringWithShadow(coordsString, 2, resolution.getScaledHeight() - (chatOpened ? 24 : 10), color(index, count));
        }

        if (textRadar.getValue()) {
            renderTextRadar(textRadarY.getValue());
        }
    }
	
	public int color(int index, int count, boolean yeah) {
        float[] hsb = new float[3];

        int color = ColorsModule.INSTANCE.getColor();
        Color.RGBtoHSB(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, hsb);

        float brightness = Math.abs(((getOffset() + (index / (float) count) * 2) % 2) - 1);
        brightness = 0.5f + (0.4f * brightness);

        hsb[2] = brightness % 1f;
        return changeAlpha(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]), alpha.getValue());
    }

    public int color(int index, int count) {
        int color = ColorsModule.INSTANCE.getColor();
        if (retardMode.getValue()) {
			return color(index, count, false);
        }
        return changeAlpha(color, alpha.getValue());
    }

    private float getOffset() {
        return (System.currentTimeMillis() % 2000) / 1000f;
    }

    public void renderTotemHUD() {
        ScaledResolution resolution = BetterScaledResolution.getInstance();
        int width = resolution.getScaledWidth();
        int height = resolution.getScaledHeight();
        int totems = ItemUtil.getItemCount(Items.TOTEM_OF_UNDYING);
        if (totems > 0) {
            GlStateManager.enableTexture2D();
            int i = width / 2;
            int y = height - 55 - (mc.player.isInWater() && mc.playerController.gameIsSurvivalOrAdventure() ? 10 : 0);
            int x = i - 189 + 9 * 20 + (totemX.getValue());
            GlStateManager.enableDepth();
            mc.getRenderItem().zLevel = 200F;
            mc.getRenderItem().renderItemAndEffectIntoGUI(TOTEM, x, y);
            mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, TOTEM, x, y, "");
            mc.getRenderItem().zLevel = 0F;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            FontManager.getInstance().drawStringWithShadow(totems + "", x + 19 - (totemX.getValue()) - FontManager.getInstance().getStringWidth(totems + ""), y + 9, changeAlpha(0xffffff, alpha.getValue()));
            GlStateManager.enableDepth();
            GlStateManager.disableLighting();
        }
    }

    public void renderTextRadar(int start) {
        int offset = 0;

        for (int i = 0, size = mc.world.playerEntities.size(); i < size; ++i) {
            final EntityPlayer player = mc.world.playerEntities.get(i);
            if (player == null || player == mc.player || player.isDead) {
                continue;
            }
            int y = start + offset;

            String playerName = player.getName();
            int x = 2;

            FontManager.getInstance().drawStringWithShadow(playerName, 2, start + offset, FriendManager.getInstance().isFriend(playerName) ? ColorsModule.INSTANCE.getFriendColor() : ColorsModule.INSTANCE.getColor());
            x += FontManager.getInstance().getStringWidth(playerName + " ");

            String hp = String.valueOf((int) EntityUtil.getHealth(player));
            FontManager.getInstance().drawStringWithShadow(hp, x, y, getHealthColor(player));
            x += FontManager.getInstance().getStringWidth(hp + " ");

            float distance = mc.player.getDistance(player);
            String dist = String.format("%.2f", distance);
            FontManager.getInstance().drawStringWithShadow(dist, x, y, changeAlpha(getColorByDistance(distance), alpha.getValue()));
            x += FontManager.getInstance().getStringWidth(dist + " ");

            int pops = TotemPopCounterModule.INSTANCE.getPopMap().getInt(player);
            if (pops > 0) {
                String pop = String.valueOf(pops);
                FontManager.getInstance().drawStringWithShadow(dist, x, y, changeAlpha(-1, alpha.getValue()));
                x += FontManager.getInstance().getStringWidth("-" + pop + " ");
            }

            offset += 10;
        }
    }

    public int getColorByDistance(double dist) {
        return (Color.HSBtoRGB((float)(Math.max(0.0, Math.min(dist * dist, 2500.0) / (double)(2500.0f)) / 3.0), 1.0f, 0.8f) | 0xFF000000);
    }

    public int getColorByDistance(Entity entity) {
        return (Color.HSBtoRGB((float)(Math.max(0.0, Math.min(mc.player.getDistanceSq(entity), 2500.0) / (double)(2500.0f)) / 3.0), 1.0f, 0.8f) | 0xFF000000);
    }

    private int getHealthColor(EntityLivingBase player) {
        return Color.HSBtoRGB(Math.max(0.0F, Math.min(player.getHealth(), 36) / 36) / 3.0F, 1.0F, 0.8f) | 0xFF000000;
    }

    public static double getDimensionCoord(double coord) {
        if (mc.player.dimension == -1) {
            return coord * 8;
        } else if (mc.player.dimension == 0) {
            return coord / 8;
        }
        return coord; // Dont do shiz if we in the end
    }

    private String getRoundedDouble(double pos) {
        return String.format("%.2f", pos);
    }

    @SubscribeEvent
    public void onHotbarRender(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
            if (HudModule.INSTANCE.hotbarKeys.getValue()) {
                int x = event.getResolution().getScaledWidth() / 2 - 87;
                int y = event.getResolution().getScaledHeight() - 18;

                int length = mc.gameSettings.keyBindsHotbar.length;
                for (int i = 0; i < length; i++) {
                    mc.fontRenderer.drawStringWithShadow(mc.gameSettings.keyBindsHotbar[i].getDisplayName(), x + i * 20, y, -1);
                }
            }
        }
    }

    public void setNeedsSort(boolean needsSort) {
        this.needsSort = needsSort;
    }

    public enum Rendering {
        UP,
        DOWN
    }

}
