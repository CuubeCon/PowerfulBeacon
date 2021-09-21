package io.github.cuubecon.powerfulbeacon.network;

import io.github.cuubecon.powerfulbeacon.PowerfulBeacon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PowerfulBeaconPacketHandler {

    private static final String PROTOCOL_VERSION = "1";

    public static int ID = 0;
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(PowerfulBeacon.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    public static void register()
    {

       INSTANCE.registerMessage(ID++, cUpdatePowerfulBeaconPacket.class, cUpdatePowerfulBeaconPacket::encode, cUpdatePowerfulBeaconPacket::new, cUpdatePowerfulBeaconPacket::handle);

    }
}
