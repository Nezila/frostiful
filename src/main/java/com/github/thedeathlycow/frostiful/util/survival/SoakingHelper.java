package com.github.thedeathlycow.frostiful.util.survival;

import com.github.thedeathlycow.frostiful.config.group.FreezingConfigGroup;
import com.github.thedeathlycow.frostiful.entity.SoakableEntity;
import com.github.thedeathlycow.frostiful.init.Frostiful;
import com.github.thedeathlycow.frostiful.mixins.entity.EntityInvoker;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.LightType;

public class SoakingHelper {

    public static int getWetnessChange(PlayerEntity player) {
        FreezingConfigGroup freezingConfig = Frostiful.getConfig().freezingConfig;
        SoakableEntity soakableEntity = (SoakableEntity) player;
        EntityInvoker invoker = (EntityInvoker) player;

        boolean isDry = true;
        int wetness = 0;

        //* set wetness from rain
        if (invoker.frostiful$invokeIsBeingRainedOn()) {
            wetness = freezingConfig.getRainWetnessIncrease();
            isDry = false;
        }

        //* add wetness when touching, but not submerged in, water
        if (player.isTouchingWater() || player.getBlockStateAtPos().isOf(Blocks.WATER_CAULDRON)) {
            wetness += freezingConfig.getTouchingWaterWetnessIncrease();
            isDry = false;
        }

        //* immediately soak players in water
        if (player.isSubmergedInWater() || invoker.frostiful$invokeIsInsideBubbleColumn()) {
            wetness = soakableEntity.frostiful$getMaxWetTicks();
            isDry = false;
        }

        //* dry off players not water
        if (isDry) {
            wetness = -freezingConfig.getDryRate();
        }

        //* increase dryness with block light
        int blockLightLevel = player.getWorld().getLightLevel(LightType.BLOCK, player.getBlockPos());
        if (blockLightLevel > 0) {
            wetness -= blockLightLevel >> 2; // fast divide by 4
        }

        //* increase dryness when on fire
        if (player.isOnFire()) {
            wetness -= freezingConfig.getOnFireDryRate();
        }

        return wetness;
    }

    /**
     * Returns by how much percentage the player's passive freezing could be increased
     * based on how wet they are.
     *
     * @param player Affected player
     * @return Returns the percentage change to passive freezing that the player should experience
     * based on their wetness, where return values of 0 means no change, and 1 means double the freezing.
     */
    public static float getWetnessFreezeModifier(PlayerEntity player) {
        SoakableEntity soakable = (SoakableEntity) player;
        if (soakable.frostiful$ignoresFrigidWater()) {
            return 0.0f;
        }
        FreezingConfigGroup config = Frostiful.getConfig().freezingConfig;
        return config.getPassiveFreezingWetnessScaleMultiplier() * soakable.frostiful$getWetnessScale();
    }

    private static boolean hasWaterFreezingImmuneEffect(PlayerEntity player) {
        return player.hasStatusEffect(StatusEffects.WATER_BREATHING)
                || player.hasStatusEffect(StatusEffects.CONDUIT_POWER);
    }
}
