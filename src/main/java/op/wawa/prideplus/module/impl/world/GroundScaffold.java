package op.wawa.prideplus.module.impl.world;

import op.wawa.prideplus.event.annotations.EventTarget;
import op.wawa.prideplus.event.events.EventMotion;
import op.wawa.prideplus.event.events.EventPacketSend;
import op.wawa.prideplus.event.events.EventTick;
import op.wawa.prideplus.module.Module;
import op.wawa.prideplus.utils.player.PlayerUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;

import java.util.ArrayList;
import java.util.List;

public class GroundScaffold extends Module {

    private final List<Packet<?>> packets = new ArrayList<>();

    public GroundScaffold() {
        super("GroundScaffold", Category.WORLD);
    }

    public void onDisable() {
        if (!packets.isEmpty()) {
            packets.forEach(mc.getNetHandler().getNetworkManager()::sendPacketNoEvent);
            packets.clear();
        }

        mc.thePlayer.motionX *= .8;
        mc.thePlayer.motionZ *= .8;
    }

    @EventTarget
    public void onUpdate(EventMotion event) {
        if (event.isPost()) return;
        if (PlayerUtils.getLastDistance() > .22 && mc.thePlayer.ticksExisted % 2 == 0 && mc.thePlayer.onGround) {
            final double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
            final double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
            final double multiplier = .5 - PlayerUtils.getSpeedEffect() * .05;
            final double random = Math.random() * .007;
            event.setX(event.getX() - xDist * (multiplier + random));
            event.setZ(event.getZ() - zDist * (multiplier + random));
            event.setY(event.getY() + .00625 + Math.random() * 1E-3);
        }
    }

    @EventTarget
    public void onMotion(EventTick event) { // you can also put this code into the TickEvent.
        if (mc.thePlayer.onGround) {
            mc.thePlayer.motionX *= 1.114 - PlayerUtils.getSpeedEffect() * .01 - Math.random() * 1E-4;
            mc.thePlayer.motionZ *= 1.114 - PlayerUtils.getSpeedEffect() * .01 - Math.random() * 1E-4;
        }

        if (mc.thePlayer.ticksExisted % 2 != 0 && !packets.isEmpty()) {
            packets.forEach(mc.getNetHandler().getNetworkManager()::sendPacketNoEvent);
            packets.clear();
        }
    }

    @EventTarget
    public void onPacket(EventPacketSend event) {
        if (mc.thePlayer == null) return;
        if (mc.thePlayer.onGround && mc.thePlayer.ticksExisted % 2 == 0
                && (event.getPacket() instanceof C08PacketPlayerBlockPlacement
                || event.getPacket() instanceof C0APacketAnimation
                || event.getPacket() instanceof C09PacketHeldItemChange)) {
            packets.add(event.getPacket());
            event.setCancelled(true);
        }
    }
}