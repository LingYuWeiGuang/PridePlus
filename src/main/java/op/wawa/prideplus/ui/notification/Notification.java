package op.wawa.prideplus.ui.notification;

import op.wawa.prideplus.ui.font.FontManager;
import op.wawa.prideplus.utils.animation.animations.Animation;
import op.wawa.prideplus.utils.animation.animations.impl.DecelerateAnimation;
import op.wawa.prideplus.utils.object.TimerUtils;
import op.wawa.prideplus.utils.render.ColorUtils;
import op.wawa.prideplus.utils.render.shader.RoundedUtils;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Getter
public class Notification {

    private final NotificationType notificationType;
    @Setter
    private String title, description;
    private final float time;
    private final TimerUtils timerUtil;
    private final Animation animation;

    public Notification(NotificationType type, String title, String description) {
        this(type, title, description, NotificationManager.getToggleTime());
    }

    public Notification(NotificationType type, String title, String description, float time) {
        this.title = title;
        this.description = description;
        this.time = (long) (time * 1000);
        timerUtil = new TimerUtils();
        this.notificationType = type;
        animation = new DecelerateAnimation(250, 1);
    }

    public void drawDefault(float x, float y, float width, float height, float alpha) {
        RoundedUtils.drawRound(x, y, width, height, 4, new Color(ColorUtils.reAlpha(notificationType.getColor().getRGB(), 190), true));

        Color textColor = ColorUtils.applyOpacity(Color.WHITE, alpha);
        FontManager.default18.drawString(getTitle(), x + 5, y + 2, textColor.getRGB());

        FontManager.default16.drawString(getDescription(), x + 5, y + 4 + FontManager.default18.getHeight(), ColorUtils.reAlpha(Color.WHITE.getRGB(), (int) (255 * alpha - 70)));

    }

    public void blurDefault(float x, float y, float width, float height) {
        RoundedUtils.drawRound(x, y, width, height, 5, notificationType.getColor());
    }
}
