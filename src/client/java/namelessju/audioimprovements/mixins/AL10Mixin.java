package namelessju.audioimprovements.mixins;

import namelessju.audioimprovements.AudioImprovements;
import org.lwjgl.openal.AL10;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.FloatBuffer;

@Mixin(value = AL10.class, remap = false)
public abstract class AL10Mixin
{
    // This is for compatibility with other mods that call the library
    // functions directly instead of going through the channel class
    
    @Inject(method = "alSourcefv(IILjava/nio/FloatBuffer;)V", at = @At("HEAD"), remap = false)
    private static void audioImprovements$alSourcefv(int source, int param, FloatBuffer values, CallbackInfo ci)
    {
        if (param != AL10.AL_POSITION) return;
        AudioImprovements.getInstance().checkSourcePositionUpdate(source, values.get(0), values.get(1), values.get(2));
    }
    
    @Inject(method = "alSourcefv(II[F)V", at = @At("HEAD"), remap = false)
    private static void audioImprovements$alSourcefv(int source, int param, float[] values, CallbackInfo ci)
    {
        if (param != AL10.AL_POSITION) return;
        AudioImprovements.getInstance().checkSourcePositionUpdate(source, values[0], values[1], values[2]);
    }
    
    @Inject(method = "alSource3f", at = @At("HEAD"), remap = false)
    private static void audioImprovements$alSource3f(int source, int param, float v1, float v2, float v3, CallbackInfo ci)
    {
        if (param != AL10.AL_POSITION) return;
        AudioImprovements.getInstance().checkSourcePositionUpdate(source, v1, v2, v3);
    }
}
