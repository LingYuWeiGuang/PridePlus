package op.wawa.prideplus.value.values;

import op.wawa.prideplus.value.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;

public class ModeValue extends Value<String> {
    private final ArrayList<String> modes;

    public ModeValue(String valueName, String defaultValue, String... modes) {
        super(valueName, defaultValue);

        this.modes = new ArrayList<>(Arrays.asList(modes));
    }

    @Override
    public ModeValue setVisible(Supplier<Boolean> visible) {
        return (ModeValue) super.setVisible(visible);
    }

    public final ArrayList<String> getModes() {
        return modes;
    }

    public boolean isCurrentMode(String value) {
        return getValue().equals(value);
    }

    public boolean isNotCurrentMode(String value) {
        return !getValue().equals(value);
    }

    public void next() {
        int index = modes.indexOf(getValue()) + 1;
        if (index == modes.size()) {
            setValue(modes.getFirst());
            return;
        }
        setValue(modes.get(index));
    }

    public void prev() {
        int index = modes.indexOf(getValue()) - 1;
        if (index == -1) {
            setValue(modes.getLast());
            return;
        }
        setValue(modes.get(index));
    }
}