package com.github.thedeathlycow.frostiful.client.render.entity;

import com.github.thedeathlycow.frostiful.client.model.FEntityModelLayers;
import com.github.thedeathlycow.frostiful.client.model.FrostologerEntityModel;
import com.github.thedeathlycow.frostiful.client.render.feature.FrostologerCloakFeatureRenderer;
import com.github.thedeathlycow.frostiful.entity.FrostologerEntity;
import com.github.thedeathlycow.frostiful.init.Frostiful;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.IllagerEntityRenderer;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class FrostologerEntityRenderer extends MobEntityRenderer<FrostologerEntity, FrostologerEntityModel<FrostologerEntity>> {


    private static final Identifier TEXTURE = new Identifier(Frostiful.MODID, "textures/entity/illager/frostologer.png");

    public FrostologerEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new FrostologerEntityModel<>(context.getPart(FEntityModelLayers.FROSTOLOGER)), 0.5F);

        this.addFeature(new HeadFeatureRenderer<>(this, context.getModelLoader(), context.getHeldItemRenderer()));
        this.addFeature(new HeldItemFeatureRenderer<>(this, context.getHeldItemRenderer()));
        this.addFeature(new FrostologerCloakFeatureRenderer(this));
    }

    @Override
    protected void scale(FrostologerEntity frostologerEntity, MatrixStack matrixStack, float amount) {
        float scale = 15f / 16f;
        matrixStack.scale(scale, scale, scale);
    }

    public Identifier getTexture(FrostologerEntity pillagerEntity) {
        return TEXTURE;
    }
}
