package cc.modlabs.resolutioncontrol.client.gui.screen;

import cc.modlabs.resolutioncontrol.util.RCUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.Nullable;

public class ScreenshotSettingsScreen extends SettingsScreen {
    private static final double[] scaleValues = {0.1, 0.25, 0.5, 1.0,
            2.0, 3.0, 4.0, 6.0, 8.0, 16.0};

    private static final Text increaseText = Text.literal("x2");
    private static final Text decreaseText = Text.literal("/2");
    private static final Text resetText = Text.literal("R");

    private TextFieldWidget widthTextField;
    private TextFieldWidget heightTextField;

    private ButtonWidget increaseButton;
    private ButtonWidget decreaseButton;
    private ButtonWidget resetButton;

    private ButtonWidget toggleOverrideSizeButton;
    private ButtonWidget toggleAlwaysAllocatedButton;

    private final int buttonSize = 20;
    private final int textFieldSize = 40;

    private long estimatedSize;

    public ScreenshotSettingsScreen(@Nullable Screen parent) {
        super(text("settings.screenshot"), parent);
    }

    @Override
    protected void init() {
        super.init();

        toggleOverrideSizeButton = new ButtonWidget.Builder(getStateText(mod.getOverrideScreenshotScale()), button -> {
            mod.setOverrideScreenshotScale(!mod.getOverrideScreenshotScale());
            button.setMessage(getStateText(mod.getOverrideScreenshotScale()));
        }).dimensions(centerX + 20, centerY - 40, 50, 20).build();
        this.addDrawableChild(toggleOverrideSizeButton);

        toggleAlwaysAllocatedButton = new ButtonWidget.Builder(getStateText(mod.isScreenshotFramebufferAlwaysAllocated()), button -> {
            mod.setScreenshotFramebufferAlwaysAllocated(!mod.isScreenshotFramebufferAlwaysAllocated());
            button.setMessage(getStateText(mod.isScreenshotFramebufferAlwaysAllocated()));
        }).dimensions(centerX + 20, centerY - 20, 50, 20).build();
        this.addDrawableChild(toggleAlwaysAllocatedButton);

        widthTextField = new TextFieldWidget(client.textRenderer,
                centerX - 85, centerY + 7,
                textFieldSize, buttonSize,
                Text.empty());
        widthTextField.setText(String.valueOf(mod.getScreenshotWidth()));
        this.addDrawableChild(widthTextField);

        heightTextField = new TextFieldWidget(client.textRenderer,
                centerX - 35, centerY + 7,
                textFieldSize, buttonSize,
                Text.empty());
        heightTextField.setText(String.valueOf(mod.getScreenshotHeight()));
        this.addDrawableChild(heightTextField);

        increaseButton = new ButtonWidget.Builder(increaseText, button -> multiply(2.0)).dimensions(centerX - 10 - 60, centerY + 35, 20, 20).build();
        this.addDrawableChild(increaseButton);

        decreaseButton = new ButtonWidget.Builder(decreaseText, button -> multiply(0.5)).dimensions(centerX + 10 - 60, centerY + 35, 20, 20).build();
        this.addDrawableChild(decreaseButton);

        resetButton = new ButtonWidget.Builder(resetText, button -> resetSize()).dimensions(centerX + 30 - 60, centerY + 35, 20, 20).build();
        this.addDrawableChild(resetButton);

        calculateSize();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        drawLeftAlignedString(context,
                "\u00a78" + text("settings.screenshot.overrideSize").getString(),
                centerX - 75, centerY - 35,
                0x000000);

        drawLeftAlignedString(context,
                "\u00a78" + text("settings.screenshot.alwaysAllocated").getString(),
                centerX - 75, centerY - 15,
                0x000000);

        drawLeftAlignedString(context,
                "\u00a78x",
                centerX - 42, centerY + 12,
                0x000000);

        drawLeftAlignedString(context,
                "\u00a78" + text("settings.main.estimate", RCUtil.formatMetric(estimatedSize) + "B").getString(),
                centerX + 25, centerY + 12,
                0x000000);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        calculateSize();
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected void applySettingsAndCleanup() {
        if (NumberUtils.isParsable(widthTextField.getText())
                && NumberUtils.isParsable(heightTextField.getText())) {
            int newWidth = (int) Math.abs(Double.parseDouble(widthTextField.getText()));
            int newHeight = (int) Math.abs(Double.parseDouble(heightTextField.getText()));

            if (newWidth != mod.getScreenshotWidth() || newHeight != mod.getScreenshotHeight()) {
                mod.setScreenshotWidth(newWidth);
                mod.setScreenshotHeight(newHeight);

                if (mod.isScreenshotFramebufferAlwaysAllocated()) {
                    mod.initScreenshotFramebuffer();
                }
            }
        }
        super.applySettingsAndCleanup();
    }

    private void multiply(double mul) {
        if (NumberUtils.isParsable(widthTextField.getText())
                && NumberUtils.isParsable(heightTextField.getText())) {
            widthTextField.setText(String.valueOf(
                    (int) Math.abs(Double.parseDouble(widthTextField.getText()) * mul)));
            heightTextField.setText(String.valueOf(
                    (int) Math.abs(Double.parseDouble(heightTextField.getText()) * mul)));
            calculateSize();
        }
    }

    private void resetSize() {
        mod.setScreenshotWidth(3840);
        mod.setScreenshotHeight(2160);
        widthTextField.setText(String.valueOf(mod.getScreenshotWidth()));
        heightTextField.setText(String.valueOf(mod.getScreenshotHeight()));
    }

    private void calculateSize() {
        if (NumberUtils.isParsable(widthTextField.getText())
                && NumberUtils.isParsable(heightTextField.getText())) {
            estimatedSize = (long) (Double.parseDouble(widthTextField.getText())
                    * Double.parseDouble(heightTextField.getText()) * 8);
        }
    }
}
