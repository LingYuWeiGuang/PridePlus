package op.wawa.prideplus.module.impl.world;

import java.awt.Color;

import op.wawa.prideplus.Pride;
import op.wawa.prideplus.event.annotations.EventTarget;
import op.wawa.prideplus.event.events.*;
import op.wawa.prideplus.module.Module;
import op.wawa.prideplus.utils.misc.MathUtils;
import op.wawa.prideplus.utils.object.TimerUtils;
import op.wawa.prideplus.utils.player.*;
import op.wawa.prideplus.value.values.BooleanValue;
import op.wawa.prideplus.value.values.ModeValue;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;


public class Scaffold extends Module {
    public static Scaffold INSTANCE;

    private final BooleanValue rotations = new BooleanValue("Rotations", true);
    private final ModeValue rotationMode = new ModeValue("Rotation Mode", "Watchdog", "Watchdog", "NCP", "Backwards");
    public static ModeValue sprintMode = new ModeValue("Sprint Mode", "Vanilla", "Vanilla", "Watchdog", "Legit", "None");
    public static ModeValue watchdogSprint = new ModeValue("Watchdog Mode", "Jump SameY", "Jump SameY").setVisible(() -> sprintMode.isCurrentMode("Watchdog"));
    public static ModeValue towerMode = new ModeValue("Tower Mode", "Watchdog", "Vanilla", "NCP", "Legit", "Watchdog");
    private ScaffoldUtils.BlockCache blockCache, lastBlockCache;
    private final TimerUtils delayTimer = new TimerUtils();
    private float[] cachedRots = new float[2];
    public static double lastGroundY = 0;
    public static double moveTicks = 0;
    public static double startY = 0;
    public static double keepYCoord;
    boolean targetCalculated = false;
    boolean onGround = false;
    private int tickCounter;
    private boolean pre;
    private float y;
    private int slot;
    private int ticks;
    private int ticks2;
    private float angle;
    double targetZ = 0;
    float yaw = 0;

    public Scaffold() {
        super("Scaffold", Category.WORLD);
        INSTANCE = this;
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer != null) {
            if (slot != mc.thePlayer.inventory.currentItem)
                PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        }
        startY = 0;
        lastGroundY = 0;
        mc.timer.timerSpeed = 1;
        mc.gameSettings.keyBindSneak.pressed = false;
        super.onDisable();
    }

    @Override
    public void onEnable() {
        lastBlockCache = null;
        if (mc.thePlayer != null) {
            slot = mc.thePlayer.inventory.currentItem;
            if (mc.thePlayer.isSprinting() && sprintMode.isNotCurrentMode("None") && sprintMode.isNotCurrentMode("Vanilla") && sprintMode.isNotCurrentMode("Legit")) {
                PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
            }
        }

        assert mc.thePlayer != null;
        targetZ = mc.thePlayer.posZ;
        tickCounter = 0;
        angle = mc.thePlayer.rotationYaw;

        if (!mc.thePlayer.onGround) {
            ticks = 100;
        }
        y = 80;
        super.onEnable();
    }

    @EventTarget
    public void onRender2D(EventRender2D event) {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        String blocks = String.valueOf(this.getBlocksAmount());
        float percentage = Math.min(1.0f, (float)this.getBlocksAmount() / 128.0f);
        mc.fontRendererObj.drawOutlinedString(blocks, (float)scaledResolution.getScaledWidth() / 2.0f - (float)
                mc.fontRendererObj.getStringWidth(blocks) / 2.0f, (float)scaledResolution.getScaledHeight() / 2.0f - 25.0f, Color.WHITE.getRGB(), true);
    }

    public int getBlocksAmount() {
        int amount = 0;
        for (int i = 36; i < 45; ++i) {
            ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (itemStack == null || !(itemStack.getItem() instanceof ItemBlock)) continue;
            Block block = ((ItemBlock)itemStack.getItem()).getBlock();
            if (mc.thePlayer.getHeldItem() != itemStack && InventoryUtils.invalidBlocks.contains(block)) continue;
            amount += itemStack.stackSize;
        }
        return amount;
    }


    @EventTarget
    public void onJump(EventJump event) {
        if (mc.gameSettings.keyBindJump.pressed && (towerMode.isCurrentMode("Watchdog") || towerMode.isCurrentMode("Test")) && MovementUtils.isMoving())
            event.setCancelled(true);
    }

    @Override
    protected String getModuleTag() {
        return sprintMode.getValue();
    }

    @EventTarget
    public void onUpdate(EventMotion e) {
        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            keepYCoord = mc.thePlayer.posY - 1;
            startY = mc.thePlayer.posY;
        }

        if (mc.thePlayer.onGround)
            lastGroundY = mc.thePlayer.posY;
        if (startY == 0) {
            if (mc.thePlayer.onGround)
                startY = mc.thePlayer.posY;
            else
                startY = keepYCoord;
        }

        if (sprintMode.isCurrentMode("Legit")) {
            if (Math.abs(MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw) - MathHelper.wrapAngleTo180_float(yaw)) > 90) {
                mc.gameSettings.keyBindSprint.pressed = false;
                mc.thePlayer.setSprinting(false);
            } else {
                mc.gameSettings.keyBindSprint.pressed = true;
                mc.thePlayer.setSprinting(true);
            }
        } else if (sprintMode.isCurrentMode("None")) {
            mc.gameSettings.keyBindSprint.pressed = false;
            mc.thePlayer.setSprinting(false);
        } else {
            mc.gameSettings.keyBindSprint.pressed = true;
            mc.thePlayer.setSprinting(true);
        }
        if (sprintMode.isCurrentMode("Watchdog") && !mc.gameSettings.keyBindJump.pressed) {
            if (watchdogSprint.isCurrentMode("Jump SameY")) {
                if (mc.thePlayer.onGround && MovementUtils.isMoving()) {
                    mc.thePlayer.jump();
                    MovementUtils.strafe(0.48);
                }
            }
        }

        if (towerMode.isCurrentMode("Watchdog")) {
            if (mc.gameSettings.keyBindJump.pressed) {
                if (mc.thePlayer.onGround) onGround = true;
            } else onGround = false;

        }
        // Rotations
        if (rotations.getValue()) {
            float[] rotations = new float[]{0, 0};
            switch (rotationMode.getValue()) {
                case "Watchdog":
                    rotations = new float[] {MovementUtils.getMoveYaw(mc.thePlayer.rotationYaw) - 180f, y};
                    if (mc.thePlayer.onGround && !MovementUtils.isMoving()) {
                        if ((blockCache = ScaffoldUtils.getBlockInfo()) == null) {
                            blockCache = lastBlockCache;
                        }

                        if (this.blockCache != null && (mc.thePlayer.ticksExisted % 3 == 0 || mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, ScaffoldUtils.getYLevel(), mc.thePlayer.posZ)).getBlock() == Blocks.air)) {
                            this.cachedRots = RotationUtils.getRotations(this.blockCache.getPosition(), this.blockCache.getFacing());
                        }
                        rotations = cachedRots;
                        yaw = rotations[0];
                        Pride.INSTANCE.rotationManager.setRotations(rotations, 360f, false);
                        break;
                    }
                    Pride.INSTANCE.rotationManager.setRotations(rotations, 360f, false);
                    break;
                case "NCP":
                    if ((blockCache = ScaffoldUtils.getBlockInfo()) == null) {
                        blockCache = lastBlockCache;
                    }
                    if (blockCache != null && (mc.thePlayer.ticksExisted % 3 == 0
                            || mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, ScaffoldUtils.getYLevel(), mc.thePlayer.posZ)).getBlock() == Blocks.air)) {
                        cachedRots = RotationUtils.getRotations(blockCache.getPosition(), blockCache.getFacing());
                    }
                    rotations = cachedRots;
                    yaw = rotations[0];
                    Pride.INSTANCE.rotationManager.setRotations(rotations, 360f, false);
                    break;
                case "Backwards":
                    rotations = new float[]{MovementUtils.getMoveYaw(mc.thePlayer.rotationYaw) - 180, 77};
                    yaw = rotations[0];
                    Pride.INSTANCE.rotationManager.setRotations(rotations, 360f, false);
                    break;
            }
            yaw = rotations[0];
        }

        // Save ground Y level for keep Y
        if (mc.thePlayer.onGround) {
            keepYCoord = Math.floor(mc.thePlayer.posY - 1.0);
        }

        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            switch (towerMode.getValue()) {
                case "Vanilla":
                    mc.thePlayer.motionY = 0.42f;
                    break;
                case "Watchdog": {
                    if (e.isPre() && onGround) {
                        if (!mc.gameSettings.keyBindJump.isKeyDown()) {
                            angle = mc.thePlayer.rotationYaw;
                            ticks = 100;
                            return;
                        }
                        tickCounter++;
                        ticks++;
                        if (tickCounter >= 35) {
                            tickCounter = 0; // Reset the counter
                        }
                        if (mc.thePlayer.onGround) {
                            ticks = 0;
                        }
                        if (!MovementUtils.isMoving()) {
                            if (!targetCalculated) {
                                // Calculate the targetZ position only once
                                targetZ = Math.floor(mc.thePlayer.posZ) + 0.99999999999998;
                                targetCalculated = true;
                            }
                            ticks2++;

                            if (Math.abs(mc.thePlayer.posY) >= 1) {
                                if (ticks2 == 1) {
                                    // Move to the middle position
                                    MovementUtils.stop();
                                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY,
                                            (mc.thePlayer.posZ + targetZ) / 2);
                                } else if (ticks2 == 2) {
                                    // Move to the final target position after 2 ticks
                                    MovementUtils.stop();
                                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY, targetZ);
//                                        doSidePlacement();
                                    ticks2 = 0; // Reset the tick counter after reaching the final position
                                    targetCalculated = false; // Reset the flag for the next cycle
                                }
                            } else {
                                // Reset ticks2 if the Y position condition is not met
                                ticks2 = 0;
                                targetCalculated = false; // Reset the flag if the condition is not met
                            }
                        }

                        float step = ticks == 1 ? 90 : 0;

                        if (MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw - angle) < step) {
                            angle = mc.thePlayer.rotationYaw;
                        } else if (MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw - angle) < 0) {
                            angle -= step;
                        } else if (MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw - angle) > 0) {
                            angle += step;
                        }

                        mc.thePlayer.movementYaw = angle;

                        if (tickCounter <= 20) {
                            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                                MovementUtils.strafe();

                                switch (ticks) {
                                    case 0:
                                        if (mc.thePlayer.posY % 1 == 0) {
                                            e.setGround(true);
                                            if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                                                mc.thePlayer.motionX *= .998765;
                                                mc.thePlayer.motionZ *= .998765;
                                            } else {

                                            }
                                        }
                                        mc.thePlayer.motionX *= .985765;
                                        mc.thePlayer.motionZ *= .985765;
                                        mc.thePlayer.motionY = 0.42f;
                                        break;

                                    case 1:
                                        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                                            mc.thePlayer.motionX *= .985765;
                                            mc.thePlayer.motionZ *= .985765;
                                        } else {

                                        }
                                        mc.thePlayer.motionY = 0.33;
                                        break;

                                    case 2:
                                        mc.thePlayer.motionX *= .985765;
                                        mc.thePlayer.motionZ *= .985765;
                                        mc.thePlayer.motionY = 1 - mc.thePlayer.posY % 1;
                                        break;
                                }
                            }
                        } else {
                            mc.thePlayer.motionX *= .985765;
                            mc.thePlayer.motionZ *= .985765;
                        }

                        if (ticks == 2) ticks = -1;
                    }
                }
                break;
                case "NCP":
                    if (!MovementUtils.isMoving() || MovementUtils.getSpeed() < 0.16) {
                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.motionY = 0.42;
                        } else if (mc.thePlayer.motionY < 0.23) {
                            mc.thePlayer.setPosition(mc.thePlayer.posX, (int) mc.thePlayer.posY, mc.thePlayer.posZ);
                            mc.thePlayer.motionY = 0.42;
                        }
                    }
                    break;
            }
        }

        // Setting Block Cache
        blockCache = ScaffoldUtils.getBlockInfo();
        if (blockCache != null) {
            lastBlockCache = ScaffoldUtils.getBlockInfo();
        } else {
            return;
        }

        if (mc.thePlayer.ticksExisted % 4 == 0) {
            pre = true;
        }
    }

    @EventTarget
    public void onTick(EventTick event) {
        if (mc.thePlayer == null) return;

        if (MovementUtils.isMoving()) moveTicks++;
        else moveTicks = 0;
    }

    private boolean place() {
        int slot = ScaffoldUtils.getBlockSlot();
        if (blockCache == null || lastBlockCache == null || slot == -1) return false;

        if (this.slot != slot) {
            this.slot = slot;

            PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(this.slot));
        }

        boolean placed = false;
        if (delayTimer.hasTimeElapsed(1000)) {
            if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld,
                    mc.thePlayer.inventory.getStackInSlot(this.slot),
                    lastBlockCache.getPosition(), lastBlockCache.getFacing(),
                    ScaffoldUtils.getHypixelVec3(lastBlockCache))) {
                placed = true;
                y = (float) MathUtils.getRandomInRange(79.5f, 83.5f);
                PacketUtils.sendPacket(new C0APacketAnimation());
            }
            blockCache = null;
        }
        return placed;
    }

    @EventTarget
    public void onBlockPlace(EventPlace event) {
        place();
    }

    @EventTarget
    public void onPacketSendEvent(EventPacketSend e) {
        if (e.getPacket() instanceof C0BPacketEntityAction
                && ((C0BPacketEntityAction) e.getPacket()).getAction() == C0BPacketEntityAction.Action.START_SPRINTING
                && sprintMode.isNotCurrentMode("None") && sprintMode.isNotCurrentMode("Vanilla") && sprintMode.isNotCurrentMode("Legit")) {
            e.setCancelled(true);
        }
        if (e.getPacket() instanceof C09PacketHeldItemChange) {
            e.setCancelled(true);
        }

        if (e.getPacket() instanceof C08PacketPlayerBlockPlacement c08PacketPlacement) {
            c08PacketPlacement.setStack(mc.thePlayer.inventory.getStackInSlot(slot));
        }

    }
}