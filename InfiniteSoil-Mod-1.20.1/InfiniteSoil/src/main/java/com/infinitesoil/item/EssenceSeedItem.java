package com.infinitesoil.item;

import com.infinitesoil.block.EssenceCropBlock;
import com.infinitesoil.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Semente dinâmica de Essência.
 * Guarda no NBT qual essência ela representa.
 * Craft: 4 essências iguais + semente qualquer no centro.
 */
public class EssenceSeedItem extends Item {

    public static final String NBT_ESSENCE = "EssenceId";

    public EssenceSeedItem(Properties properties) {
        super(properties);
    }

    public static void setEssence(ItemStack stack, ResourceLocation essenceId) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString(NBT_ESSENCE, essenceId.toString());
    }

    public static ResourceLocation getEssence(ItemStack stack) {
        if (!stack.hasTag()) return null;
        String id = stack.getTag().getString(NBT_ESSENCE);
        if (id.isEmpty()) return null;
        return ResourceLocation.tryParse(id);
    }

    public static ItemStack createSeed(ResourceLocation essenceId) {
        ItemStack stack = new ItemStack(com.infinitesoil.init.ModItems.ESSENCE_SEED.get());
        setEssence(stack, essenceId);
        return stack;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        ItemStack stack = context.getItemInHand();

        // Só planta em farmland ou Infinite Soil
        if (state.getBlock() == Blocks.FARMLAND || 
            state.getBlock() == ModBlocks.INFINITE_SOIL.get() ||
            state.getBlock().getDescriptionId().contains("farmland")) {

            BlockPos plantPos = pos.above();
            if (level.getBlockState(plantPos).isAir()) {
                // Planta o crop
                level.setBlock(plantPos, ModBlocks.ESSENCE_CROP.get().defaultBlockState(), 3);

                // Transfere o NBT da essência para o BlockEntity do crop (se tiver)
                // Por simplicidade usamos o BlockState + um método estático, mas
                // o crop vai ler do item quando colhido via loot ou via BE.

                // Para funcionar corretamente, o EssenceCropBlock guarda a essência no BE
                if (level.getBlockEntity(plantPos) instanceof com.infinitesoil.block.entity.EssenceCropBlockEntity cropBE) {
                    cropBE.setEssence(getEssence(stack));
                }

                if (!context.getPlayer().getAbilities().instabuild) {
                    stack.shrink(1);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        ResourceLocation essence = getEssence(stack);
        if (essence != null) {
            Item essenceItem = ForgeRegistries.ITEMS.getValue(essence);
            if (essenceItem != null) {
                tooltip.add(Component.literal("§aEssência: §f" + essenceItem.getDescription().getString()));
            } else {
                tooltip.add(Component.literal("§aEssência: §7" + essence.toString()));
            }
        } else {
            tooltip.add(Component.literal("§cSem essência definida"));
        }
        tooltip.add(Component.literal("§7Plante para obter a essência infinitamente"));
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public Component getName(ItemStack stack) {
        ResourceLocation essence = getEssence(stack);
        if (essence != null) {
            Item essenceItem = ForgeRegistries.ITEMS.getValue(essence);
            if (essenceItem != null) {
                return Component.literal("Semente de " + essenceItem.getDescription().getString());
            }
        }
        return Component.translatable(this.getDescriptionId(stack));
    }
}
