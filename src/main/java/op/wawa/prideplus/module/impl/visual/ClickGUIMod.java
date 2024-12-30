package op.wawa.prideplus.module.impl.visual;

import op.wawa.prideplus.Pride;
import op.wawa.prideplus.module.Module;
import org.lwjgl.input.Keyboard;

public class ClickGUIMod extends Module {
    public ClickGUIMod() {
        super("ClickGUI", Category.VISUAL);
        setCanEnable(false);
        setKeyCode(Keyboard.KEY_RSHIFT);
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen(Pride.INSTANCE.clickGui);
    }
}
