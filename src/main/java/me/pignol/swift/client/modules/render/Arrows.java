package me.pignol.swift.client.modules.render;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Arrows extends Module {

    private final Value<Double> aliveTime = new Value<>("Time", 5.0, 0.0, 20.0);
    private final Value<Float> thickness = new Value<>("Thickness", 1.0f, 0.1f, 2.0f);
    private final Value<Boolean> fade = new Value<>("Fade", true);
    private final Value<Integer> red = new Value<>("Red",0,0,255);
    private final Value<Integer> green = new Value<>("Green",0,0,255);
    private final Value<Integer> blue = new Value<>("Blue",0,0,255);

    public Arrows(){ super("Arrows", Category.RENDER);
    }

    private final List<Bullet> bullets = new CopyOnWriteArrayList<Bullet>();
    private int fps;
    private LinkedList<Long> frames = new LinkedList<>();

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (isNull()) {
            return;
        }
        this.bullets.forEach(bullet -> {
            if (bullet.time <= 0.0) {
                if (!this.fade.getValue().booleanValue()) {
                    this.bullets.remove(bullet);
                } else {
                    bullet.update();
                }
            }
            bullet.time -= 0.05;
        });
        mc.world.loadedEntityList.stream().filter(EntityArrow.class::isInstance).filter(entity -> ((EntityArrow)entity).shootingEntity == mc.player).forEach(e -> {
            Bullet bullet = this.getByEntity((EntityArrow)((Object)e));
            if (bullet != null) {
                bullet.vec3d.add(e.getPositionVector());
            } else {
                this.bullets.add(new Bullet((EntityArrow)((Object)e), this.aliveTime.getValue().doubleValue(), e.getPositionVector()));
            }
        });
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        GL11.glPushMatrix();
        GL11.glBlendFunc(770, 771);
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glLineWidth(this.thickness.getValue().floatValue());
        this.bullets.stream().filter(bullet -> bullet.vec3d.size() > 2).forEach(bullet -> {
            GL11.glBegin(1);
            for (int i = 1; i < bullet.vec3d.size(); ++i) {
                GL11.glColor4d((float)this.red.getValue() / 255.0f, (float)this.green.getValue() / 255.0f, (float)this.blue.getValue() / 255.0f, (float)bullet.alpha / 255.0f);
                List<Vec3d> pos = bullet.vec3d;
                GL11.glVertex3d(pos.get((int)i).x - mc.getRenderManager().viewerPosX, pos.get((int)i).y - mc.getRenderManager().viewerPosY, pos.get((int)i).z - mc.getRenderManager().viewerPosZ);
                GL11.glVertex3d(pos.get((int)(i - 1)).x - mc.getRenderManager().viewerPosX, pos.get((int)(i - 1)).y - mc.getRenderManager().viewerPosY, pos.get((int)(i - 1)).z - mc.getRenderManager().viewerPosZ);
            }
            GL11.glEnd();
        });
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    private Bullet getByEntity(EntityArrow entityArrow) {
        return this.bullets.stream().filter(e -> e.entity == entityArrow).findFirst().orElse(null);
    }

    class Bullet {
        int alpha;
        EntityArrow entity;
        double time;
        List<Vec3d> vec3d;

        Bullet(EntityArrow entity, double time, Vec3d vec3d) {
            this.entity = entity;
            this.time = time;
            this.alpha = 255;
            this.vec3d = new ArrayList<Vec3d>();
            vec3d.add(vec3d);
        }

        void update() {
            if (this.alpha <= 0) {
                bullets.remove(this);
            } else {
                this.alpha = (int)((float)this.alpha - Math.min(Math.max(283.33334f * getFrametime(), 0.0f), 255.0f));
            }
        }
    }


    public void update() {
        final long time = System.nanoTime();
        this.frames.add(time);
        while (true) {
            final long f = this.frames.getFirst();
            final long ONE_SECOND = 1000000000L;
            if (time - f <= 1000000000L) {
                break;
            }
            this.frames.remove();
        }
        this.fps = this.frames.size();
    }

    public int getFPS() {
        return this.fps;
    }

    public float getFrametime() {
        return 1.0f / this.fps;
    }
}
