package me.pignol.swift.client.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.pignol.swift.api.util.objects.StopWatch;
import me.pignol.swift.api.util.text.ChatUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Spammer extends Module
{

    public static Spammer INSTANCE;

    private final Value<Float> delay = new Value<>("Delay", 1F, 0.1F, 15.0F, 0.1F);
    private final Value<Boolean> message = new Value<>("Message", false);
    private final Value<String> target = new Value<>("MessageTarget", Integer.toBinaryString(0x22), v -> message.getValue());

    private final List<String> strings = new ArrayList<>();
    private final StopWatch timer = new StopWatch();

    private File file;
    private boolean warned;

    public Spammer()
    {
        super("Spammer", Category.MISC);
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event)
    {
        if (file == null)
        {
            if (!warned)
            {
                ChatUtil.sendMessage(ChatFormatting.RED + "[Spammer] Please specify a file to use.");
                warned = true;
            }
            return;
        }

        try
        {
            if (timer.passed((long) (delay.getValue() * 1000)))
            {
                String add = message.getValue() ? ("/msg " + target.getValue() + " ") : "";
                String out = strings.get(0);
                strings.remove(out);
                strings.add(out);
                mc.player.sendChatMessage(add + out);
                timer.reset();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }



    public void setFile(File file)
    {
        this.file = file;
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null)
            {
                if (!line.isEmpty())
                {
                    strings.add(line);
                }
            }
            reader.close();
            reader = null;
        } catch (IOException ex)
        {
        }
    }

    @Override
    public void onDisable()
    {
        strings.clear();
        warned = false;
    }
}
