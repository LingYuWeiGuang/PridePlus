package op.wawa.prideplus.module.impl.visual;

import op.wawa.prideplus.module.Module;
import op.wawa.prideplus.utils.render.RenderUtils;
import op.wawa.prideplus.utils.render.StencilUtils;
import op.wawa.prideplus.utils.render.shader.blur.GaussianBlur;
import op.wawa.prideplus.utils.render.shader.blur.KawaseBloom;
import op.wawa.prideplus.utils.render.shader.blur.KawaseBlur;
import op.wawa.prideplus.value.values.BooleanValue;
import op.wawa.prideplus.value.values.NumberValue;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.Display;

import java.util.ArrayList;

public class PostProcessing extends Module {

    public static PostProcessing INSTANCE;

    public static final BooleanValue blur = new BooleanValue("Blur", true);
    public static final NumberValue blurRadius = new NumberValue("Blur radius", 3f, 1f, 10f, 1f);
    public static final NumberValue blurOffset = new NumberValue("Blur offset", 2f, 1f, 10f, 1f);
    public static final BooleanValue bloom = new BooleanValue("Bloom", true);
    public static final NumberValue bloomRadius = new NumberValue("Bloom radius", 3f, 1f, 10f, 1f);
    public static final NumberValue bloomOffset = new NumberValue("Bloom offset", 1f, 1f, 10f, 1f);

    private static Framebuffer stencilFramebuffer = new Framebuffer(1, 1, false);

    private static final ArrayList<Runnable> blurTasks = new ArrayList<>();
    private static final ArrayList<Runnable> bloomTasks = new ArrayList<>();

    public PostProcessing() {
        super("PostProcessing", Category.VISUAL);
        INSTANCE = this;
    }

    public static void addBlurTask(Runnable context) {
        if (blur.getValue())
            blurTasks.add(context);
    }

    public static void addBloomTask(Runnable context) {
        if (bloom.getValue())
            bloomTasks.add(context);
    }

    public static void addBothTask(Runnable context) {
        if (bloom.getValue())
            bloomTasks.add(context);

        if (blur.getValue())
            blurTasks.add(context);
    }

    public static void draw() {
        if (!INSTANCE.isEnable() || GLFW.glfwGetWindowAttrib(Display.getWindow(), GLFW.GLFW_ICONIFIED) == GLFW.GLFW_TRUE) {
            blurTasks.clear();
            bloomTasks.clear();
            return;
        }

        if (blur.getValue()) {
            StencilUtils.initStencilToWrite();
            for (Runnable runnable : blurTasks) {
                runnable.run();
            }
            StencilUtils.readStencilBuffer(1);
            GaussianBlur.endBlur(blurRadius.getIntValue(), blurOffset.getIntValue());
            StencilUtils.uninitStencilBuffer();
            blurTasks.clear();
        }

        if (bloom.getValue()) {
            stencilFramebuffer = RenderUtils.createFrameBuffer(stencilFramebuffer);
            stencilFramebuffer.framebufferClear();
            stencilFramebuffer.bindFramebuffer(false);
            for (Runnable runnable : bloomTasks) {
                runnable.run();
            }
            stencilFramebuffer.unbindFramebuffer();
            KawaseBloom.renderBlur(stencilFramebuffer.framebufferTexture, bloomRadius.getValue().intValue(), bloomOffset.getValue().intValue(), 255);
            bloomTasks.clear();
        }
    }
}
