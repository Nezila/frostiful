package com.github.thedeathlycow.frostiful.item;

import com.github.thedeathlycow.frostiful.config.FrostifulConfig;
import com.github.thedeathlycow.frostiful.entity.FrostSpellEntity;
import com.github.thedeathlycow.frostiful.entity.RootedEntity;
import com.github.thedeathlycow.frostiful.init.Frostiful;
import com.github.thedeathlycow.frostiful.sound.FSoundEvents;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Vanishable;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class FrostWandItem extends Item implements Vanishable {

    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    public FrostWandItem(Settings settings) {
        super(settings);
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Tool modifier", 5.0, EntityAttributeModifier.Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Tool modifier", -2.9, EntityAttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return !miner.isCreative();
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        int useTime = this.getMaxUseTime(stack) - remainingUseTicks;
        if (useTime > 10 && !world.isClient) {
            fireFrostSpell(stack, world, user);
        }
    }



    public static void fireFrostSpell(ItemStack frostWandStack, World world, LivingEntity user) {
        FrostifulConfig config = Frostiful.getConfig();

        FrostSpellEntity spell = new FrostSpellEntity(
                world,
                user,
                0.0, 0.0, 0.0,
                config.combatConfig.getMaxFrostSpellDistance()
        );
        spell.setVelocity(user, user.getPitch(), user.getHeadYaw(), 0.0f, 2.5f, 1.0f);
        Vec3d firingPos = user.getEyePos();
        spell.setPosition(firingPos);
        spell.setOwner(user);
        world.spawnEntity(spell);
        world.playSound(
                null,
                firingPos.getX(), firingPos.getY(), firingPos.getZ(),
                FSoundEvents.ITEM_FROST_WAND_CAST_SPELL, SoundCategory.AMBIENT,
                1.0f, 1.0f
        );

        if (user instanceof PlayerEntity player) {
            frostWandStack.damage(2, player, (p) -> {
                p.sendToolBreakStatus(p.getActiveHand());
            });
            player.incrementStat(Stats.USED.getOrCreateStat(frostWandStack.getItem()));
            player.getItemCooldownManager().set(frostWandStack.getItem(), config.combatConfig.getFrostWandCooldown());
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (itemStack.getDamage() >= itemStack.getMaxDamage() - 1) {
            return TypedActionResult.fail(itemStack);
        } else {

            if (!world.isClient) {
                world.playSound(
                        null,
                        user.getX(), user.getY(), user.getZ(),
                        FSoundEvents.ITEM_FROST_WAND_PREPARE_CAST,
                        SoundCategory.PLAYERS,
                        1.0f, 1.0f
                );
            }

            user.setCurrentHand(hand);
            return TypedActionResult.consume(itemStack);
        }
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if ((double) state.getHardness(world, pos) != 0.0) {
            stack.damage(2, miner, (e) -> {
                e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
            });
        }

        return true;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(slot);
    }

    @Override
    public int getEnchantability() {
        return 15;
    }


}
