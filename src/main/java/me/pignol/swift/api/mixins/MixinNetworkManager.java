package me.pignol.swift.api.mixins;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.GenericFutureListener;
import me.pignol.swift.api.interfaces.mixin.INetworkManager;
import me.pignol.swift.client.event.events.PacketEvent;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.concurrent.Future;

@Mixin(NetworkManager.class)
public abstract class MixinNetworkManager implements INetworkManager {

    @Shadow
    public abstract boolean isChannelOpen();


    @Shadow
    protected abstract void flushOutboundQueue();

    @Shadow
    protected abstract void dispatchPacket(final Packet<?> inPacket, @Nullable final GenericFutureListener<? extends Future<? super Void >>[] futureListeners);

    @Override
    public Packet<?> sendPacketNoEvent(Packet<?> packetIn) {
        if (this.isChannelOpen()) {
            this.flushOutboundQueue();
            this.dispatchPacket(packetIn, null);
            return packetIn;
        }
        return null;
    }

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void sendPacket(Packet<?> packetIn, CallbackInfo ci) {
        if (MinecraftForge.EVENT_BUS.post(new PacketEvent.Send(packetIn))) {
            ci.cancel();
        }
    }

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void sendPacket(ChannelHandlerContext channel, Packet<?> packetIn, CallbackInfo ci) {
        if (MinecraftForge.EVENT_BUS.post(new PacketEvent.Receive(packetIn))) {
            ci.cancel();
        }
    }

}
