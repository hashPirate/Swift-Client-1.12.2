package me.pignol.swift.client.modules.movement;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.Stage;
import me.pignol.swift.client.event.events.BoundingBoxEvent;
import me.pignol.swift.client.event.events.LiquidJumpEvent;
import me.pignol.swift.client.event.events.PacketEvent;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class JesusModule extends Module {

    private final Value<Mode> mode = new Value<>("Mode", Mode.SOLID);

    private final Value<Boolean> glide = new Value<>("Glide", false, v -> mode.getValue() == Mode.SOLID);
    private final Value<Boolean> strict = new Value<>("Strict", false, v -> mode.getValue() == Mode.SOLID);
    private final Value<Boolean> boost = new Value<>("Boost", false, v -> mode.getValue() == Mode.TRAMPOLINE);
    private final Value<Boolean> pauseOnShift = new Value<>("PauseOnShift", false, v -> mode.getValue() == Mode.TRAMPOLINE);

    private boolean jumping;

    private int glideCounter = 0;

    private float lastOffset;

    public JesusModule() {
        super("Jesus", Category.MOVEMENT);
    }

    @SubscribeEvent
    public void onPlayerUpdate(TickEvent.ClientTickEvent event) {
        if (mode.getValue() == Mode.TRAMPOLINE || isNull()) return;
        if (!mc.player.movementInput.sneak && !mc.player.movementInput.jump && isInLiquid()) {
            mc.player.motionY = 0.1D;
        }
        if (isOnLiquid() && mc.player.fallDistance < 3.0F && !mc.player.movementInput.jump && !isInLiquid() && !mc.player.isSneaking() && glide.getValue()) {
            switch (glideCounter) {
                case 0:
                    mc.player.motionX *= 1.1D;
                    mc.player.motionZ *= 1.1D;
                    break;
                case 1:
                    mc.player.motionX *= 1.27D;
                    mc.player.motionZ *= 1.27D;
                    break;
                case 2:
                    mc.player.motionX *= 1.51D;
                    mc.player.motionZ *= 1.51D;
                    break;
                case 3:
                    mc.player.motionX *= 1.15D;
                    mc.player.motionZ *= 1.15D;
                    break;
                case 4:
                    mc.player.motionX *= 1.23D;
                    mc.player.motionZ *= 1.23D;
                    break;
            }

            glideCounter++;

            if (glideCounter > 4) {
                glideCounter = 0;
            }
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            glideCounter = 0;
        }
    }

    @SubscribeEvent
    public void onLiquidJump(LiquidJumpEvent event) {
        if ((mc.player.isInWater() || mc.player.isInLava()) && (mc.player.motionY == 0.1 || mc.player.motionY == 0.5)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onWalkingPlayerUpdatePre(UpdateEvent event) {
        if (event.getStage() == Stage.PRE && mode.getValue() == Mode.TRAMPOLINE && (!mc.gameSettings.keyBindSneak.isKeyDown() || !pauseOnShift.getValue())) {
            int minY = MathHelper.floor(mc.player.getEntityBoundingBox().minY - 0.2D);
            boolean inLiquid = checkIfBlockInBB(BlockLiquid.class, minY) != null;

            if (inLiquid && !mc.player.isSneaking()) {
                mc.player.onGround = false;
            }

            Block block = mc.world.getBlockState(new BlockPos((int) Math.floor(mc.player.posX), (int) Math.floor(mc.player.posY), (int) Math.floor(mc.player.posZ))).getBlock();

            if (jumping && !mc.player.capabilities.isFlying && !mc.player.isInWater()) {
                if (mc.player.motionY < -0.3D || mc.player.onGround || mc.player.isOnLadder()) {
                    jumping = false;
                    return;
                }

                mc.player.motionY = mc.player.motionY / 0.9800000190734863D + 0.08D;
                mc.player.motionY -= 0.03120000000005D;
            }

            if (mc.player.isInWater() || mc.player.isInLava()) {
                mc.player.motionY = 0.1D;
            }

            if (!mc.player.isInLava() && (!mc.player.isInWater() || boost.getValue()) && block instanceof BlockLiquid && mc.player.motionY < 0.2D) {
                mc.player.motionY = 0.5D;
                jumping = true;
            }
        }
    }

    public void onBoundingBox(BoundingBoxEvent event) {
        if (((event.getBlock() instanceof BlockLiquid))
                && event.getEntity() == mc.player
                && event.getPos().getY() <= mc.player.posY
                && checkIfBlockInBB(BlockLiquid.class, MathHelper.floor((mc.player.getEntityBoundingBox().minY + 0.01))) != null
                && checkIfBlockInBB(BlockLiquid.class, MathHelper.floor((mc.player.getEntityBoundingBox().minY - 0.02))) != null
                && (mc.player.fallDistance < 3.0F)
                && (!mc.player.isSneaking())) {
            event.setBoundingBox(Block.FULL_BLOCK_AABB);
        }
    }

    @SubscribeEvent
    public void sendPacket(PacketEvent.Send event) {
        if (mc.world == null || mc.player == null) return;
        if (mode.getValue() == Mode.SOLID) {
            if (event.getPacket() instanceof CPacketPlayer
                    && mc.player.ticksExisted > 20
                    && mode.getValue().equals(Mode.SOLID)
                    && mc.player.getRidingEntity() == null
                    && !mc.gameSettings.keyBindJump.isKeyDown()
                    && mc.player.fallDistance < 3.0F) {
                final CPacketPlayer packet = (CPacketPlayer) event.getPacket();
                if (isOnLiquid() && !isInLiquid()) {
                    packet.onGround = false;
                    if (strict.getValue()) {
                        lastOffset += 0.12F;

                        if (lastOffset > 0.4F) {
                            lastOffset = 0.2F;
                        }
                        packet.y = (packet.getY(mc.player.posY) - lastOffset);
                    } else {
                        packet.y = ((mc.player.ticksExisted % 2 == 0) ? (packet.getY(mc.player.posY) - 0.05D) : packet.getY(mc.player.posY));
                    }
                }
            /*}* else if (event.getPacket() instanceof CPacketVehicleMove && strict.getValue() && entityJesus.getValue()) {
                CPacketVehicleMove packet = (CPacketVehicleMove)event.getPacket();
                if (isOnLiquid() && mc.player.fallDistance < 3.0F && !mc.player.movementInput.jump && !isInLiquid() && !mc.player.isSneaking()) {
                    double originalY = packet.getY();
                    if (mc.player.ticksExisted % 3 == 0) {
                        ((ICPacketVehicleMove) packet).setY(packet.getY() - 0.48);
                    } else if (mc.player.ticksExisted % 4 == 0) {
                        ((ICPacketVehicleMove) packet).setY(packet.getY() - 0.33D);
                    } else if (mc.player.ticksExisted % 5 == 0) {
                        ((ICPacketVehicleMove) packet).setY(packet.getY() - 0.73D);
                    } else if (mc.player.ticksExisted % 6 == 0) {
                        ((ICPacketVehicleMove) packet).setY(packet.getY() - 0.63D);
                    } else if (mc.player.ticksExisted % 7 == 0) {
                        ((ICPacketVehicleMove) packet).setY(packet.getY() - 0.42D);
                    } else if (mc.player.ticksExisted % 8 == 0) {
                        ((ICPacketVehicleMove) packet).setY(packet.getY() - 0.52D);
                    }
                    if (packet.getY() == originalY) {
                        ((ICPacketVehicleMove) packet).setY(packet.getY() - 0.3D);
                    }*/
            }
        }
    }

    public static IBlockState checkIfBlockInBB(Class<? extends Block> blockClass, int minY) {
        for (int iX = MathHelper.floor(mc.player.getEntityBoundingBox().minX); iX < MathHelper.ceil(mc.player.getEntityBoundingBox().maxX); iX++) {
            for (int iZ = MathHelper.floor(mc.player.getEntityBoundingBox().minZ); iZ < MathHelper.ceil(mc.player.getEntityBoundingBox().maxZ); iZ++) {
                IBlockState state = mc.world.getBlockState(new BlockPos(iX, minY, iZ));
                if (blockClass.isInstance(state.getBlock())) {
                    return state;
                }
            }
        }
        return null;
    }

    private boolean checkCollide() {

        if (mc.player.isSneaking()) {
            return false;
        }

        if (mc.player.getRidingEntity() != null
                && mc.player.getRidingEntity().fallDistance >= 3.0f) {
            return false;
        }

        return mc.player.fallDistance <= 3.0f;
    }

    public static boolean isInLiquid() {

        if (mc.player.fallDistance >= 3.0f) {
            return false;
        }

        boolean inLiquid = false;
        final AxisAlignedBB bb = mc.player.getRidingEntity() != null ? mc.player.getRidingEntity().getEntityBoundingBox() : mc.player.getEntityBoundingBox();
        int y = (int) bb.minY;
        for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX) + 1; x++) {
            for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ) + 1; z++) {
                final Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (!(block instanceof BlockAir)) {
                    if (!(block instanceof BlockLiquid)) {
                        return false;
                    }
                    inLiquid = true;
                }
            }
        }
        return inLiquid;
    }

    public static boolean isOnLiquid() {
        if (mc.player.fallDistance >= 3.0f) {
            return false;
        }

        final AxisAlignedBB bb = mc.player.getRidingEntity() != null ? mc.player.getRidingEntity().getEntityBoundingBox().contract(0.0d, 0.0d, 0.0d).offset(0.0d, -0.05000000074505806D, 0.0d) : mc.player.getEntityBoundingBox().contract(0.0d, 0.0d, 0.0d).offset(0.0d, -0.05000000074505806D, 0.0d);
        boolean onLiquid = false;
        int y = (int) bb.minY;
        for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX + 1.0D); x++) {
            for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ + 1.0D); z++) {
                final Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (block != Blocks.AIR) {
                    if (!(block instanceof BlockLiquid)) {
                        return false;
                    }
                    onLiquid = true;
                }
            }
        }
        return onLiquid;
    }

    private enum Mode {
        SOLID, TRAMPOLINE
    }

}
