package op.wawa.prideplus.event.events;

import op.wawa.prideplus.event.impl.CancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.Packet;

@AllArgsConstructor
@Getter
@Setter
public class EventPacketSend extends CancellableEvent {
    private Packet<?> packet;
}
