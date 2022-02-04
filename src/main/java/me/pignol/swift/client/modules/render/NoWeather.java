package me.pignol.swift.client.modules.render;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.PacketEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class NoWeather extends Module {

    private final Value<Boolean> showStatus = new Value<>("Suffix", true);

    private boolean isRaining = false;
    private float rainStrength = 0.f;
    private float previousRainStrength = 0.f;

    public NoWeather() {
        super("NoWeather", Category.RENDER);
    }


    private void saveState(World world) {
        if (world != null) {
            setState(world.getWorldInfo().isRaining(), world.rainingStrength, world.prevRainingStrength);
        } else {
            setState(false, 1.f, 1.f);
        }
    }

    private void setState(boolean raining, float rainStrength, float previousRainStrength) {
        this.isRaining = raining;
        setState(rainStrength, previousRainStrength);
    }

    private void setState(float rainStrength, float previousRainStrength) {
        this.rainStrength = rainStrength;
        this.previousRainStrength = previousRainStrength;
    }

    private void disableRain() {
        if (mc.world != null) {
            mc.world.getWorldInfo().setRaining(false);
            mc.world.setRainStrength(0.f);
        }
    }

    public void resetState() {
        if (mc.world != null) {
            mc.world.getWorldInfo().setRaining(isRaining);
            mc.world.rainingStrength = rainStrength;
            mc.world.prevRainingStrength = previousRainStrength;
        }
    }

    @Override
    public void onEnable() {
        saveState(mc.world);
    }

    @Override
    public void onDisable() {
        resetState();
    }

    @SubscribeEvent
    public void onWorldChange(WorldEvent.Load event) {
        saveState(event.getWorld());
    }

    @SubscribeEvent
    public void onWorldChange(WorldEvent.Unload event) {
        saveState(event.getWorld());
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.ClientTickEvent event) {
        disableRain();
    }

    @SubscribeEvent
    public void onPacketIncoming(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketChangeGameState) {
            int state = ((SPacketChangeGameState) event.getPacket()).getGameState();
            float strength = ((SPacketChangeGameState) event.getPacket()).getValue();
            boolean isRainState = false;
            switch (state) {
                case 1: // end rain
                    isRainState = false;
                    setState(false, 0.f, 0.f);
                    break;
                case 2: // start rain
                    isRainState = true;
                    setState(true, 1.f, 1.f);
                    break;
                case 7: // fade value: sky brightness
                    isRainState = true; // needs to be cancelled to avoid flicker
                    break;
            }
            if (isRainState) {
                disableRain();
                event.setCanceled(true);
            }
        }
    }

    @Override
    public String getSuffix() {
        if (isRaining && showStatus.getValue() && mc.world != null && mc.player != null) {
            Biome biome = mc.world.getBiome(mc.player.getPosition());
            boolean canRain = biome.canRain();
            boolean canSnow = biome.getEnableSnow();

            String status;

            if (mc.world.isThundering()) {
                status = "Thunder";
            } else if (canSnow) {
                status = "Snowing";
            } else if (!canRain) {
                status = "Cloudy";
            } else {
                status = "Raining";
            }

            return status;
        }
        return "";
    }

}
