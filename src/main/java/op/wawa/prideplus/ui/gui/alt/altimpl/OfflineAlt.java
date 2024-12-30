package op.wawa.prideplus.ui.gui.alt.altimpl;

import op.wawa.prideplus.ui.gui.alt.AccountEnum;
import op.wawa.prideplus.ui.gui.alt.Alt;

public final class OfflineAlt extends Alt {
    public OfflineAlt(String userName) {
        super(userName, AccountEnum.OFFLINE);
    }
}
