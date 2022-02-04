package me.pignol.swift.api.util.render.shader.shaders;

import me.pignol.swift.api.util.BetterScaledResolution;
import me.pignol.swift.api.util.render.shader.FramebufferShader;
import org.lwjgl.opengl.GL20;

public class PersianShader extends FramebufferShader {

    public final static PersianShader SHADER = new PersianShader();

    private float time;

    public PersianShader() {
        super("persian.frag");
    }


    @Override
    public void setupUniforms() {
        this.setupUniform("resolution");
        this.setupUniform("time");
    }

    @Override
    public void updateUniforms() {
        GL20.glUniform2f((int)this.getUniform("resolution"), BetterScaledResolution.getInstance().getScaledWidth(), BetterScaledResolution.getInstance().getScaledHeight());
        GL20.glUniform1f((int)this.getUniform("time"), this.time);
        this.time += 0.003F * mc.getRenderPartialTicks();
    }

}
