package op.wawa.prideplus.module.impl.fight;

import op.wawa.prideplus.Pride;
import op.wawa.prideplus.module.Module;
import op.wawa.prideplus.utils.player.MovementUtils;
import op.wawa.prideplus.value.values.BooleanValue;
import op.wawa.prideplus.value.values.ModeValue;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public final class AntiBot extends Module {
    private static final ModeValue mode = new ModeValue("Mode","None", "Watchdog","Matrix","MineLand","MinePlex","Syuu","HeyPixel","None");
    private static final BooleanValue antiNPC = new BooleanValue("AntiNPC",false);

    public static final LinkedList<EntityLivingBase> bots = new LinkedList<>();

    public AntiBot() {
        super("AntiBot",Category.FIGHT);
    }

    public static boolean isBot(EntityPlayer entityPlayer) {
        if (entityPlayer == mc.thePlayer) return true;

        if (mode.isCurrentMode("Watchdog")) {
            if (entityPlayer.onGround && entityPlayer.isInvisible() && (int) entityPlayer.posX == (int) mc.thePlayer.posX && (int) entityPlayer.posZ == (int) mc.thePlayer.posZ && !getTabPlayerList().contains(entityPlayer) && (int) entityPlayer.posY != (int) mc.thePlayer.posY && entityPlayer.ticksExisted < 100) {
                if (!bots.contains(entityPlayer)) {
                    mc.thePlayer.addChatMessage("Detected 1 watchdog");
                    bots.add(entityPlayer);
                    return true;
                }
            }

            if (entityPlayer != mc.thePlayer && entityPlayer.isInvisible() && entityPlayer.ticksExisted < 25) {
                if (!getTabPlayerList().contains(entityPlayer)) {
                    if (!bots.contains(entityPlayer)) {
                        bots.add((entityPlayer));
                        return true;
                    }
                }
            }

            final String displayName = entityPlayer.getDisplayName().getFormattedText();
            if (!entityPlayer.isInvisible() && displayName.startsWith("§r§c") && displayName.endsWith("§r") && mc.getNetHandler().getPlayerInfo(entityPlayer.getUniqueID()).getResponseTime() != 1) {
                if (entityPlayer.posY > mc.thePlayer.posY && (double)mc.thePlayer.getDistanceToEntity(entityPlayer) <= 6.0D && !displayName.startsWith("§r§c[§fYOUTUBE§c]") && !displayName.startsWith("§c[ADMIN]")) {
                    if (!bots.contains(entityPlayer)) {
                        mc.thePlayer.addChatMessage("Detected 1 mod bot!!");
                        bots.add(entityPlayer);
                        return true;
                    }
                }
            }
        } else if (mode.isCurrentMode("Matrix")) {
            if (MovementUtils.getEntitySpeed(entityPlayer) > 25 &&
                    entityPlayer.hurtTime >= 7 &&
                    entityPlayer.inventory.armorInventory[0] != null &&
                    entityPlayer.inventory.armorInventory[1] != null &&
                    entityPlayer.inventory.armorInventory[2] != null &&
                    entityPlayer.inventory.armorInventory[3] != null &&
                    entityPlayer.inventory.getCurrentItem() != null &&
                    entityPlayer.stepHeight == 0.0 &&
                    !entityPlayer.isAirBorne &&
                    !entityPlayer.velocityChanged &&
                    !entityPlayer.isCollidedHorizontally &&
                    entityPlayer.moveForward == 0.0f &&
                    entityPlayer.moveStrafing == 0.0f &&
                    entityPlayer.fallDistance == 0.0 &&
                    entityPlayer.ticksExisted < 25) {
                final boolean contains = bots.contains(entityPlayer);
                if (!contains) mc.thePlayer.addChatMessage("Detected 1 matrix bot!!");
                if (!contains) bots.add(entityPlayer);
                return true;
            }
        } else if (mode.isCurrentMode("MineLand")) {
            if (entityPlayer.inventory.armorInventory[0] != null &&
                    entityPlayer.inventory.armorInventory[1] != null &&
                    entityPlayer.inventory.armorInventory[2] != null &&
                    entityPlayer.inventory.armorInventory[3] != null &&
                    entityPlayer.inventory.getCurrentItem() != null &&
                    mc.thePlayer.getDistanceToEntity(entityPlayer) <= 10.0 &&
                    entityPlayer.ticksExisted <= 5) {
                final boolean contains = bots.contains(entityPlayer);

                if (!contains) mc.thePlayer.addChatMessage("Detected 1 MineLand bot!!");

                if (!contains) bots.add(entityPlayer);

                return true;
            }
        } else if (mode.isCurrentMode("MinePlex")) {
            if (entityPlayer.isInvisible() &&
                    mc.thePlayer.getDistanceToEntity(entityPlayer) <= 6.0 &&
                    entityPlayer.ticksExisted <= 25 &&
                    MovementUtils.getEntitySpeed(entityPlayer) > 20) {
                final boolean contains = bots.contains(entityPlayer);

                if (!contains) {
                    mc.thePlayer.addChatMessage("Detected 1 MinePlex bot!!");
                    bots.add(entityPlayer);
                }
                return true;
            }
        } else if (mode.isCurrentMode("Syuu")) {
            if (entityPlayer.isInvisible() && entityPlayer.getHealth() > 1000.0f && MovementUtils.getEntitySpeed(entityPlayer) > 20) {
                final boolean contains = bots.contains(entityPlayer);
                if (!contains) {
                    mc.thePlayer.addChatMessage("Detected 1 syuu bot!!");
                    bots.add(entityPlayer);
                }
                return true;
            }
        } else if (mode.isCurrentMode("HeyPixel")) {
            if (entityPlayer.getHealth() > 1000.0f || MovementUtils.getEntitySpeed(entityPlayer) > 20 || entityPlayer.capabilities.isFlying) {
                if (!bots.contains(entityPlayer)) {
                    mc.thePlayer.addChatMessage("Detected 1 HeyPixel bot!!");
                    bots.add(entityPlayer);
                }
                return true;
            }
        }

        return false;
    }

    private static List<EntityPlayer> getTabPlayerList() {
        ArrayList<EntityPlayer> list = new ArrayList<>();
        List<NetworkPlayerInfo> players = GuiPlayerTabOverlay.field_175252_a.sortedCopy(mc.getNetHandler().getPlayerInfoMap());

        for (NetworkPlayerInfo o : players) {
            if (o != null) {
                list.add(mc.theWorld.getPlayerEntityByName(o.getGameProfile().getName()));
            }
        }

        return list;
    }

    public static boolean isNPC(EntityLivingBase e) {
        if (!Pride.INSTANCE.moduleManager.getModuleEnable("AntiBot")) {
            return false;
        }

        if (antiNPC.getValue()) {
            final String formattedText = e.getDisplayName().getFormattedText();
            return formattedText.contains("[NPC]") || formattedText.contains("CIT-") || formattedText.startsWith("§7§8NPC §8| ");
        }

        return false;
    }

    @Override
    protected void onEnable() {
        bots.clear();
        super.onEnable();
    }

    @Override
    protected void onDisable() {
        bots.clear();
        super.onDisable();
    }

    @Override
    protected String getModuleTag() {
        return mode.getValue();
    }
}
