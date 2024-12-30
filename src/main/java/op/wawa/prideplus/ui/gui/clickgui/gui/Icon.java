package op.wawa.prideplus.ui.gui.clickgui.gui;

import op.wawa.prideplus.utils.animation.ColorAnimation;
import op.wawa.prideplus.utils.animation.animations.Direction;
import op.wawa.prideplus.utils.render.RenderUtils;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

/**
 * @author ChengFeng
 * @since 2024/8/1
 **/
public class Icon {
    private final ResourceLocation resource;
    public float x, y;
    public final float size;
    public final ColorAnimation colorAnim;
    private boolean background = false;
    public boolean lock = false;

    public Icon(ResourceLocation resource, float size) {
        this.resource = resource;
        this.size = size;
        this.colorAnim = new ColorAnimation(new Color(255, 255, 255, 70), Color.WHITE, 100);
    }

    public Icon(ResourceLocation resource, float size, ColorAnimation colorAnim, boolean background) {
        this.resource = resource;
        this.size = size;
        this.colorAnim = colorAnim;
        this.background = background;
    }

    public void draw(float x, float y, int mouseX, int mouseY) {
        this.x = x;
        this.y = y;
        boolean hovered = RenderUtils.hovering(mouseX, mouseY, this.x, this.y, size, size);
        if (!lock) {
            if (hovered) {
                if (colorAnim.getDirection() == Direction.BACKWARDS) colorAnim.changeDirection();
            } else if (colorAnim.getDirection() == Direction.FORWARDS) colorAnim.changeDirection();
        }
        RenderUtils.drawImage(resource, this.x, this.y, size, size, colorAnim.getOutput());
    }
}
