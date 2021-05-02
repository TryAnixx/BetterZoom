package de.tryanixx.betterzoom;

import net.labymod.api.LabyModAddon;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.KeyElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import java.util.List;

public class ZoomService extends LabyModAddon {

    private int key;
    private boolean lastKeyDown;
    private float prevZoom;
    private float zoomFactor = 1;
    private int wheelSig = 0;
    private int hotbarslot;
    private boolean hotbarslotstate;

    @Override
    public void onEnable() {
        api.registerForgeListener(this);
        System.out.println("BetterZoom started");
    }

    @Override
    public void loadConfig() {
        this.key = getConfig().has("key") ? getConfig().get("key").getAsInt() : -1;
    }

    @Override
    protected void fillSettings(List<SettingsElement> list) {
        list.add(new KeyElement("Key", this, new ControlElement.IconData(Material.LEVER), "key", this.key));
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if (this.key == -1 || Minecraft.getMinecraft().currentScreen != null) {
            return;
        }
        if (this.lastKeyDown) {
            Minecraft.getMinecraft().thePlayer.inventory.currentItem = hotbarslot;
            wheelSig = Integer.signum(Mouse.getDWheel());
            if (wheelSig == 1 && zoomFactor <= 4) {
                zoomFactor++;
            } else if (wheelSig == -1 && zoomFactor >= 2) {
                zoomFactor--;
            }
        }

        boolean keyDown = Keyboard.isKeyDown(this.key);
        if (keyDown) {
            if (!this.lastKeyDown) {
                this.lastKeyDown = true;
                this.prevZoom = Minecraft.getMinecraft().gameSettings.getOptionFloatValue(GameSettings.Options.FOV);
            }
            (Minecraft.getMinecraft()).gameSettings.smoothCamera = true;
            Minecraft.getMinecraft().gameSettings.setOptionFloatValue(GameSettings.Options.FOV, 40F / zoomFactor);
            if(!hotbarslotstate) {
                hotbarslot = Minecraft.getMinecraft().thePlayer.inventory.currentItem;
                hotbarslotstate = true;
            }
        } else if (this.lastKeyDown) {
            this.lastKeyDown = false;
            (Minecraft.getMinecraft()).gameSettings.smoothCamera = false;
            Minecraft.getMinecraft().gameSettings.setOptionFloatValue(GameSettings.Options.FOV, prevZoom);
            zoomFactor = 1;
            hotbarslotstate = false;
        }
    }
}
