package me.pignol.swift.client.modules.misc;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;

public class NameProtectModule extends Module {

    public static final NameProtectModule INSTANCE = new NameProtectModule();

    private final Value<String> fakeName = new Value<>("Name", "SusNigga54");
    private final Value<Boolean> fakeSkin = new Value<>("FakeSkin", false);

    public NameProtectModule() {
        super("NameProtect", Category.MISC);
    }

    public String getFakeName() {
        return fakeName.getValue();
    }

    public boolean getFakeSkin() {
        return fakeSkin.getValue();
    }

}
