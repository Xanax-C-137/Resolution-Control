package cc.modlabs.resolutioncontrol.mixin;

import cc.modlabs.resolutioncontrol.ResolutionControlMod;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	@Unique
	private boolean waitingForResolutionChange = false;

	@Unique
	private int waitFrameCounter = 0;

	@Inject(at = @At("HEAD"), method = "renderWorld")
	private void onRenderWorldBegin(CallbackInfo callbackInfo) {
		if (!ResolutionControlMod.getInstance().hasRun) {
			ResolutionControlMod.getInstance().hasRun = true;
			waitingForResolutionChange = true;
		}

		if (waitingForResolutionChange) {
			waitFrameCounter++;
			if (waitFrameCounter >= 5) {
				waitingForResolutionChange = false;
				ResolutionControlMod.getInstance().onResolutionChanged();
			}
		}

		ResolutionControlMod.getInstance().setShouldScale(true);
	}
	
	@Inject(at = @At("RETURN"), method = "renderWorld")
	private void onRenderWorldEnd(CallbackInfo callbackInfo) {
		ResolutionControlMod.getInstance().setShouldScale(false);
	}
}
