package cc.modlabs.resolutioncontrol.mixin;

import cc.modlabs.resolutioncontrol.ResolutionControlMod;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Shadow
    public Framebuffer entityOutlinesFramebuffer;

    @Inject(at = @At("RETURN"), method = "loadEntityOutlinePostProcessor")
    private void onLoadEntityOutlineShader(CallbackInfo ci) {
        ResolutionControlMod.getInstance().resizeMinecraftFramebuffers();
    }

    @Inject(at = @At("RETURN"), method = "onResized")
    private void onOnResized(CallbackInfo ci) {
        if (entityOutlinesFramebuffer == null) return;
        ResolutionControlMod.getInstance().resizeMinecraftFramebuffers();
    }
}
