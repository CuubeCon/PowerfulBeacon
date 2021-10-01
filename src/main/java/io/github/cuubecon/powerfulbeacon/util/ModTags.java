package io.github.cuubecon.powerfulbeacon.util;

import io.github.cuubecon.powerfulbeacon.PowerfulBeacon;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

public class ModTags
{
    public static class Blocks
    {
        public static final Tags.IOptionalNamedTag<Block> POWERFUL_BEACON_BASE_BLOCKS =
                createTag("powerful_beacon_base_blocks");

        public static final Tags.IOptionalNamedTag<Item> POWERFUL_BEACON_PAYMENT_ITEMS =
                createItemTag("powerful_beacon_paymennt_items");

        private static Tags.IOptionalNamedTag<Block> createTag(String name) {
            return BlockTags.createOptional(new ResourceLocation(PowerfulBeacon.MODID, name));
        }
        private static Tags.IOptionalNamedTag<Item> createItemTag(String name) {
            return ItemTags.createOptional(new ResourceLocation(PowerfulBeacon.MODID, name));
        }
        private static Tags.IOptionalNamedTag<Block> createForgeTag(String name) {
            return BlockTags.createOptional(new ResourceLocation("forge", name));
        }
    }
}
