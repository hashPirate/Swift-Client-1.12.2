package me.pignol.swift.client.modules.movement;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class IceSpeed extends Module {

    private final Value<Float> speed = new Value<>("Speed", 0.6F, 0.1F, 3.0F);

    private static final float DEFAULT_SLIPPERINESS = 0.98F;

    public IceSpeed() {
        super("IceSpeed", Category.MOVEMENT);
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        float slipperiness = Math.abs(speed.getValue() - 1.0F);
        Blocks.ICE.setDefaultSlipperiness(slipperiness);
        Blocks.FROSTED_ICE.setDefaultSlipperiness(slipperiness);
        Blocks.PACKED_ICE.setDefaultSlipperiness(slipperiness);
    }

    @Override
    public void onDisable() {
        Blocks.ICE.setDefaultSlipperiness(DEFAULT_SLIPPERINESS);
        Blocks.FROSTED_ICE.setDefaultSlipperiness(DEFAULT_SLIPPERINESS);
        Blocks.PACKED_ICE.setDefaultSlipperiness(DEFAULT_SLIPPERINESS);
    }
}
