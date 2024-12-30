package op.wawa.prideplus.event.events;

import op.wawa.prideplus.event.impl.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EventRender3D implements Event {
    private float partialTicks;
}

