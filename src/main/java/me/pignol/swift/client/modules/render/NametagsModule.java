package me.pignol.swift.client.modules.render;

import com.mojang.realmsclient.gui.ChatFormatting;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import me.pignol.swift.api.util.BlockUtil;
import me.pignol.swift.api.util.EntityUtil;
import me.pignol.swift.api.util.ItemUtil;
import me.pignol.swift.api.util.render.RenderUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.Render3DEvent;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.managers.FontManager;
import me.pignol.swift.client.managers.FriendManager;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import me.pignol.swift.client.modules.misc.TotemPopCounterModule;
import me.pignol.swift.client.modules.other.ColorsModule;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

import static me.pignol.swift.api.util.DamageUtil.EMPTY_TAG_COMPOUND;

public class NametagsModule extends Module {

    public static NametagsModule INSTANCE = new NametagsModule();

    private Object2BooleanOpenHashMap<EntityPlayer> holeMap;

    public NametagsModule() {
        super("Nametags", Category.RENDER);
    }

    private final Value<Boolean> scaleing = new Value<>("Scale", false);
    private final Value<Float> factor = new Value<>("Factor", 0.5f, 0.1f, 1.0f);
    private final Value<Boolean> smartScale = new Value<>("SmartScale", false);
    private final Value<Float> size = new Value<>("Size", 6.4f, 0.1f, 20.0f);
    private final Value<Boolean> rect = new Value<>("Rectangle", true);
    private final Value<Boolean> holeColor = new Value<>("HoleColor", true);
    private final Value<Boolean> showEnchants = new Value<>("ShowEnchants", true);
    private final Value<Boolean> outlinedRect = new Value<>("OutlineRect", true);
    private final Value<Integer> lineWidth = new Value<>("OutlineWidth", 10, 1, 40, v -> outlinedRect.getValue());
    private final Value<Boolean> armor = new Value<>("Armor", true);
    private final Value<Boolean> health = new Value<>("Health", true);
    private final Value<Boolean> pops = new Value<>("Pops", true);
    private final Value<Boolean> ping = new Value<>("Ping", true);

    private final ICamera frustum = new Frustum();

    @SubscribeEvent(priority = EventPriority.LOWEST) // Fuck dis sequential Programming shit GEts on my Nevrers somstimes ima be honest
    public void onUpdate(UpdateEvent event) {
        if (holeColor.getValue()) {
            if (holeMap == null) {
                holeMap = new Object2BooleanOpenHashMap<>();
            }

            for (EntityPlayer player : mc.world.playerEntities) {
                holeMap.put(player, BlockUtil.isSafeFast(player));
            }

            EntityPlayer remove = null;
            for (EntityPlayer player : holeMap.keySet()) {
                if (!mc.world.playerEntities.contains(player)) {
                    remove = player;
                    break;
                }
            }

            holeMap.removeBoolean(remove);
        } else {
            if (holeMap != null) {
                holeMap.clear();
                holeMap = null;
            }
        }
    }

    @SubscribeEvent
    public void onRender3D(final Render3DEvent event) {
        if (mc.getRenderViewEntity() == null) return;
        frustum.setPosition(mc.getRenderViewEntity().posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);
        final List<EntityPlayer> players = mc.world.playerEntities;
        final int size = players.size();
        final float partialTicks = mc.getRenderPartialTicks();
        for (int i = 0; i < size; i++) {
            final EntityPlayer player = players.get(i);
            if (player != mc.getRenderViewEntity() && !EntityUtil.isDead(player) && frustum.isBoundingBoxInFrustum(player.getRenderBoundingBox())) {
                double x = interpolate(player.lastTickPosX, player.posX, partialTicks) - mc.renderManager.renderPosX;
                double y = interpolate(player.lastTickPosY, player.posY, partialTicks) - mc.renderManager.renderPosY;
                double z = interpolate(player.lastTickPosZ, player.posZ, partialTicks) - mc.renderManager.renderPosZ;
                renderNameTag(player, x, y, z, mc.getRenderPartialTicks());
            }
        }
    }

    private void renderNameTag(EntityPlayer player, double x, double y, double z, float delta) {
        double tempY = y;
        tempY += (player.isSneaking() ? 0.5D : 0.7D);
        Entity camera = mc.getRenderViewEntity();
        assert camera != null;
        double originalPositionX = camera.posX;
        double originalPositionY = camera.posY;
        double originalPositionZ = camera.posZ;
        camera.posX = interpolate(camera.prevPosX, camera.posX, delta);
        camera.posY = interpolate(camera.prevPosY, camera.posY, delta);
        camera.posZ = interpolate(camera.prevPosZ, camera.posZ, delta);

        String displayTag = getDisplayTag(player);
        double distance = camera.getDistance(x + mc.getRenderManager().viewerPosX, y + mc.getRenderManager().viewerPosY, z + mc.getRenderManager().viewerPosZ);
        int width = FontManager.getInstance().getStringWidth(displayTag) / 2;

        if (distance <= 8 && smartScale.getValue()) {
            distance = 8;
        }

        double scale = (0.0018 + size.getValue() * (distance * factor.getValue())) / 1000.0;

        if (!scaleing.getValue()) {
            scale = size.getValue() / 100.0;
        }

        GL11.glEnable(GL11.GL_POLYGON_OFFSET_LINE);
        GL11.glPolygonOffset(0.0001f, 1000f);
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.translate((float) x, (float) tempY + 1.4F, (float) z);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1.0F : 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.disableDepth();

        if (rect.getValue()) {
            RenderUtil.drawRect(-width - 2, -(mc.fontRenderer.FONT_HEIGHT + 1), width + 2F, 1, 0x55000000);
        }

        if (outlinedRect.getValue()) {
            GlStateManager.glLineWidth(lineWidth.getValue() / 10F);
            RenderUtil.drawOutlineRect(-width - 2, -(mc.fontRenderer.FONT_HEIGHT + 1), width + 2F, 1, holeColor.getValue() && holeMap != null ? (holeMap.getBoolean(player) ? Color.GREEN.getRGB() : Color.RED.getRGB()) : getDisplayColour(player, false));
        }

        if (armor.getValue()) {
            GlStateManager.pushMatrix();
            int xOffset = -48;

            renderItemStack(player.getHeldItemOffhand(), xOffset, -27);
            xOffset += 16;

            final NonNullList<ItemStack> armorInventory = player.inventory.armorInventory;
            for (int i = 0; i < 4; i++) {
                renderItemStack(armorInventory.get(i), xOffset, -27);
                xOffset += 16;
            }

            renderItemStack(player.getHeldItemMainhand(), xOffset, -27);

            GlStateManager.popMatrix();
        }

        GlStateManager.disableDepth();
        FontManager.getInstance().drawStringWithShadow(displayTag, -width, -(8), this.getDisplayColour(player, true));
        GlStateManager.enableDepth();

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();

        camera.posX = originalPositionX;
        camera.posY = originalPositionY;
        camera.posZ = originalPositionZ;
    }

    private void renderItemStack(ItemStack stack, int x, int y) {
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(true);

        RenderHelper.enableStandardItemLighting();
        GlStateManager.disableAlpha();
        GlStateManager.enableDepth();

        mc.getRenderItem().zLevel = -150.0F;

        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
        mc.getRenderItem().renderItemOverlays(mc.fontRenderer, stack, x, y);

        mc.getRenderItem().zLevel = 0.0F;

        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableAlpha();

        GlStateManager.scale(0.5F, 0.5F, 0.5F);
        GlStateManager.disableDepth();
        renderEnchantmentText(stack, x, y);
        GlStateManager.enableDepth();
        GlStateManager.scale(2F, 2F, 2F);

        GlStateManager.popMatrix();
    }

    private void renderEnchantmentText(ItemStack stack, int x, int y) {
        int enchantmentY = y - 8;

        if (ItemUtil.hasDurability(stack.getItem())) {
            FontManager.getInstance().drawStringWithShadow((int) ItemUtil.getDamageInPercent(stack) + "%", x * 2, enchantmentY, stack.getItem().getRGBDurabilityForDisplay(stack));
            enchantmentY -= 8;
        }

        if (showEnchants.getValue()) {
            final NBTTagList enchants = stack.getEnchantmentTagList();
            final int tagCount = enchants.tagCount();
            for (int index = 0; index < tagCount; ++index) {
                final short id = getCompoundTagAt(enchants, index).getShort("id");
                final short level = getCompoundTagAt(enchants, index).getShort("lvl");
                final Enchantment enc = Enchantment.getEnchantmentByID(id);
                if (enc != null) {
                    final String encName = (enc.isCurse() ? TextFormatting.RED + enc.getTranslatedName(level).substring(0, 1) : enc.getTranslatedName(level).substring(0, 1)) + level;
                    FontManager.getInstance().drawStringWithShadow(encName, x * 2, enchantmentY, -1);
                    enchantmentY -= 8;
                }
            }
        }
    }

    private static NBTTagCompound getCompoundTagAt(NBTTagList list, int i) {
        final List<NBTBase> nbtTagCompoundList = list.tagList;
        final int size = nbtTagCompoundList.size();
        if (i >= 0 && i < size) {
            final NBTBase nbtbase = list.get(i);

            if (nbtbase.getId() == 10)
            {
                return (NBTTagCompound)nbtbase;
            }
        }

        return EMPTY_TAG_COMPOUND;
    }

    private String getDisplayTag(EntityPlayer player) {
        //Bro
        final String name = player.getName();
        final String ping = " " + EntityUtil.getPing(player) + "ms";

        final float healthValue = EntityUtil.getHealth(player);
        String color;

        if (healthValue > 20) {
            color = ChatFormatting.GREEN.toString();
        } else if (healthValue > 16) {
            color = ChatFormatting.DARK_GREEN.toString();
        } else if (healthValue > 12) {
            color = ChatFormatting.YELLOW.toString();
        } else if (healthValue > 8) {
            color = ChatFormatting.GOLD.toString();
        } else if (healthValue > 4) {
            color = ChatFormatting.RED.toString();
        } else {
            color = ChatFormatting.DARK_RED.toString();
        }
        final String health = " " + color + (int) healthValue;
        final StringBuilder builder = new StringBuilder();
        builder.append(player.getName());
        if (this.ping.getValue()) {
            builder.append(ping);
        }
        if (this.health.getValue()) {
            builder.append(health);
        }
        if (pops.getValue()) {
            int pops = TotemPopCounterModule.INSTANCE.getPopMap().getInt(name);
            if (pops > 0) {
                builder.append(" \u00a7r-").append(pops);
            }
        }
        return builder.toString();
    }

    private int getDisplayColour(EntityPlayer player, boolean forText) {
        if (FriendManager.getInstance().isFriend(player.getName())) {
            return ColorsModule.INSTANCE.getFriendColor();
        }
        return forText ? 0xFFFFFFFF : ColorsModule.INSTANCE.getColor();
    }

    private double interpolate(double previous, double current, float delta) {
        return (previous + (current - previous) * delta);
    }


}
