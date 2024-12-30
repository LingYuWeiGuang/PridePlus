package op.wawa.prideplus.utils.render.shader;

import op.wawa.prideplus.utils.render.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.GL_GREATER;

public class RoundedUtils {
    private final static ShaderUtils roundedShader = new ShaderUtils("roundedRect");
    private final static ShaderUtils roundedOutlineShader = new ShaderUtils("roundRectOutline");
    private final static ShaderUtils roundedTexturedShader = new ShaderUtils("roundRectTexture");
    private final static ShaderUtils roundedGradientShader = new ShaderUtils("roundedRectGradient");

    public static void drawGradientRoundLR(float x, float y, float width, float height, float radius, Color color1, Color color2) {
        drawGradientRound(x, y, width, height, radius, color1, color2, color2, color1);
    }

    public static void drawRoundNoOffset(float x, float y, float width, float height, float radius, Color color) {
        ColorUtils.resetColor();
        GlStateManager.enableBlend();

        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL_GREATER, (float) (0 * .01));

        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        roundedShader.init();

        setupRoundedRectUniforms(x, y, width, height, radius, roundedShader);
        roundedShader.setUniformf("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);

        ShaderUtils.drawQuads(x, y, width, height);
        roundedShader.unload();
        GlStateManager.disableBlend();
    }
    public static void drawRound(float x, float y, float width, float height, float radius, Color color) {
        ColorUtils.resetColor();
        GlStateManager.enableBlend();

        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL_GREATER, (float) (0 * .01));

        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        roundedShader.init();

        setupRoundedRectUniforms(x, y, width, height, radius, roundedShader);
        roundedShader.setUniformf("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);

        ShaderUtils.drawQuads(x - 1, y - 1, width + 2, height + 2);
        roundedShader.unload();
        GlStateManager.disableBlend();
    }
    public static void drawGradientRound(float x, float y, float width, float height, float radius, Color bottomLeft, Color topLeft, Color bottomRight, Color topRight) {
        ColorUtils.resetColor();
        GlStateManager.enableBlend();

        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL_GREATER, (float) (0 * .01));

        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        roundedGradientShader.init();
        setupRoundedRectUniforms(x, y, width, height, radius, roundedGradientShader);
        roundedGradientShader.setUniformf("color1", topLeft.getRed() / 255f, topLeft.getGreen() / 255f, topLeft.getBlue() / 255f, topLeft.getAlpha() / 255f);
        roundedGradientShader.setUniformf("color2", bottomRight.getRed() / 255f, bottomRight.getGreen() / 255f, bottomRight.getBlue() / 255f, bottomRight.getAlpha() / 255f);
        roundedGradientShader.setUniformf("color3", bottomLeft.getRed() / 255f, bottomLeft.getGreen() / 255f, bottomLeft.getBlue() / 255f, bottomLeft.getAlpha() / 255f);
        roundedGradientShader.setUniformf("color4", topRight.getRed() / 255f, topRight.getGreen() / 255f, topRight.getBlue() / 255f, topRight.getAlpha() / 255f);
        ShaderUtils.drawQuads(x - 1, y - 1, width + 2, height + 2);
        roundedGradientShader.unload();
        GlStateManager.disableBlend();
    }

    public static void drawRound(float x, float y, float width, float height, float radius, boolean blur, Color color, boolean topLeftCorner, boolean bottomLeftCorner, boolean topRightCorner, boolean bottomRightCorner) {
        GlStateManager.resetColor();
        GlStateManager.enableBlend();

        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL_GREATER, (float) (0 * .01));

        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        roundedShader.init();

        float alpha = color.getAlpha() / 255f;

        setupRoundedRectUniforms(x, y, width, height, radius, roundedShader);
        roundedShader.setUniformi("blur", blur ? 1 : 0);
        roundedShader.setUniformf("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alpha);
        roundedShader.setUniformf("corner", topLeftCorner ? 1 : 0, bottomLeftCorner ? 1 : 0, topRightCorner ? 1 : 0, bottomRightCorner ? 1 : 0);

        ShaderUtils.drawQuads(x - 1, y - 1, width + 2, height + 2);
        roundedShader.unload();
        GlStateManager.disableBlend();
    }

    public static void drawRoundOutlineInGuiChat(float x, float y, float width, float height, float radius, float outlineThickness, Color color, Color outlineColor) {
        ColorUtils.resetColor();
        GlStateManager.enableBlend();

        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL_GREATER, (float) (0 * .01));

        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        roundedOutlineShader.init();

        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        setupRoundedRectUniformsInGuiChat(x, y, width, height, radius, roundedOutlineShader);
        roundedOutlineShader.setUniformf("outlineThickness", outlineThickness * sr.getScaleFactor());
        roundedOutlineShader.setUniformf("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        roundedOutlineShader.setUniformf("outlineColor", outlineColor.getRed() / 255f, outlineColor.getGreen() / 255f, outlineColor.getBlue() / 255f, outlineColor.getAlpha() / 255f);

        ShaderUtils.drawQuads(x - (2 + outlineThickness), y - (2 + outlineThickness), width + (4 + outlineThickness * 2), height + (4 + outlineThickness * 2));
        roundedOutlineShader.unload();
        GlStateManager.disableBlend();
    }

    public static void drawRoundOutline(float x, float y, float width, float height, float radius, float outlineThickness, Color color, Color outlineColor) {
        ColorUtils.resetColor();
        GlStateManager.enableBlend();

        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL_GREATER, (float) (0 * .01));

        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        roundedOutlineShader.init();

        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        setupRoundedRectUniforms(x, y, width, height, radius, roundedOutlineShader);
        roundedOutlineShader.setUniformf("outlineThickness", outlineThickness * sr.getScaleFactor());
        roundedOutlineShader.setUniformf("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        roundedOutlineShader.setUniformf("outlineColor", outlineColor.getRed() / 255f, outlineColor.getGreen() / 255f, outlineColor.getBlue() / 255f, outlineColor.getAlpha() / 255f);

        ShaderUtils.drawQuads(x - (2 + outlineThickness), y - (2 + outlineThickness), width + (4 + outlineThickness * 2), height + (4 + outlineThickness * 2));
        roundedOutlineShader.unload();
        GlStateManager.disableBlend();
    }

    public static void drawGradientCornerLR(float x, float y, float width, float height, float radius, Color topLeft, Color bottomRight) {
        Color mixedColor = ColorUtils.interpolateColorC(topLeft, bottomRight, .5f);
        drawGradientRound(x, y, width, height, radius, mixedColor, topLeft, bottomRight, mixedColor);
    }

    public static void drawGradientCornerRL(float x, float y, float width, float height, float radius, Color bottomLeft, Color topRight) {
        Color mixedColor = ColorUtils.interpolateColorC(topRight, bottomLeft, .5f);
        drawGradientRound(x, y, width, height, radius, bottomLeft, mixedColor, mixedColor, topRight);
    }

    public static void drawRoundTextured(float x, float y, float width, float height, float radius, float alpha) {
        ColorUtils.resetColor();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL_GREATER, (float) (0 * .01));
        roundedTexturedShader.init();
        roundedTexturedShader.setUniformi("textureIn", 0);
        setupRoundedRectUniforms(x, y, width, height, radius, roundedTexturedShader);
        roundedTexturedShader.setUniformf("alpha", alpha);
        ShaderUtils.drawQuads(x - 1, y - 1, width + 2, height + 2);
        roundedTexturedShader.unload();
        GlStateManager.disableBlend();
    }

    private static void setupRoundedRectUniformsInGuiChat(float x, float y, float width, float height, float radius, ShaderUtils roundedTexturedShader) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        roundedTexturedShader.setUniformf("location", x * sr.getScaleFactor(), (Minecraft.getMinecraft().displayHeight - (height * sr.getScaleFactor())) - (y * sr.getScaleFactor()));
        roundedTexturedShader.setUniformf("rectSize", width * sr.getScaleFactor(), height * sr.getScaleFactor());
        roundedTexturedShader.setUniformf("radius", radius * sr.getScaleFactor());
    }

    private static void setupRoundedRectUniforms(float x, float y, float width, float height, float radius, ShaderUtils roundedTexturedShader) {
/*        if (Minecraft.getMinecraft().currentScreen instanceof GuiLogin){
            ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
            roundedTexturedShader.setUniformf("location", x * sr.getScaleFactor(), (Minecraft.getMinecraft().displayHeight - (height * sr.getScaleFactor())) - (y * sr.getScaleFactor()));
            roundedTexturedShader.setUniformf("rectSize", width * sr.getScaleFactor(), height * sr.getScaleFactor());
            roundedTexturedShader.setUniformf("radius", radius * sr.getScaleFactor());
        }*/
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        roundedTexturedShader.setUniformf("location", x * sr.getScaleFactor(), (Minecraft.getMinecraft().displayHeight - (height * sr.getScaleFactor())) - (y * sr.getScaleFactor()));
        roundedTexturedShader.setUniformf("rectSize", width * sr.getScaleFactor(), height * sr.getScaleFactor());
        roundedTexturedShader.setUniformf("radius", radius * sr.getScaleFactor());
    }
}