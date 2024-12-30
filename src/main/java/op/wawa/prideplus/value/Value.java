package op.wawa.prideplus.value;

import op.wawa.prideplus.utils.object.Callback;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Supplier;

public abstract class Value<V> {
    private final String valueName;
    @Getter
    private V value;
    @Setter
    private Callback<V> callback;

    private Supplier<Boolean> isVisible;

    public Value(String valueName) {
        this.valueName = valueName;
    }

    public Value(String valueName, V value) {
        this.valueName = valueName;
        this.value = value;
        this.isVisible = () -> true;
    }

    public final String getValueName() {
        return valueName;
    }

    @Setter
    private Runnable runnable;

    @Getter
    @Setter
    private V future;

    public void setValue(V value) {
        if (onChangeValue(this.value,value)) {
            this.value = value;

            future = value;

            if (runnable != null && callback != null) {
                runnable.run();
                this.value = callback.callback;
            }
        }
    }

    public final void forceSetValue(V value) {
        this.value = value;
    }

    public Value<V> setVisible(Supplier<Boolean> visible) {
        this.isVisible = visible;
        return this;
    }

    public boolean isVisible() {
        return this.isVisible.get();
    }

    protected boolean onChangeValue(V pre, V post) {
        return true;
    }
}
