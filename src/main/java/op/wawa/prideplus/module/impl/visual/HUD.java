package op.wawa.prideplus.module.impl.visual;

import op.wawa.prideplus.Pride;
import op.wawa.prideplus.event.annotations.EventTarget;
import op.wawa.prideplus.event.events.EventRender2D;
import op.wawa.prideplus.manager.WaterMarkManager;
import op.wawa.prideplus.manager.watermark.Text;
import op.wawa.prideplus.module.Module;
import op.wawa.prideplus.ui.font.FontDrawer;
import op.wawa.prideplus.ui.font.FontManager;
import op.wawa.prideplus.ui.notification.NotificationManager;
import op.wawa.prideplus.utils.animation.LinearAnimation;
import op.wawa.prideplus.utils.player.MovementUtils;
import op.wawa.prideplus.utils.player.PlayerUtils;
import op.wawa.prideplus.utils.render.BasicRendering;
import op.wawa.prideplus.utils.render.RenderUtils;
import op.wawa.prideplus.utils.render.shader.RoundedUtils;
import op.wawa.prideplus.utils.render.shader.ShaderProcessor;
import op.wawa.prideplus.value.values.ColorValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.List;

public class HUD extends Module {

    public final ColorValue color = new ColorValue("Color", new Color(34, 193, 197, 255));

    public HUD() {
        super("HUD", Category.VISUAL);
    }

    public static FontDrawer fontDrawer = FontManager.default16;

    @EventTarget
    public void onRender2D(EventRender2D event) {

        WaterMarkManager.getTexts().clear();
        WaterMarkManager.getTexts().add(new Text("PlayerName", mc.thePlayer.getNameClear()));
        WaterMarkManager.getTexts().add(new Text("BPS", "BPS: " + PlayerUtils.calculateBPS(mc.thePlayer)));
        WaterMarkManager.renderWaterMark();
/*        String substring = Pride.NAME.substring(0, 1);
        String other = Pride.NAME.substring(1);
        FontManager.default22.drawStringWithShadow(substring, 3, 3, color.getValue().getRGB());
        FontManager.default22.drawStringWithShadow(other, 3.5 + FontManager.default22.getStringWidth(substring), 3, Color.WHITE.getRGB());*/

        drawArrayList(event);

        PostProcessing.addBothTask(NotificationManager::renderEffects);
        NotificationManager.render();
    }

    private void drawArrayList(EventRender2D event) {
        int counter = 1;
        final List<Module> modules = Pride.INSTANCE.moduleManager.getModsSorted();
        float y = 2;

        for (Module m : modules) {
            int color = new Color(Color.HSBtoRGB((float) ((double) mc.thePlayer.ticksExisted / 50.0 + Math.sin((double) counter / 50.0 * 1.6)) % 1.0f, 0.6f, 1.0f)).getRGB();
            int moduleWidth = fontDrawer.getStringWidth(m.getModRenderNameWithTag());
            m.setArrayX(LinearAnimation.animate(m.getArrayX(), m.isEnable() ? (event.getScaledResolution().getScaledWidth() - moduleWidth - 6) * 10 : event.getScaledResolution().getScaledWidth() * 10 + 30, 0.99f));
            m.setArrayY(LinearAnimation.animate(m.getArrayY(), m.isEnable() ? y * 10 : 0, 0.99f));

/*            if (background.getValue()) {
                ShaderElement.blurArea(m.getArrayX() / 10 - 2, m.getArrayY() / 10 - 2, moduleWidth + 4, fontDrawer.getHeight() + 6);
                ShaderElement.addBloomTask(() -> BasicRendering.drawRect(m.getArrayX() / 10 - 2, m.getArrayY() / 10 - 2, moduleWidth + 4, fontDrawer.getHeight() + 6, color));
                BasicRendering.drawRect(m.getArrayX() / 10 - 2, m.getArrayY() / 10 - 2, moduleWidth + 4, fontDrawer.getHeight() + 6, backgroundColor.getRGB());

                if (sideBar.getValue())
                    BasicRendering.drawRect(m.getArrayX() / 10 - 2 + moduleWidth + 4, m.getArrayY() / 10 - 2, 1, fontDrawer.getHeight() + 6, color);
            }*/

            fontDrawer.drawStringWithShadow(m.getModRenderNameWithTag(), m.getArrayX() / 10, m.getArrayY() / 10 + 1, color);
            if (m.isEnable()) y += fontDrawer.getHeight() + 3;
            counter++;
        }
    }
}
