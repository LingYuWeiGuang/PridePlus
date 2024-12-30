package op.wawa.prideplus.module.impl.fight;

import op.wawa.prideplus.Pride;
import op.wawa.prideplus.event.annotations.EventPriority;
import op.wawa.prideplus.event.annotations.EventTarget;
import op.wawa.prideplus.event.events.*;
import op.wawa.prideplus.module.Module;
import op.wawa.prideplus.utils.object.Rotation;
import op.wawa.prideplus.utils.object.TimerUtils;
import op.wawa.prideplus.utils.player.PlayerUtils;
import op.wawa.prideplus.utils.player.RotationUtils;
import op.wawa.prideplus.utils.render.RenderUtils;
import op.wawa.prideplus.value.values.BooleanValue;
import op.wawa.prideplus.value.values.ColorValue;
import op.wawa.prideplus.value.values.ModeValue;
import op.wawa.prideplus.value.values.NumberValue;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.types.VarIntType;
import com.viaversion.viaversion.protocols.v1_8to1_9.Protocol1_8To1_9;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.viamcp.fixes.AttackOrder;

import javax.vecmath.Vector3d;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class KillAura extends Module {
    public ModeValue priority = new ModeValue("Priority", "Range", "Range", "Health");
    public ModeValue mode = new ModeValue("Mode", "Single", "Single", "Switch", "Multi");

    public ModeValue rotationTiming = new ModeValue("Rotation Timing", "Update", "Update", "Post","Pre");
    public ModeValue rotationMode = new ModeValue("Rotation Mode", "Normal", "Normal");
    public ModeValue aimPosition = new ModeValue("Aim Position", "Body", "Head","Neck","Body","Dick","Foot","Precision","Auto");
    public ModeValue attackMode = new ModeValue("Attack Mode", "Post", "Pre", "Post","Both");

    public ModeValue blockMode = new ModeValue("Block Mode", "Post", "Pre", "Post","Both");
    public ModeValue abMode = new ModeValue("AutoBlock mode", "Grim", "Off", "Grim","BlockPlacement","UseItem","Fake");//Dick:我AB ≠ 我不攻击

    public NumberValue cps = new NumberValue("CPS", 13.0, 1.0, 20.0, 1.0);
    public NumberValue range = new NumberValue("Range", 3.0, 2.0, 6.0, 0.1);
    public NumberValue blockRange = new NumberValue("Block Range", 4.0D, 2.00, 6.00, 0.1);

    public NumberValue scanRange = new NumberValue("Scan Range", 5.00, 2.00, 6.00, 0.1);
    private final BooleanValue throughWall = new BooleanValue("Through Wall", true);
    private final NumberValue wallRange = new NumberValue("Wall Range", 3.0, 1.0, 5.0, 0.1).setVisible(() -> throughWall.isVisible() && throughWall.getValue());
    private final NumberValue fov = new NumberValue("Fov", 360.0, 0.0, 360.0, 1.0);

    public NumberValue switchDelay = new NumberValue("Switch delay", 500.0, 0.0, 1000.0, 10.0);

    public final ModeValue movement = new ModeValue("Movement", "Proper", "Proper", "No Correct");

    public BooleanValue antiEmpty = new BooleanValue("Anti Empty", true);

    public BooleanValue rayCastValue = new BooleanValue("RayCast", false);

    private final BooleanValue targetEsp = new BooleanValue("Target ESP", true);

    private final ModeValue targetEspMode = new ModeValue("ESP Mode", "Smooth", "Smooth","Box").setVisible(targetEsp::getValue);
    private final ColorValue espColor = new ColorValue("ESP Color", new Color(255, 0, 0)).setVisible(targetEsp::getValue);
    private final NumberValue radius = new NumberValue("Radius", 0.5, 0.5, 1.0, 0.1).setVisible(() -> targetEspMode.getValue().equals("Smooth"));
    private final NumberValue boost = new NumberValue("Boost", 1.0, 1.0, 10.0, 1.0).setVisible(() -> targetEspMode.getValue().equals("Smooth"));
    private final TimerUtils switchTimer = new TimerUtils();
    private final TimerUtils attackTimer = new TimerUtils();

    private final TimerUtils debugTimer = new TimerUtils();
    public List<Entity> targets = new ArrayList<>();

    public static Entity target;
    public static EntityLivingBase targetLivingBase;
    public static boolean isBlocking = false;
    public static boolean renderBlocking = false;

    public static boolean sentAnimation = false;

    public static boolean fixattack = false;
    private final boolean isPlayerBlocking = false;


    private static double whatcanisay = 0;//这个是修复你pitch的

    public int index = 0;

    public KillAura() {
        super("KillAura", Category.FIGHT);
    }

    @Override
    public String getModuleTag() {
        return targets.size()+ "";
    }

    @Override
    protected void onEnable() {
        switchTimer.reset();
        isBlocking = false;
        target = null;
        whatcanisay = 0;
        targets.clear();

        if (isBlocking && !abMode.getValue().equals("Off")) {
            stopBlocking(true);
        }

        index = 0;
        super.onEnable();
    }

    @Override
    protected void onDisable() {
        if (mc.thePlayer == null) return;
        target = null;
        targets.clear();
        if (!abMode.getValue().equals("Off")) {
            stopBlocking(true);
        }
        isBlocking = false;

        index = 0;
        super.onDisable();
    }

    private boolean heldSword() {
        return mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }
    public static float[] getRotationFromPosition(double x, double z, double y){
        double xDiff = x - Minecraft.getMinecraft().thePlayer.posX;
        double zDiff = z - Minecraft.getMinecraft().thePlayer.posZ;
        double yDiff = y - 1.3 + whatcanisay + target.getEyeHeight() - (mc.thePlayer.getEntityBoundingBox().minY + mc.thePlayer.getEyeHeight());
        double dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);

        float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0D / 3.141592653589793D) - 90.0F;
        float pitch = (float) -(Math.atan2(yDiff, dist) * 180.0D / 3.141592653589793D);
        float finalpitch = target.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - target.rotationPitch);
        return new float[]{yaw, finalpitch};
    }

    public static float[] mixRotations(Entity ent) {
        double x = ent.posX;
        double z = ent.posZ;
        double y = ent.posY + ent.getEyeHeight() / 2.0F;

        return getRotationFromPosition(x, z, y);
    }
    public static float[] predictedRotations(Entity ent) {
        double x = ent.posX + (ent.posX - ent.lastTickPosX);
        double z = ent.posZ + (ent.posZ - ent.lastTickPosZ);
        double y = ent.posY + ent.getEyeHeight() / 2.0F;
        return getRotationFromPosition(x, z, y);
    }

    public void updateRotations(){
        Vector3d targetPos;
        final double yDist = target.posY - mc.thePlayer.posY;
        if (rotationMode.getValue().equals("Normal")) {
            double eyeHeight = target.getEyeHeight();
            double yOffset = (yDist >= 1.7) ? 0 : (yDist <= -1.7) ? eyeHeight : eyeHeight / 2;
            targetPos = new Vector3d(target.posX, target.posY + yOffset, target.posZ);
            Rotation current = RotationUtils.getRotationFromEyeToPoint(targetPos);
            Pride.INSTANCE.rotationManager.setRotation(current, 180f, movement.getValue().equals("Proper"));
        }
    }
    @EventTarget
    public void onPacket(EventPacketSend event) {
        if (Pride.INSTANCE.moduleManager.getModuleFromName("Scaffold").isEnable()) {
            return;
        }
        if (target == null || target.isDead) return;
        if (antiEmpty.getValue()){
            if (event.getPacket() instanceof C0APacketAnimation){
                sentAnimation = true;
                fixattack = false;
            } else if(event.getPacket() instanceof C02PacketUseEntity && !sentAnimation){
                if (((C02PacketUseEntity) event.getPacket()).getAction() == C02PacketUseEntity.Action.ATTACK){
                    fixattack = true;
                }
                sentAnimation = false;
            }
        }
    }

    @EventTarget
    public void rotations(final EventMotion event) {
        if (event.isPre()) {
            runRotations();

            if (Pride.INSTANCE.moduleManager.getModuleFromName("Scaffold").isEnable()) {
                return;
            }
            if (blockMode.getValue().equals("Pre") || blockMode.getValue().equals("Both")) {
                if (!abMode.getValue().equals("Off") && shouldBlock())
                    doBlock();
            }
            if ((attackMode.getValue().equals("Pre") || attackMode.getValue().equals("Both")) && target != null) {
                if (!(mc.thePlayer.hurtTime >= 2)) {
                    attack();
                }
            }
            if ((rotationTiming.getValue().equals("Pre") || blockMode.getValue().equals("Both")) && target != null) {
                updateRotations();//我pre转头 ≠ 我平时不转
            }
        }

        if (event.isPost()) {
            runRotations();

            if ((rotationTiming.getValue().equals("Post") || rotationTiming.getValue().equals("Both")) && target != null) {
                updateRotations();//我post转头 ≠ 我平时不转
            }
            if ((attackMode.getValue().equals("Post") || attackMode.getValue().equals("Both")) && target != null) {
                attack();
            }
            if (blockMode.getValue().equals("Post") || blockMode.getValue().equals("Both")) {
                if (target != null) {
                    if (!abMode.getValue().equals("Off") && shouldBlock())
                        doBlock();
                }
            } //我防砍 ≠ 我不攻击
        }
    }

    @EventTarget
    public void runRotations() {
        if (Pride.INSTANCE.moduleManager.getModuleFromName("Scaffold").isEnable()) {
            return;
        }
        if (!targets.isEmpty()) {
            if (this.index >= targets.size()) {
                this.index = 0;
            }
            if (mc.thePlayer.getClosestDistanceToEntity(targets.get(this.index)) <= range.getValue()) {
                target = targets.get(this.index);
            } else {
                target = targets.getFirst();
            }
            if (target != null && !PlayerUtils.canBeSeen(target) && (!throughWall.getValue() || mc.thePlayer.getDistanceToEntity(target) >= wallRange.getValue())) {
                target = null;
            }
        }

        if (target != null && mc.thePlayer.getClosestDistanceToEntity(target) <= range.getValue()) {
            if (!rotationMode.getValue().equals("Normal")){

                if (aimPosition.getValue().equals("Body")){
                    whatcanisay = 0F;
                }
                if (aimPosition.getValue().equals("Neck")){
                    whatcanisay = 0.2F;
                }
                if (aimPosition.getValue().equals("Head")){
                    whatcanisay = 0.45F;
                }
                if (aimPosition.getValue().equals("Dick")){
                    whatcanisay = -0.55F;
                }
                if (aimPosition.getValue().equals("Foot")){
                    whatcanisay = -1F;
                }
                if (aimPosition.getValue().equals("Precision")){
                    if (target.posY > mc.thePlayer.posY + 0.7) {
                        whatcanisay = -1F;
                    } else if (target.posY > mc.thePlayer.posY && target.posY <= mc.thePlayer.posY + 0.7) {
                        whatcanisay = -0.55F;
                    } else if (target.posY == mc.thePlayer.posY) {
                        whatcanisay = 0F;
                    } else if (target.posY < mc.thePlayer.posY && target.posY + 0.7 >= mc.thePlayer.posY) {
                        whatcanisay = 0.2F;
                    } else if (target.posY + 0.7 < mc.thePlayer.posY) {
                        whatcanisay = 0.45F;
                    }
                }
                if(aimPosition.getValue().equals("Auto")){
                    if (target.posY >= mc.thePlayer.posY + 1.7) {
                        whatcanisay = -1F;
                    } else if (target.posY + 1.7 <= mc.thePlayer.posY) {
                        whatcanisay = 0.45F;
                    } else {
                        whatcanisay = 0F;
                    }
                }
            }
            if (rotationTiming.getValue().equals("Update")) {
                //我转头 ≠ 我要打
                updateRotations();
            }
        }
    }

    @EventTarget
    @EventPriority(9)
    public void onUpdate(final EventPreUpdate event) {
        if (Pride.INSTANCE.moduleManager.getModuleFromName("Scaffold").isEnable()) {
            return;
        }
        if (target == null) {
            stopBlocking(true);
        }

        if (mc.thePlayer.isDead || mc.thePlayer.isSpectator()) {
            return;
        }

        targets = getTargets(scanRange.getValue());

        if (targets.isEmpty()) {
            target = null;
        }
        sortTargets();


        if (targets.size() > 1 && mode.getValue().equals("Switch") || mode.getValue().equals("Multi")) {
            if (switchTimer.hasTimeElapsed(switchDelay.getValue().longValue()) || mode.getValue().equals("Multi")) {
                ++this.index;
                switchTimer.reset();
            }
        }
        if (targets.size() > 1 && mode.getValue().equals("Single")) {
            if (mc.thePlayer.getClosestDistanceToEntity(target) > scanRange.getValue()) {
                index += 999;
            } else if (target.isDead) {
                index += 999;
            }
        }
    }

    public boolean shouldAttack() {
        final MovingObjectPosition movingObjectPosition = mc.objectMouseOver;

        return ((mc.thePlayer.canEntityBeSeen(target) ? mc.thePlayer.getClosestDistanceToEntity(target) : mc.thePlayer.getDistanceToEntity(target)) <= range.getValue()) && (((!rayCastValue.getValue()) || !mc.thePlayer.canEntityBeSeen(target)) ||
                (rayCastValue.getValue() && movingObjectPosition != null && movingObjectPosition.entityHit == target));
    }

    private boolean isUsingItem() {
        return mc.thePlayer.isUsingItem() && !heldSword();
    }

    public boolean shouldBlock() {
        return target != null && !targets.isEmpty() && mc.thePlayer.getClosestDistanceToEntity(target) <= blockRange.getValue();
    }

    private void attack() {
        if (shouldAttack() && attackTimer.hasTimeElapsed(700L / (antiEmpty.getValue() && fixattack ? 6L : cps.getValue().intValue()))) {
            if (fixattack){
                fixattack = false;
            }
            AttackOrder.sendFixedAttack(mc.thePlayer, target);
            attackTimer.reset();
        }
    }

    private boolean isAnyTargetInAutoBlockRange() {
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (
                    entity instanceof EntityLivingBase && isValid(entity, scanRange.getValue()) &&
                            mc.thePlayer.getClosestDistanceToEntity(entity) <= blockRange.getValue()
            ) {
                return true;
            }
        }

        return false;
    }

    private void unblock() {
        if (isPlayerBlocking) {
            mc.getNetHandler().getNetworkManager().sendPacket(new C07PacketPlayerDigging(
                    C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN
            ));
            mc.thePlayer.stopUsingItem();
        }
    }

    private void block() {
        mc.getNetHandler().getNetworkManager().sendPacket(new C08PacketPlayerBlockPlacement(
                mc.thePlayer.getHeldItem()
        ));
        mc.thePlayer.setItemInUse(
                mc.thePlayer.getHeldItem(), mc.thePlayer.getHeldItem().getMaxItemUseDuration()
        );
    }

    public boolean isSword() {
        return Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem() != null && Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword;
    }

    public void stopBlocking(boolean render) {
        if (isSword() && renderBlocking) {
            switch (abMode.getValue()) {
                case "Grim":
                    mc.gameSettings.keyBindUseItem.pressed = false;
                    mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    break;
                case"BlockPlacement":
                case"UseItem":
                    mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    break;
                case "Fake":
                    break;
            }
            if (render)
                renderBlocking = false;
            isBlocking = false;
        }
    }

    private void doBlock() {
        if (isSword()) {
            switch (abMode.getValue()) {
                case "Grim":
                    mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());

                    if (mc.isSingleplayer()) break;
                    PacketWrapper use_0 = PacketWrapper.create(29, null,
                            Via.getManager().getConnectionManager().getConnections().iterator().next());
                    use_0.write(new VarIntType(), 0);
                    use_0.sendToServer(Protocol1_8To1_9.class, true);

                    PacketWrapper use_1 = PacketWrapper.create(29, null,
                            Via.getManager().getConnectionManager().getConnections().iterator().next());
                    use_1.write(new VarIntType(), 1);
                    use_1.sendToServer(Protocol1_8To1_9.class, true);
                    break;
                case "Fake":
                    break;
                case "BlockPlacement":
                    mc.getNetHandler().sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                    break;
                case "UseItem":
                    mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
                    break;
            }
            if (!abMode.getValue().equals("Fake")) isBlocking = true;

            renderBlocking = true;
        }
    }

    public List<Entity> getTargets(Double value) {
        return Minecraft.getMinecraft().theWorld.loadedEntityList.stream().filter(e -> (double) mc.thePlayer.getClosestDistanceToEntity(e) <= value && isValid(e,value)).collect(Collectors.toList());
    }

    public boolean isOnSameTeam(Entity entity) {
        try {
            String self = Minecraft.getMinecraft().thePlayer.getDisplayName().getUnformattedText();
            String target = entity.getDisplayName().getUnformattedText();
            if (self.startsWith("\u00a7")) {
                if (!target.contains("\u00a7")) {
                    return true;
                }
                if (self.length() <= 2 || target.length() <= 2) {
                    return false;
                }
                return self.substring(0, 2).equals(target.substring(0, 2));
            }
        } catch (Throwable ignored) {}
        return false;

    }
    public boolean isValid(Entity entity, double range) {
        if (!(RotationUtils.isVisibleFOV(entity, fov.getValue().floatValue() / 2F)) ) {
            return false;
        }
        if (!PlayerUtils.canBeSeen(entity) && (!throughWall.getValue() || mc.thePlayer.getDistanceToEntity(entity) >= wallRange.getValue())) {
            return false;
        }
        return Target.INSTANCE.isTarget(entity);
    }

    private void sortTargets() {
        if (!targets.isEmpty()) {
            EntityPlayerSP thePlayer = mc.thePlayer;
            switch (priority.getValue()) {
                case "Range":
                    targets.sort((o1, o2) -> (int) (o1.getClosestDistanceToEntity(thePlayer) - o2.getClosestDistanceToEntity(thePlayer)));
                    break;
                case "Health":
                    targets.sort((o1, o2) -> (int) (((EntityLivingBase) o1).getHealth() - ((EntityLivingBase) o2).getHealth()));
                    break;
            }
        }
    }

    private float getDistanceBetweenAngles(float angle1, float angle2) {
        float agl = Math.abs(angle1 - angle2) % 360.0f;
        if (agl > 180.0f) {
            agl = 0.0f;
        }
        return agl - 1;
    }
    @EventTarget
    public void onRender(EventRender3D event){
        if (target != null) {
            if (targetEsp.getValue()) {
                Color color = new Color(espColor.getValue().getRGB());
                switch (targetEspMode.getValue()) {
                    case "Smooth" : {
                        double radius = this.radius.getValue();
                        if (boost.getValue().intValue() > 1) {
                            for (int i = 0; i < boost.getValue().intValue(); i++) {
                                RenderUtils.drawTargetCapsule(target, radius, color);
                            }
                        } else {
                            RenderUtils.drawTargetCapsule(target, radius, color);
                        }
                        break;
                    }
                    case "Box" : {
                        RenderUtils.renderESP(target, color, 1);
                        break;
                    }
                }
            }
        }
    }
}