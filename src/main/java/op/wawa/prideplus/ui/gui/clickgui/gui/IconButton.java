package op.wawa.prideplus.ui.gui.clickgui.gui;

import op.wawa.prideplus.ui.font.FontDrawer;
import op.wawa.prideplus.ui.gui.clickgui.ThemeColor;
import op.wawa.prideplus.utils.animation.ColorAnimation;
import op.wawa.prideplus.utils.animation.animations.Direction;
import op.wawa.prideplus.utils.render.RenderUtils;
import op.wawa.prideplus.utils.render.shader.RoundedUtils;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

/**
 * @author ChengFeng
 * @since 2024/8/1
 **/
public class IconButton {
    public float x, y, width, height;
    private final FontDrawer font;
    private final int size;
    private final String text;
    private final ResourceLocation resource;
    private final float gap;

    private ColorAnimation colAnim;

    public IconButton(FontDrawer font, float height, String text, ResourceLocation resource, int size, float gap) {
        this.font = font;
        this.height = height;
        this.text = text;
        this.resource = resource;
        this.size = size;
        this.gap = gap;

        this.width = 4 * gap + size + font.getStringWidth(text);
        colAnim = new ColorAnimation(ThemeColor.barColor, ThemeColor.focusedColor, 200);
        colAnim.changeDirection();
    }

    public void draw(float x, float y, int mouseX, int mouseY) {
        this.x = x;
        this.y = y;
        boolean hovered = RenderUtils.hovering(mouseX, mouseY, this.x, this.y, width, height);
        if (hovered) {
            if (colAnim.getDirection() == Direction.BACKWARDS) colAnim.changeDirection();
        } else if (colAnim.getDirection() == Direction.FORWARDS) colAnim.changeDirection();
        RoundedUtils.drawRound(this.x, this.y, width, height, 3f, colAnim.getOutput());
        RenderUtils.drawImage(resource, this.x + gap, this.y + gap / 2f, size, size);
        font.drawCenteredStringWithShadow(text, this.x + 2 * gap + size, this.y + height / 2f, Color.WHITE.getRGB());
    }
}
