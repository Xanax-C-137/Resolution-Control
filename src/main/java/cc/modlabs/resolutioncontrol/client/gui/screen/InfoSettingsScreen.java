package cc.modlabs.resolutioncontrol.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

public class InfoSettingsScreen extends SettingsScreen {
    private String gpuName;
    private int maxTextureSize;

    public InfoSettingsScreen(@Nullable Screen parent) {
        super(text("settings.info"), parent);
    }

    @Override
    protected void init() {
        super.init();

        String[] gpuInfoSplit = StringUtils.split(GL11.glGetString(GL11.GL_RENDERER));
        this.gpuName = "";
        // Clean GPU string
        for (int i = 1, gpuInfoSplitLength = gpuInfoSplit.length; i < gpuInfoSplitLength; i++) {
            String s = gpuInfoSplit[i];
            if (s.contains("/")) {
                break;
            } else {
                this.gpuName += s + " ";
            }
        }

        this.maxTextureSize = RenderSystem.maxSupportedTextureSize();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        drawLeftAlignedString(context,
                " \u00A78" + text("settings.info.gpu").getString() + " \u00A7r" + gpuName,
                centerX - 75, centerY - 35,
                0x000000);

        drawLeftAlignedString(context,
                " \u00A78" + text("settings.info.maxTextureSize").getString() + " \u00A7r" + maxTextureSize,
                centerX - 75, centerY - 20,
                0x000000);
    }
}
