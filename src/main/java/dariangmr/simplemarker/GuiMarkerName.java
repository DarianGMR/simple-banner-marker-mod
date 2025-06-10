package dariangmr.simplemarker;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.io.IOException;

@SideOnly(Side.CLIENT)
public class GuiMarkerName extends GuiScreen {
    private GuiTextField nameField;
    private final EntityPlayer player;
    private final ItemStack banner;
    private final NBTTagCompound markerData;
    private GuiButton acceptButton;
    private GuiButton cancelButton;

    public GuiMarkerName(EntityPlayer player, ItemStack banner, NBTTagCompound markerData) {
        this.player = player;
        this.banner = banner;
        this.markerData = markerData;
    }

    @Override
    public void initGui() {
        // Campo de texto
        this.nameField = new GuiTextField(0, this.fontRenderer,
                this.width / 2 - 100, this.height / 2 - 10, 200, 20);
        this.nameField.setFocused(true);
        this.nameField.setMaxStringLength(32);

        // Botones
        this.buttonList.clear();
        this.acceptButton = new GuiButton(1, this.width / 2 - 105, this.height / 2 + 20, 100, 20, "Aceptar");
        this.cancelButton = new GuiButton(2, this.width / 2 + 5, this.height / 2 + 20, 100, 20, "Cancelar");
        this.buttonList.add(this.acceptButton);
        this.buttonList.add(this.cancelButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, "Nombre del Marcador",
                this.width / 2, this.height / 2 - 30, 0xFFFFFF);
        this.nameField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) { // ESC
            actionCancel();
        } else if (keyCode == 28) { // Enter
            actionAccept();
        } else {
            this.nameField.textboxKeyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 1) { // Aceptar
            actionAccept();
        } else if (button.id == 2) { // Cancelar
            actionCancel();
        }
    }

    private void actionAccept() {
        String name = this.nameField.getText();
        if (!name.isEmpty()) {
            ItemStack mapStack = null;
            if (player.getHeldItemMainhand().getItem() instanceof ItemMap) {
                mapStack = player.getHeldItemMainhand();
            } else if (player.getHeldItemOffhand().getItem() instanceof ItemMap) {
                mapStack = player.getHeldItemOffhand();
            }

            if (mapStack != null) {
                MapData mapData = ((ItemMap)mapStack.getItem()).getMapData(mapStack, player.world);
                if (mapData != null) {
                    // Crear identificador único para el marcador
                    String id = "banner_" + player.getName();

                    // Crear el NBT para el marcador
                    NBTTagCompound decorationNBT = new NBTTagCompound();
                    decorationNBT.setString("id", id);
                    decorationNBT.setByte("type", (byte)1); // Tipo BANNER = 1 en 1.12.2
                    decorationNBT.setString("name", name); // Nombre del marcador
                    decorationNBT.setByte("x", (byte)markerData.getInteger("x"));
                    decorationNBT.setByte("z", (byte)markerData.getInteger("z"));
                    decorationNBT.setByte("rot", (byte)0);

                    // Agregar el marcador al mapa
                    mapData.mapDecorations.put(id, new MapData.MapInfo(decorationNBT));

                    // Marcar el mapa como modificado
                    mapData.markDirty();

                    // Enviar mensaje de éxito al jugador
                    player.sendMessage(new TextComponentString("§a¡Marcador '" + name + "' creado exitosamente!"));

                    // Consumir el estandarte si no está en modo creativo
                    if (!player.capabilities.isCreativeMode) {
                        ItemStack bannerStack = null;
                        if (player.getHeldItemMainhand().getItem() instanceof ItemBanner) {
                            bannerStack = player.getHeldItemMainhand();
                        } else if (player.getHeldItemOffhand().getItem() instanceof ItemBanner) {
                            bannerStack = player.getHeldItemOffhand();
                        }

                        if (bannerStack != null) {
                            bannerStack.shrink(1);
                        }
                    }
                }
            }
        } else {
            player.sendMessage(new TextComponentString("§cDebes ingresar un nombre para el marcador"));
            return;
        }

        this.mc.displayGuiScreen(null);
    }

    private void actionCancel() {
        this.mc.displayGuiScreen(null);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.nameField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }
}
