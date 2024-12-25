package cc.modlabs.resolutioncontrol.util;

import cc.modlabs.resolutioncontrol.ResolutionControlMod;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;

public class DynamicResolutionHandler {
    public static final DynamicResolutionHandler INSTANCE = new DynamicResolutionHandler();
    private final FloatList scales = new FloatArrayList();
    private int baseScale;

    private int timer = 10;
    private int currentScale;

    private DynamicResolutionHandler() {
        reset();
    }

    public void tick() {
        timer--;

        if (timer <= 0) {
            //update();
        }
    }

    public void reset() {
        for (float i = Config.getInstance().drMinScale; i <= Config.getInstance().drMaxScale;
             i += Config.getInstance().drResStep) {
            scales.add(i);

            if (i == 1.0f) {
                baseScale = scales.size() - 1;
                currentScale = baseScale;
            }
        }
    }

    /*private void update() {
        MinecraftClient client = MinecraftClient.getInstance();

        final int smoothAmount = Config.getInstance().drFpsSmoothAmount;
        float sum = 0;
        for (int i = client.metricsData.getCurrentIndex() - smoothAmount;
             i < client.metricsData.getCurrentIndex(); i++) {

            sum += client.metricsData.getSamples()[Math.floorMod(i, 240)];
        }
        float fps = 1_000_000_000.0f / (sum / smoothAmount);

        if (fps > Config.getInstance().drMaxFps) {
            setCurrentScale(Math.min(currentScale + 1, scales.size() - 1));
            timer = 15;
        } else if (fps < Config.getInstance().drMinFps) {
            setCurrentScale(Math.max(currentScale - 1, 0));
            timer = 5;
        } else {
            timer = 3;
        }
    }*/

    private void setCurrentScale(int currentScale) {
        boolean equal = this.currentScale == currentScale;
        this.currentScale = currentScale;

        if (!equal) {
            ResolutionControlMod.getInstance().updateFramebufferSize();
        }
    }

    public double getCurrentScale() {
        return scales.getFloat(currentScale);
    }
}
