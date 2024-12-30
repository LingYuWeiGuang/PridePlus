package op.wawa.prideplus.ui.gui.clickgui.component.impl;

import op.wawa.prideplus.ui.font.FontManager;
import op.wawa.prideplus.ui.gui.clickgui.ThemeColor;
import op.wawa.prideplus.ui.gui.clickgui.component.Component;
import op.wawa.prideplus.ui.gui.clickgui.gui.TextField;
import op.wawa.prideplus.value.values.TextValue;

/**
 * @author ChengFeng
 * @since 2024/8/1
 **/
public class StringComponent extends Component<String> {
    private final TextField textField;

    public StringComponent(TextValue value) {
        super(value);

        width = 65f;
        height = 13f;

        textField = new TextField(width, height, FontManager.default18, ThemeColor.barColor, ThemeColor.outlineColor);
        textField.text = value.getValue();
    }

    @Override
    public void draw(float x, float y, int mouseX, int mouseY) {
        this.posX = x + panelWidth - width - xGap * 2;
        this.posY = y - 4.0F;

        textField.draw(this.posX, this.posY, mouseX, mouseY);
    }
    
    @Override
    public void onKeyTyped(char c, int keyCode) {
        textField.keyTyped(c, keyCode);
        value.setValue(textField.text);
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, int button) {
        textField.mouseClicked(mouseX, mouseY, button);
    }
}
