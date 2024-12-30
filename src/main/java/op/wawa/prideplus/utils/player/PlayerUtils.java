package op.wawa.prideplus.utils.player;

import op.wawa.prideplus.utils.MinecraftInstance;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class PlayerUtils implements MinecraftInstance {
    public static double calculateBPS(EntityPlayer player) {
        if (player == null) return 0;

        double bps = (Math.hypot(player.posX - player.prevPosX, player.posZ - player.prevPosZ) * mc.timer.timerSpeed) * 20;
        return Math.round(bps * 100.0) / 100.0;
    }
    public static Block block(final double x, final double y, final double z) {
        return mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
    }
    public static boolean canBeSeen(Entity e) {
        Vec3 vec1 = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);

        AxisAlignedBB box = e.getEntityBoundingBox();
        Vec3 vec2 = new Vec3(e.posX, e.posY + (e.getEyeHeight() / 1.32F), e.posZ);
        double minx = e.posX - 0.3;
        double maxx = e.posX + 0.3;
        double miny = e.posY;
        double maxy = e.posY + Math.abs(e.posY - box.maxY);
        double minz = e.posZ - 0.3;
        double maxz = e.posZ + 0.3;
        boolean see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
        if (see)
            return true;
        vec2 = new Vec3(maxx, miny, minz);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
        if (see)
            return true;
        vec2 = new Vec3(minx, miny, minz);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;

        if (see)
            return true;
        vec2 = new Vec3(minx, miny, maxz);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
        if (see)
            return true;
        vec2 = new Vec3(maxx, miny, maxz);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
        if (see)
            return true;

        vec2 = new Vec3(maxx, maxy, minz);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;

        if (see)
            return true;
        vec2 = new Vec3(minx, maxy, minz);

        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
        if (see)
            return true;
        vec2 = new Vec3(minx, maxy, maxz - 0.1);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
        if (see)
            return true;
        vec2 = new Vec3(maxx, maxy, maxz);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
        return see;
    }
    public static boolean isMoving() {
        return ((mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F));
    }

    public static double getLastDistance() {
        final double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
        final double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
        return Math.sqrt(xDist * xDist + zDist * zDist);
    }

    public static int getSpeedEffect() {
        return mc.thePlayer.isPotionActive(Potion.moveSpeed) ? mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1 : 0;
    }
}
