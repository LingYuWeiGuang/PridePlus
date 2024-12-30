package op.wawa.prideplus.module;

import op.wawa.prideplus.Pride;
import op.wawa.prideplus.ui.notification.NotificationManager;
import op.wawa.prideplus.ui.notification.NotificationType;
import op.wawa.prideplus.utils.MinecraftInstance;
import op.wawa.prideplus.value.Value;
import com.mojang.realmsclient.gui.ChatFormatting;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.Level;
import org.lwjgl.input.Keyboard;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class Module implements MinecraftInstance {
    private final ArrayList<Value<?>> values = new ArrayList<>();
    @Setter
    private String moduleName;
    @Setter
    private String renderName;
    private final Category category;
    private int keyCode = Keyboard.KEY_NONE;
    private boolean enable;
    @Setter
    @Getter
    private boolean canEnable = true;
    @Setter
    @Getter
    private float arrayX, arrayY;

    public Module(String moduleName, Category category) {
        this.renderName = this.moduleName = moduleName;
        this.category = category;
        // CommandManager.Instance.registerCommands(new ModValueSettingCommand(this));
    }


    public final String getModRenderNameWithTag() {
        final String modTag = getModuleTag();

        if (modTag == null) {
            return getRenderName();
        }

        return getRenderName() + " " + ChatFormatting.GRAY + modTag;
    }

    protected String getModuleTag() {
        return null;
    }

    public final boolean isEnable() {
        return enable;
    }

    public final void toggle() {
        setEnable(!enable);
    }

    public final void setEnable(boolean enable) {
        if (this.enable == enable) return;

        if (!canEnable) {
            this.enable = false;
            if (mc.theWorld != null) {
                onEnable();

                if (mc.theWorld != null && mc.thePlayer != null) {
                    mc.theWorld.playSound(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, "random.click", 0.5F, 0.6F, false);
                }
            }
            return;
        }

        this.enable = enable;

        if (mc.theWorld != null && mc.thePlayer != null) {
            mc.theWorld.playSound(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, "random.click", 0.5F, enable ? 0.6F : 0.4F, false);
        }

        if (enable) {
            if (mc.theWorld != null) {
                onEnable();

                NotificationManager.post(NotificationType.SUCCESS, "Module", "Enabled " + renderName, 1);
            }

            Pride.INSTANCE.eventManager.register(this);
        } else {
            Pride.INSTANCE.eventManager.unregister(this);

            if (mc.theWorld != null) {

                NotificationManager.post(NotificationType.DISABLE, "Module","Disabled " + renderName, 1);

                onDisable();
            }
        }
    }

    @Deprecated(since = "Pride First Version")
    protected final void registerValues(Value<?>... values) {
        this.values.addAll(Arrays.asList(values));
    }

    public final ArrayList<Value<?>> getValues() {
        return values;
    }

    public Value<?> getValue(String key) {
        for (Value<?> value : getValues())
            if (value.getValueName().equals(key))
                return value;
        return null;
    }

    public final int getKeyCode() {
        return keyCode;
    }

    public final void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    public final String getModuleName() {
        return moduleName;
    }

    public final Category getCategory() {
        return category;
    }

    public final String getRenderName() {
        return renderName;
    }

    protected void onEnable() {}
    protected void onDisable() {}

    @Setter
    @Getter
    private boolean hide = false;

    @Getter
    public enum Category {
        FIGHT("Fight"),
        VISUAL("Visual"),
        MOVE("Move"),
        PLAYER("Player"),
        WORLD("World");

        private final String name;

        Category(String name) {
            this.name = name;
        }
    }
}
