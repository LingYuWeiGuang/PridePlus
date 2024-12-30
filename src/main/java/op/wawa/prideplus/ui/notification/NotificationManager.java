package op.wawa.prideplus.ui.notification;

import op.wawa.prideplus.ui.font.FontManager;
import op.wawa.prideplus.utils.MinecraftInstance;
import op.wawa.prideplus.utils.animation.animations.Animation;
import op.wawa.prideplus.utils.animation.animations.Direction;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.ScaledResolution;

import java.util.concurrent.CopyOnWriteArrayList;

public class NotificationManager implements MinecraftInstance {
    @Getter
    @Setter
    private static float toggleTime = 2;

    @Getter
    private static final CopyOnWriteArrayList<Notification> notifications = new CopyOnWriteArrayList<>();

    public static void post(NotificationType type, String title, String description) {
        post(new Notification(type, title, description));
    }

    public static void post(NotificationType type, String title, String description, float time) {
        post(new Notification(type, title, description, time));
    }

    public static void post(Notification notification) {
        notifications.add(notification);
    }

    public static void render() {
        float yOffset = 0;
        int notificationHeight = 0;
        int notificationWidth;
        int actualOffset = 0;
        ScaledResolution sr = new ScaledResolution(mc);

        NotificationManager.setToggleTime(2);

        for (Notification notification : NotificationManager.getNotifications()) {
            Animation animation = notification.getAnimation();
            animation.setDirection(notification.getTimerUtil().hasTimeElapsed((long) notification.getTime()) ? Direction.BACKWARDS : Direction.FORWARDS);

            if (animation.finished(Direction.BACKWARDS)) {
                NotificationManager.getNotifications().remove(notification);
                continue;
            }

            float x, y;

            animation.setDuration(250);
            actualOffset = 8;

            notificationHeight = 24;
            notificationWidth = (int) Math.max(FontManager.default18.getStringWidth(notification.getTitle()), FontManager.default16.getStringWidth(notification.getDescription())) + 15;

            x = sr.getScaledWidth() - (notificationWidth + 5) * (float) animation.getOutput().floatValue();
            y = sr.getScaledHeight() - (yOffset + 18 + notificationHeight + (15));

            notification.drawDefault(x, y, notificationWidth, notificationHeight, animation.getOutput().floatValue());


            yOffset += (notificationHeight + actualOffset) * animation.getOutput().floatValue();

        }
    }

    public static void renderEffects() {
        float yOffset = 0;
        int notificationHeight = 0;
        int notificationWidth;
        int actualOffset = 0;
        ScaledResolution sr = new ScaledResolution(mc);


        for (Notification notification : NotificationManager.getNotifications()) {
            Animation animation = notification.getAnimation();
            animation.setDirection(notification.getTimerUtil().hasTimeElapsed((long) notification.getTime()) ? Direction.BACKWARDS : Direction.FORWARDS);

            if (animation.finished(Direction.BACKWARDS)) {
                NotificationManager.getNotifications().remove(notification);
                continue;
            }

            float x, y;

            actualOffset = 8;

            notificationHeight = 24;
            notificationWidth = Math.max(FontManager.default18.getStringWidth(notification.getTitle()), FontManager.default16.getStringWidth(notification.getDescription())) + 15;

            x = sr.getScaledWidth() - (notificationWidth + 5) * animation.getOutput().floatValue();
            y = sr.getScaledHeight() - (yOffset + 18 + notificationHeight + (15));

            notification.blurDefault(x, y, notificationWidth, notificationHeight);


            yOffset += (notificationHeight + actualOffset) * animation.getOutput().floatValue();

        }
    }

}
