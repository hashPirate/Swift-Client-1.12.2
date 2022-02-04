package me.pignol.swift.client.modules.render;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import me.pignol.swift.api.util.ColorUtil;
import me.pignol.swift.api.util.render.RenderUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.Render3DEvent;
import me.pignol.swift.client.managers.FriendManager;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import me.pignol.swift.client.modules.other.ColorsModule;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class SkeletonModule extends Module {
    
    public static SkeletonModule INSTANCE = new SkeletonModule();

    private final Value<Float> lineWidth = new Value<>("Width", 1.5f, 0.1f, 5.0f);
    
    private final Reference2ReferenceOpenHashMap<Entity, float[][]> rotationList = new Reference2ReferenceOpenHashMap<>();

    public SkeletonModule() {
        super("Skeleton", Category.RENDER);
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        RenderUtil.enableGL3D(lineWidth.getValue());
        final List<EntityPlayer> playerEntities = mc.world.playerEntities;
        for (int i = 0, playerEntitiesSize = playerEntities.size(); i < playerEntitiesSize; i++) {
            final EntityPlayer player = playerEntities.get(i);
            if (player != null && player != mc.getRenderViewEntity() && (!player.isDead && player.getHealth() > 0)) {
                if (rotationList.get(player) != null) {
                    final int color = FriendManager.getInstance().isFriend(player.getName()) ? ColorsModule.INSTANCE.getFriendColor() : ColorsModule.INSTANCE.getColor();
                    renderSkeleton(player, rotationList.get(player), color);
                }
            }
        }
        RenderUtil.disableGL3D();
    }

    public void onRenderModel(Entity entity, ModelBase modelBase) {
        if (entity instanceof EntityPlayer) {
            if (modelBase instanceof ModelBiped) {
                ModelBiped biped = (ModelBiped)modelBase;
                rotationList.put(entity, getBipedRotations(biped));
            }
        }
    }

    public static float[][] getBipedRotations(final ModelBiped biped) {
        final float[][] rotations = new float[5][];

        final float[] headRotation = new float[3];
        headRotation[0] = biped.bipedHead.rotateAngleX;
        headRotation[1] = biped.bipedHead.rotateAngleY;
        headRotation[2] = biped.bipedHead.rotateAngleZ;
        rotations[0] = headRotation;

        final float[] rightArmRotation = new float[3];
        rightArmRotation[0] = biped.bipedRightArm.rotateAngleX;
        rightArmRotation[1] = biped.bipedRightArm.rotateAngleY;
        rightArmRotation[2] = biped.bipedRightArm.rotateAngleZ;
        rotations [1] = rightArmRotation;

        final float[] leftArmRotation = new float[3];
        leftArmRotation[0] = biped.bipedLeftArm.rotateAngleX;
        leftArmRotation[1] = biped.bipedLeftArm.rotateAngleY;
        leftArmRotation[2] = biped.bipedLeftArm.rotateAngleZ;
        rotations[2] = leftArmRotation;

        final float[] rightLegRotation = new float[3];
        rightLegRotation[0] = biped.bipedRightLeg.rotateAngleX;
        rightLegRotation[1] = biped.bipedRightLeg.rotateAngleY;
        rightLegRotation[2] = biped.bipedRightLeg.rotateAngleZ;
        rotations[3] = rightLegRotation;

        final float[] leftLegRotation = new float[3];
        leftLegRotation[0] = biped.bipedLeftLeg.rotateAngleX;
        leftLegRotation[1] = biped.bipedLeftLeg.rotateAngleY;
        leftLegRotation[2] = biped.bipedLeftLeg.rotateAngleZ;
        rotations[4] = leftLegRotation;

        return rotations;
    }

    private void renderSkeleton(EntityLivingBase entity, float[][] rotations, int color) {
        ColorUtil.glColor(color);

        GlStateManager.pushMatrix();

        //Vec3d interp = RenderUtil.getInterpolatedRenderPos(entity, mc.getRenderPartialTicks());

        final float partialTicks = mc.getRenderPartialTicks();
        final double pX = (entity.lastTickPosX + ((entity.posX - entity.lastTickPosX) * partialTicks)) - mc.renderManager.renderPosX;
        final double pY = (entity.lastTickPosY + ((entity.posY - entity.lastTickPosY) * partialTicks)) - mc.renderManager.renderPosY;
        final double pZ = (entity.lastTickPosZ + ((entity.posZ - entity.lastTickPosZ) * partialTicks)) - mc.renderManager.renderPosZ;

        GlStateManager.translate(pX, pY, pZ);
        GlStateManager.rotate(-entity.renderYawOffset, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(0.0D, 0.0D, entity.isSneaking() ? -0.235D : 0.0D);
        float sneak = entity.isSneaking() ? 0.6F : 0.75F;

        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.125D, sneak, 0.0D);
        if (rotations[3][0] != 0.0F) {
            GlStateManager.rotate(rotations[3][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
        }
        if (rotations[3][1] != 0.0F) {
            GlStateManager.rotate(rotations[3][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
        }
        if (rotations[3][2] != 0.0F) {
            GlStateManager.rotate(rotations[3][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
        }

        GlStateManager.glBegin(3);
        GL11.glVertex3d(0.0D, 0.0D, 0.0D);
        GL11.glVertex3d(0.0D, (-sneak), 0.0D);
        GlStateManager.glEnd();
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.125D, sneak, 0.0D);
        if (rotations[4][0] != 0.0F) {
            GlStateManager.rotate(rotations[4][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
        }
        if (rotations[4][1] != 0.0F) {
            GlStateManager.rotate(rotations[4][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
        }
        if (rotations[4][2] != 0.0F) {
            GlStateManager.rotate(rotations[4][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
        }

        GlStateManager.glBegin(3);
        GL11.glVertex3d(0.0D, 0.0D, 0.0D);
        GL11.glVertex3d(0.0D, (-sneak), 0.0D);
        GlStateManager.glEnd();
        GlStateManager.popMatrix();
        GlStateManager.translate(0.0D, 0.0D, entity.isSneaking() ? 0.25D : 0.0D);

        GlStateManager.pushMatrix();
        double sneakOffset = 0.0;
        if (entity.isSneaking()) {
            sneakOffset = -0.05;
        }

        GlStateManager.translate(0.0D, sneakOffset, entity.isSneaking() ? -0.01725D : 0.0D);

        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.375D, sneak + 0.55D, 0.0D);
        if (rotations[1][0] != 0.0F) {
            GlStateManager.rotate(rotations[1][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
        }
        if (rotations[1][1] != 0.0F) {
            GlStateManager.rotate(rotations[1][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
        }
        if (rotations[1][2] != 0.0F) {
            GlStateManager.rotate(-rotations[1][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
        }

        GlStateManager.glBegin(3);
        GL11.glVertex3d(0.0D, 0.0D, 0.0D);
        GL11.glVertex3d(0.0D, -0.5D, 0.0D);
        GlStateManager.glEnd();
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.375D, sneak + 0.55D, 0.0D);
        if (rotations[2][0] != 0.0F) {
            GlStateManager.rotate(rotations[2][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
        }
        if (rotations[2][1] != 0.0F) {
            GlStateManager.rotate(rotations[2][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
        }
        if (rotations[2][2] != 0.0F) {
            GlStateManager.rotate(-rotations[2][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
        }

        GlStateManager.glBegin(3);
        GL11.glVertex3d(0.0D, 0.0D, 0.0D);
        GL11.glVertex3d(0.0D, -0.5D, 0.0D);
        GlStateManager.glEnd();
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0D, sneak + 0.55D, 0.0D);
        if (rotations[0][0] != 0.0F) {
            GlStateManager.rotate(rotations[0][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
        }

        GlStateManager.glBegin(3);
        GL11.glVertex3d(0.0D, 0.0D, 0.0D);
        GL11.glVertex3d(0.0D, 0.3D, 0.0D);
        GlStateManager.glEnd();
        GlStateManager.popMatrix();

        GlStateManager.popMatrix();
        GlStateManager.rotate(entity.isSneaking() ? 25.0F : 0.0F, 1.0F, 0.0F, 0.0F);

        if (entity.isSneaking()) {
            sneakOffset = -0.16175D;
        }

        GlStateManager.translate(0.0D, sneakOffset, entity.isSneaking() ? -0.48025D : 0.0D);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0D, sneak, 0.0D);
        GlStateManager.glBegin(3);
        GL11.glVertex3d(-0.125D, 0.0D, 0.0D);
        GL11.glVertex3d(0.125D, 0.0D, 0.0D);
        GlStateManager.glEnd();
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0D, sneak, 0.0D);
        GlStateManager.glBegin(3);
        GL11.glVertex3d(0.0D, 0.0D, 0.0D);
        GL11.glVertex3d(0.0D, 0.55D, 0.0D);
        GlStateManager.glEnd();
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0D, sneak + 0.55D, 0.0D);
        GlStateManager.glBegin(3);
        GL11.glVertex3d(-0.375D, 0.0D, 0.0D);
        GL11.glVertex3d(0.375D, 0.0D, 0.0D);
        GlStateManager.glEnd();
        GlStateManager.popMatrix();

        GlStateManager.popMatrix();
    }
    
}
