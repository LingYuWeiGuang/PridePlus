package op.wawa.prideplus.module.impl.move;

import op.wawa.prideplus.event.annotations.EventTarget;
import op.wawa.prideplus.event.events.EventTick;
import op.wawa.prideplus.module.Module;
import op.wawa.prideplus.utils.player.MovementUtils;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.vialoadingbase.ViaLoadingBase;

public final class Sprint extends Module {
    public static boolean sprint = true;

    public Sprint() {
        super("Sprint",Category.MOVE);
    }

    @EventTarget
    private void onPlayerTick(EventTick event) {
        if (mc.thePlayer == null) return;
        if (Sprint.sprint && MovementUtils.isMoving() && (ViaLoadingBase.getInstance().getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_13) || !mc.thePlayer.isSneaking()) && !mc.thePlayer.isCollidedHorizontally) {
            mc.gameSettings.keyBindSprint.pressed = true;
            if (!mc.thePlayer.isInWeb) {
                if (mc.gameSettings.keyBindForward.isKeyDown()) {
                    if ((NoSlow.INSTANCE.isEnable() || ViaLoadingBase.getInstance().getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_13)) && mc.thePlayer.isUsingItem()) {
                        mc.thePlayer.setSprinting(true);
                    }
                }
            }
        } else {
            mc.gameSettings.keyBindSprint.pressed = false;
            mc.thePlayer.setSprinting(false);
            Sprint.sprint = true;
        }
    }
}