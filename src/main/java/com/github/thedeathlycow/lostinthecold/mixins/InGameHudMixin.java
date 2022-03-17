package com.github.thedeathlycow.lostinthecold.mixins;

import com.github.thedeathlycow.lostinthecold.config.FreezingValues;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Random;

@Mixin(InGameHud.class)
abstract class InGameHudMixin {

    @Inject(
            method = "renderHealthBar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;drawHeart(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/gui/hud/InGameHud$HeartType;IIIZZ)V",
                    ordinal = 3,
                    shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    private void renderFrozenHeart(MatrixStack matrices, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, CallbackInfo ci, InGameHud.HeartType heartType, int i, int j, int k, int l, int m, int n, int o, int p, int q, int r, boolean bl3) {
        int frozenHealthPoints = (player.getFrozenTicks() / FreezingValues.TICK_INCREASE_PER_HEALTH_POINT);
        int frozenHealthHearts = MathHelper.ceil(frozenHealthPoints / 2.0D);
        if (m < frozenHealthHearts) {
            boolean isHalfHeart = m + 1 == frozenHealthHearts && (frozenHealthPoints & 1) == 1;
            ((InGameHudInvoker) this).invokeDrawHeart(matrices, InGameHud.HeartType.FROZEN, p, q, i, false, isHalfHeart);
        }
    }

}
