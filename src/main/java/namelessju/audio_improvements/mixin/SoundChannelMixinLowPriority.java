package namelessju.audio_improvements.mixin;

import com.mojang.blaze3d.audio.Channel;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import namelessju.audio_improvements.mixinaccessors.SoundChannelMixinAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Channel.class, priority = 9999999)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public class SoundChannelMixinLowPriority
{
    @Inject(method = "linearAttenuation", at = @At("RETURN"))
    private void afterSetLinearAttenuation(float maxDistance, CallbackInfo ci)
    {
        ((SoundChannelMixinAccessor) this).audioImprovements$afterSetLinearAttenuation();
    }
}
