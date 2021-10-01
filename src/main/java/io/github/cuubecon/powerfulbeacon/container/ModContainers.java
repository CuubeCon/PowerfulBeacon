package io.github.cuubecon.powerfulbeacon.container;

import io.github.cuubecon.powerfulbeacon.PowerfulBeacon;
import net.minecraft.inventory.container.BeaconContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModContainers {

    public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, PowerfulBeacon.MODID);

    public static final RegistryObject<ContainerType<PowerfulBeaconContainer>> POWERFUL_BEACON_CONTAINER =
            CONTAINER_TYPES.register("powerful_beacon_container",
                    () -> IForgeContainerType.create(((windowId, inv, data) -> new PowerfulBeaconContainer(windowId, inv, IWorldPosCallable.NULL, new IntArray(5)))));

    public static void register(IEventBus eventBus)
    {
        CONTAINER_TYPES.register(eventBus);
    }



}
