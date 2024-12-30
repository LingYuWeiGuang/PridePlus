package op.wawa.prideplus.module.impl.fight;

import op.wawa.prideplus.module.Module;
import op.wawa.prideplus.utils.player.PlayerUtils;
import op.wawa.prideplus.utils.player.RotationUtils;
import op.wawa.prideplus.value.values.BooleanValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;

public class Target extends Module {
    public static Target INSTANCE;

    private final BooleanValue player = new BooleanValue("Player",true);
    private final BooleanValue monster = new BooleanValue("Monster",false);
    private final BooleanValue animal = new BooleanValue("Animal",false);
    private final BooleanValue villager = new BooleanValue("Villager",false);
    private final BooleanValue invisibility = new BooleanValue("Invisibility",false);

    public Target() {
        super("Target", Category.FIGHT);
        this.setCanEnable(false);
        INSTANCE = this;
    }

    public boolean isTarget(Entity entity) {
        if (!(entity instanceof EntityLivingBase entityLivingBase)) return false;

        if (entityLivingBase.isEntityAlive()) {
            if (entityLivingBase == mc.thePlayer) {
                return false;
            }

            // TODO Teams, Friends

            if (entity instanceof EntityPlayer entityPlayer) {
                if (AntiBot.isBot(entityPlayer) || AntiBot.isNPC(entityPlayer)) return false;
            }

            if (!invisibility.getValue() && entityLivingBase.isInvisible()) {
                return false;
            }

            return switch (entityLivingBase) {
                case EntityPlayer entityPlayer when player.getValue() -> true;
                case EntityMob entityMob when monster.getValue() -> true;
                case EntityAnimal entityAnimal when animal.getValue() -> true;
                default -> villager.getValue() && entityLivingBase instanceof EntityVillager;
            };

        }

        return false;
    }
}
