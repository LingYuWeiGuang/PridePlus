package op.wawa.prideplus.event.events;

import op.wawa.prideplus.event.impl.CancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EventStrafe extends CancellableEvent {
    public float strafe;
    public float forward;
    public float friction;
    public float yaw;
}
