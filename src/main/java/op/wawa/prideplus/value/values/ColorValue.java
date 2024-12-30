package op.wawa.prideplus.value.values;

import op.wawa.prideplus.utils.render.ColorUtils;
import op.wawa.prideplus.value.Value;

import java.awt.*;
import java.util.Locale;
import java.util.function.Supplier;

public class ColorValue extends Value<Color> {
    public NumberValue hue;
    public NumberValue saturation;
    public NumberValue brightness;
    public NumberValue opacity;
    public NumberValue speed;

    public BooleanValue rainbow;
    public BooleanValue fade;

    public ColorValue(String name, Color defaultValue) {
        super(name, defaultValue);
        hue = new NumberValue("Hue", 0f, 0f, 1f, 0.01f);
        saturation = new NumberValue("Saturation", 0f, 0f, 1f, 0.01f);
        brightness = new NumberValue("Brightness", 0f, 0f, 1f, 0.01f);
        opacity = new NumberValue("Opacity", 1f, 0.01f, 1f, 0.01f);
        speed = new NumberValue("Speed", 15f, 5f, 30f, 1f);

        rainbow = new BooleanValue("Rainbow", false);
        fade = new BooleanValue("Fade", false);
        setValue(defaultValue);
    }

    @Override
    public ColorValue setVisible(Supplier<Boolean> visible) {
        return (ColorValue) super.setVisible(visible);
    }

    @Override
    public void setValue(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        hue.setValue((double) hsb[0]);
        saturation.setValue((double) hsb[1]);
        brightness.setValue((double) hsb[2]);
        opacity.setValue(color.getAlpha() / 255d);
    }

    public void setValue(float hue, float saturation, float brightness, float opacity) {
        this.hue.setValue((double) hue);
        this.saturation.setValue((double) saturation);
        this.brightness.setValue((double) brightness);
        this.opacity.setValue((double) opacity);
    }

    public String getHexCode() {
        Color color = getValue();
        return String.format("%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()).toUpperCase(Locale.ROOT);
    }

    @Override
    public Color getValue() {
        Color color = ColorUtils.applyOpacity(Color.getHSBColor(hue.getValue().floatValue(), saturation.getValue().floatValue(), brightness.getValue().floatValue()), opacity.getValue().floatValue());
        if (rainbow.getValue()) {
            color =  ColorUtils.rainbow(speed.getValue().intValue(), 0, saturation.getValue().floatValue(), brightness.getValue().floatValue(), opacity.getValue().floatValue());
        }
        if (fade.getValue()) {
            color = ColorUtils.fade(speed.getValue().intValue(), 0, color, opacity.getValue().floatValue());
        }
        return color;
    }

    public Color getValue(int index) {
        Color color = getValue();
        if (rainbow.getValue()) {
            color = ColorUtils.rainbow(speed.getValue().intValue(), index, saturation.getValue().floatValue(), brightness.getValue().floatValue(), opacity.getValue().floatValue());
        }
        if (fade.getValue()) {
            color = ColorUtils.fade(speed.getValue().intValue(), index, color, opacity.getValue().floatValue());
        }
        return color;
    }
}


