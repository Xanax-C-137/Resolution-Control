package cc.modlabs.resolutioncontrol.mixin;

import cc.modlabs.resolutioncontrol.ResolutionControlMod;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	@Inject(at = @At("HEAD"), method = "renderWorld")
	private void onRenderWorldBegin(CallbackInfo callbackInfo) {
		if(!ResolutionControlMod.getInstance().hasRun) {
			ResolutionControlMod.getInstance().hasRun = true;
			// TODO: Really hacky way to wait for the game to start rendering. This HAS TO be improved.
			new Thread("ResolutionControlMod") {
				@Override
				public void run() {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        ResolutionControlMod.LOGGER.error("Thread interrupted", e);
                    }
                    ResolutionControlMod.getInstance().onResolutionChanged();
				}
			}.start();
		}

		ResolutionControlMod.getInstance().setShouldScale(true);
	}
	
	@Inject(at = @At("RETURN"), method = "renderWorld")
	private void onRenderWorldEnd(CallbackInfo callbackInfo) {
		ResolutionControlMod.getInstance().setShouldScale(false);
	}
}
