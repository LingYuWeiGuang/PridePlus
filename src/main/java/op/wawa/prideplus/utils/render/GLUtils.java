package op.wawa.prideplus.utils.render;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.util.LinkedList;

import static net.minecraft.client.renderer.GlStateManager.*;
import static org.lwjgl.opengl.GL11.*;

public final class GLUtils {
    private final LinkedList<Integer> enableToggleList = new LinkedList<>();
    private final LinkedList<Integer> disableToggleList = new LinkedList<>();
    public static int[] enabledCaps = new int[32];
    public void enable(int... caps) {
        for (int cap : caps) {
            enable(cap);
        }
    }

    public void enable(int cap) {
        glEnable(cap);
        enableToggleList.add(cap);
    }

    public void disable(int... caps) {
        for (int cap : caps) {
            disable(cap);
        }
    }
    public static void enableCaps(int... caps) {
        for (int cap : caps) glEnable(cap);
        enabledCaps = caps;
    }
    public static void disableCaps() {
        for (int cap : enabledCaps) glDisable(cap);
    }
    public void disable(int cap) {
        glDisable(cap);
        disableToggleList.add(cap);
    }

    public void enableNoToggle(int... cap) {
        for (int i : cap) {
            enableNoToggle(i);
        }
    }

    public void enableNoToggle(int cap) {
        glEnable(cap);
    }

    public void disableNoToggle(int... cap) {
        for (int i : cap) {
            disableNoToggle(i);
        }
    }

    public void disableNoToggle(int cap) {
        glDisable(cap);
    }

    public void toggle() {
        int cap;

        while (!enableToggleList.isEmpty()) {
            cap = enableToggleList.poll();

            glDisable(cap);
        }

        while (!disableToggleList.isEmpty()) {
            cap = disableToggleList.poll();

            glEnable(cap);
        }
    }

    public static void setupRendering(int mode, Runnable runnable) {
        glBegin(mode);
        runnable.run();
        glEnd();
    }

    public static void glBegin(int p_glBegin_0_) {
        GL11.glBegin(p_glBegin_0_);
    }

    public static void glEnd() {
        GL11.glEnd();
    }

    public static void pushMatrix() {
        glPushMatrix();
    }

    public static void popMatrix() {
        glPopMatrix();
    }

    public static void blendFunc(int sFactor,int dFactor) {
        glBlendFunc(sFactor,dFactor);
    }

    public static void translated(double x,double y,double z) {
        glTranslated(x,y,z);
    }

    public static void rotated(double angle,double x,double y,double z) {
        glRotated(angle,x,y,z);
    }

    public static void depthMask(boolean flag) {
        glDepthMask(flag);
    }

    public static void color(int r,int g,int b) {
        color(r,g,b,255);
    }

    public static void color(int r,int g,int b,int a) {
        GlStateManager.color(r / 255f,g / 255f,b / 255f,a / 255f);
    }

    public static void color(int hex) {
        GlStateManager.color(
                (hex >> 16 & 0xFF) / 255.0f,
                (hex >> 8 & 0xFF) / 255.0f,
                (hex & 0xFF) / 255.0f,
                (hex >> 24 & 0xFF) / 255.0f);
    }

    public static void resetColor() {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void scale(double x,double y,double z) {
        glScaled(x,y,z);
    }

    public static void startBlend() {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }
    public static void endBlend() {
        GlStateManager.disableBlend();
    }
    public static void setup2DRendering() {
        setup2DRendering(true);
    }
    public static void setup2DRendering(boolean blend) {
        if (blend) {
            startBlend();
        }
        GlStateManager.disableTexture2D();
    }

    public static void setup2DRendering(Runnable runnable) {
        enableBlend();
        blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        disableAlpha();
        disableTexture2D();
        runnable.run();
        enableTexture2D();
        enableAlpha();
        disableBlend();
    }

    public static void end2DRendering() {
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
}
