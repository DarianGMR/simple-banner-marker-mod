package dariangmr.simplemarker;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = SimpleBannerMarkerMod.MODID, name = SimpleBannerMarkerMod.NAME, version = SimpleBannerMarkerMod.VERSION)
public class SimpleBannerMarkerMod {
    public static final String MODID = "simplemarker";
    public static final String NAME = "Simple Banner Marker";
    public static final String VERSION = "1.0";

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }
}
