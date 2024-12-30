package op.wawa.prideplus.event.events;

import op.wawa.prideplus.event.impl.CancellableEvent;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EventPlace extends CancellableEvent {
    private boolean shouldRightClick;
    private int slot;

    public EventPlace(final int slot) {
        this.slot = slot;
    }

}