package op.wawa.prideplus.ui.gui.clickgui.panel.impl;

import op.wawa.prideplus.Pride;
import op.wawa.prideplus.module.Module;
import op.wawa.prideplus.ui.font.FontDrawer;
import op.wawa.prideplus.ui.font.FontManager;
import op.wawa.prideplus.ui.gui.clickgui.ClickGUI;
import op.wawa.prideplus.ui.gui.clickgui.ThemeColor;
import op.wawa.prideplus.ui.gui.clickgui.panel.Panel;
import op.wawa.prideplus.ui.gui.clickgui.component.impl.ButtonComponent;
import op.wawa.prideplus.utils.animation.ColorAnimation;
import op.wawa.prideplus.utils.animation.animations.Direction;
import op.wawa.prideplus.utils.render.RenderUtils;
import op.wawa.prideplus.utils.render.shader.RoundedUtils;
import op.wawa.prideplus.value.Value;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ChengFeng
 * @since 2024/7/28
 **/
public class ModulePanel extends Panel {
    private final List<ValuePanel> valuePanelList;
    private final ButtonComponent enableBtn;
    public Module module;
    public boolean listening;
    private ColorAnimation colorAnim;

    public ModulePanel(Module module) {
        this.module = module;
        valuePanelList = new ArrayList<>();

        for (Value<?> value : module.getValues()) {
            valuePanelList.add(new ValuePanel(value));
        }

        enableBtn = new ButtonComponent(module);
        height = 0f;
        listening = false;
        colorAnim = new ColorAnimation(Color.WHITE, ThemeColor.grayColor, 100);
    }

    @Override
    public void init() {
        width = (ClickGUI.width - ClickGUI.leftWidth - 30f) / 2f;
        valuePanelList.forEach(ValuePanel::init);
        enableBtn.init();
    }

    @Override
    public void draw(float x, float y, int mouseX, int mouseY) {
        this.x = x;
        this.y = y;
        FontDrawer font = FontManager.rubik15;

        final float gap = 5f;
        float panelY = y + font.getHeight() + 3f;
        float contentX = x + 3f;
        float contentY = panelY + gap;

        boolean hovering = RenderUtils.hovering(mouseX, mouseY, x, y - 2f, width, font.getHeight() + 4f);

        if (hovering && colorAnim.getDirection() == Direction.FORWARDS) {
            colorAnim.changeDirection();
        } else if (!hovering && colorAnim.getDirection() == Direction.BACKWARDS) {
            colorAnim.changeDirection();
        }

        // Module name
        font.drawString(module.getModuleName(), contentX, y, colorAnim.getOutput().getRGB());
        String keyText = listening? "Listening..." : hovering ? "Click to bind" : "Key bind: " + (module.getKeyCode() == -1 ? "None" : Keyboard.getKeyName(module.getKeyCode()));
        font.drawString(keyText, contentX + width - 5f - font.getStringWidth(keyText), y, colorAnim.getOutput().getRGB());

        // Panel
        RoundedUtils.drawRoundOutline(x, panelY, width, height, 3f, 0.2f, ThemeColor.panelColor, ThemeColor.outlineColor);

        FontDrawer font2 = FontManager.default16;
        font2.drawString("Enabled", contentX, contentY - 1f, Color.WHITE.getRGB());
        /*if (module.locked) {
            RenderUtil.drawImage(new ResourceLocation("prideplus/icon/lock.png"), contentX + width - 17f, contentY - 3f, 12f, 12f);
        } else */enableBtn.draw(contentX, contentY, mouseX, mouseY);

        float valueY = contentY + enableBtn.height + gap;

        for (ValuePanel panel : valuePanelList) {
            if (!panel.value.isVisible()) continue;
            panel.draw(contentX, valueY, mouseX, mouseY);
            if (panel.height != 0) valueY += panel.height + gap;
            if (valuePanelList.indexOf(panel) != valuePanelList.size() - 1) {
                RoundedUtils.drawRound(contentX, valueY - gap, width - 6f, 0.15f, 0f, ThemeColor.grayColor);
            }
        }

        height = valueY - panelY - gap;
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, int button) {
        if (RenderUtils.hovering(mouseX, mouseY, x, y - 2f, width, FontManager.rubik15.getHeight() + 4f) && button == 0) {
            listening = true;
        }

        valuePanelList.stream().filter(it -> it.value.isVisible()).forEach(it -> it.onMouseClick(mouseX, mouseY, button));
        enableBtn.onMouseClick(mouseX, mouseY, button);
        // Pride.INSTANCE.configManager.saveConfigs();
    }

    @Override
    public void onKeyTyped(char c, int keyCode) {
        if (listening) {
            module.setKeyCode(keyCode == Keyboard.KEY_ESCAPE ? -1 : keyCode);
            // Pride.INSTANCE.configManager.saveConfigs();
            return;
        }

        for (ValuePanel panel : valuePanelList) {
            if (!panel.value.isVisible()) continue;
            panel.onKeyTyped(c, keyCode);
        }
    }

    @Override
    public void onMouseRelease() {
        valuePanelList.forEach(Panel::onMouseRelease);
    }
}
