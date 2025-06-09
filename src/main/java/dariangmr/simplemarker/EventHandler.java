package dariangmr.simplemarker;

import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class EventHandler {

    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        EntityPlayer player = event.getEntityPlayer();
        ItemStack mainhand = player.getHeldItemMainhand();
        ItemStack offhand = player.getHeldItemOffhand();

        // Verificar si el jugador tiene un mapa en una mano y un estandarte en la otra
        if ((mainhand.getItem() instanceof ItemMap && offhand.getItem() instanceof ItemBanner) ||
                (mainhand.getItem() instanceof ItemBanner && offhand.getItem() instanceof ItemMap)) {

            ItemStack mapStack = mainhand.getItem() instanceof ItemMap ? mainhand : offhand;
            ItemStack bannerStack = mainhand.getItem() instanceof ItemBanner ? mainhand : offhand;

            // Obtener los datos del mapa
            MapData mapData = ((ItemMap)mapStack.getItem()).getMapData(mapStack, player.world);

            if (mapData != null) {
                if (player.world.isRemote) {
                    // Solo en el cliente, mostrar la GUI
                    NBTTagCompound markerData = new NBTTagCompound();
                    markerData.setInteger("color", bannerStack.getMetadata());
                    markerData.setInteger("dimension", player.dimension);

                    // Convertir coordenadas del mundo a coordenadas del mapa (128x128)
                    int x = (int)((player.posX - mapData.xCenter) / mapData.scale);
                    int z = (int)((player.posZ - mapData.zCenter) / mapData.scale);

                    // Asegurarse de que las coordenadas estén dentro del rango del mapa
                    x = Math.min(127, Math.max(-128, x));
                    z = Math.min(127, Math.max(-128, z));

                    markerData.setInteger("x", x);
                    markerData.setInteger("z", z);

                    // Obtener el ID del mapa desde el ItemStack
                    if (mapStack.hasTagCompound() && mapStack.getTagCompound().hasKey("map", 99)) {
                        int mapId = mapStack.getTagCompound().getInteger("map");
                        markerData.setInteger("mapId", mapId);
                    }

                    Minecraft.getMinecraft().displayGuiScreen(new GuiMarkerName(player, bannerStack, markerData));
                }

                // Prevenir el comportamiento por defecto
                event.setCanceled(true);
            }
        }
    }
}
