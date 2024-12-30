package op.wawa.prideplus.event.events;

import op.wawa.prideplus.event.impl.CancellableEvent;
import op.wawa.prideplus.utils.player.MovementUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class EventMove extends CancellableEvent {
    public double x;
    public double y;
    public double z;

    public void setSpeed(double speed) {
        MovementUtils.setSpeed(this, speed);
    }
}

