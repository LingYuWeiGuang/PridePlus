package op.wawa.prideplus.ui.font;

import op.wawa.prideplus.Pride;

import java.awt.*;
import java.util.Objects;

public final class FontManager {
    public static FontDrawer default10;
    public static FontDrawer default14;
    public static FontDrawer default16;
    public static FontDrawer default17;
    public static FontDrawer default18;
    public static FontDrawer default22;
    public static FontDrawer default30;
    public static FontDrawer default42;
    public static FontDrawer rubik13;
    public static FontDrawer rubik15;
    public static FontDrawer rubik16;
    public static FontDrawer rubikBold28;
    public static FontDrawer greycliff14;
    public static FontDrawer greycliff16;
    public static FontDrawer greycliff18;

    public static void init() {
        default10 = getDefault(10);
        default14 = getDefault(14);
        default16 = getDefault(16);
        default17 = getDefault(17);
        default18 = getDefault(18);
        default22 = getDefault(22);
        default30 = getDefault(30);
        default42 = getDefault(42);
        rubik13 = getFont("rubik", 13);
        rubik15 = getFont("rubik", 15);
        rubik16 = getFont("rubik", 16);
        rubikBold28 = getFont("rubik-bold", 28);
        greycliff14 = getFont("greycliff", 14);
        greycliff16 = getFont("greycliff", 16);
        greycliff18 = getFont("greycliff", 18);
    }

    private static FontDrawer getDefault(int size) {
        try {
            return new FontDrawer(Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(FontManager.class.getResourceAsStream("/assets/minecraft/" + Pride.NAME.toLowerCase() + "/fonts/font.ttf"))).deriveFont(Font.PLAIN, size), true, true);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return new FontDrawer(new Font(Font.SANS_SERIF, Font.PLAIN, 16), false, true);
    }

    private static FontDrawer getFont(String name, int size) {
        try {
            return new FontDrawer(Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(FontManager.class.getResourceAsStream("/assets/minecraft/" + Pride.NAME.toLowerCase() + "/fonts/" + name + ".ttf"))).deriveFont(Font.BOLD, size), true, true);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return new FontDrawer(new Font(Font.SANS_SERIF, Font.BOLD, 16), false, true);
    }
}
