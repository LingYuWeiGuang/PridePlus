package op.wawa.prideplus.module.impl.fight;

import op.wawa.prideplus.Pride;
import op.wawa.prideplus.event.annotations.EventTarget;
import op.wawa.prideplus.event.events.*;
import op.wawa.prideplus.module.Module;
import op.wawa.prideplus.utils.object.TimerUtils;
import op.wawa.prideplus.utils.player.PacketUtils;
import op.wawa.prideplus.utils.player.RaycastUtils;
import op.wawa.prideplus.value.values.BooleanValue;
import op.wawa.prideplus.value.values.ModeValue;
import op.wawa.prideplus.value.values.NumberValue;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.MathHelper;
import net.viamcp.fixes.AttackOrder;

import java.util.ArrayList;
import java.util.List;

public final class Velocity extends Module {
    private final ModeValue mode = new ModeValue("Mode", "GrimAC", "GrimAC", "Watchdog");
    public BooleanValue rayTraceValue = new BooleanValue("RayCast", true);
    public NumberValue range = new NumberValue("AttackRange", 3.5, 2.0, 4.0, 0.1);

    public List<Entity> targets = new ArrayList<>();
    private boolean velocityInput = false, falling = false, attacking, C0B, lastSprinting;
    private double motionNoXZ;
    private final TimerUtils timer = new TimerUtils();

    public Velocity() {
        super("Velocity", Category.FIGHT);
    }

    @Override
    public void onEnable() {
        C0B = false;
        super.onEnable();
    }

    @EventTarget
    public void onPreUpdate(EventMotion e) {
        KillAura killAura = (KillAura) Pride.INSTANCE.moduleManager.getModuleFromName("KillAura");
        if (killAura.isEnable() && KillAura.target != null) {
            if (e.isPre()) {
                if (mc.thePlayer.fallDistance > 3) {
                    falling = true;
                }
                if (falling) {
                    if (!mc.thePlayer.velocityChanged && mc.thePlayer.onGround && mc.thePlayer.hurtTime > 0) {
                        falling = false;
                    }
                }
            }

            if (e.isPost()) {
                if (C0B) {
                    C0B = false;
                    if (!mc.thePlayer.isSprinting()) {
                        PacketUtils.sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                    }
                }
            }
        }

/*        if (mode.isCurrentMode("Watchdog")) {
            if (mc.thePlayer.isInWater() || mc.thePlayer.isInLava())
                return;

            if (mc.thePlayer.hurtTime > 8) {
                mc.thePlayer.motionX = -MathHelper.sin((float) mc.thePlayer.rotationYaw) * 0.5;
                mc.thePlayer.motionZ = MathHelper.cos((float) mc.thePlayer.rotationYaw) * 0.5;
            }
        }*/
    }

    @EventTarget
    private void onClick(EventPlace event) {
        KillAura killAura = (KillAura) Pride.INSTANCE.moduleManager.getModuleFromName("KillAura");
        if (mode.isCurrentMode("GrimAC")) {
            if (this.velocityInput) {
                if (this.attacking) {
                    if (killAura.isEnable() && KillAura.target != null) {
                           if (mc.thePlayer.getDistanceToEntity(KillAura.target) >= killAura.range.getValue()) return;
                        if (mc.thePlayer.getDistanceToEntity(KillAura.target) <= killAura.range.getValue()) {
                            if (rayTraceValue.getValue()) {
                                if (RaycastUtils.rayCast(mc.thePlayer.getLastReportedRotation(), range.getValue()).entityHit == KillAura.target) {
                                    for (int i = 0; i < 8; i++) {
                                        AttackOrder.sendFixedAttack(mc.thePlayer, KillAura.target);
/*                                        mc.thePlayer.swingItem();
                                        PacketUtils.sendPacketNoEvent(new C02PacketUseEntity(KillAura.attackEntity, C02PacketUseEntity.Action.ATTACK));*/
                                    }
                                    mc.thePlayer.motionX *= this.motionNoXZ;
                                    mc.thePlayer.motionZ *= this.motionNoXZ;
                                    this.attacking = false;
                                    C0B = true;
                                }
                            } else {
                                for (int i = 0; i < 8; i++) {
                                    AttackOrder.sendFixedAttack(mc.thePlayer, KillAura.target);
/*                                    mc.thePlayer.swingItem();
                                    PacketUtils.sendPacketNoEvent(new C02PacketUseEntity(KillAura.attackEntity, C02PacketUseEntity.Action.ATTACK));*/
                                }
                                mc.thePlayer.motionX *= this.motionNoXZ;
                                mc.thePlayer.motionZ *= this.motionNoXZ;
                                this.attacking = false;
                                C0B = true;
                            }
                        } else if (mc.thePlayer.getDistanceToEntity(KillAura.target) <= 3.0D) {
                            for (int i = 0; i < 8; i++) {
                                AttackOrder.sendFixedAttack(mc.thePlayer, KillAura.target);
                                /*PacketUtils.sendPacketNoEvent(new C0APacketAnimation());
                                PacketUtils.sendPacketNoEvent(new C02PacketUseEntity(KillAura.attackEntity, C02PacketUseEntity.Action.ATTACK));*/
                            }
                            mc.thePlayer.motionX *= this.motionNoXZ;
                            mc.thePlayer.motionZ *= this.motionNoXZ;
                            this.attacking = false;
                            C0B = true;
                        }
                    }
                } else if (mc.thePlayer.hurtTime == 6 && mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isKeyDown()) {
                    mc.thePlayer.movementInput.jump = true;
                }
                if (mc.thePlayer.hurtTime == 0) {
                    this.velocityInput = false;
                }

            }
        }
    }

    @EventTarget
    public void onPacketSend(EventPacketSend event) {
        Packet<?> packet = event.getPacket();
        if (mode.isCurrentMode("GrimAC")) {
            if (packet instanceof C0BPacketEntityAction) {
                KillAura killAura = (KillAura) Pride.INSTANCE.moduleManager.getModuleFromName("KillAura");
                if (killAura.isEnable() && KillAura.target != null) {
                    if (((C0BPacketEntityAction) packet).getAction() == C0BPacketEntityAction.Action.START_SPRINTING) {
                        if (this.lastSprinting) {
                            event.setCancelled(true);
                        }
                        this.lastSprinting = true;
                    } else if (((C0BPacketEntityAction) packet).getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING) {
                        if (!this.lastSprinting) {
                            event.setCancelled(true);
                        }
                        this.lastSprinting = false;
                    }
                }
            }
        }
    }
    @EventTarget
    public void onPacket(EventPacketReceive event) {
        Packet<?> packet = event.getPacket();
        if (packet instanceof S27PacketExplosion velocity) {
        }
        if (packet instanceof S12PacketEntityVelocity velocity && (((S12PacketEntityVelocity) packet).getEntityID() == mc.thePlayer.getEntityId())) {
            this.velocityInput = true;
            if (mode.isCurrentMode("GrimAC")){
                KillAura killAura = (KillAura) Pride.INSTANCE.moduleManager.getModuleFromName("KillAura");
                if (killAura.isEnable() && KillAura.target != null) {
                    if (mc.thePlayer.getDistanceToEntity(KillAura.target) <= 3) {
                        this.attacking = true;
                        this.motionNoXZ = 0.07776;
                        PacketUtils.sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                    }
                }
            } else if (mode.isCurrentMode("Watchdog")) {
                event.setCancelled(true);
                if (this.mc.thePlayer.onGround || velocity.getMotionY() / 8000.0D < 0.2D || velocity.getMotionY() / 8000.0D > 0.41995D)
                    this.mc.thePlayer.motionY = velocity.getMotionY() / 8000.0D;
            }
        }
    }

    @Override
    protected String getModuleTag() {
        return mode.getValue();
    }
}
