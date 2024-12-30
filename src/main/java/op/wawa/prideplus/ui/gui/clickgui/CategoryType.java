package op.wawa.prideplus.ui.gui.clickgui;

import op.wawa.prideplus.module.Module;

public enum CategoryType {
    Attack,
    Visual,
    Misc;

    public static CategoryType getType(Module.Category category) {
        switch (category) {
            case Module.Category.VISUAL -> {
                return Visual;
            }

            case Module.Category.FIGHT -> {
                return Attack;
            }

            default -> {
                return Misc;
            }
         }
    }
}
