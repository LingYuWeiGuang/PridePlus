package op.wawa.prideplus.ui.gui.clickgui.component.impl;

import op.wawa.prideplus.Pride;
import op.wawa.prideplus.ui.font.FontDrawer;
import op.wawa.prideplus.ui.font.FontManager;
import op.wawa.prideplus.ui.gui.clickgui.ThemeColor;
import op.wawa.prideplus.ui.gui.clickgui.component.Component;
import op.wawa.prideplus.ui.gui.clickgui.gui.Icon;
import op.wawa.prideplus.utils.animation.ColorAnimation;
import op.wawa.prideplus.utils.animation.animations.Direction;
import op.wawa.prideplus.utils.misc.compare.StringComparator;
import op.wawa.prideplus.utils.render.RenderUtils;
import op.wawa.prideplus.value.values.ModeValue;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ChengFeng
 * @since 2024/8/1
 **/
public class ModeComponent extends Component<String> {
    private final FontDrawer font = FontManager.default16;
    private final Icon left;
    private final Icon right;
    private final ColorAnimation colorAnim;
    private boolean selected;

    public ModeComponent(ModeValue value) {
        super(value);
        List<String> modes = value.getModes();
        modes.sort(new StringComparator(font));
        width = font.getStringWidth(modes.getLast());
        height = 13f;
        left = new Icon(new ResourceLocation(Pride.NAME.toLowerCase() + "/icon/arrow-left.png"), 12f);
        right = new Icon(new ResourceLocation(Pride.NAME.toLowerCase() + "/icon/arrow-right.png"), 12f);
        colorAnim = new ColorAnimation(Color.WHITE, ThemeColor.grayColor, 100);
    }

    @Override
    public void draw(float x, float y, int mouseX, int mouseY) {
        this.posX = x + panelWidth - 2 * xGap - width - 12f;
        this.posY = y - 3f;

        if (selected) Keyboard.enableRepeatEvents(false);

        if (selected && colorAnim.getDirection() == Direction.FORWARDS) {
            colorAnim.changeDirection();
        } else if (!selected && colorAnim.getDirection() == Direction.BACKWARDS) {
            colorAnim.changeDirection();
        }

        if (!Keyboard.isKeyDown(Keyboard.KEY_LEFT) && left.colorAnim.getDirection() == Direction.FORWARDS) {
            left.colorAnim.changeDirection();
            left.lock = false;
        }
        if (!Keyboard.isKeyDown(Keyboard.KEY_RIGHT) && right.colorAnim.getDirection() == Direction.FORWARDS) {
            right.colorAnim.changeDirection();
            right.lock = false;
        }

        left.draw(this.posX - 9f, this.posY, mouseX, mouseY);
        right.draw(this.posX + width + 3f, this.posY, mouseX, mouseY);
        font.drawCenteredString(value.getValue(), this.posX + width / 2 + 3f, this.posY + height / 2 - 1f, colorAnim.getOutput().getRGB());
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, int button) {
        if (button == 0) {
            if (RenderUtils.hovering(mouseX, mouseY, this.posX - 9f, this.posY, 12f, 12f)) {
                ((ModeValue) value).prev();
            } else if (RenderUtils.hovering(mouseX, mouseY, this.posX + width + 2f, this.posY, 12f, 12f)) {
                ((ModeValue) value).next();
            }

            selected = RenderUtils.hovering(mouseX, mouseY, this.posX - 9f, this.posY, width + 24f, height + 6f);
        }
    }

    @Override
    public void onKeyTyped(char c, int keyCode) {
        if (!selected) return;

        if (keyCode == Keyboard.KEY_LEFT) {
            ((ModeValue) value).prev();
            if (left.colorAnim.getDirection() == Direction.BACKWARDS) {
                left.colorAnim.changeDirection();
                left.lock = true;
            }
        } else if (keyCode == Keyboard.KEY_RIGHT) {
            ((ModeValue) value).next();
            if (right.colorAnim.getDirection() == Direction.BACKWARDS) {
                right.colorAnim.changeDirection();
                right.lock = true;
            }
        }
    }
}
