package me.pignol.swift.client.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.pignol.swift.api.util.BlockUtil;
import me.pignol.swift.api.util.ItemUtil;
import me.pignol.swift.api.util.RotationUtil.RotationType;
import me.pignol.swift.api.util.text.ChatUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoNomadHut extends Module {

    private final Value<RotationType> rotationType = new Value<>("Rotation", RotationType.NORMAL);

    private Vec3i[] vecs;

    public AutoNomadHut() {
        super("AutoNomadHut", Category.MISC);
    }

    @Override
    public void onEnable() {
        vecs = new Vec3i[]{new Vec3i(0.0D, 0.0D, 0.0D), new Vec3i(1.0D, 0.0D, 0.0D), new Vec3i(0.0D, 0.0D, 1.0D), new Vec3i(-1.0D, 0.0D, 0.0D), new Vec3i(0.0D, 0.0D, -1.0D), new Vec3i(1.0D, 0.0D, 1.0D), new Vec3i(1.0D, 0.0D, -1.0D), new Vec3i(-1.0D, 0.0D, 1.0D), new Vec3i(-1.0D, 0.0D, -1.0D), new Vec3i(2.0D, 0.0D, 0.0D), new Vec3i(2.0D, 0.0D, 1.0D), new Vec3i(2.0D, 0.0D, -1.0D), new Vec3i(-2.0D, 0.0D, 0.0D), new Vec3i(-2.0D, 0.0D, 1.0D), new Vec3i(-2.0D, 0.0D, -1.0D), new Vec3i(0.0D, 0.0D, 2.0D), new Vec3i(1.0D, 0.0D, 2.0D), new Vec3i(-1.0D, 0.0D, 2.0D), new Vec3i(0.0D, 0.0D, -2.0D), new Vec3i(-1.0D, 0.0D, -2.0D), new Vec3i(1.0D, 0.0D, -2.0D), new Vec3i(2.0D, 1.0D, -1.0D), new Vec3i(2.0D, 1.0D, 1.0D), new Vec3i(-2.0D, 1.0D, 0.0D), new Vec3i(-2.0D, 1.0D, 1.0D), new Vec3i(-2.0D, 1.0D, -1.0D), new Vec3i(0.0D, 1.0D, 2.0D), new Vec3i(1.0D, 1.0D, 2.0D), new Vec3i(-1.0D, 1.0D, 2.0D), new Vec3i(0.0D, 1.0D, -2.0D), new Vec3i(1.0D, 1.0D, -2.0D), new Vec3i(-1.0D, 1.0D, -2.0D), new Vec3i(2.0D, 2.0D, -1.0D), new Vec3i(2.0D, 2.0D, 1.0D), new Vec3i(-2.0D, 2.0D, 1.0D), new Vec3i(-2.0D, 2.0D, -1.0D), new Vec3i(1.0D, 2.0D, 2.0D), new Vec3i(-1.0D, 2.0D, 2.0D), new Vec3i(1.0D, 2.0D, -2.0D), new Vec3i(-1.0D, 2.0D, -2.0D), new Vec3i(2.0D, 3.0D, 0.0D), new Vec3i(2.0D, 3.0D, -1.0D), new Vec3i(2.0D, 3.0D, 1.0D), new Vec3i(-2.0D, 3.0D, 0.0D), new Vec3i(-2.0D, 3.0D, 1.0D), new Vec3i(-2.0D, 3.0D, -1.0D), new Vec3i(0.0D, 3.0D, 2.0D), new Vec3i(1.0D, 3.0D, 2.0D), new Vec3i(-1.0D, 3.0D, 2.0D), new Vec3i(0.0D, 3.0D, -2.0D), new Vec3i(1.0D, 3.0D, -2.0D), new Vec3i(-1.0D, 3.0D, -2.0D), new Vec3i(0.0D, 4.0D, 0.0D), new Vec3i(1.0D, 4.0D, 0.0D), new Vec3i(-1.0D, 4.0D, 0.0D), new Vec3i(0.0D, 4.0D, 1.0D), new Vec3i(0.0D, 4.0D, -1.0D), new Vec3i(1.0D, 4.0D, 1.0D), new Vec3i(-1.0D, 4.0D, 1.0D), new Vec3i(-1.0D, 4.0D, -1.0D), new Vec3i(1.0D, 4.0D, -1.0D), new Vec3i(2.0D, 4.0D, 0.0D), new Vec3i(2.0D, 4.0D, 1.0D), new Vec3i(2.0D, 4.0D, -1.0D)};
    }

    @Override
    public void onDisable() {
        vecs = null;
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (isNull()) {
            return;
        }

        BlockPos pos = new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ);
        int obbySlot = ItemUtil.getSlotHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
        if (obbySlot == -1) {
            ChatUtil.sendMessage(ChatFormatting.RED + "[AutoNomadHut] No obsidian!");
            setEnabled(false);
            return;
        }

        EnumHand hand = null;
        int lastSlot = mc.player.inventory.currentItem;

        if (mc.player.isHandActive()) {
            hand = mc.player.getActiveHand();
        }

        mc.getConnection().sendPacket(new CPacketHeldItemChange(obbySlot));
        for (Vec3i vec : vecs) {
            BlockUtil.placeBlock(pos.add(vec), rotationType.getValue());
        }
        mc.getConnection().sendPacket(new CPacketHeldItemChange(lastSlot));
        if (hand != null) {
            mc.player.setActiveHand(hand);
        }
        setEnabled(false);
    }

}
