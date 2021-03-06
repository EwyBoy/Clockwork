package Block.Blocks;

import Block.Technical.BlockInfo;
import Block.Texture.TextureHandler;
import CreativeTabs.CWCreativeTabs;
import Engines.ConveyorEngine;
import TechnicalTools.Coordinate;
import Tile.TileEntity.TileEntityConveyor;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

import java.util.List;

public class Conveyor extends ConveyorEngine
    {
        public Conveyor(int par1, boolean par2)
        {
            super(par1);
            setHardness(1.0F);
            setCreativeTab(CWCreativeTabs.ClockworkBlockTab);
            setBlockBounds(0.0F, 0F, 0.0F, 1.0F, 1F, 1.0F);
            setUnlocalizedName(BlockInfo.ConveyorUnlocalized);
        }

        ItemStack potionEmptyStack = new ItemStack(Item.glassBottle);

        public boolean isBlockSolid()
        {
    	    return false;
        }

        public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving entity)
        {
            int var = ((MathHelper.floor_double((double)(entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) + 2) % 4;
            world.setBlockMetadataWithNotify(x, y, z, var, 0);
        }

        public int getRenderBlockPass()
        {
            return 1;
        }

        public boolean isOpaqueCube()
        {
            return false;
          //  return true;
        }

        public boolean renderAsNormalBlock()
        {
            return false;
        }

        public static int determineOrientation(World par0World, int par1, int par2, int par3, EntityPlayer par4EntityPlayer)
        {
            if (MathHelper.abs((float)par4EntityPlayer.posX - (float)par1) < 2.0F && MathHelper.abs((float)par4EntityPlayer.posZ - (float)par3) < 2.0F)
            {
                double var5 = par4EntityPlayer.posY + 1.82D - (double)par4EntityPlayer.yOffset;

                if (var5 - (double)par2 > 2.0D)
                {
                    return 1;
                }

                if ((double)par2 - var5 > 0.0D)
                {
                    return 0;
                }
            }

            int var7 = MathHelper.floor_double((double)(par4EntityPlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
            return var7 == 0 ? 2 : (var7 == 1 ? 5 : (var7 == 2 ? 3 : (var7 == 3 ? 4 : 0)));
        }

        public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity)
        {
            double m_speed = 0.05;
            int a = par1World.getBlockMetadata(par2, par3, par4);
            int ax[] = {0, 1, 0, -1};
            int az[] = { -1, 0, 1, 0};

            if (par5Entity != null && par5Entity.posY > (par3 + 0.5)  && !par5Entity.isSneaking())
            {
                if (par5Entity instanceof EntityItem)
                {
                    int x = par2, y = par3, z = par4;
                    ItemStack my_item;

                    if (par5Entity instanceof EntityItem)
                    {
                        my_item = ((EntityItem) par5Entity).getEntityItem();
                    }
                    else
                    {
                        my_item = potionEmptyStack;
                    }

                    if (my_item != null && my_item.itemID != 0)
                    {
                        for (int j = 0; j < 4; j += 1)
                        {
                            if (par1World.getBlockTileEntity(x + ax[j % 4], y - 1, z + az[j % 4]) != null & par1World.getBlockTileEntity(x + ax[j % 4], y - 1, z + az[j % 4]) instanceof IInventory)
                            {
                                IInventory chest = (IInventory)par1World.getBlockTileEntity(x + ax[j % 4], y - 1, z + az[j % 4]);

                                for (int i = 0; i < chest.getSizeInventory(); i += 1)
                                {
                                    ItemStack ch_item = chest.getStackInSlot(i);

                                    if (ch_item != null && ch_item.itemID == my_item.itemID && ch_item.getItemDamage() == my_item.getItemDamage())
                                    {
                                        a = j % 4;
                                    }
                                }
                            }
                        }
                    }
                }

                //Move to center of conveyor
                if (ax[a] == 0 & Math.abs(par2 + 0.5 - par5Entity.posX) < 0.5 & Math.abs(par2 + 0.5 - par5Entity.posX) > 0.1)
                {
                    par5Entity.motionX += Math.signum(par2 + 0.5 - par5Entity.posX) * Math.min(m_speed, Math.abs(par2 + 0.5 - par5Entity.posX)) / 1.2;
                }

                if (az[a] == 0 & Math.abs(par4 + 0.5 - par5Entity.posZ) < 0.5 & Math.abs(par4 + 0.5 - par5Entity.posZ) > 0.1)
                {
                    par5Entity.motionZ += Math.signum(par4 + 0.5 - par5Entity.posZ) * Math.min(m_speed, Math.abs(par4 + 0.5 - par5Entity.posZ)) / 1.2;
                }

                //Jump entities up
                if (par5Entity instanceof EntityItem)
                    if (par1World.getBlockId(par2, par3 + 2, par4) == 0 & par1World.getBlockId(par2 + ax[a], par3 + 1, par4 + az[a]) == this.blockID)
                    {
                        double progress = (par5Entity.posX - par2 - 0.5) * ax[a] + (par5Entity.posZ - par4 - 0.5) * az[a];
                        double prog_speed = par5Entity.motionX * ax[a] + par5Entity.motionZ * az[a];
                        double prog_counterspeed = Math.abs(par5Entity.motionX * az[a] + par5Entity.motionZ * ax[a]);

                        if (progress > 0 | (progress > -0.2 & prog_speed < 0))
                        {
                            a = (a + 2) % 4;
                        }
                        else if (progress + 1.5 * prog_speed > 0 & prog_speed >= m_speed & prog_counterspeed < 0.2)
                        {
                            par5Entity.moveEntity(0, 0.05, 0);
                            par5Entity.isAirBorne = true;

                            if (ax[a] == 0)
                            {
                                par5Entity.motionX = 0;
                            }

                            if (az[a] == 0)
                            {
                                par5Entity.motionZ = 0;
                            }

                            //par5Entity.motionX = ax[a] * m_speed;
                            //par5Entity.motionY = 0.2;
                            //par5Entity.motionZ = az[a] * m_speed;
                            par5Entity.addVelocity(0, 0.19, 0);
                            par5Entity.setVelocity(par5Entity.motionX, par5Entity.motionY, par5Entity.motionZ);
                            return;
                        }
                    }

                //if(!(Math.signum(par5Entity.motionX)==ax[a] & Math.abs(par5Entity.motionX)>=3*m_speed))
                par5Entity.motionX = par5Entity.motionX + ax[a] * m_speed;
                //if(!(Math.signum(par5Entity.motionZ)==az[a] & Math.abs(par5Entity.motionZ)>=3*m_speed))
                par5Entity.motionZ = par5Entity.motionZ + az[a] * m_speed;

                //Other codes

                if(!par1World.isRemote && par5Entity instanceof EntityItem)
                {
                    specialRoute(par1World, par2, par3, par4, (EntityItem)par5Entity);
                }

                double xVelocity = 0;
                double yVelocity = 0;
                double zVelocity = 0;

                int md = par1World.getBlockMetadata(par2, par3, par4);

                int horizDirection = md & 0x03;
                boolean isUphill = (md & 0x04) != 0;
                boolean isDownhill = (md & 0x08) != 0;

                if(isUphill)
                {
                    yVelocity = 0.25D;
                }

                if(isUphill || isDownhill)
                {
                    par5Entity.onGround = false;
                }

                if(horizDirection == 0)
                {
                    xVelocity = 0.1D;
                }
                else if(horizDirection == 1)
                {
                    zVelocity = 0.1D;
                }
                else if(horizDirection == 2)
                {
                    xVelocity = 0.1D;
                }
                else if(horizDirection == 3)
                {
                    zVelocity = 0.1D;
                }

                if(horizDirection == 0 || horizDirection == 2)
                {
                    if(par5Entity.posZ > par4 + 0.55D)
                    {
                        zVelocity = -0.1D;
                    }
                    else if(par5Entity.posZ < par4 + 0.45D)
                    {
                        zVelocity = 0.1D;
                    }
                }
                else if(horizDirection == 1 || horizDirection == 3)
                {
                    if(par5Entity.posX > par2 + 0.55D)
                    {
                        xVelocity = -0.1D;
                    }
                    else if(par5Entity.posX < par2 + 0.45D)
                    {
                        xVelocity = 0.1D;
                    }
                }

                setEntityVelocity(par5Entity, xVelocity, yVelocity, zVelocity);

                if(par5Entity instanceof EntityLiving)
                {
                    ((EntityLiving)par5Entity).fallDistance = 0;
                }
                else if(par5Entity instanceof EntityItem)
                {
                    ((EntityItem)par5Entity).delayBeforeCanPickup = 40;
                }
            }
            }


    /*public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity)
    {
        double m_speed = 0.05;
        int a = par1World.getBlockMetadata(par2, par3, par4);
        int ax[] = {0, 1, 0, -1};
        int az[] = { -1, 0, 1, 0};

        if (par5Entity != null && par5Entity.posY > (par3 + 0.5)  && !par5Entity.isSneaking())
        {
            if (par5Entity instanceof EntityItem | par5Entity instanceof EntityBloodOrb)
            {
                int x = par2, y = par3, z = par4;
                ItemStack my_item;

                if (par5Entity instanceof EntityItem)
                {
                    my_item = ((EntityItem) par5Entity).item;
                }
                else
                {
                    my_item = potionEmptyStack;
                }

                if (my_item != null && my_item.itemID != 0)
                {
                    for (int j = 0; j < 4; j += 1)
                    {
                        if (par1World.getBlockTileEntity(x + ax[j % 4], y - 1, z + az[j % 4]) != null & par1World.getBlockTileEntity(x + ax[j % 4], y - 1, z + az[j % 4]) instanceof IInventory)
                        {
                            IInventory chest = (IInventory)par1World.getBlockTileEntity(x + ax[j % 4], y - 1, z + az[j % 4]);

                            for (int i = 0; i < chest.getSizeInventory(); i += 1)
                            {
                                ItemStack ch_item = chest.getStackInSlot(i);

                                if (ch_item != null && ch_item.itemID == my_item.itemID && ch_item.getItemDamage() == my_item.getItemDamage())
                                {
                                    a = j % 4;
                                }
                            }
                        }
                    }
                }
            }

            if (ax[a] == 0 & Math.abs(par2 + 0.5 - par5Entity.posX) < 0.5 & Math.abs(par2 + 0.5 - par5Entity.posX) > 0.2)
            {
                par5Entity.motionX += Math.signum(par2 + 0.5 - par5Entity.posX) * Math.min(m_speed, Math.abs(par2 + 0.5 - par5Entity.posX)) / 2;
            }

            if (az[a] == 0 & Math.abs(par4 + 0.5 - par5Entity.posZ) < 0.5 & Math.abs(par4 + 0.5 - par5Entity.posZ) > 0.2)
            {
                par5Entity.motionZ += Math.signum(par4 + 0.5 - par5Entity.posZ) * Math.min(m_speed, Math.abs(par4 + 0.5 - par5Entity.posZ)) / 2;
            }

            //if(!(Math.signum(par5Entity.motionX)==ax[a] & Math.abs(par5Entity.motionX)>=3*m_speed))
            par5Entity.motionX = par5Entity.motionX + ax[a] * m_speed;
            //if(!(Math.signum(par5Entity.motionZ)==az[a] & Math.abs(par5Entity.motionZ)>=3*m_speed))
            par5Entity.motionZ = par5Entity.motionZ + az[a] * m_speed;
        }
    }*/

        public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
        {
            float var5 = 0.0625F;
           // return AxisAlignedBB.getAABBPool().addOrModifyAABBInPool((double)((float)par2 + var5), (double)par3, (double)((float)par4 + var5), (double)((float)(par2 + 1) - var5), (double)((float)(par3 + 1) - var5), (double)((float)(par4 + 1) - var5));
            return AxisAlignedBB.getAABBPool().addOrModifyAABBInPool(par2, par3, par4, par2 + 1, (double)((float)(par3 + 1) - var5), par4 + 1);
        }

        public boolean canCreatureSpawn(EnumCreatureType type, World world, int x, int y, int z)
        {
            return true;
        }

        public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side)
        {
            //return side == ForgeDirection.UP;
            //return side != ForgeDirection.DOWN;
            return true;
        }

    //-------------------------------------------------------------------------------

        @SideOnly(Side.CLIENT)
        private Icon Texture;

        @SideOnly(Side.CLIENT)
        private Icon TextureTurn;

        @SideOnly(Side.CLIENT)
        private Icon Textures;

        @Override
        @SideOnly(Side.CLIENT)
        public void registerIcons(IconRegister register)
        {
            Texture = register.registerIcon(TextureHandler.TextureLocation + ":" +  BlockInfo.ConveyorTexture);
            TextureTurn = register.registerIcon(TextureHandler.TextureLocation + ":" +  BlockInfo.ConveyorTextureTurn);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public Icon getIcon(int side, int meta)
        {
            if (meta >= 6)
            {
                return TextureTurn;
            }
            else if (meta <= 6)
            {
                return Texture;
            }
            return Texture;

        }

        @SideOnly(Side.CLIENT)
        public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving entity, ItemStack stack)
        {
            if (entity == null)
            {
                return;
            }
            int facing = MathHelper.floor_double((entity.rotationYaw * 4F) / 360F + 0.5D) & 3;

            if (facing == 0)
            {
                world.setBlockMetadataWithNotify(x, y, z, 1, 2);
            }
            if (facing == 1)
            {
                world.setBlockMetadataWithNotify(x, y, z, 2, 2);
            }
            if (facing == 2)
            {
                world.setBlockMetadataWithNotify(x, y, z, 3, 2);
            }
            if (facing == 3)
            {
                world.setBlockMetadataWithNotify(x, y, z, 0, 2);
            }
        }



       /* @Override
        public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
        {

            if(!world.isRemote && entity instanceof EntityItem)
            {
                specialRoute(world, x, y, z, (EntityItem)entity);
            }

            double xVelocity = 0;
            double yVelocity = 0;
            double zVelocity = 0;

            int md = world.getBlockMetadata(x, y, z);

            int horizDirection = md & 0x03;
            boolean isUphill = (md & 0x04) != 0;
            boolean isDownhill = (md & 0x08) != 0;

            if(isUphill)
            {
                yVelocity = 0.25D;
            }

            if(isUphill || isDownhill)
            {
                entity.onGround = false;
            }

            if(horizDirection == 0)
            {
                xVelocity = 0.1D;
            }
            else if(horizDirection == 1)
            {
                zVelocity = 0.1D;
            }
            else if(horizDirection == 2)
            {
                xVelocity = 0.1D;
            }
            else if(horizDirection == 3)
            {
                zVelocity = 0.1D;
            }

            if(horizDirection == 0 || horizDirection == 2)
            {
                if(entity.posZ > z + 0.55D)
                {
                    zVelocity = -0.1D;
                }
                else if(entity.posZ < z + 0.45D)
                {
                    zVelocity = 0.1D;
                }
            }
            else if(horizDirection == 1 || horizDirection == 3)
            {
                if(entity.posX > x + 0.55D)
                {
                    xVelocity = -0.1D;
                }
                else if(entity.posX < x + 0.45D)
                {
                    xVelocity = 0.1D;
                }
            }



            setEntityVelocity(entity, xVelocity, yVelocity, zVelocity);

            if(entity instanceof EntityLiving)
            {
                ((EntityLiving)entity).fallDistance = 0;
            }
            else if(entity instanceof EntityItem)
            {
                ((EntityItem)entity).delayBeforeCanPickup = 40;
            }
        }
*/

        private void setEntityVelocity(Entity entity, double x, double y, double z)
        {
            entity.motionX = x;
            entity.motionY = y;
            entity.motionZ = z;
        }


        private void specialRoute(World world, int x, int y, int z, EntityItem entityitem)
        {
            TileEntity teBelow = world.getBlockTileEntity(x, y - 1, z);
            if(teBelow == null || entityitem.isDead)
            {
                return;
            }

            else if(teBelow instanceof TileEntityHopper)
            {
                if(!((TileEntityHopper)teBelow).isCoolingDown())
                {
                    ItemStack toInsert = entityitem.getEntityItem().copy();
                    toInsert.stackSize = 1;
                    toInsert = TileEntityHopper.insertStack((IInventory)teBelow, toInsert, ForgeDirection.UP.ordinal());
                    if(toInsert == null)
                    {
                        entityitem.getEntityItem().stackSize--;
                        ((TileEntityHopper)teBelow).setTransferCooldown(8);
                    }
                }
            }
        }


        /**
         @Override
         public void onEntityCollidedWithBlock(World world, int x, int y,int z, Entity entity) {

         double xVelocity = 1;
         double yVelocity = 2;
         double zVelocity = 3;

         int md = world.getBlockMetadata(x, y, z);

         int horizDirection = md & 0x03;
         boolean isUphill = (md & 0x4) !=0;
         boolean isDownhill = (md & 0x08) !=0;

         if (isUphill)
         {
         entity.addVelocity(0, 0.05, 0); //yVelocity = 2.25D;
         }

         if (isUphill || isDownhill)
         {
         entity.onGround = false;
         }

         if (isDownhill)
         {
         //entity.addVelocity(0, -0.1, 0);
         }

         if (horizDirection == 0)
         {
         entity.addVelocity(0, 0, -0.05); //xVelocity = 0.5D;
         }
         else if (horizDirection == 1)
         {
         entity.addVelocity(0.05, 0, 0); //zVelocity = 0.5D;
         }
         else if (horizDirection == 2)
         {
         entity.addVelocity(0, 0, 0.05); //xVelocity = -0.5D;
         }
         else if (horizDirection == 3)
         {
         entity.addVelocity(-0.05, 0, 0); //zVelocity = -0.5D;
         }


         }



         @Override
         public void onEntityCollidedWithBlock(World worldObj, int x, int y, int z, Entity entity)
         {
         TileEntity TE = worldObj.getBlockTileEntity(x, y, z);

         //int facing = ((TileEntityConveyor) (TE)).getDir(new Coordinate(x, y, z));
         //System.out.println(facing);

         if(TE instanceof TileEntityConveyor)
         {
         try{
         switch(((TileEntityConveyor) TE).getDir(new Coordinate(x, y, z))){
         case 0:
         entity.setVelocity(0, 0, 0.5);
         break;
         case 1:
         entity.setVelocity(-0.5, 0, 0);
         break;
         case 2:
         entity.setVelocity(0, 0, -0.5);
         break;
         case 3:
         entity.setVelocity(0.5, 0, 0);
         break;

         }
         }catch(NullPointerException NPEx){
         System.out.println("NPEx!");
         NPEx.printStackTrace();
         }
         }
         }

         **/


       @Override
        public void onBlockPlacedBy(World par1World, int par2, int par3, int par4,EntityLivingBase entity, ItemStack par6ItemStack) {
            int facing = MathHelper.floor_double((entity.rotationYaw * 4F) / 360F + 0.5D) & 3;
            System.out.println(facing);
            new TileEntityConveyor().setDir(facing, new Coordinate(par2, par3, par4));
        }

    }

