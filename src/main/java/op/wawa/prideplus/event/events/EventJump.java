package op.wawa.prideplus.event.events;

import op.wawa.prideplus.event.impl.CancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EventJump extends CancellableEvent {
    public float motion;
    public float yaw;
}

