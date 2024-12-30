package net.minecraft.util;

import op.wawa.prideplus.Pride;
import op.wawa.prideplus.event.events.EventMoveInput;
import net.minecraft.client.settings.GameSettings;

public class MovementInputFromOptions extends MovementInput
{
    private final GameSettings gameSettings;

    public MovementInputFromOptions(GameSettings gameSettingsIn)
    {
        this.gameSettings = gameSettingsIn;
    }

    public void updatePlayerMoveState()
    {
        this.moveStrafe = 0.0F;
        this.moveForward = 0.0F;

        if (this.gameSettings.keyBindForward.isKeyDown())
        {
            ++this.moveForward;
        }

        if (this.gameSettings.keyBindBack.isKeyDown())
        {
            --this.moveForward;
        }

        if (this.gameSettings.keyBindLeft.isKeyDown())
        {
            ++this.moveStrafe;
        }

        if (this.gameSettings.keyBindRight.isKeyDown())
        {
            --this.moveStrafe;
        }

        final EventMoveInput moveInputEvent = new EventMoveInput(moveForward, moveStrafe, this.gameSettings.keyBindJump.isKeyDown(), this.gameSettings.keyBindSneak.isKeyDown(), 0.3D);

        Pride.INSTANCE.eventManager.call(moveInputEvent);

        this.moveForward = moveInputEvent.getForward();
        this.moveStrafe = moveInputEvent.getStrafe();
        this.jump = moveInputEvent.isJump();
        this.sneak = moveInputEvent.isSneak();

        final double sneak = moveInputEvent.getSneakSlowDown();

        if (this.sneak)
        {
            this.moveStrafe = (float)((double)this.moveStrafe * sneak);
            this.moveForward = (float)((double)this.moveForward * sneak);
        }
    }
}
