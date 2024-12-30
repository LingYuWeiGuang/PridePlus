package op.wawa.prideplus.utils.animation;

import net.minecraft.util.MathHelper;

public final class AnimationUtils {
    public static double delta = 1.0;
    public static float moveUD(float current, float target, float speed) {
        float movement = (target - current) * speed;
        if (movement > 0.0f) {
            movement = Math.max(speed, movement);
            movement = Math.min(target - current, movement);
        } else if (movement < 0.0f) {
            movement = Math.min(-speed, movement);
            movement = Math.max(target - current, movement);
        }
        return current + movement;
    }
    public static float moveUD(float current, final float end) {
        return lstransition(current, end, 2f);
    }
    public static float lstransition(float now, float desired, double speed) {
        double dif = (double) Math.abs(desired - now);
        float a = (float) Math.abs((double) (desired - (desired - Math.abs(desired - now))) / (100.0 - speed * 10.0));
        float x = now;
        if (dif != 0.0 && dif < (double) a) {
            a = (float) dif;
        }

        if (dif > 0.0) {
            if (now < desired) {
                x = (float) (now + a * delta);
            } else if (now > desired) {
                x = (float) (now - a * delta);
            }
        } else {
            x = desired;
        }

        if ((double) Math.abs(desired - x) < 0.05 && x != desired) {
            x = desired;
        }

        return x;
    }
    public static double easing(double now,double target,double speed) {
        return Math.abs(target - now) * speed;
    }
    public static float smooth(float current, float target, float speed) {
        float f2 = MathHelper.wrapAngleTo180_float(target - current);
        if (f2 > speed) {
            f2 = speed;
        }
        if (f2 < -speed) {
            f2 = -speed;
        }
        return current + f2;
    }
    public static double animate(double current, double target, double speed) {
        boolean larger = target > current;
        if (speed < 0.0D) {
            speed = 0.0D;
        } else if (speed > 1.0D) {
            speed = 1.0D;
        }

        double dif = Math.max(target, current) - Math.min(target, current);
        double factor = dif * speed;
        if (factor < 0.1D) {
            factor = 0.1D;
        }

        if (larger) {
            current += factor;
        } else {
            current -= factor;
        }

        return current;
    }
}
