package me.pignol.swift.client.modules;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.pignol.swift.api.util.text.ChatUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.modules.other.HudModule;
import me.pignol.swift.client.modules.other.ManageModule;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.List;

public class Module {

    protected final static Minecraft mc = Minecraft.getMinecraft();

    private final List<Value> values = new ArrayList<>();

    private final Value<String> nameValue = new Value<>("Name", "");

    private final String name;
    private final Category category;
    private String suffix = "";

    private int key;

    private boolean enabled, needsListener = true, drawn = true;

    public Module(String name, Category category) {
        this.name = name;
        this.nameValue.setValue(name);
        this.category = category;
    }

    public Module(String name, Category category, boolean enabled) {
        this(name, category);
        if (enabled) setEnabled(enabled);
    }

    public Module(String name, Category category, boolean enabled, boolean needsListener) {
        this(name, category);
        this.needsListener = needsListener;
        if (enabled) setEnabled(enabled);
    }

    public void onEnable(){}
    public void onDisable(){}


    public boolean isEnabled() {
        return enabled;
    }

    public void setSuffix(String suffix) {
        if (!suffix.equals(this.suffix)) {
            HudModule.INSTANCE.setNeedsSort(true);
        }
        this.suffix = suffix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            onEnable();
            if (mc.player != null) {
                if (ManageModule.INSTANCE.forgeHax.getValue()) {
                    ChatUtil.sendMessage("> " + getName() + ".enabled = true", -hashCode());
                } else {
                    ChatUtil.sendMessage(getName() + " was " + (isEnabled() ? ChatFormatting.GREEN + "enabled" : ChatFormatting.RED + "disabled"), -hashCode());
                }
            }
            if (needsListener)
                MinecraftForge.EVENT_BUS.register(this);
        } else {
            onDisable();
            if (mc.player != null) {
                if (ManageModule.INSTANCE.forgeHax.getValue()) {
                    ChatUtil.sendMessage("> " + getName() + ".enabled = false", -hashCode());
                } else {
                    ChatUtil.sendMessage(getName() + " was " + (isEnabled() ? ChatFormatting.GREEN + "enabled" : ChatFormatting.RED + "disabled"), -hashCode());
                }
            }
            if (needsListener)
                MinecraftForge.EVENT_BUS.unregister(this);
        }
    }

    public boolean isNull() {
        return mc.world == null || mc.player == null;
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return nameValue.getValue();
    }

    public Category getCategory() {
        return category;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public boolean isDrawn() {
        return drawn;
    }

    public void setDrawn(boolean drawn) {
        this.drawn = drawn;
    }

    public List<Value> getValues() {
        return values;
    }

}
