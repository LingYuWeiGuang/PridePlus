package op.wawa.prideplus.utils.render.shader;

import op.wawa.prideplus.module.impl.visual.PostProcessing;
import op.wawa.prideplus.utils.render.RenderUtils;
import op.wawa.prideplus.utils.render.shader.blur.GaussianBlur;
import op.wawa.prideplus.utils.render.shader.blur.KawaseBloom;
import net.minecraft.client.shader.Framebuffer;

public class ShaderProcessor {
    private static Framebuffer stencilFramebuffer = new Framebuffer(1, 1, false);
    public static Framebuffer framebuffer = new Framebuffer(1, 1, true);

    public static void blur(Runnable runnable) {
        runnable.run();
    }

    public static void processStart() {
        stencilFramebuffer = RenderUtils.createFrameBuffer(stencilFramebuffer);
        stencilFramebuffer.framebufferClear();
        stencilFramebuffer.bindFramebuffer(false);
    }

    public static void blurEnd() {
        stencilFramebuffer.unbindFramebuffer();
        GaussianBlur.endBlur(PostProcessing.blurRadius.getValue().intValue(), PostProcessing.blurOffset.getValue().intValue());
    }

    public static void blurEnd(int iterations, int offset) {
        stencilFramebuffer.unbindFramebuffer();
        GaussianBlur.endBlur(iterations, offset);
    }

    public static void bloomEnd() {
        stencilFramebuffer.unbindFramebuffer();
        KawaseBloom.renderBlur(stencilFramebuffer.framebufferTexture, PostProcessing.bloomRadius.getValue().intValue(), PostProcessing.bloomOffset.getValue().intValue(), 255);
    }

    public static void bloomEnd(int iterations, int offset) {
        stencilFramebuffer.unbindFramebuffer();
        KawaseBloom.renderBlur(stencilFramebuffer.framebufferTexture, iterations, offset, 255);
    }
}