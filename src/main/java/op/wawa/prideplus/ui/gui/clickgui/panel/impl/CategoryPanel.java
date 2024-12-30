package op.wawa.prideplus.ui.gui.clickgui.panel.impl;

import op.wawa.prideplus.Pride;
import op.wawa.prideplus.module.Module;
import op.wawa.prideplus.ui.font.FontManager;
import op.wawa.prideplus.ui.gui.clickgui.panel.Panel;
import op.wawa.prideplus.utils.animation.animations.impl.SimpleAnimation;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ChengFeng
 * @since 2024/7/31
 **/
public class CategoryPanel extends Panel {
    public Module.Category category;
    public List<ModulePanel> modulePanelList;
    public SimpleAnimation scrollAnim;


    public CategoryPanel(Module.Category category) {
        this.category = category;
        width = 80f;
        height = 30f;
        modulePanelList = new ArrayList<>();

        for (Module module : Pride.INSTANCE.moduleManager.getCategoryModules(category)) {
            modulePanelList.add(new ModulePanel(module));
        }

        scrollAnim = new SimpleAnimation(1);
    }

    @Override
    public void draw(float x, float y, int mouseX, int mouseY) {
        this.x = x;
        this.y = y;
        this.height = FontManager.default18.getHeight();
        scrollAnim.speed = 0.2f;

        FontManager.default18.drawString(category.name(), x, y, Color.WHITE.getRGB());
    }

    public void handleScroll() {
        // Scroll
        int wheel = Mouse.getDWheel();
        if (wheel != 0) {
            if (wheel > 0) {
                scrollAnim.target += 20;
            } else {
                scrollAnim.target -= 20;
            }
            if (scrollAnim.target > 0) scrollAnim.target = 0f;
        }
    }
}
