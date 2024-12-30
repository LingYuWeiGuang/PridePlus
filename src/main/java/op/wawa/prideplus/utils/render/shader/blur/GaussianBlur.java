package op.wawa.prideplus.utils.render.shader.blur;

import op.wawa.prideplus.utils.MinecraftInstance;
import op.wawa.prideplus.utils.misc.MathUtils;
import op.wawa.prideplus.utils.render.RenderUtils;
import op.wawa.prideplus.utils.render.StencilUtils;
import op.wawa.prideplus.utils.render.shader.ShaderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_ZERO;

public class GaussianBlur implements MinecraftInstance {
    private static final ShaderUtils gaussianBlur = new ShaderUtils("gaussian");

    private static Framebuffer framebuffer = new Framebuffer(1, 1, false);

    private static void setupUniforms(float dir1, float dir2, float radius) {
        gaussianBlur.setUniformi("textureIn", 0);
        gaussianBlur.setUniformf("texelSize", 1.0F / (float) mc.displayWidth, 1.0F / (float) mc.displayHeight);
        gaussianBlur.setUniformf("direction", dir1, dir2);
        gaussianBlur.setUniformf("radius", radius);

        final FloatBuffer weightBuffer = BufferUtils.createFloatBuffer(256);
        for (int i = 0; i <= radius; i++) {
            weightBuffer.put(MathUtils.calculateGaussianValue(i, radius / 2));
        }

        weightBuffer.rewind();
        GL20.glUniform1(gaussianBlur.getUniform("weights"), weightBuffer);
    }

    public static void endBlur(float radius, float compression) {
        GlStateManager.enableBlend();
        GlStateManager.color(1, 1, 1, 1);
        OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        framebuffer = RenderUtils.createFrameBuffer(framebuffer);
        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(true);
        gaussianBlur.init();
        setupUniforms(compression, 0, radius);
        bindTexture(Minecraft.getMinecraft().getFramebuffer().framebufferTexture);
        ShaderUtils.drawQuads();
        framebuffer.unbindFramebuffer();
        gaussianBlur.unload();
        Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
        gaussianBlur.init();
        setupUniforms(0, compression, radius);
        bindTexture(framebuffer.framebufferTexture);
        ShaderUtils.drawQuads();
        gaussianBlur.unload();
        GlStateManager.resetColor();
        GlStateManager.bindTexture(0);
    }

    public static void bindTexture(int texture) {
        glBindTexture(GL_TEXTURE_2D, texture);
    }
}
