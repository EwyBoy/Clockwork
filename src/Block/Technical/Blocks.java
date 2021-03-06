package Block.Technical;

import Block.Blocks.*;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;


public class Blocks 
{
	public static Block Trans;
    public static Block Fan;
    public static Block Conveyor;
    public static Block ConveyorTest;
    public static Block Router;
    public static Block Metals;

	public static void init()
	{
		registerBlocks();
		addNames();
	}

	private static void registerBlocks() 
	{
		Trans = new TransBlock(BlockInfo.TransID, false).setUnlocalizedName(BlockInfo.TransUnlocalized);
		GameRegistry.registerBlock(Trans, BlockInfo.TransKey);

        Fan = new Fan( BlockInfo.FanID, false).setUnlocalizedName(BlockInfo.FanUnlocalized);
        GameRegistry.registerBlock(Fan, BlockInfo.FanKey);

        Conveyor = new Conveyor(BlockInfo.ConveyorID, false).setUnlocalizedName(BlockInfo.ConveyorUnlocalized);
        GameRegistry.registerBlock(Conveyor, BlockInfo.ConveyorKey);

        ConveyorTest = new ConveyorTest(BlockInfo.ConveyorID1, false).setUnlocalizedName(BlockInfo.ConveyorUnlocalized1);
        GameRegistry.registerBlock(ConveyorTest, BlockInfo.ConveyorKey1);

        Router = new Router(BlockInfo.RouterID, false).setUnlocalizedName(BlockInfo.RouterUnlocalized);
        GameRegistry.registerBlock(Router, BlockInfo.RouterKey);

        Metals = new Metals (BlockInfo.MetalsID, false).setUnlocalizedName(BlockInfo.MetalsUnlocalized);
        GameRegistry.registerBlock(Metals, BlockInfo.MetalsKey);
    }

	public static void addNames()
	{
		LanguageRegistry.addName(Trans, BlockInfo.TransName);
        LanguageRegistry.addName(Fan, BlockInfo.FanName);
        LanguageRegistry.addName(Conveyor, BlockInfo.ConveyorName);
        LanguageRegistry.addName(Router, BlockInfo.RouterName);

        for (int i = 0; i < BlockInfo.MetalsNames.length; i++)
        {
            LanguageRegistry.addName(new ItemStack(Metals, 1, i), BlockInfo.MetalsNames[i]);
        }
	}

	
}
