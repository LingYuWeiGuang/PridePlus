package op.wawa.prideplus.utils.misc.compare;

import op.wawa.prideplus.ui.font.FontDrawer;

import java.util.Comparator;

/**
 * @author ChengFeng
 * @since 2024/8/1
 **/
public class StringComparator implements Comparator<String> {
    private final FontDrawer font;

    public StringComparator(FontDrawer font) {
        this.font = font;
    }

    @Override
    public int compare(String o1, String o2) {
        return Integer.compare(font.getStringWidth(o1), font.getStringWidth(o2));
    }
}
