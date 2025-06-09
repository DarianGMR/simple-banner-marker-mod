package dariangmr.simplemarker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = SimpleBannerMarkerMod.MODID, name = SimpleBannerMarkerMod.NAME, version = SimpleBannerMarkerMod.VERSION)
public class SimpleBannerMarkerMod {
    public static final String MODID = "simplemarker";
    public static final String NAME = "Simple Banner Marker";
    public static final String VERSION = "1.0";

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        EntityPlayer player = event.getEntityPlayer();
        ItemStack mainHand = player.getHeldItemMainhand();
        ItemStack offHand = player.getHeldItemOffhand();

        // Verificar si tiene mapa en mano principal y estandarte en secundaria
        if (mainHand.getItem() instanceof ItemMap && offHand.getItem() instanceof ItemBanner) {
            MapData mapData = ((ItemMap)mainHand.getItem()).getMapData(mainHand, event.getWorld());

            if (mapData != null) {
                // Obtener el color del estandarte
                int color = ((ItemBanner)offHand.getItem()).getBaseColor(offHand).getColorValue();

                // Crear marcador
                NBTTagCompound markerData = new NBTTagCompound();
                markerData.setInteger("x", (int)player.posX);
                markerData.setInteger("z", (int)player.posZ);
                markerData.setInteger("color", color);

                // Guardar el marcador en el mapa
                if (!mapData.mapDecorations.containsKey("banner_" + player.getName())) {
                    mapData.markDirty();

                    // Abrir GUI para nombrar el marcador
                    if (event.getWorld().isRemote) {
                        Minecraft.getMinecraft().displayGuiScreen(new GuiMarkerName(player, offHand, markerData));
                    }

                    // Consumir el estandarte si no está en creativo
                    if (!player.capabilities.isCreativeMode) {
                        offHand.shrink(1);
                    }

                    event.setCanceled(true);
                } else {
                    player.sendMessage(new TextComponentString("Ya tienes un marcador en este mapa"));
                }
            }
        }
    }
}
