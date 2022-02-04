package me.pignol.swift.api.util;

import com.google.common.collect.Maps;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.UUID;

public class CapeUtil {

    public static final HashMap<UUID, ResourceLocation> CAPED_USERS = Maps.newHashMap();

    static {
        ResourceLocation jordo = new ResourceLocation("capes/jordo.png");
        ResourceLocation praam = new ResourceLocation("capes/praam.png");
        ResourceLocation brit = new ResourceLocation("capes/25.png");

        CAPED_USERS.put(UUID.fromString("94be157a-f3c1-46f8-af12-52f73adc9164"), praam);
        CAPED_USERS.put(UUID.fromString("e9e8e31a-c8d7-4dd0-a5d3-0162ed1627a7"), praam);

        CAPED_USERS.put(UUID.fromString("f9a6b946-7186-463e-b859-633f52e33e9e"), jordo);
        CAPED_USERS.put(UUID.fromString("f2e9ae88-993f-416d-a9bb-0839a26c2e55"), jordo);

        CAPED_USERS.put(UUID.fromString("c137f0cf-5e87-4176-8b82-325916bcb3bd"), new ResourceLocation("capes/proby.png"));

        CAPED_USERS.put(UUID.fromString("527f2230-e557-452a-9188-17dece1842ce"), new ResourceLocation("capes/chard.png"));

        CAPED_USERS.put(UUID.fromString("cc72ff00-a113-48f4-be18-2dda8db52355"), new ResourceLocation("capes/hollow.png"));

        CAPED_USERS.put(UUID.fromString("7d237a2b-4d8a-4be5-a1c5-f57a4a0a4221"), brit);
        CAPED_USERS.put(UUID.fromString("3f3320a8-69da-4a1f-a124-8ab4d015bc51"), brit);
    }

}
