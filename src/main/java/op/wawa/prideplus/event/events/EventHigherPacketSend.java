package op.wawa.prideplus.event.events;

import op.wawa.prideplus.event.impl.CancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.Packet;

@Setter
@Getter
@AllArgsConstructor
public class EventHigherPacketSend extends CancellableEvent {
    public Packet<?> packet;
}

