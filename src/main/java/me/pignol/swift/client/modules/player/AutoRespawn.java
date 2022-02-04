package me.pignol.swift.client.modules.player;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.pignol.swift.api.util.objects.StopWatch;
import me.pignol.swift.api.util.text.ChatUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoRespawn extends Module {

    private final Value<Boolean> chat = new Value<>("Chat", true);

    private final StopWatch stopWatch = new StopWatch();

    public AutoRespawn() {
        super("AutoRespawn", Category.PLAYER);
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (mc.currentScreen instanceof GuiGameOver && stopWatch.passed(3000)) { // literally impossible to die 2 times within 3 seconds
            mc.getConnection().sendPacket(new CPacketClientStatus(CPacketClientStatus.State.PERFORM_RESPAWN));
            if (chat.getValue()) {
                ChatUtil.sendMessage(ChatFormatting.RED + "Respawning.");
            }
            stopWatch.reset();
        }
    }

}
