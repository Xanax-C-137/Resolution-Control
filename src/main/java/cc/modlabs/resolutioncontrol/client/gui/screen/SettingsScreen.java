package cc.modlabs.resolutioncontrol.client.gui.screen;

import cc.modlabs.resolutioncontrol.ResolutionControlMod;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

@SuppressWarnings("StaticInitializerReferencesSubClass")
public class SettingsScreen extends Screen {
    protected static final Identifier WINDOW_TEXTURE = ResolutionControlMod.identifier("textures/gui/settings.png");

    protected static Text text(String path, Object... args) {
        return Text.translatable(ResolutionControlMod.MOD_ID + "." + path, args);
    }

    protected static final int containerWidth = 192;
    protected static final int containerHeight = 128;

    protected static final Map<Class<? extends SettingsScreen>,
            Function<Screen, SettingsScreen>> screensSupplierList;

    static {
        screensSupplierList = new LinkedHashMap<>();
        screensSupplierList.put(MainSettingsScreen.class, MainSettingsScreen::new);
        screensSupplierList.put(ScreenshotSettingsScreen.class, ScreenshotSettingsScreen::new);
        screensSupplierList.put(InfoSettingsScreen.class, InfoSettingsScreen::new);
    }

    protected final ResolutionControlMod mod = ResolutionControlMod.getInstance();

    @Nullable
    protected final Screen parent;

    protected int centerX;
    protected int centerY;
    protected int startX;
    protected int startY;

    protected Map<Class<? extends SettingsScreen>, ButtonWidget> menuButtons;

    protected ButtonWidget doneButton;

    protected SettingsScreen(Text title, @Nullable Screen parent) {
        super(title);
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        centerX = width / 2;
        centerY = height / 2;
        startX = centerX - containerWidth / 2;
        startY = centerY - containerHeight / 2;

        // Init menu buttons
        menuButtons = new LinkedHashMap<>();
        final int menuButtonWidth = 80;
        final int menuButtonHeight = 20;
        MutableInt o = new MutableInt();

        screensSupplierList.forEach((c, constructor) -> {
            SettingsScreen r = constructor.apply(this.parent);
            ButtonWidget b = new ButtonWidget.Builder(r.getTitle(), button -> client.setScreen(constructor.apply(this.parent))).dimensions(startX - menuButtonWidth - 20, startY + o.getValue(),
                    menuButtonWidth, menuButtonHeight).build();

            if (this.getClass().equals(c))
                b.active = false;

            menuButtons.put(c, b);
            o.add(25);
        });

        menuButtons.values().forEach(this::addDrawableChild);

        doneButton = new ButtonWidget.Builder(Text.translatable("gui.done"), button -> {
            applySettingsAndCleanup();
            client.setScreen(this.parent);
        }).dimensions(centerX + 15, startY + containerHeight - 30, 60, 20).build();
        this.addDrawableChild(doneButton);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (client.world == null) {
            renderInGameBackground(context);
        }

        RenderSystem.disableDepthTest();
        RenderSystem.setShaderTexture(0, WINDOW_TEXTURE);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        int textureWidth = 256;
        int textureHeight = 192;
        context.drawTexture(
                WINDOW_TEXTURE,
                centerX - textureWidth / 2, centerY - textureHeight / 2,
                0, 0,
                textureWidth, textureHeight
        );

        super.render(context, mouseX, mouseY, delta);

        drawLeftAlignedString(context, "\u00a7r" + getTitle().getString(),
                centerX + 15, startY + 10, 0x000000);

        drawRightAlignedString(context, text("settings.title").getString(),
                centerX + 5, startY + 10, 0x404040);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if ((ResolutionControlMod.getInstance().getSettingsKey().matchesKey(keyCode, scanCode))) {
            this.applySettingsAndCleanup();
            this.client.setScreen(this.parent);
            this.client.mouse.lockCursor();
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public void close() {
        this.applySettingsAndCleanup();
        super.close();
    }

    protected void applySettingsAndCleanup() {
        mod.saveSettings();
        mod.setLastSettingsScreen(this.getClass());
    }

    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    protected void drawCenteredString(DrawContext context, String text, int x, int y, int color) {
        context.drawText(textRenderer, text, x - textRenderer.getWidth(text) / 2, y, color, false);
    }

    protected void drawLeftAlignedString(DrawContext context, String text, int x, int y, int color) {
        context.drawText(textRenderer, text, x, y, color, false);
    }

    protected void drawRightAlignedString(DrawContext context, String text, int x, int y, int color) {
        context.drawText(textRenderer, text, x - textRenderer.getWidth(text), y, color, false);
    }

    public static SettingsScreen getScreen(Class<? extends SettingsScreen> screenClass) {
        return screensSupplierList.get(screenClass).apply(null);
    }

    protected static Text getStateText(boolean enabled) {
        return enabled ? Text.translatable("addServer.resourcePack.enabled")
                : Text.translatable("addServer.resourcePack.disabled");
    }
}
