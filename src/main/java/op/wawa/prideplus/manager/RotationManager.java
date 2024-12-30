package op.wawa.prideplus.manager;

import op.wawa.prideplus.Pride;
import op.wawa.prideplus.event.annotations.EventPriority;
import op.wawa.prideplus.event.annotations.EventTarget;
import op.wawa.prideplus.event.events.*;
import op.wawa.prideplus.module.impl.world.Scaffold;
import op.wawa.prideplus.utils.MinecraftInstance;
import op.wawa.prideplus.utils.misc.MathUtils;
import op.wawa.prideplus.utils.object.Rotation;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;

public class RotationManager implements MinecraftInstance {
    public static Rotation rotation, targetRotation, lastRotation, lastServerRotation;
    private static float rotationSpeed;
    private static boolean modify;
    private static boolean smoothed;
    public static boolean movementFix;
    private static boolean strict;

    public RotationManager() {
        Pride.INSTANCE.eventManager.register(this);
        rotation = new Rotation(0, 0);
    }

    public Rotation getRotation() {
        return rotation;
    }

    public void setRotation(Rotation rotation, float rotationSpeed, boolean movementFix) {
        targetRotation = new Rotation(rotation.yaw, MathHelper.clamp_float(rotation.pitch, -90F, 90F));
        RotationManager.rotationSpeed = rotationSpeed;
        RotationManager.movementFix = movementFix;

        modify = true;
        strict = false;
        smoothRotation();
    }

    public void setRotation(Rotation rotation, float rotationSpeed, boolean movementFix, boolean strict) {
        targetRotation = new Rotation(rotation.yaw, MathHelper.clamp_float(rotation.pitch, -90F, 90F));
        RotationManager.rotationSpeed = rotationSpeed;
        RotationManager.movementFix = movementFix;

        modify = true;
        RotationManager.strict = strict;
        smoothRotation();
    }

    public void setRotations(Rotation rotation, double rotationSpeed, boolean movementFix) {
        targetRotation = new Rotation(rotation.yaw, MathHelper.clamp_float(rotation.pitch, -90F, 90F));
        RotationManager.rotationSpeed = (float) (rotationSpeed * 18.0);
        RotationManager.movementFix = movementFix;

        modify = true;
        strict = false;

        smoothRotation();
    }

    public void setRotations(final float[] rotations, double rotationSpeed, boolean movementFix) {
        setRotations(new Rotation(rotations[0], rotations[1]), rotationSpeed, movementFix);
    }

    @EventTarget
    public void onUpdateEvent(final EventPreUpdate event) {
        if (!modify || rotation == null || lastRotation == null || targetRotation == null) {
            rotation = lastRotation = lastServerRotation = targetRotation = new Rotation(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
        }

        if (modify) {
            smoothRotation();
        }
    }

    @EventTarget
    public void onMoveInputEvent(EventMoveInput event) {
        if (modify && movementFix && !strict) {
            final float yaw = rotation.yaw;
            final float forward = event.getForward();
            final float strafe = event.getStrafe();

            float deltaYaw = mc.thePlayer.rotationYaw - yaw;

            float newX = strafe * MathHelper.cos(deltaYaw * 0.017453292f) - forward * MathHelper.sin(deltaYaw * 0.017453292f);
            float newZ = forward * MathHelper.cos(deltaYaw * 0.017453292f) + strafe * MathHelper.sin(deltaYaw * 0.017453292f);

            event.setForward(Math.round(newZ));
            event.setStrafe(Math.round(newX));
        }
    }

    @EventTarget
    public void onLookEvent(EventLook event) {
        if (modify) {
            event.setRotation(rotation);
        }
    }

    @EventTarget
    public void onPlayerMoveUpdateEvent(EventStrafe event) {
        if (modify && movementFix) {
            event.setYaw(rotation.yaw);
        }
    }

    @EventTarget
    public void onJumpFixEvent(EventJump event) {
        if (modify && movementFix) {
            event.setYaw(rotation.yaw);
        }
    }

    @EventTarget
    public void onMotionEvent(EventMotion event) {
        if (modify) {
            event.setYaw(rotation.getYaw());
            event.setPitch(rotation.getPitch());

            mc.thePlayer.rotationYawHead = rotation.yaw;
            mc.thePlayer.renderYawOffset = rotation.yaw;
            mc.thePlayer.rotationPitchHead = rotation.pitch;

            lastServerRotation = new Rotation(rotation.getYaw(), rotation.getPitch());

            if (Math.abs((rotation.getYaw() - mc.thePlayer.rotationYaw) % 360) < 1 && Math.abs((rotation.getPitch() - mc.thePlayer.rotationPitch)) < 1) {
                modify = false;

                correctDisabledRotations();
            }

            lastRotation = rotation;
        } else {
            lastRotation = new Rotation(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
        }

        rotation = new Rotation(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
        smoothed = false;
    }

    private void correctDisabledRotations() {
        final Rotation rotation = new Rotation(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
        final Rotation fixedRotation = resetRotation(applySensitivityPatch(rotation, lastRotation));

        mc.thePlayer.rotationYaw = fixedRotation.yaw;
        mc.thePlayer.rotationPitch = fixedRotation.pitch;
    }

    public Rotation resetRotation(Rotation rotation) {
        if (rotation == null) {
            return null;
        }

        final float yaw = rotation.yaw + MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw - rotation.yaw);
        final float pitch = mc.thePlayer.rotationPitch;
        return new Rotation(yaw, pitch);
    }

    public Rotation applySensitivityPatch(final Rotation rotation, final Rotation previousRotation) {
        final float mouseSensitivity = (float) (mc.gameSettings.mouseSensitivity * (1 + Math.random() / 10000000) * 0.6F + 0.2F);
        final double multiplier = mouseSensitivity * mouseSensitivity * mouseSensitivity * 8.0F * 0.15D;
        final float yaw = previousRotation.getYaw() + (float) (Math.round((rotation.getYaw() - previousRotation.getYaw()) / multiplier) * multiplier);
        final float pitch = previousRotation.getPitch() + (float) (Math.round((rotation.getPitch() - previousRotation.getPitch()) / multiplier) * multiplier);
        return new Rotation(yaw, MathHelper.clamp_float(pitch, -90, 90));
    }

    private static void smoothRotation() {
        if (!smoothed) {
            final float lastYaw = lastRotation.yaw;
            final float lastPitch = lastRotation.pitch;
            final float targetYaw = targetRotation.yaw;
            final float targetPitch = targetRotation.pitch;

            rotation = getSmoothRotation(new Rotation(lastYaw, lastPitch), new Rotation(targetYaw, targetPitch),
                    rotationSpeed + Math.random());

            if (movementFix) {
                mc.thePlayer.movementYaw = rotation.yaw;
            }

            // mc.thePlayer.velocityYaw = rotation.yaw;
        }

        smoothed = true;

        mc.entityRenderer.getMouseOver(1);
    }

    public static Rotation getSmoothRotation(Rotation lastRotation, Rotation targetRotation, double speed) {
        float yaw = targetRotation.yaw;
        float pitch = targetRotation.pitch;
        final float lastYaw = lastRotation.yaw;
        final float lastPitch = lastRotation.pitch;

        if (speed != 0) {
            final float rotationSpeed = (float) speed;

            final double deltaYaw = MathHelper.wrapAngleTo180_double(targetRotation.yaw - lastRotation.yaw);
            final double deltaPitch = pitch - lastPitch;

            final double distance = Math.sqrt(deltaYaw * deltaYaw + deltaPitch * deltaPitch);
            final double distributionYaw = Math.abs(deltaYaw / distance);
            final double distributionPitch = Math.abs(deltaPitch / distance);

            final double maxYaw = rotationSpeed * distributionYaw;
            final double maxPitch = rotationSpeed * distributionPitch;

            final float moveYaw = (float) Math.max(Math.min(deltaYaw, maxYaw), -maxYaw);
            final float movePitch = (float) Math.max(Math.min(deltaPitch, maxPitch), -maxPitch);

            yaw = lastYaw + moveYaw;
            pitch = lastPitch + movePitch;
        }

        final boolean randomise = Math.random() > 0.8;

        for (int i = 1; i <= (int) (2 + Math.random() * 2); ++i) {

            if (randomise) {
                yaw += (float) ((Math.random() - 0.5) / 100000000);
                pitch -= (float) (Math.random() / 200000000);
            }

            /*
             * Fixing GCD
             */
            final Rotation rotation = new Rotation(yaw, pitch);
            final Rotation fixedRotation = applySensitivityPatch(rotation);

            /*
             * Setting rotations
             */
            yaw = fixedRotation.yaw;
            pitch = Math.max(-90, Math.min(90, fixedRotation.pitch));
        }

        return new Rotation(yaw, pitch);
    }

    public static Rotation applySensitivityPatch(Rotation rotation) {
        final Rotation previousRotation = new Rotation(mc.thePlayer.lastReportedYaw, mc.thePlayer.lastReportedPitch);
        final float mouseSensitivity = (float) (mc.gameSettings.mouseSensitivity * (1 + Math.random() / 10000000) * 0.6F + 0.2F);
        final double multiplier = mouseSensitivity * mouseSensitivity * mouseSensitivity * 8.0F * 0.15D;
        final float yaw = previousRotation.yaw + (float) (Math.round((rotation.yaw - previousRotation.yaw) / multiplier) * multiplier);
        final float pitch = previousRotation.pitch + (float) (Math.round((rotation.pitch - previousRotation.pitch) / multiplier) * multiplier);
        return new Rotation(yaw, MathHelper.clamp_float(pitch, -90, 90));
    }
}
