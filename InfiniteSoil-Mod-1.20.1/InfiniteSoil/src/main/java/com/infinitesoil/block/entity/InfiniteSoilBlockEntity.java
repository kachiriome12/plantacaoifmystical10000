package com.infinitesoil.block.entity;

import com.infinitesoil.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * BlockEntity da Terra Infinita.
 * - Força crescimento de qualquer CropBlock acima a cada tick
 * - Quando maduro, colhe automaticamente e coloca os drops no inventário interno
 * - Hopper embaixo consegue extrair os itens (infinitamente porque colhe sempre)
 */
public class InfiniteSoilBlockEntity extends BlockEntity {

    private final ItemStackHandler itemHandler = new ItemStackHandler(9) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    // Contador para não colher todo tick (dá tempo visual, mas ainda é extremamente rápido)
    private int tickCounter = 0;
    private static final int HARVEST_INTERVAL = 2; // a cada 2 ticks = super rápido

    public InfiniteSoilBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.INFINITE_SOIL_BE.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, InfiniteSoilBlockEntity be) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        be.tickCounter++;

        BlockPos above = pos.above();
        BlockState cropState = level.getBlockState(above);
        Block cropBlock = cropState.getBlock();

        // === 1. Força crescimento máximo ===
        if (cropBlock instanceof CropBlock crop) {
            // Encontra a property de idade (AGE)
            IntegerProperty ageProperty = null;
            for (var prop : cropState.getProperties()) {
                if (prop.getName().equals("age") && prop instanceof IntegerProperty ip) {
                    ageProperty = ip;
                    break;
                }
            }

            if (ageProperty != null) {
                int currentAge = cropState.getValue(ageProperty);
                int maxAge = crop.getMaxAge();

                if (currentAge < maxAge) {
                    // Cresce 1 estágio por tick (velocidade máxima)
                    level.setBlock(above, cropState.setValue(ageProperty, currentAge + 1), 2);
                } else {
                    // Já está maduro → colhe se o intervalo passou
                    if (be.tickCounter >= HARVEST_INTERVAL) {
                        be.tickCounter = 0;
                        be.harvestCrop(serverLevel, above, cropState, crop);
                    }
                }
            }
        } else {
            // Tenta suportar outros blocos de planta que tenham "age" (ex: Mystical Agriculture)
            // MA crops geralmente extendem CropBlock ou têm age similar
            tryForceAge(level, above, cropState);
        }
    }

    private static void tryForceAge(Level level, BlockPos pos, BlockState state) {
        for (var prop : state.getProperties()) {
            if (prop.getName().equals("age") && prop instanceof IntegerProperty ageProp) {
                int current = state.getValue(ageProp);
                int max = ageProp.getPossibleValues().stream().mapToInt(i -> i).max().orElse(7);
                if (current < max) {
                    level.setBlock(pos, state.setValue(ageProp, current + 1), 2);
                }
                break;
            }
        }
    }

    private void harvestCrop(ServerLevel level, BlockPos cropPos, BlockState cropState, CropBlock crop) {
        // Gera os drops como se o jogador tivesse colhido com mão vazia
        LootParams.Builder lootBuilder = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(cropPos))
                .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                .withOptionalParameter(LootContextParams.THIS_ENTITY, null)
                .withOptionalParameter(LootContextParams.BLOCK_ENTITY, null);

        List<ItemStack> drops = cropState.getDrops(lootBuilder);

        // Tenta inserir os drops no inventário interno
        boolean anyInserted = false;
        for (ItemStack drop : drops) {
            if (drop.isEmpty()) continue;
            ItemStack remaining = drop.copy();
            for (int i = 0; i < itemHandler.getSlots() && !remaining.isEmpty(); i++) {
                remaining = itemHandler.insertItem(i, remaining, false);
            }
            if (remaining.getCount() < drop.getCount()) {
                anyInserted = true;
            }
            // Se sobrou, dropa no mundo (acima da terra)
            if (!remaining.isEmpty()) {
                Block.popResource(level, this.worldPosition.above(), remaining);
            }
        }

        // Reseta a planta para idade 0 (replant automático = infinito)
        IntegerProperty ageProperty = null;
        for (var prop : cropState.getProperties()) {
            if (prop.getName().equals("age") && prop instanceof IntegerProperty ip) {
                ageProperty = ip;
                break;
            }
        }
        if (ageProperty != null) {
            level.setBlock(cropPos, cropState.setValue(ageProperty, 0), 2);
        }

        if (anyInserted) {
            setChanged();
        }
    }

    // === Capability para Hopper ===
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            // Aceita extração de qualquer lado (especialmente de baixo pela hopper)
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }
}
