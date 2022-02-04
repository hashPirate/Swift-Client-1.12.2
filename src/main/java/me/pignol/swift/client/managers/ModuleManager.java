package me.pignol.swift.client.managers;

import me.pignol.swift.api.util.objects.ModuleList;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.KeyPressEvent;
import me.pignol.swift.client.modules.Module;
import me.pignol.swift.client.modules.combat.*;
import me.pignol.swift.client.modules.misc.*;
import me.pignol.swift.client.modules.movement.*;
import me.pignol.swift.client.modules.other.*;
import me.pignol.swift.client.modules.player.*;
import me.pignol.swift.client.modules.render.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.lang.reflect.Field;
import java.util.concurrent.CyclicBarrier;

public class ModuleManager {

    private static final ModuleManager INSTANCE = new ModuleManager();

    private final ModuleList modules = new ModuleList();
    public final CyclicBarrier cyclicBarrier = new CyclicBarrier(2);


    public static ModuleManager getInstance() {
        return INSTANCE;
    }

    public void load() {

        MinecraftForge.EVENT_BUS.register(this);

        //OTHER
        addModule(FontModule.INSTANCE);
        addModule(ManageModule.INSTANCE);
        addModule(ColorsModule.INSTANCE);
        addModule(HudModule.INSTANCE);
        addModule(ClickGuiModule.INSTANCE);

        //COMBAT
        addModule(AuraModule.INSTANCE);
        addModule(new Auto32k());
        addModule(new AutoNomadHut());
        addModule(new AutoCrystal());
        addModule(new FencerModule());
        addModule(new AntiRegear());
        addModule(new AutoArmor());
        addModule(new SelfFillModule());
        addModule(new Surround());
        addModule(new OffhandModule());
        addModule(new AutoTrapModule());
        addModule(new BowSpoof());
        addModule(new HoleFillerModule());
        addModule(new CriticalsModule());
        addModule(new SelfTrap());

        //RENDER
        addModule(SkeletonModule.INSTANCE);
        addModule(NoRenderModule.INSTANCE);
        addModule(new Arrows());
        addModule(CustomSkyModule.INSTANCE);
        addModule(NametagsModule.INSTANCE);
        addModule(EnchantGlintModule.INSTANCE);
        addModule(ViewmodelModule.INSTANCE);
        addModule(FullBrightModule.INSTANCE);
        addModule(NoInterpolationModule.INSTANCE);
        addModule(CrosshairModule.INSTANCE);
        addModule(AspectRatio.INSTANCE);
        //addModule(ChamsModule.INSTANCE);
        addModule(HeadRotations.INSTANCE);
        addModule(ViewClipModule.INSTANCE);
        addModule(Chams.INSTANCE);
        addModule(NoArmorRender.getInstance());
        addModule(NoBob.getInstance());
        addModule(new ChunkFinder());
        //addModule(new Search());
        addModule(new Freecam());
        addModule(new EntityESP());
        addModule(new BlockHighlight());
        addModule(new NoWeather());
        addModule(new ShulkerViewer());
        addModule(new PacketESP());
        addModule(new Search());
        addModule(new ItemESP());
        addModule(new TracersModule());
        addModule(new VoidESP());
        addModule(new LogoutSpotsModule());
        addModule(new HoleESPModule());

        //PLAYER
        addModule(ReachModule.INSTANCE);
        addModule(new MultiTask());
        addModule(new NoInteract());
        addModule(new TunnelSpeed());
        addModule(new AntiPopModule());
        addModule(new PositionSpoofModule());
        addModule(new PigSpeed());
        addModule(new SpeedMine());
        addModule(new FastPlaceModule());
        addModule(new AutoStackFillModule());

        //MOVEMENT
        addModule(VelocityModule.INSTANCE);
        addModule(NoSlowModule.INSTANCE);
        addModule(PacketFly.INSTANCE);
        addModule(new TunnelSpeed());
        addModule(new FastStairs());
        addModule(new Flight());
        addModule(new Clip());
        addModule(new BoatFly());
        addModule(new EntityControl());
        addModule(new IceSpeed());
        addModule(new JesusModule());
        addModule(new AntiSound());
        addModule(new InventoryMove());
        addModule(new FastLiquid());
        addModule(new NoFallModule());
        addModule(new SprintModule());
        addModule(new ScaffoldModule());
        addModule(new StepModule());
        addModule(new SpeedModule());
        addModule(new ReverseStep());

        //MISC
        addModule(TotemPopCounterModule.INSTANCE);
        addModule(new EgapFinder());
        addModule(BetterTabModule.INSTANCE);
        addModule(NameProtectModule.INSTANCE);
        addModule(TrueDurability.getInstance());
        addModule(Tooltips.getInstance());
        addModule(new PacketLog());
        addModule(new AutoTool());
        addModule(new AntiLag());
        addModule(new AntiVanish());
        addModule(new EntityVanish());
        addModule(new LogInModule());
        addModule(new Nuker());
        addModule(new LiquidPlace());
        addModule(new BuildHeight());
        addModule(new NoRotate());
        addModule(new TickShift());
        addModule(new AutoRespawn());
        addModule(new DeathCoordsLog());
        addModule(new FakeRotationModule());
        addModule(new BreederModule());
        addModule(new AutoLogModule());
        addModule(new PearlTraceModule());
        addModule(new Regear());
        addModule(new StashLog());
        addModule(new AutoNomadHut());
        addModule(new AutoGG());
        addModule(new Blink());
        addModule(new PingSpoofModule());
        addModule(new PacketCancellerModule());
        addModule(new ChatTimestampsModule());
        addModule(new VisualRangeModule());
        addModule(new NoQuitDesyncModule());
        addModule(new Spammer());
        addModule(new NoEntityTrace());
        addModule(new PayloadSpoofModule());
        addModule(new FakePlayerModule());
        addModule(new XCarry());

        HudModule.INSTANCE.setupModules();
        HudModule.INSTANCE.sortModules();

        Thread thread = new Thread(this::startThreadLoop, "Module Thread");
        thread.setDaemon(true);
        thread.start();

        //loadModules();
    }

    @SubscribeEvent
    public void onKeyPress(KeyPressEvent event) {
        if (event.getKey() > 0) {
            for (int i = 0, modulesSize = modules.size(); i < modulesSize; i++) {
                final Module module = modules.get(i);
                if (event.getState() && module.getKey() == event.getKey()) {
                    module.toggle();
                }
            }
        }
    }

    private void startThreadLoop() {
        while (true) {
            try {
                try {
                    HoleManager.getInstance().onThread();
                } catch(Throwable t) {
                    System.out.println("Exception while posting ThreadEvent");
                    t.printStackTrace();
                }
                try {
                    cyclicBarrier.await();
                }
                catch (Throwable t) {
                }
                cyclicBarrier.reset();
            }
            catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public void addModule(Module module) {
        try {
            for (Field field : module.getClass().getDeclaredFields()) {
                if (Value.class.isAssignableFrom(field.getType())) {
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    module.getValues().add((Value<?>) field.get(module));
                }
            }
            modules.add(module);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public ModuleList getModules() {
        return modules;
    }
}
