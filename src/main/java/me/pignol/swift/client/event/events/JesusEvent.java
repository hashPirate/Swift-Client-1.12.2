package me.pignol.swift.client.event.events;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class JesusEvent extends Event {

    private int stage;
    private BlockPos pos;
    private AxisAlignedBB boundingBox;

    public JesusEvent(int stage, BlockPos pos) {
        this.stage = stage;
        this.pos = pos;
    }

    public void setBoundingBox(AxisAlignedBB boundingBox) {
        this.boundingBox = boundingBox;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public int getStage() {
        return stage;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public AxisAlignedBB getBoundingBox() {
        return this.boundingBox;
    }

}
