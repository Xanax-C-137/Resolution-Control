package cc.modlabs.resolutioncontrol.util;

public final class Config {
	public float scaleFactor = 1.0f;

	public ScalingAlgorithm upscaleAlgorithm = ScalingAlgorithm.NEAREST;
	public ScalingAlgorithm downscaleAlgorithm = ScalingAlgorithm.LINEAR;

	public boolean mipmapHighRes = false;

	public boolean overrideScreenshotScale = true;

	public int screenshotWidth = 3840;
	public int screenshotHeight = 2160;

	public boolean screenshotFramebufferAlwaysAllocated = false;

	public boolean enableDynamicResolution = false;

	public float drMinScale = 0.5f;
	public float drMaxScale = 2.0f;
	public float drResStep = 0.0625f;
	public int drMinFps = 60;
	public int drMaxFps = 70;
	public int drFpsSmoothAmount = 10;

	public static Config getInstance() {
		return ConfigHandler.instance.getConfig();
	}

}
