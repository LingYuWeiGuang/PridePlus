package op.wawa.prideplus.utils.object;

import net.minecraft.util.BlockPos;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum Direction {
    POSITIVE_X,
    NEGATIVE_X,
    POSITIVE_Z,
    NEGATIVE_Z;

    @Contract("_ -> new")
    public @NotNull BlockPos offsetWith(@NotNull BlockPos blockPos) {
        return switch (this) {
            case POSITIVE_X -> new BlockPos(blockPos.getX() + 1, blockPos.getY(), blockPos.getZ());
            case NEGATIVE_X -> new BlockPos(blockPos.getX() - 1, blockPos.getY(), blockPos.getZ());
            case POSITIVE_Z -> new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ() + 1);
            case NEGATIVE_Z -> new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ() - 1);
        };
    }
}