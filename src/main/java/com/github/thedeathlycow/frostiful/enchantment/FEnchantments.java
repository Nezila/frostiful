package com.github.thedeathlycow.frostiful.enchantment;

import com.github.thedeathlycow.frostiful.init.Frostiful;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FEnchantments {

    public static final Enchantment ENERVATION = new EnervationEnchantment(Enchantment.Rarity.RARE, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    public static final Enchantment ICE_BREAKER = new IceBreakerEnchantment(Enchantment.Rarity.RARE, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    public static final Enchantment FROZEN_TOUCH_CURSE = new FrozenTouchCurse(Enchantment.Rarity.RARE, new EquipmentSlot[]{EquipmentSlot.MAINHAND});

    public static void registerEnchantments() {
        register("enervation", ENERVATION);
        register("ice_breaker", ICE_BREAKER);
        register("frozen_touch_curse", FROZEN_TOUCH_CURSE);
    }

    private static void register(String name, Enchantment enchantment) {
        Registry.register(Registry.ENCHANTMENT, new Identifier(Frostiful.MODID, name), enchantment);
    }

}
