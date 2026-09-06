//? fabric {
package namelessju.audio_improvements.platform.fabric.mixin.vinurl;

import com.vinurl.client.SoundManager;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import namelessju.audio_improvements.AudioImprovements;
import net.minecraft.client.resources.sounds.SoundInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(SoundManager.class)
@MixinEnvironment(value = "vinurl", type = MixinEnvironment.Env.CLIENT)
public class VinURLSoundManagerMixin
{
    @ModifyArg(
        method = "playSound",
        at = @At(
            value = "INVOKE",
            //? if >=1.21.11 {
            target = "Lnet/minecraft/client/sounds/SoundManager;play(Lnet/minecraft/client/resources/sounds/SoundInstance;)Lnet/minecraft/client/sounds/SoundEngine$PlayResult;"
            //? } else {
            /*target = "Lnet/minecraft/client/sounds/SoundManager;play(Lnet/minecraft/client/resources/sounds/SoundInstance;)V"
            *///? }
        ),
        index = 0
    )
    private static SoundInstance audioImprovements$beforePlaySound(SoundInstance soundInstance)
    {
        AudioImprovements.LOGGER.debug("VinURL sound played");
        AudioImprovements.instance().musicDiscSoundInstances.add(soundInstance);
        return soundInstance;
    }
}
//? }
