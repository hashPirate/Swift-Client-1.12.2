package me.pignol.swift.api.util.text;


import me.pignol.swift.api.util.objects.StopWatch;
import me.pignol.swift.client.modules.other.ManageModule;

public class IdleUtil {

    private static final StopWatch dotTimer = new StopWatch();

    private static String dots = "";

    public static String getDots() {
        if (dotTimer.passed(ManageModule.INSTANCE.idleSpeed.getValue())) {
            dots += ".";
            dotTimer.reset();
        }

        if (dots.length() > 3) {
            dots = "";
        }

        return dots;
    }

}
