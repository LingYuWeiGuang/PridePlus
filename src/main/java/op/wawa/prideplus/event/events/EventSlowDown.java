package op.wawa.prideplus.event.events;

import op.wawa.prideplus.event.impl.CancellableEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventSlowDown extends CancellableEvent {
    private float strafeMultiplier, forwardMultiplier;

    public EventSlowDown(float strafeMultiplier, float forwardMultiplier) {
        this.strafeMultiplier = strafeMultiplier;
        this.forwardMultiplier = forwardMultiplier;
    }
}
