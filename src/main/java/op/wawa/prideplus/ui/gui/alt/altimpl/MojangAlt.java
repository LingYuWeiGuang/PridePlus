package op.wawa.prideplus.ui.gui.alt.altimpl;

import op.wawa.prideplus.ui.gui.alt.AccountEnum;
import op.wawa.prideplus.ui.gui.alt.Alt;
import lombok.Getter;

@Getter
public final class MojangAlt extends Alt {
    private final String account;
    private final String password;

    public MojangAlt(String account, String password,String userName) {
        super(userName, AccountEnum.MOJANG);
        this.account = account;
        this.password = password;
    }

}
