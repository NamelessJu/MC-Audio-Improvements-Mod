package namelessju.audio_improvements.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import namelessju.audio_improvements.AudioImprovements;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Options.class)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public abstract class OptionsMixin
{
    @ModifyReturnValue(
        method = "getSoundSourceVolume",
        at = @At(
            value = "RETURN"
        )
    )
    private float onReturnSoundSourceVolume(float volume)
    {
        return AudioImprovements.instance().processVolume(volume);
    }
}
