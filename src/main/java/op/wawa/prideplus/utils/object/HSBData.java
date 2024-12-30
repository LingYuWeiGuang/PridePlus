package op.wawa.prideplus.utils.object;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Setter
@Getter
public class HSBData {

    private float hue, saturation, brightness, alpha = 1;

    public HSBData(float hue, float saturation, float brightness, float alpha) {
        this.hue = hue;
        this.saturation = saturation;
        this.brightness = brightness;
        this.alpha = alpha;
    }

    public HSBData(Color color) {

        final float[] hsbColor = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);

        this.hue = hsbColor[0];
        this.saturation = hsbColor[1];
        this.brightness = hsbColor[2];
    }

    public Color getAsColor() {
        final Color beforeReAlpha = Color.getHSBColor(hue, saturation, brightness);
        return new Color(beforeReAlpha.getRed(), beforeReAlpha.getGreen(), beforeReAlpha.getBlue(), Math.round(255 * alpha));
    }

}
