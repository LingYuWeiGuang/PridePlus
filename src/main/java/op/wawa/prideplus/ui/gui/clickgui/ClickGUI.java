package op.wawa.prideplus.ui.gui.clickgui;

import op.wawa.prideplus.Pride;
import op.wawa.prideplus.module.Module;
import op.wawa.prideplus.module.impl.visual.PostProcessing;
import op.wawa.prideplus.ui.font.FontDrawer;
import op.wawa.prideplus.ui.font.FontManager;
import op.wawa.prideplus.ui.gui.clickgui.panel.Panel;
import op.wawa.prideplus.ui.gui.clickgui.panel.impl.CategoryPanel;
import op.wawa.prideplus.ui.gui.clickgui.panel.impl.ModulePanel;
import op.wawa.prideplus.utils.animation.ColorAnimation;
import op.wawa.prideplus.utils.animation.animations.Animation;
import op.wawa.prideplus.utils.animation.animations.Direction;
import op.wawa.prideplus.utils.animation.animations.impl.CustomAnimation;
import op.wawa.prideplus.utils.animation.animations.impl.DecelerateAnimation;
import op.wawa.prideplus.utils.animation.animations.impl.SmoothStepAnimation;
import op.wawa.prideplus.ui.gui.clickgui.gui.TextField;
import op.wawa.prideplus.utils.render.ColorUtils;
import op.wawa.prideplus.utils.render.RenderUtils;
import op.wawa.prideplus.utils.render.StencilUtils;
import op.wawa.prideplus.utils.render.shader.RoundedUtils;
import op.wawa.prideplus.utils.render.shader.ShaderProcessor;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author ChengFeng
 * @since 2024/7/28
 **/
public class ClickGUI extends GuiScreen {
    public static float width, height, leftWidth, topWidth, radius;
    private float x, y;
    private boolean dragging;
    private float dragX, dragY;

    private Animation windowAnim;
    private final CustomAnimation panelAnim;
    private final Animation topOpacityAnim;
    private final ColorAnimation topColorAnim;
    private ColorAnimation iconColorAnim;

    private final List<CategoryPanel> categoryPanelList;
    private CategoryPanel currentPanel;
    private final TextField searchField;

    public ClickGUI() {
        width = 420f;
        height = 310f;
        leftWidth = 90f;
        topWidth = 35f;
        radius = 4f;
        x = 10f;
        y = 10f;

        categoryPanelList = new ArrayList<>();
        for (Module.Category value : Module.Category.values()) {
            categoryPanelList.add(new CategoryPanel(value));
        }
        currentPanel = categoryPanelList.getFirst();

        float iconSize = 14f;
        searchField = new TextField(90f, 20f, FontManager.default22, ThemeColor.titleColor, ThemeColor.grayColor);
        searchField.radius = 5f;
        searchField.offsetX = iconSize + 2f;

        panelAnim = new CustomAnimation(SmoothStepAnimation.class, 200, 0, 0);
        topOpacityAnim = new SmoothStepAnimation(100, 0.8d);
        topOpacityAnim.changeDirection();
        topColorAnim = new ColorAnimation(ThemeColor.barColor, ThemeColor.barBgColor, 100);
    }

    @Override
    public void initGui() {
        dragging = false;
        windowAnim = new DecelerateAnimation(150, 1d);
        iconColorAnim = new ColorAnimation(Color.WHITE, ThemeColor.grayColor, 100);

        currentPanel.modulePanelList.forEach(ModulePanel::init);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (windowAnim.finished(Direction.BACKWARDS)) mc.displayGuiScreen(null);

        // Drag
        if (dragging) {
            x += mouseX - dragX;
            y += mouseY - dragY;
            dragX = mouseX;
            dragY = mouseY;
        }

        ScaledResolution sr = new ScaledResolution(mc);
        if (x < 10) x = 10;
        if (y < 10) y = 10;
        if (x + width > sr.getScaledWidth() - 10) x = sr.getScaledWidth() - 10 - width;
        if (y + height > sr.getScaledHeight() - 10) y = sr.getScaledHeight() - 10 - height;

        if (PostProcessing.INSTANCE.isEnable() && GLFW.glfwGetWindowAttrib(Display.getWindow(), GLFW.GLFW_ICONIFIED) == GLFW.GLFW_FALSE) {
            ShaderProcessor.processStart();
            Gui.drawNewRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), Color.BLACK.getRGB());
            ShaderProcessor.blurEnd();
        }

        RenderUtils.scaleStart(x + width / 2, y + height / 2, windowAnim.getOutput().floatValue());

        // Window BG
        RoundedUtils.drawRound(x, y, width, height, radius, ThemeColor.windowColor);

        // Category BG
        StencilUtils.initStencilToWrite();
        RoundedUtils.drawRound(x - 1f, y + 1f, leftWidth + radius, height + 1f, radius, ThemeColor.categoryColor);
        StencilUtils.readStencilBuffer(1);
        RenderUtils.scissorStart(x, y, leftWidth, height);
        RoundedUtils.drawRoundOutline(x - radius, y - radius, leftWidth + radius + 0.4f, height + radius * 2, radius, 0.2f, ThemeColor.categoryColor, ThemeColor.outlineColor);
        RenderUtils.scissorEnd();
        StencilUtils.uninitStencilBuffer();

        // Top BG
        StencilUtils.initStencilToWrite();
        RoundedUtils.drawRound(x + leftWidth, y, width - leftWidth, topWidth, radius, Color.BLACK);
        StencilUtils.readStencilBuffer(1);
        RenderUtils.scissorStart(x + leftWidth, y, width - leftWidth, topWidth);
        RoundedUtils.drawRoundOutline(x + leftWidth - radius, y - radius, width - leftWidth + radius * 2, topWidth + radius, radius, 0.2f, ThemeColor.titleColor, ThemeColor.outlineColor);
        RenderUtils.scissorEnd();
        StencilUtils.uninitStencilBuffer();

        // Title
        FontDrawer font = FontManager.rubikBold28;
        float centerX = x + leftWidth / 2f;

        if (PostProcessing.INSTANCE.isEnable() && GLFW.glfwGetWindowAttrib(Display.getWindow(), GLFW.GLFW_ICONIFIED) == GLFW.GLFW_FALSE) {
            ShaderProcessor.processStart();
            font.drawCenteredString(Pride.NAME.toUpperCase(), centerX + 0.5f, y + 13.5f, ThemeColor.focusedColor.getRGB());
            font.drawCenteredString(Pride.NAME.toUpperCase(), centerX, y + 13f, Color.WHITE.getRGB());
            ShaderProcessor.bloomEnd();
        }

        font.drawCenteredString(Pride.NAME.toUpperCase(), centerX + 0.5f, y + 13.5f, ThemeColor.focusedColor.getRGB());
        font.drawCenteredString(Pride.NAME.toUpperCase(), centerX, y + 13f, Color.WHITE.getRGB());

        // Category
        float categoryX = centerX - (leftWidth - 14f) / 2f;
        float categoryY = y + font.getHeight() + 50f;

        if (PostProcessing.INSTANCE.isEnable() && GLFW.glfwGetWindowAttrib(Display.getWindow(), GLFW.GLFW_ICONIFIED) == GLFW.GLFW_FALSE) {
            ShaderProcessor.processStart();
            RoundedUtils.drawRound(categoryX - 2f, categoryY + panelAnim.getOutput().floatValue() - 4f, leftWidth - 14f, currentPanel.height + 9f, 4f, ThemeColor.barColor);
            ShaderProcessor.bloomEnd();
        }

        RoundedUtils.drawRound(categoryX - 2f, categoryY + panelAnim.getOutput().floatValue() - 4f, leftWidth - 14f, currentPanel.height + 9f, 4f, ThemeColor.barColor);

        for (CategoryType type : CategoryType.values()) {
            FontManager.default14.drawString(type.toString(), categoryX, categoryY - 15f, ThemeColor.grayColor.getRGB());
            for (CategoryPanel panel : categoryPanelList) {
                if (!CategoryType.getType(panel.category).equals(type)) continue;

                RenderUtils.drawImage(new ResourceLocation(Pride.NAME.toLowerCase() + "/icon/" + panel.category.name().toLowerCase(Locale.ROOT) + ".png"), categoryX, categoryY - 2f, 12, 12, ThemeColor.focusedColor);
                panel.draw(categoryX + 20f, categoryY - 1.5f, mouseX, mouseY);
                categoryY += panel.height + 15f;
            }
            categoryY += 20f;
        }

        // Module
        float scroll = (float) currentPanel.scrollAnim.animate();

        float moduleX;
        float originalY = y + topWidth + 8f;
        float leftY = originalY + scroll, rightY = originalY + scroll;

        int panelIndex = 0;

        StencilUtils.initStencilToWrite();
        RoundedUtils.drawRound(x + leftWidth + 5f, originalY - 3f, width - leftWidth - 10f, height - topWidth - 10f, 1f, Color.BLACK);
        StencilUtils.readStencilBuffer(1);
        RenderUtils.scissorStart(x + leftWidth + 5f, originalY - 3f, width - leftWidth - 10f, height - topWidth - 10f);
        for (ModulePanel panel : currentPanel.modulePanelList) {
            if (!searchField.text.isEmpty()) {
                if (!panel.module.getModuleName().toLowerCase().contains(searchField.text.toLowerCase()))
                    continue;
            }
            boolean isLeft = panelIndex % 2 == 0;
            moduleX = x + leftWidth + 10f + (isLeft ? 0 : panel.width + 10);
            panel.draw(moduleX, isLeft ? leftY : rightY, mouseX, mouseY);
            if (isLeft) leftY += panel.height + 20;
            else rightY += panel.height + 20;
            panelIndex++;
        }
        RenderUtils.scissorEnd();
        StencilUtils.uninitStencilBuffer();

        // Scroll
        currentPanel.handleScroll();

        if (currentPanel.scrollAnim.target < 0) {
            if (topOpacityAnim.getDirection() == Direction.BACKWARDS) topOpacityAnim.changeDirection();
        } else if (topOpacityAnim.getDirection() == Direction.FORWARDS) topOpacityAnim.changeDirection();

        float opacity = topOpacityAnim.getOutput().floatValue();
        if (opacity != 0) {
            float size = 20f;
            float gap = 5f;
            float roundX = x + width - gap - size;
            float roundY = y + height - gap - size;
            boolean hovering = RenderUtils.hovering(mouseX, mouseY, roundX, roundY, size, size);

            if (hovering && topColorAnim.getDirection() == Direction.FORWARDS) {
                topColorAnim.changeDirection();
            } else if (!hovering && topColorAnim.getDirection() == Direction.BACKWARDS) topColorAnim.changeDirection();

            if (hovering && Mouse.isButtonDown(0)) currentPanel.scrollAnim.target = 0f;

            if (PostProcessing.INSTANCE.isEnable() && GLFW.glfwGetWindowAttrib(Display.getWindow(), GLFW.GLFW_ICONIFIED) == GLFW.GLFW_FALSE) {
                ShaderProcessor.processStart();
                RoundedUtils.drawRound(roundX, roundY, size, size, 10f, ColorUtils.applyOpacity(topColorAnim.getOutput(), opacity));
                RenderUtils.drawImage(new ResourceLocation(Pride.NAME.toLowerCase() + "/icon/top.png"), roundX + 2.6f, roundY + 3.1f, 14f, 14f, ColorUtils.applyOpacity(Color.WHITE, opacity));
                ShaderProcessor.bloomEnd();
            }

            RoundedUtils.drawRound(roundX, roundY, size, size, 10f, ColorUtils.applyOpacity(topColorAnim.getOutput(), opacity));
            RenderUtils.drawImage(new ResourceLocation(Pride.NAME.toLowerCase() + "/icon/top.png"), roundX + 2.6f, roundY + 3.1f, 14f, 14f, ColorUtils.applyOpacity(Color.WHITE, opacity));
        }

        // Search
        float searchX = x + leftWidth + 10f;
        float searchY = y + topWidth / 2 - 11f;
        float iconSize = 14f;

        searchField.outlineColor = iconColorAnim.getOutput();
        searchField.draw(searchX, searchY, mouseX, mouseY);
        RenderUtils.drawImage(new ResourceLocation(Pride.NAME.toLowerCase() + "/icon/search.png"), searchX + 3f, searchY + 3f, iconSize, iconSize, iconColorAnim.getOutput());

        boolean selected = searchField.focused || RenderUtils.hovering(mouseX, mouseY, searchX, searchY, iconSize + searchField.width + 5f, iconSize);
        if (selected && iconColorAnim.getDirection() == Direction.FORWARDS) {
            iconColorAnim.changeDirection();
        } else if (!selected && iconColorAnim.getDirection() == Direction.BACKWARDS) {
            iconColorAnim.changeDirection();
        }

        RenderUtils.scaleEnd();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        boolean close = true;

        for (ModulePanel panel : currentPanel.modulePanelList) {
            if (!searchField.text.isEmpty()) {
                if (!panel.module.getModuleName().toLowerCase().contains(searchField.text.toLowerCase()))
                    continue;
            }
            panel.onKeyTyped(typedChar, keyCode);
            if (panel.listening) {
                panel.listening = false;
                close = false;
            }
        }

        if (keyCode == Keyboard.KEY_ESCAPE && windowAnim.getDirection() == Direction.FORWARDS && close) {
            windowAnim.changeDirection();
            Keyboard.enableRepeatEvents(false);
        }

        searchField.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (!RenderUtils.hovering(mouseX, mouseY, x, y, width, height)) return;

        if (RenderUtils.hovering(mouseX, mouseY, x, y, width, topWidth)) {
            dragging = true;
            dragX = mouseX;
            dragY = mouseY;
        }

        for (CategoryPanel panel : categoryPanelList) {
            if (RenderUtils.hovering(mouseX, mouseY, panel.x, panel.y - 2, panel.width, panel.height + 4) && mouseButton == 0 && panel != currentPanel) {
                float categoryY = y + FontManager.rubikBold28.getHeight() + 50f;
                panelAnim.setStartPoint(currentPanel.y - categoryY);
                currentPanel = panel;
                panelAnim.setEndPoint(panel.y - categoryY);
                panelAnim.getAnimation().reset();

                currentPanel.modulePanelList.forEach(ModulePanel::init);
                break;
            }
        }

        for (ModulePanel panel : currentPanel.modulePanelList) {
            if (!searchField.text.isEmpty()) {
                if (!panel.module.getModuleName().toLowerCase().contains(searchField.text.toLowerCase()))
                    continue;
            }
            panel.onMouseClick(mouseX, mouseY, mouseButton);
        }

        searchField.mouseClicked(mouseX, mouseY, mouseButton);

        Pride.INSTANCE.configManager.save("modules");
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        dragging = false;
        currentPanel.modulePanelList.stream().filter(panel -> searchField.text.isEmpty() || panel.module.getModuleName().toLowerCase().contains(searchField.text.toLowerCase())).forEach(Panel::onMouseRelease);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onGuiClosed() {
        Pride.INSTANCE.configManager.save("modules");
        super.onGuiClosed();
    }
}
