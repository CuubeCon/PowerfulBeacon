package io.github.cuubecon.powerfulbeacon.util;

import io.github.cuubecon.powerfulbeacon.PowerfulBeacon;
import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

public class ModTags
{
    public static class Blocks
    {
        public static final Tags.IOptionalNamedTag<Block> POWERFUL_BEACON_BASE_BLOCKS =
                createTag("powerful_beacon_base_blocks");

        private static Tags.IOptionalNamedTag<Block> createTag(String name) {
            return BlockTags.createOptional(new ResourceLocation(PowerfulBeacon.MODID, name));
        }

        private static Tags.IOptionalNamedTag<Block> createForgeTag(String name) {
            return BlockTags.createOptional(new ResourceLocation("forge", name));
        }
    }
}
