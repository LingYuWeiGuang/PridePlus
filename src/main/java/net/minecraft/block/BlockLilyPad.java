package net.minecraft.block;

import java.util.List;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.vialoadingbase.ViaLoadingBase;

public class BlockLilyPad extends BlockBush
{
    protected BlockLilyPad()
    {
        float f = 0.5F;
        float f1 = 0.015625F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f1, 0.5F + f);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity)
    {
        if (!(collidingEntity instanceof EntityBoat))
        {
            super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
        }
    }

    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state)
    {
        if (ViaLoadingBase.getInstance().getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            return new AxisAlignedBB(pos.getX() + this.minX, pos.getY() + this.minY, pos.getZ() + this.minZ, pos.getX() + this.maxX, pos.getY() + this.maxY, pos.getZ() + this.maxZ);
        } else {
            return new AxisAlignedBB(pos.getX() + 0.0625D, pos.getY(), pos.getZ() + 0.0625D, pos.getX() + 0.9375D, pos.getY() + 0.09375D, pos.getZ() + 0.9375D);
        }
    }

    public int getBlockColor()
    {
        return 7455580;
    }

    public int getRenderColor(IBlockState state)
    {
        return 7455580;
    }

    public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass)
    {
        return 2129968;
    }

    protected boolean canPlaceBlockOn(Block ground)
    {
        return ground == Blocks.water;
    }

    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
    {
        if (pos.getY() >= 0 && pos.getY() < 256)
        {
            IBlockState iblockstate = worldIn.getBlockState(pos.down());
            return iblockstate.getBlock().getMaterial() == Material.water && iblockstate.getValue(BlockLiquid.LEVEL) == 0;
        }
        else
        {
            return false;
        }
    }

    public int getMetaFromState(IBlockState state)
    {
        return 0;
    }
}
