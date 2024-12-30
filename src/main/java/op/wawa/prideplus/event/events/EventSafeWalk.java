package op.wawa.prideplus.event.events;

import op.wawa.prideplus.event.impl.Event;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EventSafeWalk implements Event {
    private boolean safe;
}