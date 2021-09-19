package io.github.cuubecon.powerfulbeacon.tileentity;

import io.github.cuubecon.powerfulbeacon.PowerfulBeacon;
import io.github.cuubecon.powerfulbeacon.block.ModBlocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntitys {

    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, PowerfulBeacon.MODID);

    // We don't have a datafixer for our TileEntities, so we pass null into build.
    public static final RegistryObject<TileEntityType<PowerfulBeaconTile>> POWERFUL_BEACON_TILE =
            TILE_ENTITY_TYPES.register("powerful_beacon_tile", () ->
            TileEntityType.Builder.of(PowerfulBeaconTile::new, ModBlocks.POWERFUL_BEACON.get())
                    .build(null)
    );

    public static void register(IEventBus eventBus)
    {
        TILE_ENTITY_TYPES.register(eventBus);
    }


}
