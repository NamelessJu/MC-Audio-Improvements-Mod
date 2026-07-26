package namelessju.audioimprovements.mixins;

import com.mojang.blaze3d.audio.Channel;
import namelessju.audioimprovements.mixinaccessors.SoundChannelMixinAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Channel.class, priority = 9999999)
public class SoundChannelMixinLowPriority
{
    @Inject(method = "linearAttenuation", at = @At("RETURN"))
    private void audioImprovements$linearAttenuation(float attenuation, CallbackInfo ci)
    {
        ((SoundChannelMixinAccessor) this).audioImprovements$afterSetLinearAttenuation();
    }
}
