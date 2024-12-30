package op.wawa.prideplus.utils.misc.compare;

import op.wawa.prideplus.module.Module;
import op.wawa.prideplus.ui.font.FontDrawer;

import java.util.Comparator;

/**
 * @author ChengFeng
 * @since 2024/7/31
 **/
public class ModuleComparator implements Comparator<Module> {
    private final CompareMode mode;
    private final FontDrawer font;

    public ModuleComparator(CompareMode mode, FontDrawer font) {
        this.mode = mode;
        this.font = font;
    }

    @Override
    public int compare(Module o1, Module o2) {
        if (mode == CompareMode.Alphabet) {
            return o1.getModuleName().compareTo(o2.getModuleName());
        } else {
            return Integer.compare(font.getStringWidth(o2.getModuleName()), font.getStringWidth(o1.getModuleName()));
        }
    }
}
