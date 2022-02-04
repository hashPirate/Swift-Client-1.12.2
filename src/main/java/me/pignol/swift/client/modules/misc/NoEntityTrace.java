package me.pignol.swift.client.modules.misc;

import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.item.ItemPickaxe;

public class NoEntityTrace extends Module
{

    public NoEntityTrace()
    {
        super("NoEntityTrace", Category.MISC);
    }

    public enum Mode {
        NEVER {
            @Override
            public boolean cancelEntityTrace() {
                return false;
            }
        },
        PICKAXE {
            @Override
            public boolean cancelEntityTrace() {
                return mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe;
            }
        },
        ALWAYS {
            @Override
            public boolean cancelEntityTrace() {
                return true;
            }
        };

        public abstract boolean cancelEntityTrace();
    }

}
