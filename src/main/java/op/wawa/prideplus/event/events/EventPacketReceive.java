package op.wawa.prideplus.event.events;

import op.wawa.prideplus.event.impl.CancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;

@AllArgsConstructor
@Getter
@Setter
public class EventPacketReceive extends CancellableEvent {
    private Packet<?> packet;
    private final INetHandler netHandler;
    private final EnumPacketDirection direction;
}
