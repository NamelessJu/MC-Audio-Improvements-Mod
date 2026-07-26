package namelessju.audioimprovements.mixins.vinurl;

import com.vinurl.client.SoundManager;
import namelessju.audioimprovements.AudioImprovements;
import net.minecraft.client.resources.sounds.SoundInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(SoundManager.class)
public class VinURLSoundManagerMixin
{
    @ModifyArg(
        method = "playSound",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/sounds/SoundManager;play(Lnet/minecraft/client/resources/sounds/SoundInstance;)V"
        ),
        index = 0
    )
    private static SoundInstance audioImprovements$beforePlaySound(SoundInstance soundInstance)
    {
        AudioImprovements.getInstance().musicDiscSoundInstances.add(soundInstance);
        return soundInstance;
    }
}
