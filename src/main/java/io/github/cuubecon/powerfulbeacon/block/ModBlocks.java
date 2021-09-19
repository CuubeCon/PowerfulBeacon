package io.github.cuubecon.powerfulbeacon.block;


import io.github.cuubecon.powerfulbeacon.PowerfulBeacon;
import io.github.cuubecon.powerfulbeacon.item.ModItems;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Rarity;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ModBlocks {

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, PowerfulBeacon.MODID);

    public static final RegistryObject<Block> POWERFUL_BEACON =
            registerBlock("powerful_beacon", () -> new PowerfulBeaconBlock(AbstractBlock.Properties.of(Material.GLASS, MaterialColor.DIAMOND).strength(6.0F).lightLevel((p_235456_0_) -> {
        return 15;
    }).noOcclusion().harvestLevel(3).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops()));

    private static <T extends Block>RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }
    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(),
                new Item.Properties().tab(ItemGroup.TAB_MISC).rarity(Rarity.EPIC)));
    }

    public static void register(IEventBus eventbus) {
        BLOCKS.register(eventbus);
    }
}
