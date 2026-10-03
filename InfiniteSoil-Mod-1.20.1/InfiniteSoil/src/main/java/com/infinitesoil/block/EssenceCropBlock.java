package com.infinitesoil.block;

import com.infinitesoil.block.entity.EssenceCropBlockEntity;
import com.infinitesoil.init.ModBlockEntities;
import com.infinitesoil.item.EssenceSeedItem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * Crop que dropa a essência definida no NBT da semente.
 */
public class EssenceCropBlock extends CropBlock implements EntityBlock {

    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 7);
    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
            Block.box(0, 0, 0, 16, 2, 16),
            Block.box(0, 0, 0, 16, 4, 16),
            Block.box(0, 0, 0, 16, 6, 16),
            Block.box(0, 0, 0, 16, 8, 16),
            Block.box(0, 0, 0, 16, 10, 16),
            Block.box(0, 0, 0, 16, 12, 16),
            Block.box(0, 0, 0, 16, 14, 16),
            Block.box(0, 0, 0, 16, 16, 16)
    };

    public EssenceCropBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public int getMaxAge() {
        return 7;
    }

    @Override
    protected ItemLike getBaseSeedItem() {
        return com.infinitesoil.init.ModItems.ESSENCE_SEED.get();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_BY_AGE[state.getValue(AGE)];
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Crescimento normal (mais lento). Na Terra Infinita o BE da terra força o crescimento.
        super.randomTick(state, level, pos, random);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        // Drops customizados via BlockEntity
        BlockEntity be = builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.BLOCK_ENTITY);
        if (be instanceof EssenceCropBlockEntity cropBE && cropBE.getEssence() != null) {
            var essenceItem = ForgeRegistries.ITEMS.getValue(cropBE.getEssence());
            if (essenceItem != null) {
                // Dropa 1-3 essências + chance de semente
                int count = 1 + levelRandom(builder).nextInt(3);
                ItemStack essenceStack = new ItemStack(essenceItem, count);

                ItemStack seedStack = EssenceSeedItem.createSeed(cropBE.getEssence());

                return List.of(essenceStack, seedStack);
            }
        }
        return Collections.emptyList();
    }

    private RandomSource levelRandom(LootParams.Builder builder) {
        return RandomSource.create(); // simplificado
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EssenceCropBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return null; // não precisa de ticker próprio
    }
}
