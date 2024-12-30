package op.wawa.prideplus.module.impl.move;

import op.wawa.prideplus.Pride;
import op.wawa.prideplus.event.annotations.EventTarget;
import op.wawa.prideplus.event.events.*;
import op.wawa.prideplus.module.Module;
import op.wawa.prideplus.utils.player.BlockUtils;
import op.wawa.prideplus.utils.player.PacketUtils;
import op.wawa.prideplus.value.values.ModeValue;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.types.VarIntType;
import com.viaversion.viaversion.protocols.v1_8to1_9.Protocol1_8To1_9;
import io.netty.buffer.Unpooled;
import net.minecraft.item.*;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;

public class NoSlow extends Module {
    public static NoSlow INSTANCE;

    private final ModeValue mode = new ModeValue("Mode", "Hypixel", "Vanilla", "Hypixel", "GrimAC");

    public NoSlow() {
        super("NoSlow", Category.MOVE);
        INSTANCE = this;
    }

    @EventTarget
    public void onSlowDownEvent(EventSlowDown event) {
        if (mode.isCurrentMode("GrimAC") && mc.thePlayer.getHeldItem().getItem() instanceof ItemFood) return;

        event.setCancelled(true);
    }

    @Override
    protected String getModuleTag() {
        return mode.getValue();
    }

    @EventTarget
    public void onMotionEvent(EventMotion e) {
        if (mc.thePlayer == null) return;

        switch (this.mode.getValue()) {
            case "Hypixel":
                if (mc.thePlayer.getHeldItem() == null) {
                    return;

                } else if (mc.thePlayer.isUsingItem()) {
                    if (mc.thePlayer.getHeldItem().getItem() instanceof ItemFood) {
                        PacketUtils.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), EnumFacing.UP.getIndex(), null, 0.0F, 0.0F, 0.0F));

                    } else if (mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
                        PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
                        PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));

                    }
                }
                break;
            case "GrimAC": {
                boolean anti = true;
                MovingObjectPosition movingObjectPosition = mc.objectMouseOver;
                if (movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    if (mc.thePlayer.getHeldItem().getItem() instanceof ItemFood && BlockUtils.isValidBlock(movingObjectPosition.getBlockPos())) {
                        anti = false;
                    }
                }
                if (anti) {
                    if (mc.thePlayer.getHeldItem().getItem() instanceof ItemBow && mc.thePlayer.isUsingItem()){
                        mc.getNetHandler().sendPacket(new C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + 1) % 9 ));
                        mc.getNetHandler().sendPacket(new C17PacketCustomPayload(Pride.NAME, new PacketBuffer(Unpooled.buffer())));
                        mc.getNetHandler().sendPacket(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                    }
                    if (isBlocking()) {
                        mc.getNetHandler().sendPacket(new C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + 1) % 9 ));
                        mc.getNetHandler().sendPacket(new C17PacketCustomPayload(Pride.NAME, new PacketBuffer(Unpooled.buffer())));
                        mc.getNetHandler().sendPacket(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                    }
                }

                if (e.isPost()) {
                    if (movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                        if (mc.thePlayer.getHeldItem().getItem() instanceof ItemFood && BlockUtils.isValidBlock(movingObjectPosition.getBlockPos())) {
                            anti = false;
                        }
                    }
                    if (anti) {
                        if (isBlocking()) {
                            PacketUtils.sendPacketC0F();
                            PacketUtils.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                            PacketWrapper useItem = PacketWrapper.create(29, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
                            useItem.write(new VarIntType(), 1);
                            useItem.sendToServer(Protocol1_8To1_9.class, true);
                        }
                    }
                }
            }
        }
    }

    @EventTarget
    public void onLegitUpdateEvent(EventPreUpdate eventMotion) {
        if (mode.isCurrentMode("GrimAC")) {
            if (mc.thePlayer.getHeldItem() != null) {
                boolean anti = true;
                MovingObjectPosition movingObjectPosition = mc.objectMouseOver;
                if (movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    if (mc.thePlayer.getHeldItem().getItem() instanceof ItemFood && BlockUtils.isInteractBlock(mc.theWorld.getBlockState(movingObjectPosition.getBlockPos()).getBlock())) {
                        anti = false;
                    }
                }
                if (mc.thePlayer.getHeldItem().stackSize <= 1) {
                    anti = false;
                }
                if (anti) {
                    if (mc.thePlayer.getHeldItem().getItem() instanceof ItemAppleGold) {
                        mc.gameSettings.keyBindUseItem.pressed = false;
                        if (mc.thePlayer.isUsingItem()) {
                            PacketUtils.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.DROP_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                            PacketUtils.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.getHeldItem(), 0, 0, 0));
                        }
                    }
                }
            }
        }
    }

    @EventTarget
    public void onPacketSendEvent(EventPacketSend event) {
        Packet<?> packet = event.getPacket();
        if (mc.thePlayer == null) return;
        if (mc.thePlayer.getHeldItem() != null) {
            if (mode.isCurrentMode("GrimAC")) {
                if (mc.thePlayer.getHeldItem().getItem() instanceof ItemFood){
                    if (mc.thePlayer.getHeldItem().stackSize <= 1) {
                        return;
                    }
                    boolean anti = true;
                    MovingObjectPosition movingObjectPosition = mc.objectMouseOver;
                    if (movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                        if (mc.thePlayer.getHeldItem().getItem() instanceof ItemFood && BlockUtils.isInteractBlock(mc.theWorld.getBlockState(movingObjectPosition.getBlockPos()).getBlock())) {
                            anti = false;
                        }
                    }
                    if (mc.thePlayer.getHeldItem().stackSize <= 1) {
                        anti = false;
                    }
                    if (anti) {
                        if (packet instanceof C07PacketPlayerDigging){
                            if (((C07PacketPlayerDigging) packet).getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM){
                                event.setCancelled(true);
                            }
                        }
                        if (packet instanceof C08PacketPlayerBlockPlacement){
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    private boolean isBlocking() {
        return ((mc.thePlayer.isBlocking()/* || (KillAura.wasBlocking)*/) && mc.thePlayer.getHeldItem() != null  && (mc.thePlayer.getHeldItem().getItem() instanceof ItemSword));
    }
}
