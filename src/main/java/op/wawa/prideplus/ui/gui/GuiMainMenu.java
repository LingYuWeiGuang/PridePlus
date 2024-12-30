package op.wawa.prideplus.ui.gui;

import op.wawa.prideplus.Pride;
import op.wawa.prideplus.ui.font.FontManager;
import op.wawa.prideplus.ui.gui.alt.GuiAltManager;
import op.wawa.prideplus.ui.gui.button.CustomMenuButton;
import op.wawa.prideplus.utils.render.RenderUtils;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.src.Config;
import org.bytedeco.javacv.FrameGrabber;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

public class GuiMainMenu extends GuiScreen {
    private final CopyOnWriteArrayList<CustomMenuButton> clientButtons = new CopyOnWriteArrayList<>();

    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {
        Pride.INSTANCE.videoPlayer.suspended = false;
        clientButtons.clear();
        clientButtons.add(new CustomMenuButton("单人游戏",width / 2 - 50,height / 2 - 42 + 30,100,18, FontManager.default16,
                () -> mc.displayGuiScreen(new GuiSelectWorld(GuiMainMenu.this))));
        clientButtons.add(new CustomMenuButton("多人游戏",width / 2 - 50,height / 2 - 21 + 30,100,18, FontManager.default16,
                () -> mc.displayGuiScreen(new GuiMultiplayer(GuiMainMenu.this))));
        clientButtons.add(new CustomMenuButton("设置",width / 2 - 50,height / 2 + 30,100,18, FontManager.default16,
                () -> mc.displayGuiScreen(new GuiOptions(GuiMainMenu.this,mc.gameSettings))));
        clientButtons.add(new CustomMenuButton("账户", width / 2 - 50, height / 2 + 21 + 30, 100, 18, FontManager.default16,
                () -> mc.displayGuiScreen(new GuiAltManager(GuiMainMenu.this))));
        clientButtons.add(new CustomMenuButton("退出",width / 2 - 50,height / 2 + 42 + 30,100,18, FontManager.default16,
                () -> mc.shutdown()));

        for (CustomMenuButton clientButton : clientButtons) {
            clientButton.initGui();
        }
        super.initGui();
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        RenderUtils.resetColor();
        try {
            Pride.INSTANCE.videoPlayer.render(0, 0, width, height);
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }
        RenderUtils.resetColor();

        for (CustomMenuButton clientButton : clientButtons) {
            clientButton.drawScreen(mouseX, mouseY, partialTicks);
        }

        // RenderUtils.drawImage(width / 2 - 34, height / 2 - 30 - 67 + 10, 67, 67, new Color(255, 255, 255, 220).getRGB(), new ResourceLocation(Pride.NAME.toLowerCase() + "/logo.png"));
        // FontManager.zzz16.drawString("Copyright Mojang AB. Do not distribute!",8, height - 16, new Color(255, 255, 255, 170).getRGB());

        FontManager.default30.drawString(Pride.NAME, width / 2 - FontManager.default30.getStringWidth(Pride.NAME) / 2, height / 2 - 42, new Color(255, 255, 255, 220).getRGB());

        FontManager.default16.drawString(Pride.NAME + " " + Pride.VERSION + " §a(Latest)", 10, height - 35, new Color(255, 255, 255, 120).getRGB());
        FontManager.default16.drawString("Copyright Mojang AB. Do not distribute!", 10, height - 25, new Color(255, 255, 255, 120).getRGB());
        FontManager.default16.drawString("Minecraft 1.8.9 (" + Config.getVersion() + ")", 10, height - 15, new Color(255, 255, 255, 120).getRGB());
        FontManager.default16.drawString("Welcome!", width - FontManager.default16.getStringWidth("Welcome!") - 8, height - 25, new Color(255, 255, 255, 120).getRGB());
        FontManager.default16.drawString("Powered by " + Pride.AUTHOR, width - FontManager.default16.getStringWidth("Powered by " + Pride.AUTHOR) - 8, height - 14, new Color(255, 255, 255, 120).getRGB());
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (CustomMenuButton clientButton : clientButtons) {
            clientButton.mouseClicked(mouseX, mouseY, mouseButton);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void onGuiClosed() {
        for (CustomMenuButton clientButton : clientButtons) {
            clientButton.onGuiClosed();
        }
        super.onGuiClosed();
        Pride.INSTANCE.videoPlayer.suspended = true;
    }
}
