package io.github.cuubecon.powerfulbeacon.block;

import io.github.cuubecon.powerfulbeacon.container.PowerfulBeaconContainer;
import io.github.cuubecon.powerfulbeacon.tileentity.ModTileEntitys;
import io.github.cuubecon.powerfulbeacon.tileentity.PowerfulBeaconTile;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class PowerfulBeaconBlock extends ContainerBlock  implements IBeaconBeamColorProvider {

    protected PowerfulBeaconBlock(Properties p_i48446_1_) {
        super(p_i48446_1_);
    }

    @Override
    public DyeColor getColor() {
        return DyeColor.YELLOW;
    }



    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
        return ModTileEntitys.POWERFUL_BEACON_TILE.get().create();
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }

    @Override
    public ActionResultType use(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
        if (p_225533_2_.isClientSide) {
            return ActionResultType.SUCCESS;
        } else {
            TileEntity tileentity = p_225533_2_.getBlockEntity(p_225533_3_);
            if (tileentity instanceof PowerfulBeaconTile) {
                p_225533_4_.openMenu((PowerfulBeaconTile)tileentity);

            }

            return ActionResultType.CONSUME;
        }
    }




    @Override
    public BlockRenderType getRenderShape(BlockState p_149645_1_) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void setPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, LivingEntity p_180633_4_, ItemStack p_180633_5_) {
        if (p_180633_5_.hasCustomHoverName()) {
            TileEntity tileentity = p_180633_1_.getBlockEntity(p_180633_2_);
            if (tileentity instanceof BeaconTileEntity) {
                ((BeaconTileEntity)tileentity).setCustomName(p_180633_5_.getHoverName());
            }
        }

    }
}
