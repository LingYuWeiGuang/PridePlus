package op.wawa.prideplus.utils.player;

import op.wawa.prideplus.utils.MinecraftInstance;
import op.wawa.prideplus.utils.misc.MathUtils;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;

public class PacketUtils implements MinecraftInstance {

    public static void sendPacket(Packet<?> packet) {
        if (mc.thePlayer != null) {
            mc.getNetHandler().getNetworkManager().sendPacket(packet);
        }
    }
    public static void sendPacketNoEvent(Packet<?> packet) {
        if (mc.thePlayer != null) {
            mc.getNetHandler().getNetworkManager().sendPacketNoEvent(packet);
        }
    }

    public static boolean isPacketValid(final Packet<?> packet) {
        return !(packet instanceof C00PacketLoginStart) && !(packet instanceof C00Handshake) && !(packet instanceof C00PacketServerQuery) && !(packet instanceof C01PacketPing);
    }

    public static void sendPacketC0F() {
        // if (!Disabler.getGrimPost()) {
            sendPacket(new C0FPacketConfirmTransaction(MathUtils.getRandom(102, 1000024123), (short) MathUtils.getRandom(102, 1000024123), true));
        // }

    }
}