package op.wawa.prideplus.value.values;

import op.wawa.prideplus.value.Value;

import java.util.function.Supplier;

public class BooleanValue extends Value<Boolean> {
    public BooleanValue(String valueName, Boolean defaultValue) {
        super(valueName, defaultValue);
    }

    public final boolean getReversedValue() {
        return !getValue();
    }

    @Override
    public BooleanValue setVisible(Supplier<Boolean> visible) {
        return (BooleanValue) super.setVisible(visible);
    }
}

