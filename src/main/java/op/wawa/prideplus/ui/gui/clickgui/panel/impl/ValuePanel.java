package op.wawa.prideplus.ui.gui.clickgui.panel.impl;

import op.wawa.prideplus.ui.font.FontManager;
import op.wawa.prideplus.ui.gui.clickgui.component.Component;
import op.wawa.prideplus.ui.gui.clickgui.component.impl.*;
import op.wawa.prideplus.ui.gui.clickgui.panel.Panel;
import op.wawa.prideplus.value.Value;
import op.wawa.prideplus.value.values.*;

import java.awt.*;

/**
 * @author ChengFeng
 * @since 2024/7/28
 **/
public class ValuePanel extends Panel {
    public Value<?> value;
    private Component<?> component;

    public ValuePanel(Value<?> value) {
        this.value = value;

        if (value instanceof BooleanValue bv) {
            component = new ButtonComponent(bv);
        } else if (value instanceof NumberValue nv) {
            component = new NumberComponent(nv);
        } else if (value instanceof ModeValue mv) {
            component = new ModeComponent(mv);
        } else if (value instanceof TextValue sv) {
            component = new StringComponent(sv);
        } else if (value instanceof ColorValue cv){
            component = new ColorComponent(cv);
        }
    }

    @Override
    public void init() {
        component.init();
    }

    @Override
    public void draw(float x, float y, int mouseX, int mouseY) {
        int textColor = Color.WHITE.getRGB();
        if (component instanceof ButtonComponent bc) {
            textColor = bc.textColAnim.getOutput().getRGB();
        }
        FontManager.default16.drawString(value.getValueName(), x, y - 2, textColor);
        component.draw(x, y, mouseX, mouseY);
        height = component.height;
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, int button) {
        component.onMouseClick(mouseX, mouseY, button);
    }

    @Override
    public void onKeyTyped(char c, int keyCode) {
        component.onKeyTyped(c, keyCode);
    }

    @Override
    public void onMouseRelease() {
        component.onMouseRelease();
    }
}
