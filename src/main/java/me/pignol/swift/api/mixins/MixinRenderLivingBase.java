package me.pignol.swift.api.mixins;

import me.pignol.swift.client.managers.FriendManager;
import me.pignol.swift.client.modules.other.ColorsModule;
import me.pignol.swift.client.modules.render.Chams;
import me.pignol.swift.client.modules.render.SkeletonModule;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(RenderLivingBase.class)
public class MixinRenderLivingBase<T extends EntityLivingBase> extends Render<T> {

    @Shadow
    protected ModelBase mainModel;

    protected MixinRenderLivingBase(RenderManager renderManager) {
        super(renderManager);
    }

    @Inject(method = "renderModel", at = @At("HEAD"), cancellable = true)
    public void renderModel(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, CallbackInfo ci) {
        if (SkeletonModule.INSTANCE.isEnabled()) {
            SkeletonModule.INSTANCE.onRenderModel(entityIn, mainModel);
        }

        if (Chams.INSTANCE.isEnabled() && Chams.INSTANCE.players.getValue()) {
            ci.cancel();
            renderChams(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
        }
    }

    private void renderChams(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
        boolean friend = FriendManager.getInstance().isFriend(entityIn.getName());
        float red = (friend ? ColorsModule.INSTANCE.friendRed.getValue() : Chams.INSTANCE.red.getValue()) / 255.0F;
        float green = (friend ? ColorsModule.INSTANCE.friendGreen.getValue() : Chams.INSTANCE.green.getValue()) / 255.0F;
        float blue = (friend ? ColorsModule.INSTANCE.friendBlue.getValue() : Chams.INSTANCE.blue.getValue()) / 255.0F;
        float alpha = Chams.INSTANCE.alpha.getValue() / 255.0F;

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(red, green, blue, alpha);
        mainModel.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return null;
    }

}
