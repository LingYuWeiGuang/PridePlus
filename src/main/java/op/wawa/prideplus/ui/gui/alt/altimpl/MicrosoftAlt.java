package op.wawa.prideplus.ui.gui.alt.altimpl;

import op.wawa.prideplus.ui.gui.alt.AccountEnum;
import op.wawa.prideplus.ui.gui.alt.Alt;
import lombok.Getter;

@Getter
public final class MicrosoftAlt extends Alt {
    private final String refreshToken;

    public MicrosoftAlt(String userName,String refreshToken) {
        super(userName, AccountEnum.MICROSOFT);
        this.refreshToken = refreshToken;
    }

}
