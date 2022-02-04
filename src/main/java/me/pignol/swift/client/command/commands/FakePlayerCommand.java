package me.pignol.swift.client.command.commands;

import com.mojang.authlib.GameProfile;
import me.pignol.swift.api.interfaces.Globals;
import me.pignol.swift.client.command.Command;
import net.minecraft.client.entity.EntityOtherPlayerMP;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class FakePlayerCommand extends Command implements Globals
{

    private final static FakePlayerCommand INSTANCE = new FakePlayerCommand();

    public static FakePlayerCommand getInstance()
    {
        return INSTANCE;
    }

    private final Queue<EntityOtherPlayerMP> fakes = new LinkedList<>();
    private final AtomicInteger count = new AtomicInteger(0); // literally useless but it look coooool shout out to objects fr

    public FakePlayerCommand()
    {
        super("FakePlayer", new String[]{"fk", "fake"});
    }

    @Override
    public void run(String[] args)
    {
        int length = args.length;
        if (length == 1 || (length == 2 && args[1].equalsIgnoreCase("add")))
        {
            GameProfile profile = new GameProfile(UUID.randomUUID(), "fakeplayer");
            EntityOtherPlayerMP fake = new EntityOtherPlayerMP(mc.world, profile);
            fake.inventory.copyInventory(mc.player.inventory);
            fake.copyLocationAndAnglesFrom(mc.player);
            fake.setHealth(mc.player.getHealth());
            fake.onGround = mc.player.onGround;
            mc.world.addEntityToWorld(-999 + count.decrementAndGet(), fake);
            fakes.add(fake);
        }

        if (length == 2 && (args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("del")))
        {
            EntityOtherPlayerMP polledFake = fakes.poll();
            if (polledFake != null && mc.world.getLoadedEntityList().contains(polledFake))
            {
                mc.world.removeEntity(polledFake);
                count.decrementAndGet();
            }
        }
    }

    public void clear() {
        count.set(count.intValue() - fakes.size());
        fakes.clear();
    }


}
