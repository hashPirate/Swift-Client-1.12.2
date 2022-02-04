package me.pignol.swift.client.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.pignol.swift.api.util.BlockUtil;
import me.pignol.swift.api.util.ItemUtil;
import me.pignol.swift.api.util.objects.StopWatch;
import me.pignol.swift.api.util.text.ChatUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

import static me.pignol.swift.api.util.RotationUtil.RotationType;

public class SelfTrap extends Module
{

    private final Value<RotationType> rotation = new Value<>("Rotation", RotationType.NONE);
    private final Value<Integer> delay = new Value<>("Delay/Place", 50, 0, 250);
    private final Value<Integer> blocksPerPlace = new Value<>("Block/Place", 8, 1, 30);
    private final Value<Boolean> onlyInHole = new Value<>("OnlyInHole", true);

    private final StopWatch timer = new StopWatch();

    private int placements = 0;
    private boolean placed;

    public SelfTrap()
    {
        super("SelfTrap", Category.COMBAT);
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event)
    {
        if (!timer.passed(delay.getValue()))
            return;

        if (placed)
        {
            timer.reset();
        }
        placed = false;

        int obbySlot = ItemUtil.getSlotHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
        if (obbySlot == -1)
        {
            obbySlot = ItemUtil.getSlotHotbar(Item.getItemFromBlock(Blocks.ENDER_CHEST));
            if (obbySlot == -1)
            {
                ChatUtil.sendMessage(ChatFormatting.RED + "[SelfTrap] No obsidian.", -554);
                setEnabled(false);
                return;
            }
        }

        placements = 0;

        if (onlyInHole.getValue() && !BlockUtil.isSafeFast(mc.player)) {
            setEnabled(false);
            return;
        }

        List<BlockPos> targets = getTargets();
        if (targets.size() == 0) {
            setEnabled(false);
            return;
        }

        EnumHand hand = mc.player.isHandActive() ? mc.player.getActiveHand() : null;
        int lastSlot = mc.player.inventory.currentItem;

        mc.getConnection().sendPacket(new CPacketHeldItemChange(obbySlot));

        for (BlockPos pos : getTargets()) {
            placeBlock(pos);
        }

        mc.getConnection().sendPacket(new CPacketHeldItemChange(lastSlot));
        if (hand != null)
        {
            mc.player.setActiveHand(hand);
        }
    }

    public List<BlockPos> getTargets()
    {
        List<BlockPos> positions = new ArrayList<>();
        positions.add(new BlockPos(mc.player.posX, mc.player.posY + 2, mc.player.posZ));
        EnumFacing direction = mc.player.getHorizontalFacing();
        int placeability = BlockUtil.isPositionPlaceable(positions.get(0));
        switch (placeability)
        {
            case 0:
                return new ArrayList<>();
            case 3:
                return positions;
            case 1:
                if (BlockUtil.isPositionPlaceable(positions.get(0)) == 3)
                {
                    return positions;
                }
            case 2:
                positions.add(new BlockPos(mc.player.posX + -direction.getXOffset(), mc.player.posY + 1, mc.player.posZ + -direction.getZOffset()));
                positions.add(new BlockPos(mc.player.posX + -direction.getXOffset(), mc.player.posY + 2, mc.player.posZ + -direction.getZOffset()));
                break;
            default:
        }
        positions.removeIf(pos -> !BlockUtil.isReplaceable(pos));
        return positions;
    }

    private void placeBlock(BlockPos pos)
    {
        if (placements < blocksPerPlace.getValue())
        {
            BlockUtil.placeBlock(pos, rotation.getValue());
            placed = true;
            placements++;
        }
    }

}
