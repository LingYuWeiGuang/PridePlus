package op.wawa.prideplus.event.events;

import op.wawa.prideplus.event.impl.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.ScaledResolution;

@Getter
@AllArgsConstructor
public class EventRender2D implements Event {
    @Setter
    private float partialTicks;
    private final ScaledResolution scaledResolution;
}

