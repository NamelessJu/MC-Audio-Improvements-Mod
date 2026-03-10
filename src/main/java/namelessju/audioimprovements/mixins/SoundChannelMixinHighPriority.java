package namelessju.audioimprovements.mixins;

import com.mojang.blaze3d.audio.Channel;
import namelessju.audioimprovements.mixinaccessors.SoundChannelMixinAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = Channel.class, priority = -9999999)
public class SoundChannelMixinHighPriority
{
    @ModifyVariable(method = "linearAttenuation", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float audioImprovements$linearAttenuationParameter(float value)
    {
        ((SoundChannelMixinAccessor) this).audioImprovements$setOriginalAttenuation(value);
        return value;
    }
}
