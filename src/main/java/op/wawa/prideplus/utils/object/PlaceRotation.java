package op.wawa.prideplus.utils.object;

import op.wawa.prideplus.utils.player.ScaffoldUtils;

public record PlaceRotation(ScaffoldUtils.BlockCache blockCache, Rotation rotation) {
    public PlaceRotation copy(ScaffoldUtils.BlockCache placeInfo, Rotation rotation) {
        return new PlaceRotation(placeInfo, rotation);
    }

    public static PlaceRotation copy$default(PlaceRotation var0, ScaffoldUtils.BlockCache var1, Rotation var2, int var3, Object var4) {
        if ((var3 & 1) != 0) {
            var1 = var0.blockCache;
        }
        if ((var3 & 2) != 0) {
            var2 = var0.rotation;
        }
        return var0.copy(var1, var2);
    }

    public String toString() {
        return "PlaceRotation(blockCache=" + this.blockCache + ", rotation=" + this.rotation + ")";
    }

    public boolean equals(Object var1) {
        if (this != var1) {
            if (var1 instanceof PlaceRotation var2) {
                return this.blockCache.equals(var2.blockCache) && this.rotation.equals(var2.rotation);
            }
            return false;
        }
        return true;
    }
}
