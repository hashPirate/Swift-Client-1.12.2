package me.pignol.swift.client.modules.misc;

import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import java.lang.reflect.Field;

public class LogInModule extends Module {

    public LogInModule() {
        super("LogIn", Category.MISC);
    }

    @Override
    public void onEnable() {
        try {
            setSession(new Session("Parvl", "a4e0860b-326b-4f1f-9352-1ec41ba463f4", "eyJhbGciOiJIUzI1NiJ9.eyJhZ2ciOiJBZHVsdCIsInN1YiI6IjE2NWFjODM4M2Y0MmE3OTVkZTgwMTRjZWVmMjQ5M2VmIiwieWdndCI6ImJjM2ZhZGZhMTk1MzQ0ZTU5YjllNTI3YWMwYzAwNjk2Iiwic3ByIjoiYTRlMDg2MGIzMjZiNGYxZjkzNTIxZWM0MWJhNDYzZjQiLCJpc3MiOiJZZ2dkcmFzaWwtQXV0aCIsImV4cCI6MTYzNjg0MTczMCwiaWF0IjoxNjM2NjY4OTMwfQ.5omrasBU-UdgN7eNPKxd_1vkd_nwz8tbI9Ys-xJ3vss", "mojang"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setSession(Session s) throws Exception {
        Class<? extends Minecraft> mc = Minecraft.getMinecraft().getClass();
        try {
            Field session = null;

            for (Field f : mc.getDeclaredFields()) {
                if (f.getType().isInstance(s)) {
                    session = f;
                    //FMLLog.log.info("Found field " + f.toString() + ", injecting...");
                }
            }

            if (session == null) {
                throw new IllegalStateException("No field of type " + Session.class.getCanonicalName() + " declared.");
            }

            session.setAccessible(true);
            session.set(Minecraft.getMinecraft(), s);
            session.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}
