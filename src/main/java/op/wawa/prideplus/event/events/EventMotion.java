package op.wawa.prideplus.event.events;

import op.wawa.prideplus.event.impl.CancellableEvent;
import op.wawa.prideplus.event.type.EventType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EventMotion extends CancellableEvent {
    public float yaw;
    public float pitch;
    private boolean ground;
    private double x;
    private double y;
    private double z;
    private final EventType type;

    public void setRotation(float yaw, float pitch) {
        this.setYaw(yaw);
        this.setPitch(pitch);
    }

    public boolean isPre() {
        return this.type == EventType.Pre;
    }

    public boolean isPost() {
        return this.type == EventType.Post;
    }
}