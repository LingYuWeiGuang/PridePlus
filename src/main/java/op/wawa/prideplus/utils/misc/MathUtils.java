package op.wawa.prideplus.utils.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.Random;

public class MathUtils {
    public static Random random = new Random();

    public static int getRandom(final int min, final int max) {
        if (max < min) {
            return 0;
        }
        return min + MathUtils.random.nextInt(max - min + 1);
    }
    public static double randomNumber(double max, double min) {
        return (Math.random() * (max - min)) + min;
    }
    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
    public static double getRandom(final double min, final double max) {
        final double range = max - min;
        double scaled = MathUtils.random.nextDouble() * range;
        if (scaled > max) {
            scaled = max;
        }
        double shifted = scaled + min;
        if (shifted > max) {
            shifted = max;
        }
        return shifted;
    }

    public static float randomizeFloat(float min, float max) {
        return (float) (min + (max - min) * Math.random());
    }

    public static float process(float value, float defF, float defV) {
        return defV / (value / defF);
    }

    public static float bal_float( float max,  float value, float width) {
        float percentage = MathHelper.clamp_float(value / max, 0, 1);
        return Math.max(0, width * percentage);
    }

    public static float toDegree(double x, double z) {
        return (float)(Math.atan2(z - (Minecraft.getMinecraft()).thePlayer.posZ, x - (Minecraft.getMinecraft()).thePlayer.posX) * 180.0D / Math.PI) - 90.0F;
    }

    public static float calculateGaussianValue(float x, float sigma) {
        double PI = 3.141592653;
        double output = 1.0 / Math.sqrt(2.0 * PI * (sigma * sigma));
        return (float) (output * Math.exp(-(x * x) / (2.0 * (sigma * sigma))));
    }

    public static float interpolateFloat(float oldValue, float newValue, double interpolationValue){
        return interpolate(oldValue, newValue, (float) interpolationValue).floatValue();
    }

    public static double interpolateSmooth(double current, double old, double scale) {
        return old + (current - old) * scale;
    }

    public static double incValue(double val, double inc) {
        double one = 1.0 / inc;
        return Math.round(val * one) / one;
    }

    public static Double interpolate(double oldValue, double newValue, double interpolationValue){
        return (oldValue + (newValue - oldValue) * interpolationValue);
    }

    public static int interpolateInt(int oldValue, int newValue, double interpolationValue){
        return interpolate(oldValue, newValue, (float) interpolationValue).intValue();
    }

    public static double getRandomInRange(double min, double max) {
        SecureRandom random = new SecureRandom();
        return min == max ? min : random.nextDouble() * (max - min) + min;
    }

    public static double round(double num, double increment) {
        BigDecimal bd = new BigDecimal(num);
        bd = (bd.setScale((int) increment, RoundingMode.HALF_UP));
        return bd.doubleValue();
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static String round(String value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.stripTrailingZeros();
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.toString();
    }
}
