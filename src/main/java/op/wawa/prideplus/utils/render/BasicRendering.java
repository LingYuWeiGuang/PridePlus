package op.wawa.prideplus.utils.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import static org.lwjgl.opengl.GL11.*;

public class BasicRendering {
    private static final Tessellator tessellator;
    private static final WorldRenderer worldrenderer;

    public static void drawRect(double x, double y, double width, double height, int color) {
        GLUtils.setup2DRendering(() -> {
            glColor(color);
            worldrenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION);
            worldrenderer.pos(x, y + height, 0.0D).endVertex();
            worldrenderer.pos(x + width, y + height, 0.0D).endVertex();
            worldrenderer.pos(x + width, y, 0.0D).endVertex();
            worldrenderer.pos(x, y, 0.0D).endVertex();
            tessellator.draw();
            GlStateManager.resetColor();
        });
    }

    public static void drawCircle(double x, double y, float radius, int color) {
        final float correctRadius = radius * 2;
        GLUtils.setup2DRendering(() -> {
            glColor(color);
            glEnable(GL_POINT_SMOOTH);
            glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
            glPointSize(correctRadius);
            GLUtils.setupRendering(GL_POINTS, () -> glVertex2d(x, y));
            glDisable(GL_POINT_SMOOTH);
            GlStateManager.resetColor();
        });
    }

    public static void glColor(int color) {
        final float red = (float) (color >> 16 & 255) / 255F;
        final float green = (float) (color >> 8 & 255) / 255F;
        final float blue = (float) (color & 255) / 255F;
        final float alpha = (float) (color >> 24 & 255) / 255F;
        GlStateManager.color(red, green, blue, alpha);
    }

    static {
        tessellator = Tessellator.getInstance();
        worldrenderer = tessellator.getWorldRenderer();
    }
}
