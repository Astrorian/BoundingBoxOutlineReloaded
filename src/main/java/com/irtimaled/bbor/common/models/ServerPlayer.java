package com.irtimaled.bbor.common.models;

import com.irtimaled.bbor.common.messages.PayloadBuilder;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.EntityPlayer;

import java.util.function.Consumer;

public class ServerPlayer {

    private final DimensionId dimensionId;
    private final Consumer<Packet<?>> packetConsumer;

    public ServerPlayer(EntityPlayer player) {
        this.dimensionId = DimensionId.from(player.H.ab());
        this.packetConsumer = player.b::a;
    }

    public DimensionId getDimensionId() {
        return dimensionId;
    }

    public void sendPacket(PayloadBuilder payloadBuilder) {
        packetConsumer.accept(payloadBuilder.build());
    }
}
