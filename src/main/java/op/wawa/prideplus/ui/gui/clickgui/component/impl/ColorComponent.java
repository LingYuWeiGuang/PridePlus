package op.wawa.prideplus.ui.gui.clickgui.component.impl;

import op.wawa.prideplus.ui.font.FontDrawer;
import op.wawa.prideplus.ui.font.FontManager;
import op.wawa.prideplus.ui.gui.clickgui.ThemeColor;
import op.wawa.prideplus.ui.gui.clickgui.component.Component;
import op.wawa.prideplus.utils.animation.animations.Direction;
import op.wawa.prideplus.utils.animation.animations.impl.CustomAnimation;
import op.wawa.prideplus.utils.animation.animations.impl.DecelerateAnimation;
import op.wawa.prideplus.utils.render.ColorUtils;
import op.wawa.prideplus.utils.render.RenderUtils;
import op.wawa.prideplus.utils.render.StencilUtils;
import op.wawa.prideplus.utils.render.shader.RoundedUtils;
import op.wawa.prideplus.value.values.ColorValue;
import net.minecraft.client.gui.FontRenderer;

import java.awt.*;

/**
 * @author ChengFeng
 * @since 2024/8/2
 **/
public class ColorComponent extends Component<Color> {
    public ColorComponent(ColorValue value) {
        super(value);

        colorWidth = 65f;
        colorHeight = 12f;

        float maxHeight = 13f * 8f + 5f * 7f;
        heightAnim = new CustomAnimation(DecelerateAnimation.class, 100, maxHeight, minHeight);

        buttons = new ButtonComponent[2];
        buttons[0] = new ButtonComponent(value.rainbow);
        buttons[1] = new ButtonComponent(value.fade);

        numbers = new NumberComponent[5];
        numbers[0] = new NumberComponent(value.hue);
        numbers[1] = new NumberComponent(value.saturation);
        numbers[2] = new NumberComponent(value.brightness);
        numbers[3] = new NumberComponent(value.opacity);
        numbers[4] = new NumberComponent(value.speed);
    }

    private boolean expanded;
    private float minX, minY;
    private final float minHeight = 13f;
    private final float colorWidth, colorHeight;
    private final CustomAnimation heightAnim;
    private final ButtonComponent[] buttons;
    private final NumberComponent[] numbers;

    @Override
    public void init() {
        for (ButtonComponent button : buttons) {
            button.init();
        }
        for (NumberComponent number : numbers) {
            number.init();
        }
    }

    @Override
    public void draw(float x, float y, int mouseX, int mouseY) {
        this.minX = x;
        this.minY = y;
        this.posX = x + panelWidth - 2 * xGap - colorWidth;
        this.posY = y - 3f;
        this.height = heightAnim.getOutput().floatValue();
        ColorValue cv = (ColorValue) value;

        if (heightAnim.getAnimation().finished(Direction.FORWARDS))
            expanded = false;

        RoundedUtils.drawRoundOutline(posX, posY, colorWidth, colorHeight, 2f, 0.2f, cv.getValue(), ThemeColor.outlineColor);
        FontManager.default16.drawCenteredStringWithShadow("#" + cv.getHexCode(), posX + colorWidth / 2f, posY + colorHeight / 2f, ColorUtils.getOppositeColor(cv.getValue()).getRGB());

        if (expanded) {
            RoundedUtils.drawRound(minX + 2f, minY + minHeight + 2f, 0.5f, heightAnim.getOutput().floatValue() - minHeight - 6f, 1f, ThemeColor.grayColor);

            StencilUtils.initStencilToWrite();
            RoundedUtils.drawRoundOutline(minX, minY, panelWidth - 2 * xGap + 2f, heightAnim.getOutput().floatValue(), 2f, 0.2f, cv.getValue(), ThemeColor.outlineColor);
            StencilUtils.readStencilBuffer(1);

            FontDrawer font = FontManager.default16;
            float textX = minX + 5f;
            float valueY = minY + minHeight + 5f;

            for (ButtonComponent button : buttons) {
                font.drawString(button.value.getValueName(), textX, valueY, button.textColAnim.getOutput().getRGB());
                button.draw(minX, valueY, mouseX, mouseY);
                valueY += button.height + 5f;
            }

            for (NumberComponent number : numbers) {
                font.drawString(number.value.getValueName(), textX, valueY, Color.WHITE.getRGB());
                number.draw(minX, valueY, mouseX, mouseY);
                valueY += number.height + 5f;
            }

            StencilUtils.uninitStencilBuffer();
        }
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, int button) {
        if (RenderUtils.hovering(mouseX, mouseY, posX, posY, colorWidth, colorHeight)) {
            expanded = !expanded;
            heightAnim.changeDirection();
        }

        if (expanded && RenderUtils.hovering(mouseX, mouseY, minX, minY, panelWidth - 2 * xGap, heightAnim.getOutput().floatValue())) {
            for (ButtonComponent buttonComponent : buttons) {
                buttonComponent.onMouseClick(mouseX, mouseY, button);
            }
            for (NumberComponent number : numbers) {
                number.onMouseClick(mouseX, mouseY, button);
            }
        }
    }

    @Override
    public void onMouseRelease() {
        if (expanded) {
            for (NumberComponent number : numbers) {
                number.onMouseRelease();
            }
        }
    }

    @Override
    public void onKeyTyped(char c, int keyCode) {
        if (expanded) {
            for (NumberComponent number : numbers) {
                number.onKeyTyped(c, keyCode);
            }
        }
    }
}
