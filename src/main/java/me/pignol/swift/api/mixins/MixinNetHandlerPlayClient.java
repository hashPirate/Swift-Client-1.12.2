package me.pignol.swift.api.mixins;

import me.pignol.swift.client.modules.render.ChunkFinder;
import me.pignol.swift.client.modules.render.NoRenderModule;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {

    @ModifyArg(
            method = {"handleJoinGame", "handleRespawn"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;displayGuiScreen(Lnet/minecraft/client/gui/GuiScreen;)V")
    )
    private GuiScreen skipTerrainScreen(GuiScreen original) {
        if (NoRenderModule.INSTANCE.isEnabled() && NoRenderModule.INSTANCE.loadingScreen.getValue()) {
            return null;
        }
        return original;
    }

    @Inject(method = "handleChunkData", at = @At("RETURN"))
    public void handleChunkData(SPacketChunkData chunkData, CallbackInfo ci) {
        if (!chunkData.isFullChunk()) {
            if (ChunkFinder.INSTANCE.isEnabled()) {
                ChunkFinder.INSTANCE.addChunk(new ChunkPos(chunkData.getChunkX(), chunkData.getChunkZ()));
            }
        }
    }

}
