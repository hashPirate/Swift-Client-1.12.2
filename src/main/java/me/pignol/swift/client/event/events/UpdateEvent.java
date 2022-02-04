package me.pignol.swift.client.event.events;

import me.pignol.swift.client.event.EventStageable;
import me.pignol.swift.client.event.Stage;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class UpdateEvent extends EventStageable {

    public UpdateEvent(Stage stage) {
        super(stage);
    }

}
