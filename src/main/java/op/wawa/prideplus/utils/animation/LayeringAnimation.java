package op.wawa.prideplus.utils.animation;

import op.wawa.prideplus.utils.render.BasicRendering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

public class LayeringAnimation {
    private static GuiScreen targetScreen;
    private static int progress;
    private static boolean played = false;

    public static void play(GuiScreen target) {
        targetScreen = target;
        progress = 0;
        played = true;
    }

    public static void drawAnimation() {
        if (!played) return;
        progress = (int) LinearAnimation.animate(progress, targetScreen == null ? 0 : 25500, 0.2f);
        if (progress > 25400) {
            Minecraft.getMinecraft().displayGuiScreen(targetScreen);
            targetScreen = null;
        }
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        BasicRendering.drawRect(0, 0, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), new Color(0, 0, 0, progress / 100).getRGB());
    }
}
