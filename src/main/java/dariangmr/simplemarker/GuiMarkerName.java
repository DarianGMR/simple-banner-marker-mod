package com.dariangmr.simplemarker;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiMarkerName extends GuiScreen {
    private GuiTextField nameField;
    private final EntityPlayer player;
    private final ItemStack banner;
    private final NBTTagCompound markerData;

    public GuiMarkerName(EntityPlayer player, ItemStack banner, NBTTagCompound markerData) {
        this.player = player;
        this.banner = banner;
        this.markerData = markerData;
    }

    @Override
    public void initGui() {
        this.nameField = new GuiTextField(0, this.fontRenderer,
                this.width / 2 - 100, this.height / 2 - 10, 200, 20);
        this.nameField.setFocused(true);
        this.nameField.setMaxStringLength(32);
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
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 1) { // ESC
            this.mc.displayGuiScreen(null);
        } else if (keyCode == 28) { // Enter
            String name = this.nameField.getText();
            if (!name.isEmpty()) {
                markerData.setString("name", name);
                // Guardar el marcador y cerrar GUI
                this.mc.displayGuiScreen(null);
            }
        }

        this.nameField.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        this.nameField.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
