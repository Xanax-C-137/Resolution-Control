package cc.flawcra.resolutioncontrol.mixin;

import cc.flawcra.resolutioncontrol.ResolutionControlMod;
import cc.flawcra.resolutioncontrol.util.Config;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL45;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.nio.IntBuffer;

@Mixin(Framebuffer.class)
public abstract class FramebufferMixin {
    @Unique private boolean isMipmapped;
    @Unique private float scaleMultiplier;

    @Shadow public abstract int getColorAttachment();

    @Inject(method = "initFbo", at = @At("HEAD"))
    private void onInitFbo(int width, int height, boolean getError, CallbackInfo ci) {
        scaleMultiplier = (float) width / MinecraftClient.getInstance().getWindow().getWidth();
        isMipmapped = Config.getInstance().mipmapHighRes && scaleMultiplier > 2.0f;
    }


    @ModifyArgs(method = "*", at = @At(value = "INVOKE",
            target = "Lcom/mojang/blaze3d/platform/GlStateManager;_texParameter(III)V"))
    private void modifyTexParameterArgs(Args args) {

        int target = args.get(0);
        int pname = args.get(1);
        int param = args.get(2);

        if (pname == GL11.GL_TEXTURE_MIN_FILTER) {
            param = ResolutionControlMod.getInstance().getUpscaleAlgorithm().getId(isMipmapped);
        } else if (pname == GL11.GL_TEXTURE_MAG_FILTER) {
            param = ResolutionControlMod.getInstance().getDownscaleAlgorithm().getId(false);
        } else if (pname == GL11.GL_TEXTURE_WRAP_S || pname == GL11.GL_TEXTURE_WRAP_T) {
            // Fix linear scaling creating black borders
            param = GL12.GL_CLAMP_TO_EDGE;
        }

        args.setAll(target, pname, param);
    }

    @Redirect(method = "initFbo", at = @At(value = "INVOKE",
            target = "Lcom/mojang/blaze3d/platform/GlStateManager;_texImage2D(IIIIIIIILjava/nio/IntBuffer;)V"))
    private void onTexImage(int target, int level, int internalFormat, int width, int height, int border, int format,
                            int type, IntBuffer pixels) {
        if (isMipmapped) {
            int mipmapLevel = MathHelper.ceil(Math.log(scaleMultiplier) / Math.log(2));
            for (int i = 0; i < mipmapLevel; i++) {
                GlStateManager._texImage2D(target, i, internalFormat,
                       width << i, height << i,
                        border, format, type, pixels);
            }
        } else {
            GlStateManager._texImage2D(target, 0, internalFormat, width, height, border, format, type, pixels);
        }

    }

    @Inject(method = "drawInternal", at = @At("HEAD"))
    private void onDraw(int width, int height, boolean bl, CallbackInfo ci) {
        if (isMipmapped) {
            GlStateManager._bindTexture(this.getColorAttachment());
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        }
    }
}
