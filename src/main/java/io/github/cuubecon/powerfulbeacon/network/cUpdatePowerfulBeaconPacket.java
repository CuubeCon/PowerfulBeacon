package io.github.cuubecon.powerfulbeacon.network;

import io.github.cuubecon.powerfulbeacon.container.PowerfulBeaconContainer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class cUpdatePowerfulBeaconPacket
{
    private int primary;
    private int secondary;

     cUpdatePowerfulBeaconPacket(PacketBuffer buffer){
    read(buffer);
    }

    @OnlyIn(Dist.CLIENT)
    public cUpdatePowerfulBeaconPacket(int p_i49544_1_, int p_i49544_2_) {
        this.primary = p_i49544_1_;
        this.secondary = p_i49544_2_;
    }

     void read(PacketBuffer p_148837_1_){
        this.primary = p_148837_1_.readVarInt();
        this.secondary = p_148837_1_.readVarInt();
    }

     void encode(PacketBuffer p_148840_1_) {
        p_148840_1_.writeVarInt(this.primary);
        p_148840_1_.writeVarInt(this.secondary);
    }

     static void handle(cUpdatePowerfulBeaconPacket msg, Supplier<NetworkEvent.Context> ctx) {

         ctx.get().enqueueWork(() -> {
             // Work that needs to be thread-safe (most work)
             ServerPlayerEntity sender = ctx.get().getSender(); // the client that sent this packet
             // Do stuff

             if (sender.containerMenu instanceof PowerfulBeaconContainer) {
                 ((PowerfulBeaconContainer)sender.containerMenu).updateEffects(msg.getPrimary(), msg.getSecondary());
             }
         });
         ctx.get().setPacketHandled(true);
    }

    public int getPrimary() {
        return this.primary;
    }

    public int getSecondary() {
        return this.secondary;
    }
}
