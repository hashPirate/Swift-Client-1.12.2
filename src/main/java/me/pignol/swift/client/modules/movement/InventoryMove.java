package me.pignol.swift.client.modules.movement;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.gui.blowbui.glowclient.clickgui.ClickGUI;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreenOptionsSounds;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class InventoryMove extends Module {

    private final Value<Boolean> sneak = new Value<>("Sneak", false);
    private final Value<Boolean> jump = new Value<>("Jump", false);
    private final Value<Boolean> sprint = new Value<>("Sprint", false);

    public InventoryMove() {
        super("InventoryMove", Category.MOVEMENT);
    }

    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() == mc.player) {
            if (mc.currentScreen instanceof GuiOptions || mc.currentScreen instanceof GuiVideoSettings || mc.currentScreen instanceof GuiScreenOptionsSounds || mc.currentScreen instanceof GuiContainer || mc.currentScreen instanceof GuiIngameMenu || mc.currentScreen instanceof ClickGUI) {
                mc.gameSettings.keyBindForward.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode());
                mc.gameSettings.keyBindBack.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode());
                mc.gameSettings.keyBindRight.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode());
                mc.gameSettings.keyBindLeft.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode());
                mc.gameSettings.keyBindJump.pressed = jump.getValue() && Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
                mc.gameSettings.keyBindSneak.pressed = sneak.getValue() && Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode());
                mc.gameSettings.keyBindSprint.pressed = sprint.getValue() && Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode());
            }
        }
    }

    
}
