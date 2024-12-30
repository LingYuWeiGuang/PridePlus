package op.wawa.prideplus.event.events;

import op.wawa.prideplus.event.impl.CancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EventMoveInput extends CancellableEvent {
    private float forward, strafe;
    private boolean jump, sneak;
    private double sneakSlowDown;
}

