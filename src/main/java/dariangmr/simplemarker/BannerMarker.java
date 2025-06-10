package dariangmr.simplemarker;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.MapData;
import java.util.HashMap;

public class BannerMarker {
    private final byte x;
    private final byte z;
    private final String name;
    private final int color;

    public BannerMarker(int x, int z, String name, int color) {
        this.x = (byte)x;
        this.z = (byte)z;
        this.name = name;
        this.color = color;
    }

    public void addToMap(MapData mapData, String id) {
        if (mapData.mapDecorations == null) {
            mapData.mapDecorations = new HashMap<>();
        }

        NBTTagCompound markerNBT = new NBTTagCompound();
        markerNBT.setString("id", id);
        markerNBT.setByte("type", (byte)1); // 1 = Banner en Minecraft
        markerNBT.setByte("x", x);
        markerNBT.setByte("z", z);
        markerNBT.setByte("rot", (byte)0);
        markerNBT.setString("name", name);
        markerNBT.setInteger("color", color);

        mapData.markDirty();
    }
}
