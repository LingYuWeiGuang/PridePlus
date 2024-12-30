package op.wawa.prideplus.ui.gui.button;

import op.wawa.prideplus.ui.font.FontDrawer;
import op.wawa.prideplus.ui.font.FontManager;
import op.wawa.prideplus.utils.animation.animations.Animation;
import op.wawa.prideplus.utils.animation.animations.Direction;
import op.wawa.prideplus.utils.animation.animations.impl.DecelerateAnimation;
import op.wawa.prideplus.utils.render.RenderUtils;
import op.wawa.prideplus.utils.render.shader.RoundedUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;

@Getter
@Setter

public class CustomMenuButton extends GuiScreen {

    public final String text;
    private Animation displayAnimation;

    private Animation hoverAnimation = new DecelerateAnimation(500, 1);;
    public float x, y, width, height;
    public Runnable clickAction;
    public FontDrawer font;

    public CustomMenuButton(String text, int x, int y, int width, int height, FontDrawer font, Runnable clickAction) {
        this.text = text;
        displayAnimation = new DecelerateAnimation(1000, 255);
        this.font = font;
        this.clickAction = clickAction;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public CustomMenuButton(String text, Runnable clickAction) {
        this.text = text;
        displayAnimation = new DecelerateAnimation(1000, 255);
        font = FontManager.default22;
        this.clickAction = clickAction;
    }

    public CustomMenuButton(String text) {
        this.text = text;
        displayAnimation = new DecelerateAnimation(1000, 255);
        font = FontManager.default22;
    }

    @Override
    public void initGui() {
        hoverAnimation = new DecelerateAnimation(500, 1);
        displayAnimation.setDirection(Direction.FORWARDS);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float ticks) {
        boolean hovered = RenderUtils.hovering(mouseX, mouseY, x, y, width, height);
        hoverAnimation.setDirection(hovered ? Direction.FORWARDS : Direction.BACKWARDS);
        Color rectColor = new Color(32, 32, 32, (int) (displayAnimation.getOutput() * Math.max(0.7, hoverAnimation.getOutput())));
        RoundedUtils.drawRound(x, y, width, height, 4, rectColor);
        font.drawCenteredString(text, x + width / 2f, y + font.getMiddleOfBox(height) + 2f, new Color(255, 255, 255, displayAnimation.getOutput().intValue()).getRGB());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        boolean hovered = RenderUtils.hovering(mouseX, mouseY, x, y, width, height);
        if (hovered) clickAction.run();
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
    }

    @Override
    public void onGuiClosed() {
        displayAnimation.setDirection(Direction.BACKWARDS);
    }
}