package op.wawa.prideplus.ui.gui;

import op.wawa.prideplus.Pride;
import op.wawa.prideplus.module.impl.visual.PostProcessing;
import op.wawa.prideplus.ui.font.FontManager;
import op.wawa.prideplus.ui.gui.button.CustomMenuButton;
import op.wawa.prideplus.utils.render.ColorUtils;
import op.wawa.prideplus.utils.render.RenderUtils;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

public class GuiPostProcessing extends GuiScreen {
    private final CopyOnWriteArrayList<CustomMenuButton> buttons = new CopyOnWriteArrayList<>();
    @Override
    public void initGui() {
        Pride.INSTANCE.videoPlayer.suspended = false;
        buttons.clear();
        CustomMenuButton button = new CustomMenuButton("关闭", () -> {
            PostProcessing.INSTANCE.setEnable(false);
            mc.displayGuiScreen(new GuiMainMenu());
        }) {
            @Override
            public void drawScreen(int mouseX, int mouseY, float ticks) {
                font.drawCenteredString(text, x + width / 2f, y + height / 2f, new Color(255, 255, 255, 80).getRGB());
            }
        };
        button.x = width / 2F - 110F / 2F;
        button.y = height / 2F + 20;
        button.width = 50;
        button.height = 25;
        buttons.add(button);

        CustomMenuButton button2 = new CustomMenuButton("启用", () -> {
            PostProcessing.INSTANCE.setEnable(true);
            mc.displayGuiScreen(new GuiMainMenu());
        }) {
            @Override
            public void drawScreen(int mouseX, int mouseY, float ticks) {
                font.drawCenteredString(text, x + width / 2f, y + height / 2f, new Color(255, 255, 255, 80).getRGB());
            }
        };
        button2.x = width / 2F + 10 / 2F;
        button2.y = height / 2F + 20;
        button2.width = 50;
        button2.height = 25;
        buttons.add(button2);

        for (CustomMenuButton clientButton : buttons) {
            clientButton.initGui();
        }
        super.initGui();
    }
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtils.drawRect(0, 0, width, height, ColorUtils.getRGB(0,0,0,255));

        FontManager.default42.drawCenteredString("是否启用后处理？", width / 2F, height / 2F - 50, new Color(255, 255, 255, 100).getRGB());

        FontManager.default18.drawCenteredString("部分显卡无法支持后处理模块提供的高级效果。", width / 2F, height / 2F - 38 + FontManager.default42.getHeight(), new Color(255, 255, 255, 80).getRGB());
        FontManager.default18.drawCenteredString("如果出现渲染错误，请考虑禁用后处理模块以获得正常游戏体验。", width / 2F, height / 2F - 36 + FontManager.default42.getHeight() + FontManager.default18.getHeight(), new Color(255, 255, 255, 80).getRGB());

        for (CustomMenuButton clientButton : buttons) {
            clientButton.drawScreen(mouseX, mouseY, partialTicks);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (CustomMenuButton clientButton : buttons) {
            clientButton.mouseClicked(mouseX, mouseY, mouseButton);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void onGuiClosed() {
        for (CustomMenuButton clientButton : buttons) {
            clientButton.onGuiClosed();
        }
        super.onGuiClosed();
    }
}
