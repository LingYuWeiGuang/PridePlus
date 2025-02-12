package op.wawa.prideplus.ui.gui.alt;

import lombok.Getter;

@Getter
public enum AccountEnum {
    OFFLINE("OFFLINE"),     // OFFLINE:USERNAME
    MOJANG("MOJANG"),       // MOJANG:ACCOUNT:PASSWORD:USERNAME
    MICROSOFT("MICROSOFT"), // MICROSOFT:USERNAME:REFRESHTOKEN
    ORIGINAL("ORIGINAL");   // ORIGINAL:TYPE:USERNAME:TOKEN:UUID

    private final String writeName;

    AccountEnum(String name) {
        this.writeName = name;
    }

    public static AccountEnum parse(String str) {
        for (AccountEnum value : values()) {
            if (value.writeName.equals(str)) {
                return value;
            }
        }

        return null;
    }
}
