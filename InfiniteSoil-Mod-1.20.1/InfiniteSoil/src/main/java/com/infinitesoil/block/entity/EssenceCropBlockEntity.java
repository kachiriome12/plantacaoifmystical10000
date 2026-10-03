package com.infinitesoil.block.entity;

import com.infinitesoil.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class EssenceCropBlockEntity extends BlockEntity {

    private ResourceLocation essenceId = null;

    public EssenceCropBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ESSENCE_CROP_BE.get(), pos, state);
    }

    public void setEssence(ResourceLocation id) {
        this.essenceId = id;
        setChanged();
    }

    public ResourceLocation getEssence() {
        return essenceId;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (essenceId != null) {
            tag.putString("EssenceId", essenceId.toString());
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("EssenceId")) {
            essenceId = ResourceLocation.tryParse(tag.getString("EssenceId"));
        }
    }
}
