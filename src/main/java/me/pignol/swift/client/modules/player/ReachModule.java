package me.pignol.swift.client.modules.player;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.item.ItemPickaxe;

public class ReachModule extends Module {

    public static final ReachModule INSTANCE = new ReachModule();

    public final Value<Float> reachAdd = new Value<>("ReachAdd", 0.0F, 0.0F, 5.0F, 0.1F);
    public final Value<NoEntityTrace> entityTrace = new Value<>("CancelTrace", NoEntityTrace.NEVER);

    public ReachModule() {
        super("Reach", Category.PLAYER, false, false);
    }

    public enum NoEntityTrace {
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
