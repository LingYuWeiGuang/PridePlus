package op.wawa.prideplus.manager;

import op.wawa.prideplus.Pride;
import op.wawa.prideplus.manager.watermark.Text;
import op.wawa.prideplus.module.impl.visual.PostProcessing;
import op.wawa.prideplus.ui.font.FontDrawer;
import op.wawa.prideplus.ui.font.FontManager;
import op.wawa.prideplus.utils.MinecraftInstance;
import op.wawa.prideplus.utils.animation.AnimationUtils;
import op.wawa.prideplus.utils.render.RenderUtils;
import op.wawa.prideplus.utils.render.shader.RoundedUtils;
import op.wawa.prideplus.utils.render.shader.ShaderProcessor;
import com.mojang.realmsclient.gui.ChatFormatting;
import lombok.Getter;
import lombok.val;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class WaterMarkManager implements MinecraftInstance {
    @Getter
    private static final List<Text> texts = new ArrayList<>();
    private static final StringBuilder builder = new StringBuilder();

    private static double aimWidth = 0;

    public static void renderWaterMark() {
        builder.setLength(0);
        builder.append(Pride.NAME);
        texts.forEach(text -> builder.append(ChatFormatting.GRAY).append(" | ").append(ChatFormatting.RESET).append(text.getMsg()));

        var string = builder.toString();
        var default16 = FontManager.default16;
        int stringWidth = default16.getStringWidth("  " + string + "  ");
        aimWidth = AnimationUtils.animate(aimWidth, stringWidth, 0.1);

        int scaledWidth = new ScaledResolution(mc).getScaledWidth();
        PostProcessing.addBlurTask(() -> RoundedUtils.drawRound((float) ((scaledWidth - aimWidth) / 2), 5F, (float) aimWidth, default16.getHeight() + 6, 6, new Color(0, 0, 0, 100)));
        PostProcessing.addBloomTask(() -> RoundedUtils.drawRound((float) ((scaledWidth - aimWidth) / 2), 5F, (float) aimWidth, default16.getHeight() + 6, 6, new Color(0, 0, 0, 200)));
        RoundedUtils.drawRound((float) ((scaledWidth - aimWidth) / 2), 5F, (float) aimWidth, default16.getHeight() + 6, 6, new Color(0, 0, 0, 100));

        default16.drawString(string, (scaledWidth - default16.getStringWidth(string)) / 2F, 5 + 2, Color.WHITE.getRGB());
    }
}
