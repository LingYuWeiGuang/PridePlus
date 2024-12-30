package op.wawa.prideplus.ui.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;

@Getter
@AllArgsConstructor
public enum NotificationType {
    SUCCESS(new Color(20, 190, 20)),
    DISABLE(new Color(190, 30, 30)),
    INFO(Color.WHITE),
    WARNING(Color.YELLOW);
    private final Color color;
}