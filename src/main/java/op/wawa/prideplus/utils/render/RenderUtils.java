package op.wawa.prideplus.utils.render;

import op.wawa.prideplus.utils.MinecraftInstance;
import op.wawa.prideplus.utils.misc.MathUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.*;
import static java.lang.Math.PI;
import static org.lwjgl.opengl.GL11.*;

public class RenderUtils implements MinecraftInstance {
    private static Map<BufferedImage, Integer> textureMap = new HashMap<>();

    public static double lastFrame = System.nanoTime();
    public static double frameTime = 0;

    public static void calcFrameDelta() {
        frameTime = ((System.nanoTime() - lastFrame) / 10000000.0);
        lastFrame = System.nanoTime();
    }

    public static void setAlphaLimit(float limit) {
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL_GREATER, (float) (limit * .01));
    }

    public static void drawCircle(double x, double y, double r, float lineWidth, boolean isFull, int color) {
        drawCircle(x, y, r, 10, lineWidth, 360, isFull, color);
    }

    public static void drawCircle(double cx, double cy, double r, int segments, float lineWidth, int part, boolean isFull, int color) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        glEnable(GL_LINE_SMOOTH);
        GL11.glLineWidth(lineWidth);
        glColor(color);

        GL11.glBegin(GL_LINE_STRIP);
        for (int i = segments - part; i <= segments; ++i) {
            glVertex2f((float) (cx + (cos(i * PI / 180) * (r * 1.001F))), (float) (cy + (sin(i * PI / 180) * (r * 1.001F))));

            if (!isFull) continue;
            GL11.glVertex2d(cx, cy);
        }
        GL11.glEnd();

        glDisable(GL_LINE_SMOOTH);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawCircle(float x, float y, float radius, int start, int end) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        glColor(Color.WHITE.getRGB());

        glEnable(GL_LINE_SMOOTH);
        glLineWidth(3F);
        glBegin(GL_LINE_STRIP);
        for (float i = end; i >= start; i -= (360 / 90.0f)) {
            glVertex2f((float) (x + (cos(i * PI / 180) * (radius * 1.001F))), (float) (y + (sin(i * PI / 180) * (radius * 1.001F))));
        }
        glEnd();
        glDisable(GL_LINE_SMOOTH);

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static boolean isHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2;
    }

    public static void drawBorderedRect(float x, float y, float x1, float y1, float width, int borderColor, int color) {
        drawRect(x + width, y + width, x1 - width, y1 - width, color);
        drawRect(x + width, y, x1 - width, y + width, borderColor);
        drawRect(x, y, x + width, y1, borderColor);
        drawRect(x1 - width, y, x1, y1, borderColor);
        drawRect(x + width, y1 - width, x1 - width, y1, borderColor);
    }

    public static void glColor(int color) {
        float f = (color >> 24 & 0xFF) / 255.0F;
        float f1 = (color >> 16 & 0xFF) / 255.0F;
        float f2 = (color >> 8 & 0xFF) / 255.0F;
        float f3 = (color & 0xFF) / 255.0F;
        GL11.glColor4f(f1, f2, f3, f);
    }
    public static void glColor(final int red, final int green, final int blue, final int alpha) {
        GlStateManager.color(red / 255F, green / 255F, blue / 255F, alpha / 255F);
    }

    public static void fixBlendIssues() {
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.0f);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public static Framebuffer createFrameBuffer(Framebuffer framebuffer) {
        return createFrameBuffer(framebuffer, false);
    }

    public static Framebuffer createFrameBuffer(Framebuffer framebuffer, boolean depth) {
        if (needsNewFramebuffer(framebuffer)) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(mc.displayWidth, mc.displayHeight, depth);
        }
        return framebuffer;
    }

    public static void drawRect(float x, float y, float x2, float y2, int color) {
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);

        color(color);
        glBegin(GL_QUADS);

        glVertex2f(x2, y);
        glVertex2f(x, y);
        glVertex2f(x, y2);
        glVertex2f(x2, y2);
        glEnd();

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
    }

    public static void drawRectWithTexture(float left, float top, float right, float bottom) {
        glPushMatrix();
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDepthMask(false);
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        // 绘制矩形
        glBegin(GL_QUADS);
        glTexCoord2f(0.0f, 1.0f);
        glVertex3f(left, bottom, 0);
        glTexCoord2f(1.0f, 1.0f);
        glVertex3f(right, bottom, 0);
        glTexCoord2f(1.0f, 0.0f);
        glVertex3f(right, top, 0);
        glTexCoord2f(0.0f, 0.0f);
        glVertex3f(left, top, 0);
        glEnd();

        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
        glPopMatrix();
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void drawQuads(float[] leftTop, float[] leftBottom, float[] rightTop, float[] rightBottom, Color rightColor, Color leftColor) {
        GLUtils.setup2DRendering();
        glEnable(GL_LINE_SMOOTH);
        glShadeModel(GL_SMOOTH);
        glPushMatrix();
        glBegin(GL_QUADS);
        color(leftColor.getRGB());
        glVertex2d(leftTop[0], leftTop[1]);
        glVertex2d(leftBottom[0], leftBottom[1]);
        color(rightColor.getRGB());
        glVertex2d(rightBottom[0], rightBottom[1]);
        glVertex2d(rightTop[0], rightTop[1]);
        glEnd();
        glPopMatrix();
        glDisable(GL_LINE_SMOOTH);
        GLUtils.end2DRendering();
        resetColor();
    }

    public static boolean needsNewFramebuffer(Framebuffer framebuffer) {
        return framebuffer == null || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight;
    }

    public static void bindTexture(int texture) {
        glBindTexture(GL_TEXTURE_2D, texture);
    }

    public static void drawImage(ResourceLocation resourceLocation, float x, float y, float imgWidth, float imgHeight) {
        GLUtils.startBlend();

        mc.getTextureManager().bindTexture(resourceLocation);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glEnable(GL13.GL_MULTISAMPLE);

        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, imgWidth, imgHeight, imgWidth, imgHeight);

        glDisable(GL13.GL_MULTISAMPLE);

        GLUtils.endBlend();
    }

    public static void drawImage(DynamicTexture image, float x, float y, float imgWidth, float imgHeight) {
        GLUtils.startBlend();

        // 绑定纹理并设置过滤参数
        GlStateManager.bindTexture(image.getGlTextureId());
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // 启用多重采样
        glEnable(GL13.GL_MULTISAMPLE);

        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, imgWidth, imgHeight, imgWidth, imgHeight);

        // 禁用多重采样
        glDisable(GL13.GL_MULTISAMPLE);

        GLUtils.endBlend();
    }

    public static void drawImage(ResourceLocation resourceLocation, float x, float y, float imgWidth, float imgHeight, Color color) {
        color(color.getRGB());
        drawImage(resourceLocation, x, y, imgWidth, imgHeight);
        resetColor();
    }

    /**
     * Sometimes colors get messed up in for loops, so we use this method to reset it to allow new colors to be used
     */
    public static void resetColor() {
        GlStateManager.color(1, 1, 1, 1);
    }

    /**
     * Scales the data that you put in the runnable
     *
     * @param x     start x pos
     * @param y     start y pos
     * @param scale scale
     */
    public static void scaleStart(float x, float y, float scale) {
        glPushMatrix();
        glTranslatef(x, y, 0);
        glScalef(scale, scale, 1);
        glTranslatef(-x, -y, 0);
    }

    /**
     * End scale
     */
    public static void scaleEnd() {
        glPopMatrix();
    }

    /**
     * GL Scissor
     *
     * @param x      x
     * @param y      y
     * @param width  width
     * @param height height
     */
    public static void scissorStart(double x, double y, double width, double height) {
        glPushMatrix();
        glEnable(GL_SCISSOR_TEST);
        ScaledResolution sr = new ScaledResolution(mc);
        double scale = sr.getScaleFactor();
        double finalHeight = height * scale;
        double finalY = (sr.getScaledHeight() - y) * scale;
        double finalX = x * scale;
        double finalWidth = width * scale;
        glScissor((int) finalX, (int) (finalY - finalHeight), (int) finalWidth, (int) finalHeight);
    }

    /**
     * End GL Scissor
     */
    public static void scissorEnd() {
        glDisable(GL_SCISSOR_TEST);
        glPopMatrix();
    }

    /**
     * Judge if cursor is hovering specific area.
     *
     * @param mouseX mX
     * @param mouseY mY
     * @param x      x
     * @param y      y
     * @param width  width
     * @param height height
     * @return boolean
     */
    public static boolean hovering(float mouseX, float mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

    /**
     * Judge if mouse button is down while the cursor in specific area.
     *
     * @param mouseX mX
     * @param mouseY mY
     * @param x      x
     * @param y      y
     * @param width  width
     * @param height height
     * @param button mouse button
     * @return boolean
     */
    public static boolean holding(float mouseX, float mouseY, float x, float y, float width, float height, int button) {
        return Mouse.isButtonDown(button) && hovering(mouseX, mouseY, x, y, width, height);
    }

    /**
     * This method colors the next available texture with a specified alpha value ranging from 0-1
     */
    public static void color(int color, float alpha) {
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        GlStateManager.color(r, g, b, alpha);
    }

    /**
     * Colors the next texture without a specified alpha value
     *
     * @param color color
     */
    public static void color(int color) {
        color(color, (float) (color >> 24 & 255) / 255.0F);
    }

    public static void renderESP(Entity entity, Color color, float alpha) {
        float ticks = mc.timer.renderPartialTicks;
        final double[] renderingEntityPos = new double[]{
                MathUtils.interpolate(entity.lastTickPosX, entity.posX, ticks) - mc.getRenderManager().viewerPosX,
                MathUtils.interpolate(entity.lastTickPosY, entity.posY, ticks) - mc.getRenderManager().viewerPosY,
                MathUtils.interpolate(entity.lastTickPosZ, entity.posZ, ticks) - mc.getRenderManager().viewerPosZ
        };

        final double entityRenderWidth = entity.width / 1.5;
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(renderingEntityPos[0] - entityRenderWidth,
                renderingEntityPos[1], renderingEntityPos[2] - entityRenderWidth, renderingEntityPos[0] + entityRenderWidth,
                renderingEntityPos[1] + entity.height + (entity.isSneaking() ? -0.3 : 0.18), renderingEntityPos[2] + entityRenderWidth).expand(0.15, 0.15, 0.15);

        GlStateManager.pushMatrix();
        GLUtils.setup2DRendering();
        GLUtils.enableCaps(GL_BLEND, GL_POINT_SMOOTH, GL_POLYGON_SMOOTH, GL_LINE_SMOOTH);

        double XZOffset = 0.2;
        double NYOffset = 0.15;
        double MYOffset = 0.23;

        if (entity instanceof EntityItem) {
            XZOffset = 0.1415;
        }

        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glLineWidth(3);
        float actualAlpha = .3f * alpha;
        glColor4f(color.getRed(), color.getGreen(), color.getBlue(), actualAlpha);
        color(color.getRGB(), actualAlpha);
        GL11.glBegin(7);
        GL11.glVertex3d(axisAlignedBB.minX + XZOffset, axisAlignedBB.minY + NYOffset, axisAlignedBB.minZ + XZOffset);
        GL11.glVertex3d(axisAlignedBB.minX + XZOffset, axisAlignedBB.maxY - MYOffset, axisAlignedBB.minZ + XZOffset);
        GL11.glVertex3d(axisAlignedBB.maxX - XZOffset, axisAlignedBB.minY + NYOffset, axisAlignedBB.minZ + XZOffset);
        GL11.glVertex3d(axisAlignedBB.maxX - XZOffset, axisAlignedBB.maxY - MYOffset, axisAlignedBB.minZ + XZOffset);
        GL11.glVertex3d(axisAlignedBB.maxX - XZOffset, axisAlignedBB.minY + NYOffset, axisAlignedBB.maxZ - XZOffset);
        GL11.glVertex3d(axisAlignedBB.maxX - XZOffset, axisAlignedBB.maxY - MYOffset, axisAlignedBB.maxZ - XZOffset);
        GL11.glVertex3d(axisAlignedBB.minX + XZOffset, axisAlignedBB.minY + NYOffset, axisAlignedBB.maxZ - XZOffset);
        GL11.glVertex3d(axisAlignedBB.minX + XZOffset, axisAlignedBB.maxY - MYOffset, axisAlignedBB.maxZ - XZOffset);
        GL11.glEnd();
        GL11.glBegin(7);
        GL11.glVertex3d(axisAlignedBB.maxX - XZOffset, axisAlignedBB.maxY - MYOffset, axisAlignedBB.minZ + XZOffset);
        GL11.glVertex3d(axisAlignedBB.maxX - XZOffset, axisAlignedBB.minY + NYOffset, axisAlignedBB.minZ + XZOffset);
        GL11.glVertex3d(axisAlignedBB.minX + XZOffset, axisAlignedBB.maxY - MYOffset, axisAlignedBB.minZ + XZOffset);
        GL11.glVertex3d(axisAlignedBB.minX + XZOffset, axisAlignedBB.minY + NYOffset, axisAlignedBB.minZ + XZOffset);
        GL11.glVertex3d(axisAlignedBB.minX + XZOffset, axisAlignedBB.maxY - MYOffset, axisAlignedBB.maxZ - XZOffset);
        GL11.glVertex3d(axisAlignedBB.minX + XZOffset, axisAlignedBB.minY + NYOffset, axisAlignedBB.maxZ - XZOffset);
        GL11.glVertex3d(axisAlignedBB.maxX - XZOffset, axisAlignedBB.maxY - MYOffset, axisAlignedBB.maxZ - XZOffset);
        GL11.glVertex3d(axisAlignedBB.maxX - XZOffset, axisAlignedBB.minY + NYOffset, axisAlignedBB.maxZ - XZOffset);
        GL11.glEnd();
        GL11.glBegin(7);
        GL11.glVertex3d(axisAlignedBB.minX + XZOffset, axisAlignedBB.maxY - MYOffset, axisAlignedBB.minZ + XZOffset);
        GL11.glVertex3d(axisAlignedBB.maxX - XZOffset, axisAlignedBB.maxY - MYOffset, axisAlignedBB.minZ + XZOffset);
        GL11.glVertex3d(axisAlignedBB.maxX - XZOffset, axisAlignedBB.maxY - MYOffset, axisAlignedBB.maxZ - XZOffset);
        GL11.glVertex3d(axisAlignedBB.minX + XZOffset, axisAlignedBB.maxY - MYOffset, axisAlignedBB.maxZ - XZOffset);
        GL11.glVertex3d(axisAlignedBB.minX + XZOffset, axisAlignedBB.maxY - MYOffset, axisAlignedBB.minZ + XZOffset);
        GL11.glVertex3d(axisAlignedBB.minX + XZOffset, axisAlignedBB.maxY - MYOffset, axisAlignedBB.maxZ - XZOffset);
        GL11.glVertex3d(axisAlignedBB.maxX - XZOffset, axisAlignedBB.maxY - MYOffset, axisAlignedBB.maxZ - XZOffset);
        GL11.glVertex3d(axisAlignedBB.maxX - XZOffset, axisAlignedBB.maxY - MYOffset, axisAlignedBB.minZ + XZOffset);
        GL11.glEnd();
        GL11.glBegin(7);
        GL11.glVertex3d(axisAlignedBB.minX + XZOffset, axisAlignedBB.minY + NYOffset, axisAlignedBB.minZ + XZOffset);
        GL11.glVertex3d(axisAlignedBB.maxX - XZOffset, axisAlignedBB.minY + NYOffset, axisAlignedBB.minZ + XZOffset);
        GL11.glVertex3d(axisAlignedBB.maxX - XZOffset, axisAlignedBB.minY + NYOffset, axisAlignedBB.maxZ - XZOffset);
        GL11.glVertex3d(axisAlignedBB.minX + XZOffset, axisAlignedBB.minY + NYOffset, axisAlignedBB.maxZ - XZOffset);
        GL11.glVertex3d(axisAlignedBB.minX + XZOffset, axisAlignedBB.minY + NYOffset, axisAlignedBB.minZ + XZOffset);
        GL11.glVertex3d(axisAlignedBB.minX + XZOffset, axisAlignedBB.minY + NYOffset, axisAlignedBB.maxZ - XZOffset);
        GL11.glVertex3d(axisAlignedBB.maxX - XZOffset, axisAlignedBB.minY + NYOffset, axisAlignedBB.maxZ - XZOffset);
        GL11.glVertex3d(axisAlignedBB.maxX - XZOffset, axisAlignedBB.minY + NYOffset, axisAlignedBB.minZ + XZOffset);
        GL11.glEnd();
        GL11.glBegin(7);
        GL11.glVertex3d(axisAlignedBB.minX + XZOffset, axisAlignedBB.minY + NYOffset, axisAlignedBB.minZ + XZOffset);
        GL11.glVertex3d(axisAlignedBB.minX + XZOffset, axisAlignedBB.maxY - MYOffset, axisAlignedBB.minZ + XZOffset);
        GL11.glVertex3d(axisAlignedBB.minX + XZOffset, axisAlignedBB.minY + NYOffset, axisAlignedBB.maxZ - XZOffset);
        GL11.glVertex3d(axisAlignedBB.minX + XZOffset, axisAlignedBB.maxY - MYOffset, axisAlignedBB.maxZ - XZOffset);
        GL11.glVertex3d(axisAlignedBB.maxX - XZOffset, axisAlignedBB.minY + NYOffset, axisAlignedBB.maxZ - XZOffset);
        GL11.glVertex3d(axisAlignedBB.maxX - XZOffset, axisAlignedBB.maxY - MYOffset, axisAlignedBB.maxZ - XZOffset);
        GL11.glVertex3d(axisAlignedBB.maxX - XZOffset, axisAlignedBB.minY + NYOffset, axisAlignedBB.minZ + XZOffset);
        GL11.glVertex3d(axisAlignedBB.maxX - XZOffset, axisAlignedBB.maxY - MYOffset, axisAlignedBB.minZ + XZOffset);
        GL11.glEnd();
        GL11.glBegin(7);
        GL11.glVertex3d(axisAlignedBB.minX + XZOffset, axisAlignedBB.maxY - MYOffset, axisAlignedBB.maxZ - XZOffset);
        GL11.glVertex3d(axisAlignedBB.minX + XZOffset, axisAlignedBB.minY + NYOffset, axisAlignedBB.maxZ - XZOffset);
        GL11.glVertex3d(axisAlignedBB.minX + XZOffset, axisAlignedBB.maxY - MYOffset, axisAlignedBB.minZ + XZOffset);
        GL11.glVertex3d(axisAlignedBB.minX + XZOffset, axisAlignedBB.minY + NYOffset, axisAlignedBB.minZ + XZOffset);
        GL11.glVertex3d(axisAlignedBB.maxX - XZOffset, axisAlignedBB.maxY - MYOffset, axisAlignedBB.minZ + XZOffset);
        GL11.glVertex3d(axisAlignedBB.maxX - XZOffset, axisAlignedBB.minY + NYOffset, axisAlignedBB.minZ + XZOffset);
        GL11.glVertex3d(axisAlignedBB.maxX - XZOffset, axisAlignedBB.maxY - MYOffset, axisAlignedBB.maxZ - XZOffset);
        GL11.glVertex3d(axisAlignedBB.maxX - XZOffset, axisAlignedBB.minY + NYOffset, axisAlignedBB.maxZ - XZOffset);
        GL11.glEnd();
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);

        GLUtils.disableCaps();
        GLUtils.end2DRendering();

        GlStateManager.popMatrix();
    }

    public static void drawTargetCapsule(Entity entity, double radius, Color color) {
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glEnable(2832);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glHint(3153, 4354);
        GL11.glDepthMask(false);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableCull();
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);

        final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().getRenderPosX();
        final double y = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks - mc.getRenderManager().getRenderPosY()) + Math.sin(System.currentTimeMillis() / 2E+2) + 1;
        final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().getRenderPosZ();

        Color c;

        for (float i = 0; i < Math.PI * 2; i += (float) (Math.PI * 2 / 64.F)) {
            final double vecX = x + radius * Math.cos(i);
            final double vecZ = z + radius * Math.sin(i);

            c = color;

            GL11.glColor4f(c.getRed() / 255.F,
                    c.getGreen() / 255.F,
                    c.getBlue() / 255.F,
                    0
            );
            GL11.glVertex3d(vecX, y - Math.cos(System.currentTimeMillis() / 2E+2) / 2.0F, vecZ);
            GL11.glColor4f(c.getRed() / 255.F,
                    c.getGreen() / 255.F,
                    c.getBlue() / 255.F,
                    0.55F
            );
            GL11.glVertex3d(vecX, y, vecZ);
        }

        GL11.glEnd();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.enableCull();
        GL11.glDisable(2848);
        GL11.glDisable(2848);
        GL11.glEnable(2832);
        GL11.glEnable(3553);
        GL11.glPopMatrix();
        GL11.glColor3f(255, 255, 255);
    }
}
