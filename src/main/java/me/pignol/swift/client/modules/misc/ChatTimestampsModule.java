package me.pignol.swift.client.modules.misc;

import me.pignol.swift.api.util.text.TextColor;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.PacketEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ChatTimestampsModule extends Module {

    private final Value<Boolean> rainbow = new Value<>("Rainbow", false);
    private final Value<TextColor.Color> bracketColor = new Value<>("BracketColor", TextColor.Color.WHITE);
    private final Value<TextColor.Color> timeColor = new Value<>("TimeColor", TextColor.Color.WHITE);

    private final Value<Boolean> system = new Value<>("SystemMsgs", true);

    public ChatTimestampsModule() {
        super("ChatTimestamps", Category.MISC);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketChat) {
            SPacketChat packet = (SPacketChat) event.getPacket();
            if (packet.isSystem() && !system.getValue())
                return;

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("H:mm");
            String timeStamp = simpleDateFormat.format(calendar.getTime());
            String time = rainbow.getValue() ? "\u00a7+<" + timeStamp + ">\u00a7r" : TextColor.coloredString("<", bracketColor.getValue()) + TextColor.coloredString(timeStamp, timeColor.getValue()) + TextColor.coloredString(">", bracketColor.getValue());
            packet.chatComponent = new TextComponentString(time + " " + packet.getChatComponent().getFormattedText());
        }
    }

}
