package com.github.thedeathlycow.frostiful.particle.client;

import com.github.thedeathlycow.frostiful.particle.WindParticleEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class WindParticle extends SpriteBillboardParticle {

    private static final Vec3f FROM = Util.make(new Vec3f(0.5F, 0.5F, 0.5F), Vec3f::normalize);
    private static final Vec3f TO = new Vec3f(-1.0F, -1.0F, 0.0F);

    private final SpriteProvider spriteProvider;

    protected WindParticle(ClientWorld clientWorld, double x, double y, double z, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z);
        this.spriteProvider = spriteProvider;
        this.velocityX *= 2;
        this.scale *= 3;
        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteForAge(this.spriteProvider);
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        this.buildGeometry(vertexConsumer, camera, tickDelta, true, (quaternion) -> {
            quaternion.hamiltonProduct(Vec3f.POSITIVE_Y.getRadialQuaternion(0));
        });
        this.buildGeometry(vertexConsumer, camera, tickDelta, false, (quaternion) -> {
            quaternion.hamiltonProduct(Vec3f.POSITIVE_Y.getRadialQuaternion(-MathHelper.PI));
        });
    }

    private void buildGeometry(
            VertexConsumer vertexConsumer,
            Camera camera,
            float tickDelta,
            boolean flip,
            Consumer<Quaternion> rotator
    ) {
        Vec3d cameraPos = camera.getPos();
        float dx = (float) (MathHelper.lerp(tickDelta, this.prevPosX, this.x) - cameraPos.getX());
        float dy = (float) (MathHelper.lerp(tickDelta, this.prevPosY, this.y) - cameraPos.getY());
        float dz = (float) (MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - cameraPos.getZ());
        Quaternion quaternion = new Quaternion(FROM, 0.0F, true);
        rotator.accept(quaternion);
        TO.rotate(quaternion);
        Vec3f[] points = new Vec3f[]{
                new Vec3f(-1.0F, -1.0F, 0.0F),
                new Vec3f(-1.0F, 1.0F, 0.0F),
                new Vec3f(1.0F, 1.0F, 0.0F),
                new Vec3f(1.0F, -1.0F, 0.0F)
        };

        float size = this.getSize(tickDelta) * (flip ? -1 : 1);

        for (int i = 0; i < 4; ++i) {
            Vec3f point = points[i];
            point.rotate(quaternion);
            point.scale(size);
            point.add(dx, dy, dz);
        }

        int brightness = this.getBrightness(tickDelta);
        this.vertex(vertexConsumer, points[0], this.getMaxU(), this.getMaxV(), brightness);
        this.vertex(vertexConsumer, points[1], this.getMaxU(), this.getMinV(), brightness);
        this.vertex(vertexConsumer, points[2], this.getMinU(), this.getMinV(), brightness);
        this.vertex(vertexConsumer, points[3], this.getMinU(), this.getMaxV(), brightness);
    }

    private void vertex(VertexConsumer vertexConsumer, Vec3f pos, float u, float v, int light) {
        vertexConsumer.vertex(pos.getX(), pos.getY(), pos.getZ())
                .texture(u, v)
                .color(this.red, this.green, this.blue, this.alpha)
                .light(light)
                .next();
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<WindParticleEffect> {

        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Nullable
        @Override
        public Particle createParticle(WindParticleEffect parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new WindParticle(world, x, y, z, this.spriteProvider);
        }
    }
}
