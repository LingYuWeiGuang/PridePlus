package op.wawa.prideplus.value.values;

import op.wawa.prideplus.value.Value;

import java.util.function.Supplier;

public class TextValue extends Value<String> {
    public TextValue(String valueName, String defaultValue) {
        super(valueName, defaultValue);
    }

    @Override
    public TextValue setVisible(Supplier<Boolean> visible) {
        return (TextValue) super.setVisible(visible);
    }
}
