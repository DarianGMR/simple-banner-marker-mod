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
                    // Obtener el color del estandarte usando los metadatos
                    markerData.setInteger("color", bannerStack.getMetadata());
                    markerData.setDouble("x", player.posX);
                    markerData.setDouble("z", player.posZ);

                    Minecraft.getMinecraft().displayGuiScreen(new GuiMarkerName(player, bannerStack, markerData));
                }

                // Prevenir el comportamiento por defecto
                event.setCanceled(true);
            }
        }
    }
}
