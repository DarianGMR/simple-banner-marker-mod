package dariangmr.simplemarker;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
            markerData.setString("name", name);

            // Obtener el mapa del jugador
            ItemStack mapStack = null;
            if (player.getHeldItemMainhand().getItem() instanceof ItemMap) {
                mapStack = player.getHeldItemMainhand();
            } else if (player.getHeldItemOffhand().getItem() instanceof ItemMap) {
                mapStack = player.getHeldItemOffhand();
            }

            if (mapStack != null) {
                // Guardar los datos del marcador en el mapa
                if (!mapStack.hasTagCompound()) {
                    mapStack.setTagCompound(new NBTTagCompound());
                }
                NBTTagCompound mapNbt = mapStack.getTagCompound();

                if (!mapNbt.hasKey("Decorations")) {
                    mapNbt.setTag("Decorations", new NBTTagCompound());
                }
                NBTTagCompound decorations = mapNbt.getCompoundTag("Decorations");

                // Crear nueva entrada para el marcador
                NBTTagCompound decoration = new NBTTagCompound();
                decoration.setString("id", "banner_" + System.currentTimeMillis());
                decoration.setByte("type", (byte)markerData.getInteger("color"));
                decoration.setDouble("x", markerData.getDouble("x"));
                decoration.setDouble("z", markerData.getDouble("z"));
                decoration.setString("name", name);

                // Agregar al mapa
                decorations.setTag(name, decoration);

                // Consumir el estandarte
                ItemStack bannerStack = null;
                if (player.getHeldItemMainhand().getItem() instanceof ItemBanner) {
                    bannerStack = player.getHeldItemMainhand();
                    bannerStack.shrink(1);
                } else if (player.getHeldItemOffhand().getItem() instanceof ItemBanner) {
                    bannerStack = player.getHeldItemOffhand();
                    bannerStack.shrink(1);
                }
            }
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
