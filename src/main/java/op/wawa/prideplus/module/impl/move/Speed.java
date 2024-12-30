package op.wawa.prideplus.module.impl.move;

import op.wawa.prideplus.event.annotations.EventTarget;
import op.wawa.prideplus.event.events.EventPreUpdate;
import op.wawa.prideplus.event.events.EventStrafe;
import op.wawa.prideplus.event.events.EventTick;
import op.wawa.prideplus.module.Module;
import op.wawa.prideplus.module.impl.world.Scaffold;
import op.wawa.prideplus.utils.object.Move;
import op.wawa.prideplus.utils.player.MovementUtils;
import op.wawa.prideplus.utils.player.PlayerUtils;
import op.wawa.prideplus.value.values.BooleanValue;
import op.wawa.prideplus.value.values.ModeValue;
import op.wawa.prideplus.value.values.NumberValue;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.vialoadingbase.ViaLoadingBase;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;

public final class Speed extends Module {
    private final BooleanValue strafe = new BooleanValue("Strafe", false);
    private final NumberValue minAngle = new NumberValue("Min angle", 30, 15, 90, 15).setVisible(strafe::getValue);
    private final BooleanValue fullStrafe = new BooleanValue("Full strafe", false).setVisible(strafe::getValue);

    private double lastAngle;

    public Speed() {
        super("Speed", Category.MOVE);
    }

    @EventTarget
    public void onPreUpdate(EventPreUpdate event) {
        if (!strafe.getValue() || !canStrafe())
            return;

        if (mc.thePlayer.offGroundTicks == 9) {
            MovementUtils.strafe(0.2);
            mc.thePlayer.motionY += 0.1;
        } else {
            MovementUtils.strafe(0.11);
        }
    }

    private boolean canStrafe() {
        if (mc.thePlayer.onGround)
            return false;
        final double curAngle = Move.fromMovement(mc.thePlayer.moveForward, mc.thePlayer.moveStrafing).getDeltaYaw()
               /* + TargetStrafe.getMovementYaw()*/;

        if (Math.abs(curAngle - lastAngle) < minAngle.getFloatValue() || mc.thePlayer.hurtTime != 0)
            return false;
        lastAngle = curAngle;

        if (fullStrafe.getValue())
            return mc.thePlayer.offGroundTicks >= 4 && mc.thePlayer.offGroundTicks <= 9;
        return mc.thePlayer.offGroundTicks == 4 || mc.thePlayer.offGroundTicks == 9;
    }

    @EventTarget
    public void onPrePlayerInput(EventStrafe event) {
        if (noAction()) return;

        if (!jumpDown() && PlayerUtils.isMoving() && mc.currentScreen == null && mc.thePlayer.onGround) {
            MovementUtils.strafe(MovementUtils.getAllowedHorizontalDistance() - Math.random() / 100f);
            mc.thePlayer.jump();
        }
    }

    public boolean noAction() {
        return !(mc.thePlayer != null && mc.theWorld != null)
                || ((mc.thePlayer.isInWater() || mc.thePlayer.isInLava()))
                || (mc.thePlayer.isSneaking())
                || Scaffold.INSTANCE.isEnable();
    }

    public static boolean jumpDown() {
        try {
            return Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
        } catch (Throwable e) {
            try {
                return mc.gameSettings.keyBindJump.isKeyDown();
            } catch (Throwable e2) {
                return false;
            }
        }
    }

    @Override
    public void onEnable() {
        lastAngle = MovementUtils.direction() * (180 / Math.PI);
    }
}