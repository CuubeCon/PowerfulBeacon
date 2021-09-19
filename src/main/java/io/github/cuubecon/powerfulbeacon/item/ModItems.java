package io.github.cuubecon.powerfulbeacon.item;

import io.github.cuubecon.powerfulbeacon.PowerfulBeacon;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS
            = DeferredRegister.create(ForgeRegistries.ITEMS, PowerfulBeacon.MODID);


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
