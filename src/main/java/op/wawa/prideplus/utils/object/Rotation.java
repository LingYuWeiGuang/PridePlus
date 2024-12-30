package op.wawa.prideplus.utils.object;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.Entity;

@Setter
@Getter
public class Rotation {

    public float yaw, pitch;

    public Rotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Rotation(Entity entity) {
        this.yaw = entity.rotationYaw;
        this.pitch = entity.rotationPitch;
    }

    public void add(Rotation rotation) {
        this.yaw += rotation.getYaw();
        this.pitch += rotation.getPitch();
    }

    public Rotation createAdded(Rotation rotation) {
        return new Rotation(this.yaw + rotation.getYaw(), this.pitch + rotation.getPitch());
    }

}