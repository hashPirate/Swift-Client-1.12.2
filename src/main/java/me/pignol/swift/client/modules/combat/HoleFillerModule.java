package me.pignol.swift.client.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.pignol.swift.api.util.BlockUtil;
import me.pignol.swift.api.util.EntityUtil;
import me.pignol.swift.api.util.ItemUtil;
import me.pignol.swift.api.util.MathUtil;
import me.pignol.swift.api.util.RotationUtil.RotationType;
import me.pignol.swift.api.util.objects.Hole;
import me.pignol.swift.api.util.objects.StopWatch;
import me.pignol.swift.api.util.text.ChatUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.managers.HoleManager;
import me.pignol.swift.client.managers.SwitchManager;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HoleFillerModule extends Module {

    private final Value<RotationType> rotation = new Value<>("Rotation", RotationType.NONE);
    private final Value<Float> enemyRange = new Value<>("EnemyRange", 8F, 1F, 10F);
    private final Value<Float> enemyHoleRange = new Value<>("EnemyHoleRange", 3F, 1F, 10F);
    private final Value<Float> holeRange = new Value<>("HoleRange", 5F, 1F, 6F);
    private final Value<Float> holeRangeY = new Value<>("HoleRangeY", 5F, 1F, 6F);
    private final Value<Integer> blocksPerTick = new Value<>("Block/Tick", 10, 1, 20);
    private final Value<Integer> delay = new Value<>("Delay", 100, 0, 500);
    private final Value<Boolean> autoDisable = new Value<>("AutoDisable", true);
    private final Value<Boolean> holeCheck = new Value<>("HoleCheck", true);

    private final StopWatch timer = new StopWatch();

    private boolean placed;
    private int placeAmount;
    private int obbySlot;

    public HoleFillerModule() {
        super("HoleFiller", Category.COMBAT);
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (isNull()) return;

        if (!timer.passed(delay.getValue())) {
            return;
        }

        placeAmount = 0;
        placed = false;
        final EntityPlayer target = EntityUtil.getClosestPlayer(enemyRange.getValue());
        if (target == null) {
            if (autoDisable.getValue()) {
                setEnabled(false);
            }
            return;
        }

        List<Hole> holes;
        synchronized (HoleManager.getInstance().getHoles()) {
            holes = HoleManager.getInstance().getHoles().stream().sorted(Comparator.comparing(pos -> target.getDistanceSq(pos.getPos()))).collect(Collectors.toList());
        }

        if (holes.size() == 0) {
            if (autoDisable.getValue()) {
                setEnabled(false);
            }
            return;
        }

        obbySlot = ItemUtil.getSlotHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
        if (obbySlot == -1) {
            if (autoDisable.getValue()) {
                ChatUtil.sendMessage(ChatFormatting.RED + "[HoleFiller] No obsidian.", -554);
                setEnabled(false);
            }
            return;
        }

        if (holeCheck.getValue()) {
            if (!BlockUtil.isSafe(mc.player, 0, true) && BlockUtil.isSafe(target, 0, true)) {
                return;
            }
        }

        int lastSlot = mc.player.inventory.currentItem;
        boolean switched = false;

        for (Hole pair : holes) {
            if (pair == null) continue;
            BlockPos pos = pair.getPos();
            if (pos == null) continue;
            if (mc.player.getDistanceSq(pos.getX(), pos.getY(), pos.getZ()) < MathUtil.square(holeRange.getValue()) && target.getDistanceSq(pos.getX(), pos.getY(), pos.getZ()) < MathUtil.square(enemyHoleRange.getValue())) {
                if (MathUtil.getYDifferenceSq(mc.player.posY, pos.getY()) > MathUtil.square(holeRangeY.getValue()))
                    continue;
                if (BlockUtil.isPositionPlaceable(pos) != 3) continue;
                if (!switched) {
                    SwitchManager.getInstance().setDontReset(true);
                    mc.getConnection().sendPacket(new CPacketHeldItemChange(obbySlot));
                    switched = true;
                }
                placeBlock(pos);
            }
        }

        if (switched) {
            mc.getConnection().sendPacket(new CPacketHeldItemChange(lastSlot));
            SwitchManager.getInstance().setDontReset(false);
        }

        if (autoDisable.getValue()) {
            setEnabled(false);
        }

        if (placed) {
            timer.reset();
        }
    }



    private void placeBlock(BlockPos pos) {
        if (blocksPerTick.getValue() > placeAmount) {
            BlockUtil.placeBlock(pos, rotation.getValue());
            placeAmount++;
            placed = true;
        }
    }


}
