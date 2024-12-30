package op.wawa.prideplus.ui.gui.alt.altimpl;

import op.wawa.prideplus.ui.gui.alt.AccountEnum;
import op.wawa.prideplus.ui.gui.alt.Alt;
import lombok.Getter;

public final class OriginalAlt extends Alt {
    @Getter
    private final String accessToken;
    private final String uuid;
    @Getter
    private final String type;

    public OriginalAlt(String userName,String accessToken,String uuid,String type) {
        super(userName, AccountEnum.ORIGINAL);
        this.accessToken = accessToken;
        this.uuid = uuid;
        this.type = type;
    }

    public String getUUID() {
        return uuid;
    }

}
