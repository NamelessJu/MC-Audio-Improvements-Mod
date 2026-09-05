package namelessju.audio_improvements.mixin;

import com.mojang.blaze3d.audio.Channel;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import namelessju.audio_improvements.mixinaccessors.SoundChannelMixinAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = Channel.class, priority = -9999999)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public class SoundChannelMixinHighPriority
{
    @ModifyVariable(
        method = "linearAttenuation",
        at = @At("HEAD"),
        argsOnly = true,
        //? if >= 26 {
        name = "maxDistance"
        //? } else {
        /*ordinal = 0
        *///? }
    )
    private float modifyLinearAttenuationMaxDistance(float maxDistance)
    {
        ((SoundChannelMixinAccessor) this).audioImprovements$setOriginalAttenuation(maxDistance);
        return maxDistance;
    }
}
