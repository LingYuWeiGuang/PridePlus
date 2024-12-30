package op.wawa.prideplus.ui.gui.clickgui.component.impl;

import op.wawa.prideplus.ui.font.FontManager;
import op.wawa.prideplus.ui.gui.clickgui.ThemeColor;
import op.wawa.prideplus.ui.gui.clickgui.component.Component;
import op.wawa.prideplus.utils.animation.ColorAnimation;
import op.wawa.prideplus.utils.animation.animations.Animation;
import op.wawa.prideplus.utils.animation.animations.Direction;
import op.wawa.prideplus.utils.animation.animations.impl.CustomAnimation;
import op.wawa.prideplus.utils.animation.animations.impl.DecelerateAnimation;
import op.wawa.prideplus.utils.animation.animations.impl.SmoothStepAnimation;
import op.wawa.prideplus.utils.misc.MathUtils;
import op.wawa.prideplus.utils.render.ColorUtils;
import op.wawa.prideplus.utils.render.RenderUtils;
import op.wawa.prideplus.utils.render.shader.RoundedUtils;
import op.wawa.prideplus.value.values.NumberValue;
import org.lwjgl.input.Keyboard;

import java.awt.*;

/**
 * @author ChengFeng
 * @since 2024/8/2
 **/
public class NumberComponent extends Component<Double> {

    public NumberComponent(NumberValue value) {
        super(value);
        height = 13f;
        width = 65f;

        steps = (int) Math.floor((value.getMaxValue() - value.getMinValue()) / value.getIncrease());
        widthPerStep = width / steps;

        colorAnim = new ColorAnimation(ThemeColor.focusedColor, ThemeColor.unfocusedColor, 100);
        alphaAnim = new SmoothStepAnimation(100, 1d);
    }

    private CustomAnimation animation;
    private final ColorAnimation colorAnim;
    private final Animation alphaAnim;

    private final int steps;
    private final float widthPerStep;
    private boolean dragging;
    private boolean selected;

    @Override
    public void init() {
        NumberValue value = (NumberValue) this.value;
        int startStep = (int) ((value.getValue() - value.getMinValue()) / value.getIncrease());
        if (startStep > steps) startStep = steps;
        if (startStep < 0) startStep = 0;
        animation = new CustomAnimation(DecelerateAnimation.class, 100, 0, startStep * widthPerStep);
    }

    @Override
    public void draw(float x, float y, int mouseX, int mouseY) {
        this.posX = x + panelWidth - width - 9f;
        this.posY = y + 2f;

        float barHeight = 2f;
        NumberValue value = (NumberValue) this.value;
        Double valueX = animation.getOutput();

        if (dragging) {
            int nowStep = (int) ((mouseX - posX) / widthPerStep);
            if (nowStep < 0) nowStep = 0;
            if (nowStep > steps) nowStep = steps;

            setEndPoint(nowStep * widthPerStep);

            value.setValue(value.getMinValue() + value.getIncrease() * nowStep);
        }

        if (selected && colorAnim.getDirection() == Direction.FORWARDS) {
            colorAnim.changeDirection();
        } else if (!selected && colorAnim.getDirection() == Direction.BACKWARDS) {
            colorAnim.changeDirection();
        }

        if (selected) {
            if (alphaAnim.getDirection() == Direction.BACKWARDS) alphaAnim.changeDirection();
        } else if (alphaAnim.getDirection() == Direction.FORWARDS) alphaAnim.changeDirection();

        if (alphaAnim.getOutput() != 0) {
            FontManager.rubik13.drawCenteredStringWithShadow(String.valueOf(MathUtils.round(value.getValue(), 2)), this.posX + 0.5f + valueX.floatValue(), this.posY - 10f, ColorUtils.applyOpacity(Color.WHITE.getRGB(), alphaAnim.getOutput().floatValue()));
        }
        RoundedUtils.drawRound(this.posX, this.posY, width, barHeight, 1f, ThemeColor.barBgColor);
        RoundedUtils.drawRound(this.posX, this.posY, valueX.floatValue(), barHeight, 1f, ThemeColor.barColor);
        RoundedUtils.drawRound(this.posX - 2f + valueX.floatValue(), this.posY - 2f, 6f, 6f, 2.5f, colorAnim.getOutput());
    }

    private void setEndPoint(double endPoint) {
        animation.setStartPoint(animation.getOutput());
        animation.setEndPoint(endPoint);
        animation.getAnimation().reset();
    }

    @Override
    public void onKeyTyped(char c, int keyCode) {
        if (!selected) return;

        NumberValue value = (NumberValue) this.value;
        if (keyCode == Keyboard.KEY_LEFT) {
            setEndPoint(animation.getEndPoint() - widthPerStep);
            double newValue = value.getValue() - value.getIncrease();
            value.setValue(Math.max(newValue, value.getMinValue()));
        } else if (keyCode == Keyboard.KEY_RIGHT) {
            setEndPoint(animation.getEndPoint() + widthPerStep);
            double newValue = value.getValue() + value.getIncrease();
            value.setValue(Math.min(newValue, value.getMaxValue()));
        }
        if (animation.getEndPoint() < 0) animation.setEndPoint(0);
        if (animation.getEndPoint() > steps * widthPerStep) animation.setEndPoint(steps * widthPerStep);
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, int button) {
        if (RenderUtils.hovering(mouseX, mouseY, posX - 2f, posY - 8f, width + 4f, height + 4f)) {
            dragging = true;
            selected = true;
            Keyboard.enableRepeatEvents(true);
        } else {
            selected = false;
        }
    }

    @Override
    public void onMouseRelease() {
        dragging = false;
    }
}
