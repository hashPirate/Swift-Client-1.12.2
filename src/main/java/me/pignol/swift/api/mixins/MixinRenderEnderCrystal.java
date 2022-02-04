package me.pignol.swift.api.mixins;

import me.pignol.swift.client.modules.render.Chams;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderEnderCrystal.class)
public class MixinRenderEnderCrystal {

    @Redirect(method = "doRender(Lnet/minecraft/entity/item/EntityEnderCrystal;DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    public void doRender(ModelBase instance, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (Chams.INSTANCE.isEnabled()) {
            boolean chams = Chams.INSTANCE.crystals.getValue();
            GL11.glScalef(Chams.INSTANCE.crystalScale.getValue(), Chams.INSTANCE.crystalScale.getValue(), Chams.INSTANCE.crystalScale.getValue());
            if (chams) {
                GL11.glPushMatrix();
                GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glEnable(GL11.GL_LINE_SMOOTH);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glColor4f(Chams.INSTANCE.red.getValue() / 255.0F, Chams.INSTANCE.green.getValue() / 255.0F, Chams.INSTANCE.blue.getValue() / 255.0F, Chams.INSTANCE.alpha.getValue() / 255.0F);
            }
            instance.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            if (chams) {
                GL11.glPopAttrib();
                GL11.glPopMatrix();
            }
            GL11.glScalef(1.0F, 1.0F, 1.0F);
        } else {
            instance.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

}
