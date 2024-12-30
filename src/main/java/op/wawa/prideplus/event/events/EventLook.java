package op.wawa.prideplus.event.events;

import op.wawa.prideplus.event.impl.Event;
import op.wawa.prideplus.utils.object.Rotation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EventLook implements Event {
    private Rotation rotation;
}
