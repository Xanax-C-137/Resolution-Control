package cc.modlabs.resolutioncontrol.mixin;

import cc.modlabs.resolutioncontrol.ResolutionControlMod;
import net.minecraft.client.util.Window;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Window.class)
public abstract class WindowMixin {
	@Inject(method = "getFramebufferWidth", at = @At("RETURN"), cancellable = true)
	private void onGetFramebufferWidth(CallbackInfoReturnable<Integer> cir) {
		// If screenshotting, override with screenshot width;
		// Otherwise, use the normal scale factor for everyday rendering.
		if (ResolutionControlMod.getInstance().isScreenshotting()) {
			cir.setReturnValue(ResolutionControlMod.getInstance().getScreenshotWidth());
		} else {
			// Multiply the “real” width by the user’s chosen scale factor.
			double userFactor = ResolutionControlMod.getInstance().getCurrentScaleFactor();
			if (userFactor != 1.0) {
				int actualWidth = cir.getReturnValueI();
				int scaledWidth = Math.max(MathHelper.ceil(actualWidth * userFactor), 1);
				cir.setReturnValue(scaledWidth);
			}
		}
	}

	@Inject(method = "getFramebufferHeight", at = @At("RETURN"), cancellable = true)
	private void onGetFramebufferHeight(CallbackInfoReturnable<Integer> cir) {
		if (ResolutionControlMod.getInstance().isScreenshotting()) {
			cir.setReturnValue(ResolutionControlMod.getInstance().getScreenshotHeight());
		} else {
			double userFactor = ResolutionControlMod.getInstance().getCurrentScaleFactor();
			if (userFactor != 1.0) {
				int actualHeight = cir.getReturnValueI();
				int scaledHeight = Math.max(MathHelper.ceil(actualHeight * userFactor), 1);
				cir.setReturnValue(scaledHeight);
			}
		}
	}

	@Unique
	private int scale(int value) {
		double scaleFactor = ResolutionControlMod.getInstance().getCurrentScaleFactor();
		return Math.max(MathHelper.ceil(((double) value) * scaleFactor), 1);
	}

	@Inject(method = "getScaleFactor", at = @At("RETURN"), cancellable = true)
	private void onGetScaleFactor(CallbackInfoReturnable<Double> cir) {
		double baseFactor = cir.getReturnValueD();
		double userFactor = ResolutionControlMod.getInstance().getCurrentScaleFactor();

		// If the user has set something other than 1.0, multiply the base.
		if (userFactor != 1.0) {
			cir.setReturnValue(baseFactor * userFactor);
		}
	}

	@Inject(at = @At("RETURN"), method = "onFramebufferSizeChanged")
	private void onFramebufferSizeChanged(CallbackInfo ci) {
		ResolutionControlMod.getInstance().onResolutionChanged();
	}

	@Inject(at = @At("RETURN"), method = "updateFramebufferSize")
	private void onUpdateFramebufferSize(CallbackInfo ci) {
		ResolutionControlMod.getInstance().onResolutionChanged();
	}
}
